package pl.edu.pwr.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    Button openPedometerBtn, showStatsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openPedometerBtn = findViewById(R.id.trainingBtn);
        openPedometerBtn.setOnClickListener(this);

        showStatsBtn = findViewById(R.id.showMyStatistics);
        showStatsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.trainingBtn)
        {
            Intent pedometerIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(pedometerIntent);
        }

        if (v.getId() == R.id.showMyStatistics)
        {
            Intent statsIntent = new Intent(MainActivity.this, StatsActivity.class);
            startActivity(statsIntent);
        }
    }
}