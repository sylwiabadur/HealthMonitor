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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    public class Message {
        public void displayMessage(int resultCode, Bundle resultData)
        {
            switch (resultCode)
            {
                case 1:
                    DecimalFormat df = new DecimalFormat("0.00");
                    if (startedFlag == true && pausedFlag == false && stoppedFlag == false) {
                        numSteps = resultData.getInt("STEPS_");
                        numberOfStepsTxtView.setText(numberOfStepsTxt + numSteps);
                        distanceTxtView.setText(numberOfMetersTxt + df.format((double) numSteps * metersForStep));
                    }
                    break;
            }
        }
    }

    private TextView numberOfStepsTxtView, distanceTxtView;
    private Button startTrainingBtn, stopTrainingBtn, pauseTrainingBtn;
    private String numberOfStepsTxt = "Steps counted: ";
    private String numberOfMetersTxt = "Distance measured [m]: ";

    private int numSteps = 0;
    private double metersForStep = 0.72;
    private String sharedPrefs = "mySharedPrefs";
    private long startTime = 0;
    private long differenceTime;

    public boolean startedFlag = false;
    private boolean stoppedFlag = false;
    private boolean pausedFlag = false;

    DatabaseHelper dbHelper;

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    Intent pedometerService;
    MessageReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        pedometerService = new Intent(this, PedometerService.class);

        receiver = new MessageReceiver(new HomeActivity.Message());
        pedometerService.putExtra("receiver", receiver);
        startService(pedometerService);

        setUp();

        addListeners();

        loadData();
        buildLocationRequest();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        System.out.println("STOPPED STATE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!-----------------------------------------");
    }

    private void setUp()
    {
        dbHelper = new DatabaseHelper(this);

        numberOfStepsTxtView = findViewById(R.id.tv_steps);
        distanceTxtView = findViewById(R.id.distance);

        startTrainingBtn = findViewById(R.id.startTrainingBtn);
        stopTrainingBtn = findViewById(R.id.stopTrainingBtn);
        pauseTrainingBtn = findViewById(R.id.pauseTrainingBtn);
    }

    private void addListeners()
    {
        startTrainingBtn.setOnClickListener(this);
        stopTrainingBtn.setOnClickListener(this);
        pauseTrainingBtn.setOnClickListener(this);

        stopTrainingBtn.setEnabled(false);
        pauseTrainingBtn.setEnabled(false);
    }

    private void loadData()
    {
        System.out.println("LOAD DATA ACTIVITY -----------------------------------");
        SharedPreferences sharedPref = getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        int a = sharedPref.getInt("steps", 0);
        startedFlag = sharedPref.getBoolean("startFlag", false);
        stoppedFlag = sharedPref.getBoolean("stopFlag", false);
        pausedFlag = sharedPref.getBoolean("pauseFlag", false);
        numSteps = 0;
        numberOfStepsTxtView.setText(numberOfStepsTxt + numSteps);
        distanceTxtView.setText(numberOfMetersTxt+ (double)numSteps * metersForStep);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.startTrainingBtn)
        {
            handleStartTraining();

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
            handleStopTraining();
        }
        if (v.getId() == R.id.pauseTrainingBtn)
        {
            handlePauseTraining();
        }
    }

    private void handleStartTraining()
    {
        System.out.println("HANDLE START TRAINING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        pedometerService.putExtra("start", true);
        startService(pedometerService);

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
        stopTrainingBtn.setEnabled(true);
        pauseTrainingBtn.setEnabled(true);
    }

    private void updateLocation() {
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

    private void handleStopTraining()
    {
        differenceTime = System.currentTimeMillis() - startTime;
        stoppedFlag = true;
        startedFlag = false;
        startTrainingBtn.setEnabled(true);
        pauseTrainingBtn.setEnabled(false);
        stopTrainingBtn.setEnabled(false);

        Toast.makeText(HomeActivity.this, "Stopped training", Toast.LENGTH_SHORT).show();

        if (stopTraining())
        {
            Toast.makeText(HomeActivity.this, "Data added successfully to db", Toast.LENGTH_LONG).show();
        }
        else  Toast.makeText(HomeActivity.this, "Sth went wrong while adding to db", Toast.LENGTH_SHORT).show();

        pedometerService.putExtra("clear", true);
        startService(pedometerService);
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


    private void saveLocationToDb() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (stoppedFlag==true && startedFlag==false)
        {
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

    private void handlePauseTraining()
    {
        differenceTime = System.currentTimeMillis() - startTime;
        pausedFlag = true;
        startTrainingBtn.setEnabled(true);
        Toast.makeText(HomeActivity.this, "Paused training", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (startedFlag == true)
        {
            pedometerService.putExtra("resume", true);
            startService(pedometerService);

            numberOfStepsTxtView.setText(numberOfStepsTxt + numSteps);
            distanceTxtView.setText(numberOfMetersTxt + (double)numSteps * metersForStep);
        }
        System.out.println("HERE ON RESUME !!!!!!!!!!!!!!!!!!");
        numberOfStepsTxtView.setText(numberOfStepsTxt + numSteps);
        distanceTxtView.setText(numberOfMetersTxt + (double)numSteps * metersForStep);
    }

    @Override
    public void onPause() {
        super.onPause();

        saveFlags();

        if (startedFlag == true)
        {
            pedometerService.putExtra("pause", true);
            startService(pedometerService);

            saveData();
        }
        System.out.println("HERE ON PAUSE !!!!!!!!!!!!!!!!!!");
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

}

