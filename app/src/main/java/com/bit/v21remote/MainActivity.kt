package com.bit.v21remote

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.ConsumerIrManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    var irManager: ConsumerIrManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitymain)

        // IR Manager
        irManager = getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
        val home = findViewById<LinearLayout>(R.id.btnHome)
        val tv = findViewById<LinearLayout>(R.id.btnTV)

        // Check IR support
        val manager = irManager
        if (manager == null || !manager.hasIrEmitter()) {
            Toast.makeText(this, "IR not supported", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "IR supported ✅", Toast.LENGTH_SHORT).show()
        }

        home.setOnClickListener {
            val i = Intent(this@MainActivity, HomeTheaterActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        tv.setOnClickListener {
            val i = Intent(this@MainActivity, TVRemoteActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun applyPressEffect(v: View) {
        v.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.alpha = 0.7f // thoda fade
                    view.animate().scaleX(0.85f).scaleY(0.85f).setDuration(80).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.alpha = 1f
                    view.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                }
            }
            false
        }
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
}