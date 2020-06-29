package pl.edu.pwr.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import java.util.ArrayList;


public class StatsActivity extends AppCompatActivity implements View.OnClickListener
{

    private Switch sortByBtn;
    private DatabaseHelper dbHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        sortByBtn = findViewById(R.id.sortBy);
        sortByBtn.setOnClickListener(this);

        listView = findViewById(R.id.listView);

        dbHelper = new DatabaseHelper(this);
        populateListView(3); // by default sort by date
    }

    private void populateListView(int option)
    {
        Cursor data;
        if (option == 1)
        {
            data = dbHelper.sortByDate();
        }
        else if (option == 2)
        {
            data = dbHelper.sortBySteps();
        }
        else
        {
            data = dbHelper.getData();
        }

        ArrayList<DataTuple> arrayOfTuples = new ArrayList<>();

        while(data.moveToNext())
        {
            DataTuple dataTuple = new DataTuple(data.getString(3), data.getString(1), data.getString(2), data.getString(4), data.getString(0));
            arrayOfTuples.add(dataTuple);
        }

        DataAdapter adapter = new DataAdapter(this, arrayOfTuples);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v)
    {
        if (sortByBtn.isChecked())
        {
            sortByBtn.setText("By steps");
            populateListView(2);
        }

        else
        {
            sortByBtn.setText("By date");
            populateListView(1);
        }
    }
}