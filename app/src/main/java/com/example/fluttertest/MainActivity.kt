package com.example.fluttertest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fluttertest.databinding.ActivityMainBinding
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import java.util.*

class MainActivity : FlutterViewActivity(), DataModelObserver {
    private lateinit var binding: ActivityMainBinding
    private lateinit var flutterViewEngine: FlutterViewEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DataModel.instance.addObserver(this)

        val engine = FlutterEngine(applicationContext)
        engine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint(
                FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                "showCell"))

        binding.button.setOnClickListener {
            startActivity(Intent(this, StandaloneFlutterView::class.java))
        }

        flutterViewEngine = FlutterViewEngine(engine)
        // The activity and FlutterView have different lifecycles.
        // Attach the activity right away but only start rendering when the
        // view is also scrolled into the screen.
        //flutterViewEngine.attachToActivity(this)

        val layoutManager = LinearLayoutManager(this)
        val recyclerView = binding.recyclerView
        val adapter = ListAdapter(this, this, getList())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // If the activity was restarted, keep track of the previous scroll
        // position and of the previous cell indices that were randomly selected
        // as Flutter cells to preserve immersion.
        layoutManager.onRestoreInstanceState(savedInstanceState?.getParcelable("layoutManager"))
        val previousFlutterCellsArray = savedInstanceState?.getIntegerArrayList("adapter")
        if (previousFlutterCellsArray != null) {
            adapter.previousFlutterCells = TreeSet(previousFlutterCellsArray)
        }


        /*button4.setOnClickListener {
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
        }*/
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable("layoutManager", binding.recyclerView.layoutManager?.onSaveInstanceState())
        val previousFlutterCells = (binding.recyclerView.adapter as? ListAdapter)?.previousFlutterCells
        if (previousFlutterCells != null) {
            outState.putIntegerArrayList(
                "adapter",
                ArrayList(previousFlutterCells)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //flutterViewEngine.detachActivity()
        //flutterViewEngine.detachFlutterView()
        DataModel.instance.removeObserver(this)
    }

    private fun getList(): List<ListAdapter.Item> {
        val list = mutableListOf<ListAdapter.Item>()
        val random = Random()

        for (i in 0..200) {
            if (random.nextInt(3) == 0) {
                list.add(ListAdapter.Item("Flutter $i", true, i))
            } else {
                list.add(ListAdapter.Item("Android $i", false, i))
            }
        }
        return list
    }

    override fun onCountUpdate(newCount: Int) {
        TODO("Not yet implemented")
    }
}