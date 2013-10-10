package au.com.celero.sensify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Map extends View {

	private float direction;
	private float startX;
	private float startY;
	private float stopX;
	private float stopY;
	
	
	public Map(Context context) {
		super(context);
	}

	public Map(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Map(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
				MeasureSpec.getSize(heightMeasureSpec));
	}

	@SuppressLint("DrawAllocation")
    @Override
	protected void onDraw(Canvas canvas) {
	    
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		int d = 50;
		
		// Load settings
		if (startX == 0) {
		    startX = w / 2;
		    stopX = startX;
		}
		else {
		    startX = stopX;
		    stopX = (float) (startX + d * Math.sin(direction) * -1);
		}
		if (startY == 0) {
		    startY = h / 2;
		    stopY = startY;
		}
		else {
		    startY = stopY;
		    stopY = (float) (startY + d * Math.cos(direction) * -1); 
		}


		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(Color.WHITE);

		canvas.drawRect(0, 0, w, h, paint);
		paint.setColor(Color.RED);
		
		Log.d("MAP", startX + " " + startY + " " + stopX + " " + stopY);
		canvas.drawLine(startX, startY, stopX, stopY, paint);
		
	}

	public void update(float dir) {
		direction = dir;
		invalidate();
	}
	
	public void reset() {
	    startX = 0;
	    startY = 0;
	}

}