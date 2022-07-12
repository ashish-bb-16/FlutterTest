package com.example.fluttertest

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class HomeActivity : FlutterViewActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.button3).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}