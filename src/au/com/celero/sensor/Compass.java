package au.com.celero.sensor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Compass extends View {

	private float direction;

	public Compass(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Compass(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Compass(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
				MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		int r;
		if (w > h) {
			r = h / 2;
		} else {
			r = w / 2;
		}

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(Color.WHITE);

		canvas.drawCircle(w / 2, h / 2, r, paint);

		paint.setColor(Color.RED);
		canvas.drawLine(w / 2, h / 2,
				(float) (w / 2 + r * Math.sin(-direction)), (float) (h / 2 - r
						* Math.cos(-direction)), paint);
	}

	public void update(float dir) {
		direction = dir;
		invalidate();
	}

}