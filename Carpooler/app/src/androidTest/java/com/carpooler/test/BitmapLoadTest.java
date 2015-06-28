package com.carpooler.test;

import android.graphics.Bitmap;
import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;

import junit.framework.Assert;

/**
 * Created by raymond on 6/27/15.
 */
public class BitmapLoadTest extends DatabaseServiceTest{

    public void testLoadBitmap() throws RemoteException, InterruptedException {
        String url = "http://image10.bizrate-images.com/resize?sq=60&uid=2216744464";
        conn.loadBitmap(url,new BitmapResponseCallback());
        checkResponse();
    }


    class BitmapResponseCallback extends AbstractTestCallback<Bitmap> implements DatabaseService.BitmapCallback{

        @Override
        public void doSuccess(Bitmap data) {
            Assert.assertNotNull("Success is null", data);
            latch.countDown();
        }
    }
}
