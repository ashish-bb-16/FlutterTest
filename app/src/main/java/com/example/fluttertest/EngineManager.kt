package com.example.fluttertest

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor
import java.util.*

object EngineManager {
    private var flutterEngineGroup: FlutterEngineGroup? = null
    val availableFlutterEngines: Queue<FlutterViewEngine> = LinkedList()

    private fun getEngineGroup(context: Context): FlutterEngineGroup {
        if (flutterEngineGroup == null) {
            flutterEngineGroup = FlutterEngineGroup(context)
        }
        return flutterEngineGroup!!
    }

    fun createViewEngine(activity: Activity): FlutterViewEngine {
        val flutterViewEngine = FlutterViewEngine(
            getEngineGroup(activity).createAndRunEngine(
                activity, DartExecutor.DartEntrypoint(
                    FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                    "showCell"
                )
            )
        )
        flutterViewEngine.attachToActivity(activity as ComponentActivity)
        availableFlutterEngines.add(flutterViewEngine)
        return flutterViewEngine
    }

    fun detachAllEngines(activity: Activity) {
        while (availableFlutterEngines.isNotEmpty()) {
            val flutterViewEngine = availableFlutterEngines.remove()
            flutterViewEngine.detachActivity()
            flutterViewEngine.engine.destroy()
        }
        flutterEngineGroup = null
    }
}