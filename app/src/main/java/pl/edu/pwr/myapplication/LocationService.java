package pl.edu.pwr.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LocationService extends BroadcastReceiver
{
    public static final String ACTION_PROCESS_UPDATE = "edmt.dev.googlelocationbackground.UPDATE_LOCATION";
    public static final String ACTION_SAVE_TO_DB = "edmt.dev.googlelocationbackground.SAVE_TO_DB";

    LocationDataBaseHelper locationDataBaseHelper;

    private static final ArrayList<Double> latitude = new ArrayList<>();
    private static final ArrayList<Double> longitude = new ArrayList<>();

    public LocationService() {}

    @Override
    public void onReceive(Context context, Intent intent)
    {
        locationDataBaseHelper = new LocationDataBaseHelper(context);

        Toast.makeText(context, "Getting location updates", Toast.LENGTH_SHORT).show();

        if (intent!=null)
        {
            final String action = intent.getAction();

            if (ACTION_PROCESS_UPDATE.equals(action))
            {
                LocationResult result = LocationResult.extractResult(intent);

                if (result!=null)
                {
                    Location location = result.getLastLocation();
                    String locationStr = new StringBuilder(" " + location.getLatitude()).append("/").append(location.getLongitude()).toString();

                    try
                    {
                        latitude.add(location.getLatitude());
                        longitude.add(location.getLongitude());
                        System.out.println(locationStr);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(context, locationStr, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if (ACTION_SAVE_TO_DB.equals(action))
            {
                String params_id = intent.getExtras().getString("lastId");
                saveToDb(locationDataBaseHelper, params_id);
            }
        }
    }

    public static String strSeparator = ";";
    public static String convertArrayToString(ArrayList<Double> array)
    {
        String str = "";

        for (int i = 0; i<array.size(); i++)
        {
            str = str+array.get(i);

            if (i < array.size()-1)
            {
                str = str + strSeparator;
            }
        }
        return str;
    }

    public void saveToDb(LocationDataBaseHelper dbHelper, String params_id)
    {
        Date today = new Date();
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(today);

        if (!longitude.isEmpty() && !latitude.isEmpty())
        {
            dbHelper.addData(convertArrayToString(latitude), convertArrayToString(longitude), strDate, params_id);
        }

        latitude.clear();
        longitude.clear();
    }


}