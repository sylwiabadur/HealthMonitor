package pl.edu.pwr.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LocationActivity extends AppCompatActivity implements View.OnClickListener {
    private Button returnButton, mapsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        returnButton = findViewById(R.id.returnToMain);
        returnButton.setOnClickListener(this);

        mapsButton = findViewById(R.id.mapsBtn);
        mapsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.returnToMain)
        {
            Intent returnIntent = new Intent(LocationActivity.this, MainActivity.class);
            startActivity(returnIntent);
        }
        if(v.getId() == R.id.mapsBtn)
        {
            Intent mapsIntent = new Intent(LocationActivity.this, MapsActivity.class);
            startActivity(mapsIntent);
        }
    }
}
