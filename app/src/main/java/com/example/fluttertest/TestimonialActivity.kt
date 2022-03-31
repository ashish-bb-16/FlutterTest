package com.example.fluttertest

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.json.JSONObject

class TestimonialActivity : FlutterViewActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testimonial)

        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val newtextView = findViewById<TextView>(R.id.newtextView)
        val name = intent.getStringExtra("name")
        val age = intent.getStringExtra("age")
        if(name!=null && age!=null) {
            newtextView.text = name+age
        }

        button2.setOnClickListener {
            launchWithRoute(this, "/single_testimonial")
            val jsonObject = JSONObject()
            jsonObject.put("name", "Sakshi")
            jsonObject.put("msg", "Good App")
            sendData("single_testimonial", jsonObject)
        }

        button3.setOnClickListener {
            launchWithRoute(this, "/settings")
            val jsonObject = JSONObject()
            sendData("settings", jsonObject)
        }
    }
}