package com.carpooler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.UserDataService;
import com.carpooler.dao.dto.UserData;
import com.carpooler.dao.dto.Vehicle;
import com.carpooler.ui.activities.CarpoolerSettings;


public class CarpoolerActivity extends ActionBarActivity {
    DatabaseService.Connection conn;
    private class ActHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Object resp = msg.obj;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpooler);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Messenger responseMessenger = new Messenger(new ActHandler());
        conn = new DatabaseService.Connection(responseMessenger);
        Intent intent = new Intent(this, DatabaseService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_carpooler, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, CarpoolerSettings.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickRegister(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
