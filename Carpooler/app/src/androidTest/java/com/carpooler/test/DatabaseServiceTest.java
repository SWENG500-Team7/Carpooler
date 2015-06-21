package com.carpooler.test;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.test.ServiceTestCase;

import com.carpooler.dao.DatabaseService;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by raymond on 6/14/15.
 */
public abstract class DatabaseServiceTest extends ServiceTestCase<DatabaseService> {
    private HandlerThread handlerThread;
    protected CountDownLatch latch;
    private class TestResponseHandler extends Handler{
        private TestResponseHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DatabaseService.ERROR:
                    errorResp = (String) msg.obj;
                    break;
                case DatabaseService.EXCEPTION:
                    errorException = (Exception) msg.obj;
                    break;
                case DatabaseService.QUERY_INDEX:
                    assertTrue("Empty List Found",!((List)msg.obj).isEmpty());
                default:
                    resp = msg.obj;
            }
            latch.countDown();
        }

    }
    private Object resp;
    private String errorResp;
    private Exception errorException;
    protected DatabaseService.Connection conn;

    public DatabaseServiceTest() {
        super(DatabaseService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        handlerThread = new HandlerThread("Test");
        handlerThread.start();

        Messenger responseMessenger = new Messenger(new TestResponseHandler(handlerThread.getLooper()));
        conn = new DatabaseService.Connection(responseMessenger);
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), DatabaseService.class);
        IBinder binder = bindService(startIntent);
        conn.onServiceConnected(null, binder);
        latch = new CountDownLatch(1);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        handlerThread.quit();
        errorException=null;
        errorResp=null;
        resp=null;
    }

    protected void checkResponse() throws InterruptedException {
        latch.await(20, TimeUnit.SECONDS);
        assertNull("Exception found", errorException);
        assertNull("Error Message Found:" + errorResp, errorResp);
        assertNotNull("Null Response", resp);
    }
}
