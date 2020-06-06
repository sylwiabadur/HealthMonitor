package pl.edu.pwr.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class DataAdapter extends ArrayAdapter<DataTuple> {
    public DataAdapter(@NonNull Context context, ArrayList<DataTuple> dataTuples) {
        super(context, 0, dataTuples);
    }
    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        DataTuple dataTuple = getItem(position);
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView tvDate = (TextView) convertView.findViewById(R.id.dateTuple);
        TextView tvSteps = (TextView) convertView.findViewById(R.id.stepsTuple);
        TextView tvDistance = (TextView) convertView.findViewById(R.id.distanceTuple);

        tvDate.setText("Date:" + dataTuple.date);
        tvSteps.setText("Steps: " + dataTuple.steps);
        tvDistance.setText("Distance [m]:" + dataTuple.distance);

        return convertView;
    }
}
