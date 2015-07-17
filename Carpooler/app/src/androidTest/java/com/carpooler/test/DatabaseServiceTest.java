package com.carpooler.test;

import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.test.ServiceTestCase;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.DatabaseObject;

import junit.framework.Assert;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.searchbox.core.SearchResult;

/**
 * Created by raymond on 6/14/15.
 */
public abstract class DatabaseServiceTest extends ServiceTestCase<DatabaseService> {
    private HandlerThread handlerThread;
    protected CountDownLatch latch;
    protected DatabaseService.Connection conn;

    public DatabaseServiceTest() {
        super(DatabaseService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        handlerThread = new HandlerThread("Test");
        handlerThread.start();

        conn = new DatabaseService.Connection(handlerThread.getLooper());
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
    }

    protected void checkResponse() throws InterruptedException {
        latch.await(20, TimeUnit.SECONDS);
    }

    abstract class AbstractTestCallback<T> implements DatabaseService.Callback<T> {
        @Override
        public void doError(String message) {
            Assert.assertNull("Error found", message);
            latch.countDown();
        }

        @Override
        public void doException(Exception exception) {
            Assert.assertNull("Exception found", exception);
            latch.countDown();
        }

    }
    class StringResponseCallback extends AbstractTestCallback<String> implements DatabaseService.IndexCallback, DatabaseService.DeleteCallback,DatabaseService.UpdateCallback,DatabaseService.PutMappingCallback{
        @Override
        public void doSuccess(String id) {
            Assert.assertNotNull("Success is null", id);
            latch.countDown();
        }
    }

    class GetResponseCallback<T extends DatabaseObject> extends AbstractTestCallback<T> implements DatabaseService.GetCallback<T>{
        @Override
        public void doSuccess(T data) {
            Assert.assertNotNull("Success is null", data);
            latch.countDown();
        }
    }
    class QueryResponseCallback<T extends DatabaseObject> extends AbstractTestCallback<List<T>> implements DatabaseService.QueryCallback<T>{

        @Override
        public void doSuccess(List<T> data) {
            Assert.assertNotNull("Success is null", data);
            latch.countDown();
        }
    }

    class QueryHitsResponseCallback<T extends DatabaseObject> extends AbstractTestCallback<List<SearchResult.Hit<T,Void>>> implements DatabaseService.QueryHitsCallback<T>{
        @Override
        public void doSuccess(List<SearchResult.Hit<T, Void>> data) {
            Assert.assertNotNull("Success is null", data);
            latch.countDown();
        }
    }
}
