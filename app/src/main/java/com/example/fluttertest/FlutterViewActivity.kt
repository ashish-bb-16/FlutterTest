package com.example.fluttertest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject

open class FlutterViewActivity : AppCompatActivity() {

    private val flutterEngine: FlutterEngine by lazy {
        FlutterEngine(this.applicationContext)
    }

    companion object {
        const val FLUTTER_ENGINE = "flutter_engine"
        const val CHANNEL = "callback"

        fun launchWithRoute(host: Context, route: String) {
            host.startActivity(FlutterActivity.withCachedEngine(FLUTTER_ENGINE).build(host))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        registerMethodChannel()
    }

    private fun init() {
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        FlutterEngineCache.getInstance().put(FLUTTER_ENGINE, flutterEngine)
    }

    private fun registerMethodChannel() {
        flutterEngine.let {
            MethodChannel(it.dartExecutor, CHANNEL).setMethodCallHandler { call, result ->
                // manage method calls here
                if (call.method == "fromFlutterToNative") {
                    val resultStr = call.arguments.toString()
                    Log.d("FlutterTest", "Received : $resultStr")
                    val jsonObject = JSONObject(resultStr)
                    val route = jsonObject["route"]
                    val data = JSONObject(jsonObject["args"].toString())
                    Log.d("FlutterTest", "Received route : $route")
                    Log.d("FlutterTest", "Received data : $data")
                    when (route) {
                        "testimonial" ->{
                            val intent = Intent(this, StandaloneFlutterView::class.java)
                            intent.putExtra("name", data["name"].toString())
                            intent.putExtra("age", data["age"].toString())
                            startActivity(intent)
                        }
                        else -> {
                            startActivity(Intent(this, MainActivity::class.java))
                        }

                    }
                } else {
                    result.notImplemented()
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
        }
    }

    fun sendData(route: String, json: JSONObject) {
        val jsonObject = JSONObject()
        jsonObject.put("route", route)
        jsonObject.put("args", json.toString())
        MethodChannel(flutterEngine.dartExecutor, CHANNEL).invokeMethod(
            "fromNativeToFlutter",
            jsonObject.toString()
        )
    }
}