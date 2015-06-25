package com.carpooler.dao;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import com.carpooler.dao.handlers.DeleteDataHandler;
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
import java.util.List;

import io.searchbox.client.JestClient;

/**
 * Created by raymond on 6/12/15.
 */
public class DatabaseService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static Logger log = LoggerFactory.getLogger(JestClientFactory.class);
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
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                if (jestClient==null){
                    Messenger replyTo = msg.replyTo;
                    if (replyTo != null) {
                        String errorMessage = "Database setup invalid";
                        Message response = Message.obtain(null, DatabaseService.ERROR, errorMessage);
                        replyTo.send(response);
                    }

                }else {
                        for (AbstractHandler handler : handlers) {
                            if (msg.what == handler.getWhat()) {
                                handler.process(jestClient, msg);
                                break;
                            }
                        }
                }
            } catch (RemoteException ex) {
                log.error("", ex);
            }
        }
    }

    public static class Connection implements ServiceConnection {
        private Messenger sendMessenger;
        private Messenger replyTo;
        private List<Message> messageHolder = new ArrayList<>();
        public Connection(Messenger replyTo) {
            this.replyTo = replyTo;
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

        public <T extends DatabaseObject> void putMapping(Class<T> type, int callbackId) throws RemoteException {
            Message message = Message.obtain(null, PUT_MAPPING, type);
            message.replyTo = replyTo;
            sendMessage(message,callbackId);
        }
        private void sendMessage(Message message, int callbackId) throws RemoteException {
            message.replyTo = replyTo;
            message.arg1 = callbackId;
            if (sendMessenger==null){
                messageHolder.add(message);
            }else{
                sendMessenger.send(message);
            }
        }

        public <T extends DatabaseObject> void create(T data, int callbackId) throws RemoteException {
            Message message = Message.obtain(null, CREATE_INDEX, data);
            sendMessage(message,callbackId);
        }

        public <T extends DatabaseObject> void get(IdRequest<T> request, int callbackId) throws RemoteException {
            Message message = Message.obtain(null, GET_INDEX, request);
            sendMessage(message,callbackId);
        }

        public <T extends DatabaseObject> void delete(IdRequest<T> request, int callbackId) throws RemoteException {
            Message message = Message.obtain(null, DELETE_INDEX, request);
            sendMessage(message,callbackId);
        }
        public <T extends DatabaseObject> void update(T data, int callbackId) throws RemoteException {
            Message message = Message.obtain(null, UPDATE_INDEX, data);
            sendMessage(message,callbackId);
        }

        public <T extends DatabaseObject> void query(QueryRequest<T> request, int callbackId) throws RemoteException {
            Message message = Message.obtain(null, QUERY_INDEX, request);
            sendMessage(message,callbackId);
        }
    }
}
