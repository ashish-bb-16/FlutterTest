package com.example.fluttertest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import org.json.JSONObject

class MainActivity : FlutterViewActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            startActivity(Intent(this, TestimonialActivity::class.java))
        }

        val button4 = findViewById<Button>(R.id.button4)
        val button5 = findViewById<Button>(R.id.button5)
        val button6 = findViewById<Button>(R.id.button6)

        button4.setOnClickListener {
            launchWithRoute(this, "/profile_form")
            val jsonObject = JSONObject()
            jsonObject.put("usertype", "admin")
            sendData("profile_form",jsonObject)
        }

        button5.setOnClickListener {
            launchWithRoute(this, "/single_testimonial")
            val jsonObject = JSONObject()
            jsonObject.put("name", "Sakshi")
            jsonObject.put("msg", "Good App")
            sendData("single_testimonial", jsonObject)
        }

        button6.setOnClickListener {
            launchWithRoute(this, "/settings")
            val jsonObject = JSONObject()
            sendData("settings", jsonObject)
        }
    }
}