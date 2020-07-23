package com.GoogleGlass.googleglasslevellingapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class OrientationData implements SensorEventListener {

    private SensorManager manager;
    private Sensor accelorometer;
    private Sensor magnometer;

    private float[] accelOutput;
    private float[] magOutput;

    //Three floats for pitch, yaw and roll (X, Y, Z rotation)
    private float[] orientation = new float[3];
    public float[] GetOrientation(){
        return orientation;
    }

    private float[] startOrientation;
    public float[] GetStartOrientation(){
        return startOrientation;
    }

    public void Reset(){
        startOrientation = null;
    }


    public OrientationData(){
        //Set the sensors
        manager = (SensorManager) MainActivity.getContext().getSystemService(Context.SENSOR_SERVICE);
        accelorometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void Register(){
        manager.registerListener(this, accelorometer, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelOutput = sensorEvent.values;
        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magOutput = sensorEvent.values;
        }

        if(accelOutput != null && magOutput != null){

            float[] R = new float[9];
            float[] I = new float[9];
            Boolean success = SensorManager.getRotationMatrix(R, I, accelOutput, magOutput);
            if(success){
                SensorManager.getOrientation(R, orientation);
                if(startOrientation == null){
                    startOrientation = new float[orientation.length];
                    System.arraycopy(orientation, 0, startOrientation, 0, orientation.length);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
