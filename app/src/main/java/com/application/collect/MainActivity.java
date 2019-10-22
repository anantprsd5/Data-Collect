package com.application.collect;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private boolean color = false;
    private long lastUpdate;
    private Button normalButton;
    private Button anomalyButton;
    private String type;
    private DatabaseReference mDatabase;
    private DatabaseReference ref;
    private HashMap<String, String> sensorData = new HashMap<>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
        normalButton = findViewById(R.id.button);
        anomalyButton = findViewById(R.id.button2);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Write a message to the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = database.getReference("/normal");
                if (normalButton.getText().toString().equals("Normal Data")) {
                    normalButton.setText("Stop");
                    registerListener("normal");
                    type = "normal";
                } else {
                    normalButton.setText("Normal Data");
                    unregisterListener();
                    mDatabase.setValue(sensorData);
                    sensorData = new HashMap<>();
                }


            }
        });

        anomalyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = database.getReference("/anomaly");
                if (anomalyButton.getText().toString().equals("Anomaly Data")) {
                    anomalyButton.setText("Stop");
                    registerListener("anomaly");
                    type = "anomaly";
                } else {
                    anomalyButton.setText("Anomaly Data");
                    unregisterListener();
                    mDatabase.setValue(sensorData);
                    sensorData = new HashMap<>();
                }
            }
        });


    }

    private void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    private void registerListener(final String type) {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;

            Toast.makeText(this, "x = " + Float.toString(x) + "y= " + y + "z= " + z, Toast.LENGTH_SHORT)
                    .show();

            sensorData.put(Long.toString(System.currentTimeMillis()),
                    "x = " + Float.toString(x) + "y= " + y + "z= " + z);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors

    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
