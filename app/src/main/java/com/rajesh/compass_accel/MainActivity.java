package com.rajesh.compass_accel;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements SensorEventListener,StepListener {

    private SensorManager mSensorManager;
    SimpleStepDetector simpleStepDetector;
    Sensor accelerometer;
    Sensor magnetometer;
    Sensor rotationVectorSensor;
    TextView txt;
    TextView txt1;
    public int numSteps;
    long ts;
    double gy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        ts = System.currentTimeMillis();
        gy=0.0;

        txt = (TextView) findViewById(R.id.textView);
        txt1 = (TextView) findViewById(R.id.textView2);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onResume() {
        super.onResume();
        numSteps=0;
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_UI);
        simpleStepDetector = new SimpleStepDetector();
        simpleStepDetector.registerListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    float[] mGravity;
    float[] mGeomagnetic;


    double time;
    Double deg=0.0,gy1;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){

            double vel = (double) (event.values[1]) * 180 / Math.PI;
            Log.d("Gyro", Float.toString(event.values[0]));
            time = System.currentTimeMillis() - ts;
            ts = System.currentTimeMillis();
            time /= 1000;
            deg += time * (vel + gy) / 2;
            if (deg > 360)
                deg -= 360;
            if (deg < -360)
                deg += 360;
            gy = vel;
            //Toast.makeText(this, "Sensing"+Double.toString(vel), Toast.LENGTH_SHORT).show();
            //deg = deg+theta;
            txt.setText("Rotation: " + Math.round(deg) + " degrees");

        }

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        txt1.setText("Steps"+numSteps);
    }
}
