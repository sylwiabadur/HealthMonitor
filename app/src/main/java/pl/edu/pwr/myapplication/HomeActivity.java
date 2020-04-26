package pl.edu.pwr.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements SensorEventListener, StepListener, View.OnClickListener {
    private StepDetector stepDetector;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private String numberOfStepsTxt = "Steps counted: ";
    private int numSteps;
    private TextView numberOfStepsTxtView;
    private Button returnButton;
    private String sharedPrefs = "mySharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);

        numberOfStepsTxtView = findViewById(R.id.tv_steps);
        returnButton = findViewById(R.id.returnButton);

        returnButton.setOnClickListener(this);

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
        numberOfStepsTxtView.setText(numberOfStepsTxt + numSteps);
    }

    @Override
    public void onResume() {
        super.onResume();
        numberOfStepsTxtView.setText(numberOfStepsTxt + numSteps);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        saveData();
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
        numSteps++;
        numberOfStepsTxtView.setText(numberOfStepsTxt + numSteps);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.returnButton)
        {
            Intent returnIntent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(returnIntent);
        }
    }
}

