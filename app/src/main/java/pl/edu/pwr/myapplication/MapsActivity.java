package pl.edu.pwr.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.SymbolTable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    LocationDataBaseHelper locationDataBaseHelper;
    ArrayList<Double> latitude,longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationDataBaseHelper = new LocationDataBaseHelper(this);

        String idToFindBy = getIntent().getStringExtra("id_to_find_by");

        Cursor data = locationDataBaseHelper.getDataWhere(idToFindBy);
        String sth = "";
        data.moveToFirst();
        sth += data.getString(1) + " \n " + data.getString(2) +  " " + data.getString(4) + "\n";
        System.out.println(sth);

        String latitudeStr = data.getString(1);
        String longitudeStr = data.getString(2);

        latitude = convertStringToArray(latitudeStr);
        longitude = convertStringToArray(longitudeStr);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public static  ArrayList<Double> convertStringToArray(String str)
    {
        String[] elements = str.split(";");
        ArrayList<Double> array = new ArrayList<>();

        for (String element : elements)
        {
            array.add(Double.parseDouble(element));
        }
        return array;
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        List<LatLng> path = new ArrayList<>();

        for (int i = 0; i < longitude.size(); ++i)
        {
            path.add(new LatLng(latitude.get(i), longitude.get(i)));
        }

        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .addAll(path).color(Color.BLUE));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(path.get(0), 20f));
    }
}
