package com.GoogleGlass.googleglasslevellingapp;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GlassGestureDetector.OnGestureListener {

    private GlassGestureDetector glassGestureDetector;

    private long initTime;

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }




    private OrientationData orientationData;
    private long frameTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //orientationData.Reset();
        initTime = System.currentTimeMillis();
        mContext = getApplicationContext();

        try {
            orientationData = new OrientationData();
            orientationData.Register();
        }
        catch(Exception e){
            TextView rotationText = findViewById(R.id.Rotation);
            rotationText.setText(e.toString());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glassGestureDetector = new GlassGestureDetector(this, this);
        Update();

    }



    private Boolean isRunning = false;
    private void Update(){


        if(isRunning == true) return;
        isRunning=true;

        Timer t = new Timer();
//Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {

                                      TextView rotationText = findViewById(R.id.Rotation);

                                      //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                      if(frameTime < initTime){
                                          frameTime = initTime;

                                      }
                                      int elapsedTime = (int)(System.currentTimeMillis() - frameTime);
                                      frameTime = System.currentTimeMillis();


                                      if (orientationData.GetOrientation() != null && orientationData.GetStartOrientation() != null) {
                                          float pitch = orientationData.GetOrientation()[1] - orientationData.GetStartOrientation()[1];
                                          float roll = orientationData.GetOrientation()[2] - orientationData.GetStartOrientation()[2];

                                          rotationText.setText(pitch + " : " + roll);
                                          //Full rotation is equal to 6. 360 / 6 = 60 which is why the roll/pitch must be multiplied by 6
                                          findViewById(R.id.imageView).setRotation(0 - (roll * 60));
                                      }
                                      else{
                                          rotationText.setText("No data available");
                                      }
                                  }

                              },
//Set how long before to start calling the TimerTask (in milliseconds)
                0,
//Set the amount of time between each execution (in milliseconds)
                50);









        }




    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                // Response for TAP gesture
                //Update();
                return true;
            case SWIPE_FORWARD:
                // Response for SWIPE_FORWARD gesture
                return true;
            case SWIPE_BACKWARD:
                // Response for SWIPE_BACKWARD gesture
                return true;
            case SWIPE_DOWN:
                // Response for SWIPE_Down gesture
                System.exit(0);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (glassGestureDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}
