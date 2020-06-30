package pl.edu.pwr.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class PedometerService extends Service implements SensorEventListener, StepListener
{

    private StepDetector stepDetector;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private int numSteps;
    private String sharedPrefs = "mySharedPrefs";

    private SharedPreferences sharedPref;

    private boolean startFlag = false;
    private boolean serviceStopped = false;

    NotificationManager notificationManager;

    Intent intent;
    private static final String TAG = "PedometerService";
    public static final String BROADCAST_ACTION = "pl.edu.pwr.myapplication.broadcast";
    private final Handler handler = new Handler();

    public PedometerService(){}


    private void loadData()
    {
        numSteps = 0;

        broadcastSensorValue();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        sharedPref = getSharedPreferences(sharedPrefs, MODE_PRIVATE);

        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        showNotification();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        handler.removeCallbacks(updateBroadcastData);
        handler.post(updateBroadcastData);

        loadData();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        serviceStopped = true;

        dismissNotification();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    private void handleIntent()
    {
        if (sharedPref.getBoolean("start", false) == true)
        {
            startFlag = true;
        }
        else
        {
            startFlag = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            stepDetector.updateAccelerometer(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void step(long timeNs)
    {
        if (startFlag)
        {
            numSteps++;
            System.out.println(numSteps + "--------------------------");
        }
    }

    private void showNotification()
    {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("Health monitor");
        notificationBuilder.setContentText("Counting steps");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setColor(Color.parseColor("#6600cc"));
        int colorLED = Color.argb(255, 0, 255, 0);
        notificationBuilder.setLights(colorLED, 500, 500);

        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setOngoing(true);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,new Intent(),0);
        notificationBuilder.setContentIntent(resultPendingIntent);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void dismissNotification()
    {
        notificationManager.cancel(0);
    }

    private Runnable updateBroadcastData = new Runnable()
    {
        public void run()
        {
            if (!serviceStopped)
            {
                broadcastSensorValue();

                handleIntent();

                handler.postDelayed(this, 500);
            }
        }
    };

    private void broadcastSensorValue()
    {
        intent.putExtra("STEPS_", numSteps);

        sendBroadcast(intent);
    }
}
