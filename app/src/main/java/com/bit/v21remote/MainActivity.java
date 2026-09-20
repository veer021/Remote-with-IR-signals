package com.bit.v21remote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.content.Context;

public class MainActivity extends AppCompatActivity {

    ConsumerIrManager irManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);

        // IR Manager
        irManager = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);
        LinearLayout home = findViewById(R.id.btnHome);
        LinearLayout tv = findViewById(R.id.btnTV);




        // Check IR support
        if (irManager == null || !irManager.hasIrEmitter()) {
            Toast.makeText(this, "IR not supported", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "IR supported ✅", Toast.LENGTH_SHORT).show();
        }




        home.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, HomeTheaterActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


        tv.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, TVRemoteActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });



    }


    @SuppressLint("ClickableViewAccessibility")
    void applyPressEffect(View v) {

        v.setOnTouchListener((view, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    view.setAlpha(0.7f); // thoda fade
                    view.animate().scaleX(0.85f).scaleY(0.85f).setDuration(80).start();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    view.setAlpha(1f);
                    view.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                    break;
            }

            return false;
        });
    }

    void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (v != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(50);
            }
        }
    }
}