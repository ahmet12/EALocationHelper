package com.ahmetkilic.ealocation;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ahmetkilic.ealocationhelper.EALocationHelper;
import com.ahmetkilic.ealocationhelper.LocationListener;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EALocationHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv_location);
        initHelper();
    }

    private void initHelper() {
        helper = new EALocationHelper(this, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                final String msg = "Updated Location: \n" +
                        Double.toString(location.getLatitude()) + "\n" +
                        Double.toString(location.getLongitude());
                Log.v("LocationChanged", msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(msg);
                    }
                });
            }
        });
    }

    private void getLastLocation(){
        Location location = helper.getLastLocationFromUpdates();

        //Or get by broadcast receiver or listener

        helper.getLastLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        helper.stopLocationUpdates();
    }
}
