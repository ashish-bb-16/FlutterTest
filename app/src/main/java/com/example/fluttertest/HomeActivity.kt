package com.example.fluttertest

import android.os.Bundle

class HomeActivity : FlutterViewActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
}