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

public class PedometerService extends IntentService implements SensorEventListener, StepListener
{

    private StepDetector stepDetector;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private int numSteps;
    private String sharedPrefs = "sharedPre";

    ResultReceiver resultReceiver;
    Bundle bundle;

    private boolean startFlag = false;

    public PedometerService()
    {
        super("PedometerService");
        numSteps = 0;
    }

    private void onResume()
    {
        bundle.putInt("STEPS_", numSteps);
        resultReceiver.send(1, bundle);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void onPause()
    {
        sensorManager.unregisterListener(this);
        saveData();
    }

    private void onClear()
    {
//        numSteps = 0;
//
//        bundle.putInt("STEPS_", numSteps);
//        resultReceiver.send(1, bundle);
    }

    private void saveData()
    {
        System.out.println("SAVE DATA PEDOMETER SERVICE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt("steps", numSteps);
        myEdit.commit();

        if (startFlag)
        {
            bundle.putInt("STEPS_", numSteps);
            resultReceiver.send(1, bundle);
        }
    }

    private void loadData()
    {
        SharedPreferences sharedPref = getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        int a = sharedPref.getInt("steps", 0);
        numSteps = a;

        System.out.println("LOAD DATA PEDOMIETER SERVICE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        if (resultReceiver!=null)
        {
            bundle.putInt("STEPS_", numSteps);
            resultReceiver.send(1, bundle);
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        System.out.println("ON CREATE PEDOMETER SERVICE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);
        bundle = new Bundle();

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        loadData();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        System.out.println("ON HANDLE INTENT PEDOMETER SERIVCE !!!!!!!!!!!!!!!!!!!!!!!!!");
        resultReceiver = intent.getParcelableExtra("receiver");

        if (intent.getBooleanExtra("resume",false) == true)
        {
            onResume();
        }
        if (intent.getBooleanExtra("pause", false) == true)
        {
            onPause();
        }
        if (intent.getBooleanExtra("clear", false) == true)
        {
            onClear();
        }
        if (intent.getBooleanExtra("start", false) == true)
        {
            startFlag = true;
            System.out.println("START FROM SERVICE ______________________________________________________________________________________________________________________________");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            stepDetector.updateAccelerometer(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void step(long timeNs)
    {
        if (startFlag)
        {
            numSteps++;
            System.out.println(numSteps + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            bundle.putInt("STEPS_", numSteps);
            resultReceiver.send(1, bundle);
        }
    }
}
