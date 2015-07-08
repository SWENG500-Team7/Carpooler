package com.carpooler.dao;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import com.carpooler.dao.dto.DatabaseObject;
import com.carpooler.dao.handlers.AbstractHandler;
import com.carpooler.dao.handlers.BitmapLoadHandler;
import com.carpooler.dao.handlers.DeleteDataHandler;
import com.carpooler.dao.handlers.GeocodeHandler;
import com.carpooler.dao.handlers.GetDataHandler;
import com.carpooler.dao.handlers.IndexDataHandler;
import com.carpooler.dao.handlers.PutMappingHandler;
import com.carpooler.dao.handlers.QueryDataHandler;
import com.carpooler.dao.handlers.UpdateDataHandler;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.searchbox.client.JestClient;

/**
 * Created by raymond on 6/12/15.
 */
public class DatabaseService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static Logger log = LoggerFactory.getLogger(DatabaseService.class);
    public static final String DATABASE_URL = "database.url";
    public static final String DATABASE_USER = "database.user";
    public static final String DATABASE_PASSWORD = "database.password";
    private JestClient jestClient;
    public static final int EXCEPTION = -2;
    public static final int ERROR = -1;
    public static final int CREATE_INDEX = 0;
    public static final int PUT_MAPPING = 1;
    public static final int GET_INDEX = 2;
    public static final int UPDATE_INDEX = 3;
    public static final int DELETE_INDEX = 4;
    public static final int QUERY_INDEX = 5;
    public static final int GEOCODE = 100;
    public static final int BITMAP = 200;
    private Messenger serviceMessenger;
    private HandlerThread handlerThread;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(sharedPreferences, null);
        handlerThread = new HandlerThread(DatabaseService.class.getSimpleName());
        handlerThread.start();
        serviceMessenger = new Messenger(new DatabaseHandler(handlerThread.getLooper()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handlerThread!=null && handlerThread.isAlive()) {
            handlerThread.quit();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == null
                || DATABASE_URL.equals(key)
                || DATABASE_USER.equals(key)
                || DATABASE_PASSWORD.equals(key)) {
            String url = sharedPreferences.getString(DATABASE_URL, null);
            String user = sharedPreferences.getString(DATABASE_USER, null);
            String password = sharedPreferences.getString(DATABASE_PASSWORD, null);

            if (url != null && user != null && password != null) {
                DroidClientConfig config = new DroidClientConfig.Builder(url)
                        .defaultCredentials(user, password)
                        .multiThreaded(true)
                        .build();
                JestClientFactory jestClientFactory = new CustomJestClientFactory();
                jestClientFactory.setDroidClientConfig(config);
                jestClient = jestClientFactory.getObject();
            } else {
                log.error("Credentials not set");
            }
        }

    }

    private class DatabaseHandler extends Handler {
        private final List<AbstractHandler> handlers = new ArrayList<>();

        DatabaseHandler(Looper looper) {
            super(looper);
            handlers.add(new IndexDataHandler());
            handlers.add(new PutMappingHandler());
            handlers.add(new GetDataHandler());
            handlers.add(new UpdateDataHandler());
            handlers.add(new DeleteDataHandler());
            handlers.add(new QueryDataHandler());
            handlers.add(new GeocodeHandler(getApplicationContext()));
            handlers.add(new BitmapLoadHandler());
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                if (jestClient==null){

                }else {
                        for (AbstractHandler handler : handlers) {
                            if (msg.what == handler.getWhat()) {
                                if (jestClient==null && handler.isJestRequired()){
                                    DatabaseService.CallbackMessage callbackMessage = (DatabaseService.CallbackMessage) msg.obj;
                                    handler.replyError(msg,"Database setup invalid",callbackMessage);
                                }else {
                                    handler.process(jestClient, msg);
                                }
                                break;
                            }
                        }
                }
            } catch (RemoteException ex) {
                log.error("", ex);
            }
        }
    }

   private static class DataHandler extends Handler {

       public DataHandler() {
           super();
       }
        public DataHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            CallbackMessage callbackMessage = (CallbackMessage) msg.obj;
            if (callbackMessage.callback!=null) {
                switch (msg.what) {
                    case DatabaseService.ERROR:
                        callbackMessage.callback.doError(callbackMessage.errorMessage);
                        break;
                    case DatabaseService.EXCEPTION:
                        callbackMessage.callback.doException(callbackMessage.exception);
                        break;
                    default:
                        callbackMessage.callback.doSuccess(callbackMessage.response);
                }
            }
        }
    }
    public static class Connection implements ServiceConnection {
        private Messenger sendMessenger;
        private Messenger replyTo;
        private Queue<Message> messageHolder = new LinkedList<>();
        public Connection() {
            this.replyTo = new Messenger(new DataHandler());
        }

        public Connection(Looper looper) {
            this.replyTo = new Messenger(new DataHandler(looper));
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sendMessenger = new Messenger(service);
            for (Message message:messageHolder){
                try {
                    sendMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        private void sendMessage(CallbackMessage callbackMessage, int what) throws RemoteException {
            Message message = Message.obtain(null, what, callbackMessage);
            message.replyTo = replyTo;
            if (sendMessenger==null){
                messageHolder.add(message);
            }else{
                sendMessenger.send(message);
            }
        }

        public <T extends DatabaseObject> void putMapping(Class<T> type, PutMappingCallback callback) throws RemoteException {
            CallbackMessage callbackMessage = new CallbackMessage(callback,type);
            sendMessage(callbackMessage,PUT_MAPPING);
        }
        public <T extends DatabaseObject> void create(T data, IndexCallback callback) throws RemoteException {
            CallbackMessage callbackMessage = new CallbackMessage(callback,data);
            sendMessage(callbackMessage,CREATE_INDEX);
        }

        public <T extends DatabaseObject> void get(IdRequest<T> request, GetCallback<T> callback) throws RemoteException {
            CallbackMessage callbackMessage = new CallbackMessage(callback,request);
            sendMessage(callbackMessage,GET_INDEX);
        }

        public <T extends DatabaseObject> void delete(IdRequest<T> request, DeleteCallback callback) throws RemoteException {
            CallbackMessage callbackMessage = new CallbackMessage(callback,request);
            sendMessage(callbackMessage,DELETE_INDEX);
        }
        public <T extends DatabaseObject> void update(T data, UpdateCallback callback) throws RemoteException {
            CallbackMessage callbackMessage = new CallbackMessage(callback,data);
            sendMessage(callbackMessage,UPDATE_INDEX);
        }

        public <T extends DatabaseObject> void query(QueryRequest<T> request, QueryCallback<T> callback) throws RemoteException {
            CallbackMessage callbackMessage = new CallbackMessage(callback,request);
            sendMessage(callbackMessage,QUERY_INDEX);
        }
        public void geocode(String address, GeocodeCallback callback) throws RemoteException {
            CallbackMessage callbackMessage = new CallbackMessage(callback,address);
            sendMessage(callbackMessage,GEOCODE);
        }
        public void loadBitmap(String url, BitmapCallback callback) throws RemoteException {
            CallbackMessage callbackMessage = new CallbackMessage(callback,url);
            sendMessage(callbackMessage,BITMAP);
        }
    }

    public static class CallbackMessage{
        private final Callback callback;
        private final Object request;
        private Object response;
        private String errorMessage;
        private Exception exception;

        public CallbackMessage(Callback callback, Object request) {
            this.callback = callback;
            this.request = request;
        }

        public Callback getCallback() {
            return callback;
        }

        public Object getRequest() {
            return request;
        }

        public Object getResponse() {
            return response;
        }

        public void setResponse(Object response) {
            this.response = response;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    public static interface Callback<T> {
        public void doError(String message);
        public void doException(Exception exception);
        public void doSuccess(T data);
    }

    public static interface GetCallback<T extends DatabaseObject> extends Callback<T> {
    }
    public static interface QueryCallback<T extends DatabaseObject> extends Callback<List<T>> {
    }
    public static interface PutMappingCallback extends Callback<String> {
    }
    public static interface UpdateCallback extends Callback<String> {
    }
    public static interface DeleteCallback extends Callback<String> {
    }
    public static interface IndexCallback extends Callback<String> {
    }
    public static interface GeocodeCallback extends Callback<Address>{

    }

    public static interface BitmapCallback extends Callback<Bitmap>{

    }
}
