package pl.edu.pwr.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener, StepListener {
    private TextView numberOfStepsTxtView, distanceTxtView;
    private Button startTrainingBtn, stopTrainingBtn, pauseTrainingBtn;
    private String numberOfStepsTxt = "Steps counted: ";
    private String numberOfMetersTxt = "Distance measured [m]: ";

    private StepDetector stepDetector;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private int numSteps = 0;
    private double metersForStep = 0.72;
    private String sharedPrefs = "mySharedPrefs";
    private long startTime = 0;
    private long differenceTime;

    private boolean startedFlag = false;
    private boolean stoppedFlag = false;
    private boolean pausedFlag = false;

    DatabaseHelper dbHelper;

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);

        numberOfStepsTxtView = findViewById(R.id.tv_steps);
        distanceTxtView = findViewById(R.id.distance);

        startTrainingBtn = findViewById(R.id.startTrainingBtn);
        stopTrainingBtn = findViewById(R.id.stopTrainingBtn);
        pauseTrainingBtn = findViewById(R.id.pauseTrainingBtn);

        startTrainingBtn.setOnClickListener(this);
        stopTrainingBtn.setOnClickListener(this);
        pauseTrainingBtn.setOnClickListener(this);

        loadData();
        buildLocationRequest();
    }

    private void updateLocation() {
        Toast.makeText(HomeActivity.this, "STARTED FLAG, UPDATE LOCATION", Toast.LENGTH_LONG).show();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (stoppedFlag == false && startedFlag==true)
        {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(LocationService.ACTION_PROCESS_UPDATE);

        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void saveLocationToDb() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (stoppedFlag==true && startedFlag==false)
        {
            System.out.println("SAVE LOCATION TO DB!!!!!!!");
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, getDbIntent());
        }
    }

    private PendingIntent getDbIntent() {
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(LocationService.ACTION_SAVE_TO_DB);

        String params_id = "";
        Cursor data = dbHelper.getLastId();

        while(data.moveToNext())
        {
            params_id = data.getString(0);
        }
        if(params_id == null )
        {
            int i = 1;
            params_id = String.valueOf(i);
        }
        else
        {
            int i = Integer.parseInt(params_id);
            i++;
            params_id = String.valueOf(i);

        }

        intent.putExtra("lastId", params_id);

        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @SuppressLint("RestrictedApi")
    private void buildLocationRequest() {

        Toast.makeText(HomeActivity.this, "LOCATION REQUEST", Toast.LENGTH_LONG).show();

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(10f);
    }

    private void saveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt("steps", numSteps);
        myEdit.commit();
    }
    private void saveFlags()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("startFlag", startedFlag);
        myEdit.putBoolean("stopFlag", stoppedFlag);
        myEdit.putBoolean("pauseFlag", pausedFlag);
        myEdit.commit();
    }

    private void loadData()
    {
        SharedPreferences sharedPref = getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        int a = sharedPref.getInt("steps", 0);
        startedFlag = sharedPref.getBoolean("startFlag", false);
        stoppedFlag = sharedPref.getBoolean("stopFlag", false);
        pausedFlag = sharedPref.getBoolean("pauseFlag", false);
        numSteps = 0;
        numberOfStepsTxtView.setText(numberOfStepsTxt + numSteps);
        distanceTxtView.setText(numberOfMetersTxt+ (double)numSteps * metersForStep);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.startTrainingBtn)
        {
            if (startTime == 0)
            {
                startTime = System.currentTimeMillis(); // nie bylo wczesniej pomiaru
            }
            else
            {
                startTime = differenceTime + System.currentTimeMillis(); // byl wczesniej pomiar
            }

            startedFlag = true;
            stoppedFlag = false;
            pausedFlag = false;
            Toast.makeText(HomeActivity.this, "Started training", Toast.LENGTH_SHORT).show();
            startTrainingBtn.setEnabled(false);

            Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {
                    updateLocation();
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {
                    Toast.makeText(HomeActivity.this, "You must accept this location", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                }
            }).check();
        }

        if (v.getId() == R.id.stopTrainingBtn)
        {
            differenceTime = System.currentTimeMillis() - startTime;
            stoppedFlag = true;
            startedFlag = false;
            startTrainingBtn.setEnabled(true);
            Toast.makeText(HomeActivity.this, "Stopped training", Toast.LENGTH_SHORT).show();
            if (stopTraining())
            {
                Toast.makeText(HomeActivity.this, "Data added successfully to db", Toast.LENGTH_LONG).show();
            }
            else  Toast.makeText(HomeActivity.this, "Sth went wrong while adding to db", Toast.LENGTH_SHORT).show();
        }
        if (v.getId() == R.id.pauseTrainingBtn)
        {
            differenceTime = System.currentTimeMillis() - startTime;
            pausedFlag = true;
            startTrainingBtn.setEnabled(true);
            Toast.makeText(HomeActivity.this, "Paused training", Toast.LENGTH_SHORT).show();
        }
    }
    boolean stopTraining()
    {
        Date today = new Date();
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(today);
        double speedKmH = ((double)numSteps * metersForStep /1000)/((double)differenceTime/1000/3600);
        double metersDistance = numSteps * metersForStep;

        saveLocationToDb();

        return dbHelper.addData(numSteps, metersDistance, strDate, speedKmH);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        if (startedFlag==true)
        {
            numberOfStepsTxtView.setText(numberOfStepsTxt + numSteps);
            distanceTxtView.setText(numberOfMetersTxt + (double)numSteps * metersForStep);
        }
        System.out.println("NOT HERE ON RESUME !!!!!!!!!!!!!!!!!!");
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        saveFlags();
        if (startedFlag==true)
        {
            saveData();
        }
        System.out.println("NOT HERE ON PAUSE !!!!!!!!!!!!!!!!!!");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            stepDetector.updateAccelerometer(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (startedFlag==true && pausedFlag==false && stoppedFlag==false)
        {
            numSteps++;
            numberOfStepsTxtView.setText(numberOfStepsTxt + numSteps);
            distanceTxtView.setText(numberOfMetersTxt + df.format((double)numSteps * metersForStep));
        }
    }

}

