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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView numberOfStepsTxtView;
    private Button returnButton;
    private String numberOfStepsTxt = "Steps counted: ";

    Intent pedometerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        numberOfStepsTxtView = findViewById(R.id.tv_steps);
        returnButton = findViewById(R.id.returnButton);

        returnButton.setOnClickListener(this);

        MessageReceiver receiver = new MessageReceiver(new Message());

        pedometerService = new Intent(this, PedometerService.class);
        pedometerService.putExtra("receiver", receiver);
        startService(pedometerService);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.returnButton)
        {
            Intent returnIntent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(returnIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        pedometerService.putExtra("resume", true);
    }

    @Override
    public void onPause() {
        super.onPause();
        pedometerService.putExtra("pause", true);
    }

    public class Message {
        public void displayMessage(int resultCode, Bundle resultData)
        {
            switch (resultCode)
            {
                case 1:
                    numberOfStepsTxtView.setText(numberOfStepsTxt + resultData.getInt("stepsCounted"));
                    break;
            }
        }
    }

}

