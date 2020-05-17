package pl.edu.pwr.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class PedometerService extends IntentService implements SensorEventListener, StepListener {
    private StepDetector stepDetector;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private int numSteps;
    private String sharedPrefs = "mySharedPrefs";

    ResultReceiver resultReceiver;
    Bundle bundle;

    public PedometerService()
    {
        super("PedometerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("pedometer", "PEDOMETER STARTED SERVICE");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);

        bundle = new Bundle();

        loadData();
    }
    private void saveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt("steps", numSteps);
        myEdit.commit();
    }

    private void loadData()
    {
        SharedPreferences sharedPref = getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        int a = sharedPref.getInt("steps", 0);
        numSteps = a;
        System.out.println("LOAD DATA!!!!!!!!!!!!!!!");
        if(resultReceiver!=null)
        {
            bundle.putInt("stepsCounted", numSteps);
            resultReceiver.send(1, bundle);
        }
    }

    public void onResume() {
        bundle.putInt("stepsCounted", numSteps);
        resultReceiver.send(1, bundle);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onPause() {
        sensorManager.unregisterListener(this);
        saveData();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        System.out.println("ON CHANGED SENSOR !!!!!!!!!!!!");
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            stepDetector.updateAccelerometer(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        bundle.putInt("stepsCounted", numSteps);
        resultReceiver.send(1, bundle);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.v("pedometer", "On handle intent");
        resultReceiver = intent.getParcelableExtra("receiver");

        loadData();

        if(intent.getBooleanExtra("resume",false)==true)
        {
            onResume();
        }
        if(intent.getBooleanExtra("pause", false) == true)
        {
            onPause();
        }
    }

}
