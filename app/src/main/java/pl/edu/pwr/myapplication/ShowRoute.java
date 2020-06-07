package pl.edu.pwr.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowRoute extends AppCompatActivity {
    TextView showTxt;
    LocationDataBaseHelper locationDataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_route);

        showTxt = findViewById(R.id.showTxt);

        locationDataBaseHelper = new LocationDataBaseHelper(this);

        String dateToFindBy = getIntent().getStringExtra("date_to_find_by");

        Cursor data = locationDataBaseHelper.getDataWhere(dateToFindBy);

        showTxt.setText(data.getString(1)+ " "+ data.getString(2) + " "+data.getString(3));
    }
}