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
import com.carpooler.dao.handlers.GetDataHandler;
import com.carpooler.dao.handlers.IndexDataHandler;
import com.carpooler.dao.handlers.PutMappingHandler;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        private IndexDataHandler indexDataHandler = new IndexDataHandler();
        private PutMappingHandler putMappingHandler = new PutMappingHandler();
        private GetDataHandler getDataHandler = new GetDataHandler();

        DatabaseHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case CREATE_INDEX:
                        handleCreateIndex(msg);
                        break;
                    case PUT_MAPPING:
                        handlePutMapping(msg);
                        break;
                    case GET_INDEX:
                        handleGet(msg);
                        break;
                }
            } catch (RemoteException ex) {
            }

        }

        private void handleCreateIndex(Message msg) throws RemoteException {
            indexDataHandler.process(jestClient, msg);
        }

        private void handlePutMapping(Message msg) throws RemoteException {
            putMappingHandler.process(jestClient, msg);
        }

        private void handleGet(Message msg) throws RemoteException {
            getDataHandler.process(jestClient, msg);
        }
    }

    public static class Connection implements ServiceConnection {
        private Messenger sendMessenger;
        private Messenger replyTo;

        public Connection(Messenger replyTo) {
            this.replyTo = replyTo;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sendMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        public <T extends DatabaseObject> void putMapping(Class<T> type) throws RemoteException {
            Message message = Message.obtain(null, PUT_MAPPING, type);
            message.replyTo = replyTo;
            sendMessenger.send(message);
        }

        public <T extends DatabaseObject> void create(T data) throws RemoteException {
            Message message = Message.obtain(null, CREATE_INDEX, data);
            message.replyTo = replyTo;
            sendMessenger.send(message);
        }

        public <T extends DatabaseObject> void get(GetRequest<T> request) throws RemoteException {
            Message message = Message.obtain(null, GET_INDEX, request);
            message.replyTo = replyTo;
            sendMessenger.send(message);
        }
    }
}
