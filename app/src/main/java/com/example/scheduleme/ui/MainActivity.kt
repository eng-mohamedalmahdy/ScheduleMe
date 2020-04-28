package com.example.scheduleme.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.scheduleme.R
import com.example.scheduleme.util.AlarmReceiver

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AlarmReceiver.mediaPlayer?.stop()
        Handler().postDelayed({
            startActivity(Intent(this@MainActivity, ScheduleActivity::class.java))
            finish()
        }, 1000)

    }
}