package com.example.fluttertest

import android.os.Bundle
import com.example.fluttertest.databinding.ActivityTestimonialBinding
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject

class StandaloneFlutterView : FlutterViewActivity() {
    private lateinit var binding: ActivityTestimonialBinding
    private lateinit var flutterViewEngine: FlutterViewEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestimonialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val engine = FlutterEngine(applicationContext)
        engine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint(
                FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                "showCell"))

        flutterViewEngine = FlutterViewEngine(engine)
        // The activity and FlutterView have different lifecycles.
        // Attach the activity right away but only start rendering when the
        // view is also scrolled into the screen.
        flutterViewEngine.attachToActivity(this)
        flutterViewEngine.attachFlutterView(binding.flutterView)
        val flutterChannel = MethodChannel(flutterViewEngine.engine.dartExecutor, "my_cell")
        flutterChannel.invokeMethod("setCellData", 2020202)

        binding.button.setOnClickListener {
            launchWithRoute(this, "/single_testimonial")
            val jsonObject = JSONObject()
            jsonObject.put("name", "Sakshi")
            jsonObject.put("msg", "Good App")
            sendData("single_testimonial", jsonObject)
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        flutterViewEngine.detachActivity()
    }
}