package com.bit.v21remote

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeTheaterActivity : AppCompatActivity() {

    var ir: ConsumerIrManager? = null
    var led: View? = null

    val POWER = 0xE8177F80L
    val VOL_PLUS = 0xF50A7F80L
    val VOL_MINUS = 0xF9067F80L
    val MUTE = 0xB04F7F80L

    val USB = 0xF40B7F80L
    val MODE_5 = 0xF00F7F80L
    val MODE_2 = 0xB14E7F80L

    val FRONT_PLUS = 0xB6497F80L
    val FRONT_MINUS = 0xBA457F80L

    val REAR_PLUS = 0xBE417F80L
    val REAR_MINUS = 0xFD027F80L

    val BASS_PLUS = 0xB7487F80L
    val BASS_MINUS = 0xBB447F80L

    val CEN_PLUS = 0xF6097F80L
    val CEN_MINUS = 0xFA057F80L

    val TUNE_PLUS = 0xF7087F80L
    val TUNE_MINUS = 0xB9467F80L

    val EQ_3D = 0xED127F80L
    val FM = 0xF03C7F80L

    val PLAY = 0xA9567F80L
    val STOP = 0xA8577F80L

    val NEXT = 0xAD527F80L
    val PREV = 0xAC537F80L

    val REPEAT = 0xAE517F80L

    val handler = Handler(Looper.getMainLooper())
    var isHolding = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_theater)

        ir = getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

        val power = findViewById<Button>(R.id.btnPower)
        val mute = findViewById<Button>(R.id.btnMute)
        val usb = findViewById<Button>(R.id.btnUSB)
        val bassPlus = findViewById<Button>(R.id.btnBassPlus)
        val bassMinus = findViewById<Button>(R.id.btnBassMinus)
        val mode5 = findViewById<Button>(R.id.btn5M)
        val mode2 = findViewById<Button>(R.id.btn2M)
        val volPlus = findViewById<Button>(R.id.btnVolPlus)
        val volMinus = findViewById<Button>(R.id.btnVolMinus)
        val frontPlus = findViewById<Button>(R.id.btnFrontPuls)
        val frontMinus = findViewById<Button>(R.id.btnFrontMinus)
        val rearPlus = findViewById<Button>(R.id.btnRearPlus)
        val rearMinus = findViewById<Button>(R.id.btnRearMinus)
        val cenPlus = findViewById<Button>(R.id.btnCenPlus)
        val cenMinus = findViewById<Button>(R.id.btnCenMinus)
        val tunePlus = findViewById<Button>(R.id.btnTunePlus)
        val tuneMinus = findViewById<Button>(R.id.btnTuneMinus)
        val eq = findViewById<Button>(R.id.btn3D)
        val fm = findViewById<Button>(R.id.btnFM)
        val play = findViewById<Button>(R.id.btnPlay)
        val repeat = findViewById<Button>(R.id.btnRepeat)
        val next = findViewById<Button>(R.id.btnNext)
        val prev = findViewById<Button>(R.id.btnPrev)
        val pause = findViewById<Button>(R.id.btnPause)
        led = findViewById(R.id.led)

        clickWithVibration(power) { sendNEC(POWER) }

        clickWithVibration(mute) { sendNEC(MUTE) }
        clickWithVibration(usb) { sendNEC(USB) }

        clickWithVibration(mode5) { sendNEC(MODE_5) }
        clickWithVibration(mode2) { sendNEC(MODE_2) }

        clickWithVibration(eq) { sendNEC(EQ_3D) }
        clickWithVibration(fm) { sendNEC(FM) }

        clickWithVibration(play) { sendNEC(PLAY) }
        clickWithVibration(next) { sendNEC(NEXT) }
        clickWithVibration(prev) { sendNEC(PREV) }
        clickWithVibration(repeat) { sendNEC(REPEAT) }
        clickWithVibration(pause) { sendNEC(STOP) }

        holdButton(volPlus, VOL_PLUS)
        holdButton(volMinus, VOL_MINUS)

        holdButton(tunePlus, TUNE_PLUS)
        holdButton(tuneMinus, TUNE_MINUS)

        holdButton(frontPlus, FRONT_PLUS)
        holdButton(frontMinus, FRONT_MINUS)

        holdButton(rearPlus, REAR_PLUS)
        holdButton(rearMinus, REAR_MINUS)

        holdButton(bassPlus, BASS_PLUS)
        holdButton(bassMinus, BASS_MINUS)

        holdButton(cenPlus, CEN_PLUS)
        holdButton(cenMinus, CEN_MINUS)
    }

    fun sendNEC(code: Long) {
        val pattern = IntArray(67)
        var index = 0

        pattern[index++] = 9000
        pattern[index++] = 4500

        for (i in 0 until 32) {
            pattern[index++] = 560

            if ((code and (1L shl i)) != 0L) {
                pattern[index++] = 1690
            } else {
                pattern[index++] = 560
            }
        }

        pattern[index] = 560

        ir?.transmit(38000, pattern)
    }

    fun vibrate() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

        if (v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(50)
            }
        }
    }

    fun clickWithVibration(btn: Button, action: Runnable) {
        btn.setOnClickListener {
            vibrate()
            blinkLED()
            action.run()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun holdButton(btn: Button, code: Long) {
        btn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isHolding = true
                    vibrate()

                    // 🔥 steady ON (no blink)
                    led?.setBackgroundResource(R.drawable.led_on)
                    findViewById<View>(R.id.ledGlow)?.alpha = 1f

                    val runnable = object : Runnable {
                        override fun run() {
                            if (isHolding) {
                                sendNEC(code)
                                handler.postDelayed(this, 120)
                            }
                        }
                    }
                    handler.post(runnable)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isHolding = false

                    // 🔥 OFF
                    led?.setBackgroundResource(R.drawable.led_off)
                    findViewById<View>(R.id.ledGlow)?.alpha = 0f
                }
            }
            true
        }
    }

    fun blinkLED() {
        val glow = findViewById<View>(R.id.ledGlow)

        // ON
        led?.setBackgroundResource(R.drawable.led_on)
        glow?.alpha = 1f // 🔥 glow ON

        Handler(Looper.getMainLooper()).postDelayed({
            led?.setBackgroundResource(R.drawable.led_off)
            glow?.alpha = 0f // 🔥 glow OFF
        }, 100)
    }
}