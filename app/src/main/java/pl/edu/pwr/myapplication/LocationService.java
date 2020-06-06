package pl.edu.pwr.myapplication;

import android.app.IntentService;
import android.content.Intent;

public class LocationService extends IntentService {
    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("SERVICE WORKING");
    }
}