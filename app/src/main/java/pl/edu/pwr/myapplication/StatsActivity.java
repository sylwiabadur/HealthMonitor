package pl.edu.pwr.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StatsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button returnBtn;
    private Switch sortByBtn;
    private DatabaseHelper dbHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        returnBtn = findViewById(R.id.returnFromStats);
        returnBtn.setOnClickListener(this);
        sortByBtn = findViewById(R.id.sortBy);
        sortByBtn.setOnClickListener(this);

        listView = findViewById(R.id.listView);

        dbHelper = new DatabaseHelper(this);
        populateListView(1); // by default sort by date
    }

    private void populateListView(int option) {
        Cursor data;
        if (option == 1)
        {
            data = dbHelper.sortByDate();
        }
        else
        {
            data = dbHelper.sortBySteps();
        }
        ArrayList<DataTuple> arrayOfTuples = new ArrayList<>();
        while(data.moveToNext())
        {
            DataTuple dataTuple = new DataTuple(data.getString(3), data.getString(1), data.getString(2));
            arrayOfTuples.add(dataTuple);
        }
        DataAdapter adapter = new DataAdapter(this, arrayOfTuples);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.returnFromStats)
        {
            Intent returnIntent = new Intent(StatsActivity.this, MainActivity.class);
            startActivity(returnIntent);
        }
        if(sortByBtn.isChecked())
        {
            sortByBtn.setText("By steps");
            populateListView(2);
        }
        if(!sortByBtn.isChecked())
        {
            sortByBtn.setText("By date");
            populateListView(1);
        }
    }
}