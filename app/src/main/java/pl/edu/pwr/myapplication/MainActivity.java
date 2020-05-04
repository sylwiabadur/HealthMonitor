package pl.edu.pwr.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button openPedometerBtn;
    Button openLocationBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openPedometerBtn = findViewById(R.id.openPedometer);
        openPedometerBtn.setOnClickListener(this);
        openLocationBtn = findViewById(R.id.openLocation);
        openLocationBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.openPedometer)
        {
            Intent pedometerIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(pedometerIntent);
        }
        if(v.getId() == R.id.openLocation)
        {
            Intent locationIntent = new Intent(MainActivity.this, LocationActivity.class);
            startActivity(locationIntent);
        }
    }
}