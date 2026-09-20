package com.bit.v21remote;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.util.AttributeSet;
public class DpadView extends View {

    Paint paint = new Paint();
    String pressed = "";
    public DpadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
    }
    public DpadView(Context context) {
        super(context);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;
        int radius = Math.min(w, h) / 2 - 20;
        int gap = radius / 2;

        paint.setAntiAlias(true);

        // Outer circle
        paint.setColor(Color.parseColor("#E0E0E0")); // outer
        canvas.drawCircle(cx, cy, radius, paint);

        // Center circle
        paint.setColor(Color.parseColor("#2A2A2A")); // center
        canvas.drawCircle(cx, cy, radius/3, paint);

        // Arrow text
        paint.setColor(Color.parseColor("#555555")); // arrows
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);


        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.FILL);

        canvas.drawText("▲", cx, cy - 150, paint);
        canvas.drawText("▼", cx, cy + 200, paint);
        canvas.drawText("◀", cx - 150, cy + 20, paint);
        canvas.drawText("▶", cx + 150, cy + 20, paint);

        paint.setColor(Color.parseColor("#33FFFFFF"));

        if (pressed.equals("UP"))
            canvas.drawCircle(cx, cy - gap, 60, paint);

        if (pressed.equals("DOWN"))
            canvas.drawCircle(cx, cy + gap, 60, paint);

        if (pressed.equals("LEFT"))
            canvas.drawCircle(cx - gap, cy, 60, paint);

        if (pressed.equals("RIGHT"))
            canvas.drawCircle(cx + gap, cy, 60, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        int w = getWidth();
        int h = getHeight();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (y < h / 3) {
                pressed = "UP";
            } else if (y > h * 2 / 3) {
                pressed = "DOWN";
            } else if (x < w / 3) {
                pressed = "LEFT";
            } else if (x > w * 2 / 3) {
                pressed = "RIGHT";
            } else {
                pressed = "CENTER";
            }

            invalidate(); // redraw
        }

        return true;
    }
}