package com.SimpleScan.simplescan;

import java.util.Timer;
import java.util.TimerTask;

import com.SimpleScan.simplescan.Tools.GifPlayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SplashWelcome extends Activity {

	GifPlayer gPlayer ; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        startActivity(new Intent(SplashWelcome.this, Main.class));
        /* comment out the anime temporarily 
        gPlayer = (GifPlayer)findViewById(R.id.gifScan);       
        TimerTask task = new TimerTask()
        {
        	public void run()
        	{
        		finish();
        		startActivity(new Intent(SplashWelcome.this, Main.class));
        	}
        };
        Timer openning = new Timer();
        openning.schedule(task, 1500);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}