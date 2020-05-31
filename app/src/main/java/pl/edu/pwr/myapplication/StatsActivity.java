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

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button returnBtn;
    private DatabaseHelper dbHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        returnBtn = findViewById(R.id.returnFromStats);
        returnBtn.setOnClickListener(this);
        listView = findViewById(R.id.listView);
        dbHelper = new DatabaseHelper(this);
        populateListView();
    }

    private void populateListView() {
        Cursor data = dbHelper.getData();
        ArrayList<String> dataList = new ArrayList<String>();
        while(data.moveToNext())
        {
            dataList.add(data.getString(1));
            dataList.add(data.getString(2));
            dataList.add(data.getString(3));
        }
        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.returnFromStats)
        {
            Intent returnIntent = new Intent(StatsActivity.this, MainActivity.class);
            startActivity(returnIntent);
        }
    }
}