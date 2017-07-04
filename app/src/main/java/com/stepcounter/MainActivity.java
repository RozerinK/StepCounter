package com.stepcounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView count;
    boolean activityRunning;
    private static final String TOTAL_STEP ="Total step in sensor";
private Float stepsInSensor;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count = (TextView) findViewById(R.id.activity_main_txt_count);
        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            Toast.makeText(getApplicationContext(),"yes you have dude",Toast.LENGTH_SHORT).show();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        getSensorList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();


        activityRunning = false;
        editor.putFloat(TOTAL_STEP,stepsInSensor);
        editor.apply();
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
       sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
         stepsInSensor = event.values[0];
        if(!sharedPreferences.contains(TOTAL_STEP)){

            editor.putFloat(TOTAL_STEP, stepsInSensor);
            editor.commit();
        }
        Float startingStepCount = sharedPreferences.getFloat(TOTAL_STEP, 0);
        Float stepCount = stepsInSensor - startingStepCount;
        if (activityRunning) {
            count.setText(String.valueOf(stepCount));



        }
        while(stepCount==10.0){
            Toast.makeText(this,"Tebrikler",Toast.LENGTH_SHORT).show();
            break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    private void getSensorList() {
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        StringBuilder strLog = new StringBuilder();
        int iIndex = 1;
        for (Sensor item : sensors) {
            strLog.append(iIndex + ".");
            strLog.append(" Sensor Type - " + item.getType() + "\r\n");
            strLog.append(" Sensor Name - " + item.getName() + "\r\n");
            strLog.append(" Sensor Version - " + item.getVersion() + "\r\n");
            strLog.append(" Sensor Vendor - " + item.getVendor() + "\r\n");
            strLog.append(" Maximum Range - " + item.getMaximumRange() + "\r\n");
            strLog.append(" Minimum Delay - " + item.getMinDelay() + "\r\n");
            strLog.append(" Power - " + item.getPower() + "\r\n");
            strLog.append(" Resolution - " + item.getResolution() + "\r\n");
            strLog.append("\r\n");
            iIndex++;
        }
        System.out.println(strLog.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        editor.putFloat(TOTAL_STEP,stepsInSensor);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putFloat(TOTAL_STEP,stepsInSensor);
        editor.apply();
    }
}