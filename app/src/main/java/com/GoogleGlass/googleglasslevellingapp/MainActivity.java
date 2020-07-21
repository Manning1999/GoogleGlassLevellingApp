package com.GoogleGlass.googleglasslevellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import static android.util.Half.EPSILON;

public class MainActivity extends AppCompatActivity implements GlassGestureDetector.OnGestureListener {

    private GlassGestureDetector glassGestureDetector;

    SensorManager sensorManager;
    Sensor gyroscopeSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glassGestureDetector = new GlassGestureDetector(this, this);


        // Register the listener
        sensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    // Create a listener
    final SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            float axisX = 0;
            float axisY = 0;
            float axisZ = 0;
            if (timestamp != 0) {
                final float dT = (sensorEvent.timestamp - timestamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                axisX = sensorEvent.values[0];
                axisY = sensorEvent.values[1];
                axisZ = sensorEvent.values[2];

                // Calculate the angular speed of the sample
                float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                // (that is, EPSILON should represent your maximum allowable margin of error)
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
                deltaRotationVector[0] = sinThetaOverTwo * axisX;
                deltaRotationVector[1] = sinThetaOverTwo * axisY;
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                deltaRotationVector[3] = cosThetaOverTwo;
            }
            timestamp = sensorEvent.timestamp;
            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);

            // User code should concatenate the delta rotation we computed with the current rotation
            // in order to get the updated rotation.
             float rotationCurrent = axisX;
             rotationCurrent = rotationCurrent * deltaRotationMatrix[0];

            TextView rotationText = (TextView) findViewById(R.id.Rotation);
          //  rotationText.setText(deltaRotationMatrix[0] + " : " + deltaRotationMatrix[1] + " : " + deltaRotationMatrix[2]);
          //  rotationText.setText(deltaRotationVector[0] + " : " + deltaRotationVector[1] + " : " + deltaRotationVector[2]);
            rotationText.setText(String.valueOf(rotationCurrent));
            float rotationTolerance = 0.005f;
            if (rotationCurrent > 0 + rotationTolerance) {

                findViewById(R.id.imageView).setRotation(findViewById(R.id.imageView).getRotation() + rotationCurrent * 360f);
            } else if (rotationCurrent < 0 - rotationTolerance)
            {
                findViewById(R.id.imageView).setRotation(findViewById(R.id.imageView).getRotation() - rotationCurrent * 360f);
            }
            //rotationText.setText(axisX + " : " + axisY + " : " + axisZ);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };



    public float[] transposeMatrix(float[] m){
        float[] res = new float[9];
        res[0] = m[0];
        res[1] = m[3];
        res[2] = m[6];
        res[3] = m[1];
        res[4] = m[4];
        res[5] = m[7];
        res[6] = m[2];
        res[7] = m[5];
        res[8] = m[8];
        return m; }




    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                // Response for TAP gesture
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
