package au.com.celero.sensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MySensorListener implements SensorEventListener {

    Activity mActivity;
    UpdateSensor mCallback;
    
    private float[] acceleration_values;
    private float[] magnetic_values;

    /** Our callback interface **/
    public interface UpdateSensor {
        public void updateAccelerometer(double x, double y, double z);

        public void updateMagnetic(double x, double y, double z);

        public void updateCompass(float[] acceleration_values, float[] magnetic_values);
    }

    public MySensorListener() {
        acceleration_values = new float[3];
        magnetic_values = new float[3];
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

    public void attach(Activity activity) {
        try {
            mCallback = (UpdateSensor) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement UpdateSensor");
        }
    }

    private void AccelerometerChange() {
        double x = acceleration_values[0];
        double y = acceleration_values[1];
        double z = acceleration_values[2];

        mCallback.updateAccelerometer(x, y, z);
    }

    private void MagneticChange() {
        double x = magnetic_values[0];
        double y = magnetic_values[1];
        double z = magnetic_values[2];

        mCallback.updateMagnetic(x, y, z);
    }

    private void CalculateCompass() {
        mCallback.updateCompass(acceleration_values, magnetic_values);
    }

}
