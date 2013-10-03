package au.com.celero.sensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	public SensorManager mSensorManager;
	public Compass myCompass;
	public Sensor mAccelerometer, mMagnetic, mGyroscope;

	private float[] matrixR;
	private float[] matrixI;
	private float[] matrixValues;
	private float[] acceleration_values;
	private float[] magnetic_values;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Set up our sensors and services */
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		/* Set up our data matrixes */
		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];
		acceleration_values = new float[3];
		magnetic_values = new float[3];

		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			System.arraycopy(event.values, 0, acceleration_values, 0, 3);
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			System.arraycopy(event.values, 0, magnetic_values, 0, 3);
			break;
		default:
			return;
		}
		AccelerometerChange();
		MagneticChange();
		CalculateCompass();
	}

	private void AccelerometerChange() {
		TextView x_view = (TextView) findViewById(R.id.accelerometer_x);
		TextView y_view = (TextView) findViewById(R.id.accelerometer_y);
		TextView z_view = (TextView) findViewById(R.id.accelerometer_z);

		Float x_accelerometer = acceleration_values[0];
		Float y_accelerometer = acceleration_values[1];
		Float z_accelerometer = acceleration_values[2];

		x_view.setText("X value= " + x_accelerometer.toString());
		y_view.setText("Y value= " + y_accelerometer.toString());
		z_view.setText("Z value= " + z_accelerometer.toString());
	}

	private void MagneticChange() {
	    TextView x_view = (TextView) findViewById(R.id.magnetic_x);
		TextView y_view = (TextView) findViewById(R.id.magnetic_y);
		TextView z_view = (TextView) findViewById(R.id.magnetic_z);

		Float x_magnetic = magnetic_values[0];
		Float y_magnetic = magnetic_values[1];
		Float z_magnetic = magnetic_values[2];

		x_view.setText("X value= " + x_magnetic.toString());
		y_view.setText("Y value= " + y_magnetic.toString());
		z_view.setText("Z value= " + z_magnetic.toString());
	}

	private void CalculateCompass() {
		boolean success = SensorManager.getRotationMatrix(matrixR, matrixI,
				acceleration_values, magnetic_values);
		if (success) {
			SensorManager.getOrientation(matrixR, matrixValues);
			double azimuth = Math.toDegrees(matrixValues[0]);
			Compass mCompass = (Compass) findViewById(R.id.mycompass);
			TextView readingAzimuth = (TextView) findViewById(R.id.compass);
			readingAzimuth.setText("Azimuth: " + String.valueOf(azimuth));
			mCompass.update(matrixValues[0]);
		}
	}
}
