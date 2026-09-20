package com.bit.v21remote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.Handler;
import android.hardware.ConsumerIrManager;


public class HomeTheaterActivity extends AppCompatActivity {


    ConsumerIrManager ir;
    View led;
    long POWER     = 0xE8177F80L;
    long VOL_PLUS  = 0xF50A7F80L;
    long VOL_MINUS = 0xF9067F80L;
    long MUTE      = 0xB04F7F80L;

    long USB       = 0xF40B7F80L;
    long MODE_5    = 0xF00F7F80L;
    long MODE_2    = 0xB14E7F80L;

    long FRONT_PLUS  = 0xB6497F80L;
    long FRONT_MINUS = 0xBA457F80L;

    long REAR_PLUS   = 0xBE417F80L;
    long REAR_MINUS  = 0xFD027F80L;

    long BASS_PLUS   = 0xB7487F80L;
    long BASS_MINUS  = 0xBB447F80L;

    long CEN_PLUS    = 0xF6097F80L;
    long CEN_MINUS   = 0xFA057F80L;

    long TUNE_PLUS   = 0xF7087F80L;
    long TUNE_MINUS  = 0xB9467F80L;

    long EQ_3D       = 0xED127F80L;
    long FM          = 0xF03C7F80L;

    long PLAY        = 0xA9567F80L;
    long STOP        = 0xA8577F80L;

    long NEXT        = 0xAD527F80L;
    long PREV        = 0xAC537F80L;

    long REPEAT      = 0xAE517F80L;


    Handler handler = new Handler();
    boolean isHolding = false;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_theater);

        ir = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);

        Button power = findViewById(R.id.btnPower);
        Button mute = findViewById(R.id.btnMute);
        Button usb = findViewById(R.id.btnUSB);
        Button bassPlus = findViewById(R.id.btnBassPlus);
        Button bassMinus = findViewById(R.id.btnBassMinus);
        Button mode5 = findViewById(R.id.btn5M);
        Button mode2 = findViewById(R.id.btn2M);
        Button volPlus = findViewById(R.id.btnVolPlus);
        Button volMinus = findViewById(R.id.btnVolMinus);
        Button frontPlus = findViewById(R.id.btnFrontPuls);
        Button frontMinus = findViewById(R.id.btnFrontMinus);
        Button rearPlus = findViewById(R.id.btnRearPlus);
        Button rearMinus = findViewById(R.id.btnRearMinus);
        Button cenPlus = findViewById(R.id.btnCenPlus);
        Button cenMinus = findViewById(R.id.btnCenMinus);
        Button tunePlus = findViewById(R.id.btnTunePlus);
        Button tuneMinus = findViewById(R.id.btnTuneMinus);
        Button eq = findViewById(R.id.btn3D);
        Button fm = findViewById(R.id.btnFM);
        Button play = findViewById(R.id.btnPlay);
        Button repeat = findViewById(R.id.btnRepeat);
        Button next = findViewById(R.id.btnNext);
        Button prev = findViewById(R.id.btnPrev);
        Button pause= findViewById(R.id.btnPause);
        led = findViewById(R.id.led);
        View glow = findViewById(R.id.ledGlow);




        clickWithVibration(power, () -> sendNEC(POWER));


        clickWithVibration(mute, () -> sendNEC(MUTE));
        clickWithVibration(usb, () -> sendNEC(USB));



        clickWithVibration(mode5, () -> sendNEC(MODE_5));
        clickWithVibration(mode2, () -> sendNEC(MODE_2));





        clickWithVibration(eq, () -> sendNEC(EQ_3D));
        clickWithVibration(fm, () -> sendNEC(FM));

        clickWithVibration(play, () -> sendNEC(PLAY));
        clickWithVibration(next, () -> sendNEC(NEXT));
        clickWithVibration(prev, () -> sendNEC(PREV));
        clickWithVibration(repeat, () -> sendNEC(REPEAT));
        clickWithVibration(pause, () -> sendNEC(STOP));

        holdButton(volPlus, VOL_PLUS);
        holdButton(volMinus, VOL_MINUS);

        holdButton(tunePlus, TUNE_PLUS);
        holdButton(tuneMinus, TUNE_MINUS);

        holdButton(frontPlus, FRONT_PLUS);
        holdButton(frontMinus, FRONT_MINUS);

        holdButton(rearPlus, REAR_PLUS);
        holdButton(rearMinus, REAR_MINUS);

        holdButton(bassPlus, BASS_PLUS);
        holdButton(bassMinus, BASS_MINUS);

        holdButton(cenPlus, CEN_PLUS);
        holdButton(cenMinus, CEN_MINUS);

    }

    void sendNEC(long code) {

        int[] pattern = new int[67];
        int index = 0;

        pattern[index++] = 9000;
        pattern[index++] = 4500;

        for (int i = 0; i < 32; i++) {
            pattern[index++] = 560;

            if ((code & (1L << i)) != 0) {
                pattern[index++] = 1690;
            } else {
                pattern[index++] = 560;
            }
        }

        pattern[index] = 560;

        ir.transmit(38000, pattern);
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
    void clickWithVibration(Button btn, Runnable action) {
        btn.setOnClickListener(v -> {
            vibrate();
            blinkLED();
            action.run();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    void holdButton(Button btn, long code) {

        btn.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    isHolding = true;

                    vibrate();

                    // 🔥 steady ON (no blink)
                    led.setBackgroundResource(R.drawable.led_on);
                    findViewById(R.id.ledGlow).setAlpha(1f);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isHolding) {
                                sendNEC(code);
                                handler.postDelayed(this, 120);
                            }
                        }
                    });
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isHolding = false;

                    // 🔥 OFF
                    led.setBackgroundResource(R.drawable.led_off);
                    findViewById(R.id.ledGlow).setAlpha(0f);

                    break;
            }

            return true;
        });
    }
    void blinkLED() {

        View glow = findViewById(R.id.ledGlow);

        // ON
        led.setBackgroundResource(R.drawable.led_on);
        glow.setAlpha(1f); // 🔥 glow ON

        new Handler().postDelayed(() -> {
            led.setBackgroundResource(R.drawable.led_off);
            glow.setAlpha(0f); // 🔥 glow OFF
        }, 100);
    }
}