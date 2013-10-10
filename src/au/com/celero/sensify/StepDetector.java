/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package au.com.celero.sensify;

import java.util.ArrayList;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;
import au.com.celero.sensify.Compass;

/**
 * Detects steps and notifies all listeners (that implement StepListener).
 * 
 * @author Levente Bagi
 * @author Clint Ryan
 * @todo REFACTOR: SensorListener is deprecated
 */
public class StepDetector implements SensorEventListener {
    private final static String TAG = "StepDetector";
    private float mLimit = 10;
    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;

    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    private float[] acceleration_values;
    private float[] magnetic_values;
    
    Activity activity = Pedometer.pedometer;
    private float lastDirection;

    private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();

    public StepDetector() {
        int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));

        acceleration_values = new float[3];
        magnetic_values = new float[3];
    }

    public void setSensitivity(float sensitivity) {
        mLimit = sensitivity; // 1.97 2.96 4.44 6.66 10.00 15.00 22.50 33.75
                              // 50.62
    }

    public void addStepListener(StepListener sl) {
        mStepListeners.add(sl);
    }

    // public void onSensorChanged(int sensor, float[] values) {
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        synchronized (this) {
            switch (sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    System.arraycopy(event.values, 0, acceleration_values, 0, 3);

                    float vSum = 0;
                    for (int i = 0; i < 3; i++) {
                        final float v = mYOffset + event.values[i] * mScale[1];
                        vSum += v;
                    }
                    int k = 0;
                    float v = vSum / 3;

                    float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                    if (direction == -mLastDirections[k]) {
                        // Direction changed
                        int extType = (direction > 0 ? 0 : 1); // minumum or
                                                               // maximum?
                        mLastExtremes[extType][k] = mLastValues[k];
                        float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                        if (diff > mLimit) {

                            boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                            boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                            boolean isNotContra = (mLastMatch != 1 - extType);

                            if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                                Log.i(TAG, "step");
                                for (StepListener stepListener : mStepListeners) {
                                    stepListener.onStep();
                                }
                                // This is where I'll update the canvas view
                                // Attach listener for the canvas to update
                                Map mMap = (Map) activity.findViewById(R.id.mymap);
                                if (lastDirection != 0) {
                                    mMap.update(lastDirection);
                                }
                                
                                mLastMatch = extType;
                            } else {
                                mLastMatch = -1;
                            }
                        }
                        mLastDiff[k] = diff;
                    }
                    mLastDirections[k] = direction;
                    mLastValues[k] = v;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    System.arraycopy(event.values, 0, magnetic_values, 0, 3);
                    break;
                default:;
                    System.arraycopy(event.values, 0, magnetic_values, 0, 3);
                    return;
            }
        }
        CalculateCompass();
    }

    private void CalculateCompass() {
        float[] matrixR = new float[9];
        float[] matrixI = new float[9];
        float[] matrixValues = new float[3];

        boolean success = SensorManager.getRotationMatrix(matrixR, matrixI, acceleration_values, magnetic_values);
        if (success) {
            SensorManager.getOrientation(matrixR, matrixValues);
            double azimuth = Math.toDegrees(matrixValues[0]);
            Compass mCompass = (Compass) activity.findViewById(R.id.mycompass);
            // TextView readingAzimuth = (TextView)
            // this.findViewById(R.id.compass);
            // readingAzimuth.setText("Azimuth: " + degrees);
            mCompass.update(matrixValues[0]);
            lastDirection = matrixValues[0];
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

}