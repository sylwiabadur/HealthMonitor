package pl.edu.pwr.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class DataAdapter extends ArrayAdapter<DataTuple> {

    private Context ctx;

    public DataAdapter(@NonNull Context context, ArrayList<DataTuple> dataTuples)
    {
        super(context, 0, dataTuples);
        this.ctx = context;
    }
    @Override public View getView(final int position, View convertView, ViewGroup parent)
    {
        DataTuple dataTuple = getItem(position);
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView tvDate = (TextView) convertView.findViewById(R.id.dateTuple);
        TextView tvSteps = (TextView) convertView.findViewById(R.id.stepsTuple);
        TextView tvDistance = (TextView) convertView.findViewById(R.id.distanceTuple);
        TextView tvSpeed = (TextView) convertView.findViewById(R.id.speedTuple);
        final TextView tvId = (TextView) convertView.findViewById(R.id.idTuple);
        Button btn = (Button) convertView.findViewById(R.id.showRouteBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showRoute = new Intent(getContext(), MapsActivity.class);

                String idToFindBy =  tvId.getText().toString();
                idToFindBy = idToFindBy.substring(3,idToFindBy.length());

                showRoute.putExtra("id_to_find_by", idToFindBy);
                ctx.startActivity(showRoute);
            }
        });

        tvDate.setText("Date:" + dataTuple.date);
        tvSteps.setText("Steps: " + dataTuple.steps);
        tvDistance.setText("Distance [m]:" + dataTuple.distance);
        tvSpeed.setText("Speed [km/h]:" + dataTuple.speed);
        tvId.setText("Id:" + dataTuple.id);

        return convertView;
    }
}
