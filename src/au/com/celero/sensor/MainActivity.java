package au.com.celero.sensor;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity implements MySensorListener.UpdateSensor {

    public SensorManager mSensorManager;
    public Compass myCompass;
    public Sensor mAccelerometer, mMagnetic, mGyroscope;
    MySensorListener myListener;
    
    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;
    
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myListener = new MySensorListener();
        myListener.attach(this);

        /* Set up our sensors and services */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];


        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openMap() {
       Intent intent = new Intent(this, MapActivity.class); 
       startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(myListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(myListener, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(myListener);
    }

    @Override
    public void updateAccelerometer(double x, double y, double z) {
        TextView x_view = (TextView) this.findViewById(R.id.accelerometer_x);
        TextView y_view = (TextView) this.findViewById(R.id.accelerometer_y);
        TextView z_view = (TextView) this.findViewById(R.id.accelerometer_z);
        
        x_view.setText("X value= " + df.format(x));
        y_view.setText("Y value= " + df.format(y));
        z_view.setText("Z value= " + df.format(z));
    }
    
    @Override
    public void updateMagnetic(double x, double y, double z) {
        TextView x_view = (TextView) this.findViewById(R.id.magnetic_x);
        TextView y_view = (TextView) this.findViewById(R.id.magnetic_y);
        TextView z_view = (TextView) this.findViewById(R.id.magnetic_z);
        
        x_view.setText("X value= " + df.format(x));
        y_view.setText("Y value= " + df.format(y));
        z_view.setText("Z value= " + df.format(z));
    }
    

    @Override
    public void updateCompass(float[] acceleration_values, float[] magnetic_values) {
        boolean success = SensorManager.getRotationMatrix(matrixR, matrixI, acceleration_values, magnetic_values);
        if (success) {
            SensorManager.getOrientation(matrixR, matrixValues);
            double azimuth = Math.toDegrees(matrixValues[0]);
            String degrees = df.format(azimuth);
            Compass mCompass = (Compass) this.findViewById(R.id.mycompass);
            TextView readingAzimuth = (TextView) this.findViewById(R.id.compass);
            readingAzimuth.setText("Azimuth: " + degrees);
            mCompass.update(matrixValues[0]);
        } 
    }
}
