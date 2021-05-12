package jp.ac.titech.itpro.sdl.resist;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private RotationView rotationView;
    private SensorManager manager;
    private Sensor gyroscope;

    private long previousSensorChangedTime = -1;
    private double thetaZ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        rotationView = findViewById(R.id.rotation_view);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager == null) {
            Toast.makeText(this, R.string.toast_no_sensor_manager, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope == null) {
            Toast.makeText(this, R.string.toast_no_gyroscope, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        manager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float omegaZ = event.values[2];  // z-axis angular velocity (rad/sec)

        long changedTime = System.nanoTime();
        // 最初の時は無視
        if (previousSensorChangedTime != -1) {
            thetaZ += (changedTime - previousSensorChangedTime) / 1e9  * omegaZ; // 平均速度として近似
        }

        previousSensorChangedTime = changedTime;
        rotationView.setDirection(thetaZ);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged: accuracy=" + accuracy);
    }
}