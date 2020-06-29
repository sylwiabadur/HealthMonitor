//package pl.edu.pwr.myapplication;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.TextView;
//
//public class DraftActivity extends AppCompatActivity {
//
//    Intent pedometerService;
//    TextView stepsTxt;
//    MessageReceiver receiver;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_draft);
//
//        pedometerService = new Intent(this, PedometerService.class);
//
//        stepsTxt = findViewById(R.id.stepsTxt);
//        receiver = new MessageReceiver(new Message());
//        pedometerService.putExtra("receiver", receiver);
//        startService(pedometerService);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        pedometerService.putExtra("resume", true);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        pedometerService.putExtra("pause", true);
//    }
//
//    public class Message {
//        public void displayMessage(int resultCode, Bundle resultData)
//        {
//            switch (resultCode)
//            {
//                case 1:
//                    System.out.println("HEHEHEHHEHEH!!!!!!!!!!!!!!!!!!");
//                    int data = resultData.getInt("STEPS_");
//                    stepsTxt.setText(""+data);
//                    break;
//            }
//        }
//    }
//}
