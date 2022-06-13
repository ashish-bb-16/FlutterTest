package com.example.fluttertest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fluttertest.databinding.ActivityMainBinding
import io.flutter.FlutterInjector
import io.flutter.Log
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor
import java.util.*

class MainActivity : FlutterViewActivity(), DataModelObserver, EngineBindingsDelegate,
    ListAdapter.EngineHandler {
    private lateinit var binding: ActivityMainBinding
    private lateinit var flutterViewEngine: FlutterViewEngine
    private lateinit var flutterEngineGroup: FlutterEngineGroup
    private lateinit var initialEngine: FlutterEngine
    private lateinit var adapter: ListAdapter
    private val availableFlutterEngines: Queue<FlutterViewEngine> = LinkedList()

    private val viewBinding: EngineBindings by lazy {
        EngineBindings(activity = this, delegate = this, entrypoint = "showCell")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DataModel.instance.addObserver(this)
        flutterEngineGroup = FlutterEngineGroup(this)

        /* val engine = FlutterEngine(applicationContext)
         engine.dartExecutor.executeDartEntrypoint(
             DartExecutor.DartEntrypoint(
                 FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                 "showCell"
             )
         )

         val init = System.currentTimeMillis()
         initialEngine = flutterEngineGroup.createAndRunEngine(
             this, DartExecutor.DartEntrypoint(
                 FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                 "showCell"
             )
         )*/

        val init = System.currentTimeMillis()
        for (i in 0..100) {
            val engine1 = FlutterViewEngine(
                flutterEngineGroup.createAndRunEngine(
                    this, DartExecutor.DartEntrypoint(
                        FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                        "showCell"
                    )
                )
            )
            engine1.attachToActivity(this)
            availableFlutterEngines.add(engine1)
        }
        Toast.makeText(this, "Took " + (System.currentTimeMillis() - init)/1000F + " seconds to create " + availableFlutterEngines.size + " engines", Toast.LENGTH_LONG).show()

        //Log.d("PerformTest", "First engine time: ${System.currentTimeMillis() - init}")

        binding.button.setOnClickListener {
            startActivity(Intent(this, StandaloneFlutterView::class.java))
        }

        //flutterViewEngine = FlutterViewEngine(engine)
        // The activity and FlutterView have different lifecycles.
        // Attach the activity right away but only start rendering when the
        // view is also scrolled into the screen.
        //flutterViewEngine.attachToActivity(this)


        val items = getList()
        val random = Random()

        /*items.forEach {
            with(it) {
                it.engine?.let { it1 ->
                    it1.attachToActivity(this@MainActivity)
                }
            }
        }*/

        //Adding engines to flutter engine group is taking around 11.297 secs for 76 engines
        /* Log.d("PerformTest", "Initialising list: ${System.currentTimeMillis()}")
         var isFirst = true
         var count = 1
         showProgress(true)

         GlobalScope.launch(Dispatchers.IO) {
             for (i in 0..200) {
                 if (random.nextInt(3) == 0) {
                     binding.progressText.text = "Creating engine $count"
                     items.add(
                         ListAdapter.Item(
                             "Flutter $i",
                             true,
                             i,
                             addEngine(isFirst, i)
                         )
                     )
                     isFirst = false
                     count++
                 } else {
                     items.add(ListAdapter.Item("Android $i", false, i, null))
                 }
             }
             launch(Dispatchers.Main) {
                 showProgress(false)

                 Log.d("PerformTest", "Initialising attach to activity: ${System.currentTimeMillis()}")
                 items.forEach {
                     with(it) {
                         it.engine?.let { it1 ->
                             it1.attachToActivity(this@MainActivity)
                         }
                     }
                 }
                 adapter.setItems(items)
                 Log.d("PerformTest", "Finalised attach to activity: ${System.currentTimeMillis()}")
             }
         }*/

        val layoutManager = LinearLayoutManager(this)
        val recyclerView = binding.recyclerView
        adapter = ListAdapter(this, this, items.toMutableList(), availableFlutterEngines)
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

        outState.putParcelable(
            "layoutManager",
            binding.recyclerView.layoutManager?.onSaveInstanceState()
        )
        val previousFlutterCells =
            (binding.recyclerView.adapter as? ListAdapter)?.previousFlutterCells
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

        //Adding engines to flutter engine group is taking around 11.297 secs for 76 engines
        Log.d("PerformTest", "Initialising list: ${System.currentTimeMillis()}")
        var isFirst = true
        var count = 1
        showProgress(true)
        for (i in 0..200) {
            if (random.nextInt(3) == 0) {
                binding.progressText.text = "Creating engine $count"
                list.add(
                    ListAdapter.Item(
                        "Flutter $i",
                        true,
                        i,
                        null
                    )
                )
                isFirst = false
                count++
            } else {
                list.add(ListAdapter.Item("Android $i", false, i, null))
            }
        }
        showProgress(false)
        Log.d("PerformTest", "Finalising List: ${System.currentTimeMillis()} , $count")
        return list
    }

    private fun addEngine(isFirst: Boolean, position: Int): FlutterViewEngine {
        val init = System.currentTimeMillis()
        //Log.d("PerformTest", "Begin Creating engine for $position: ${System.currentTimeMillis()}")
        val engine = if (isFirst) FlutterViewEngine(initialEngine) else FlutterViewEngine(
            flutterEngineGroup.createAndRunEngine(
                this, DartExecutor.DartEntrypoint(
                    FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                    "showCell"
                )
            )
        )
        Log.d(
            "PerformTest",
            "End Creating engine for $position: ${System.currentTimeMillis() - init}"
        )

        return engine
    }

    override fun onCountUpdate(newCount: Int) {
        TODO("Not yet implemented")
    }

    override fun onNext() {
        TODO("Not yet implemented")
    }

    override fun onEngineCreated(position: Int, flutterViewEngine: FlutterViewEngine) {
        flutterViewEngine.attachToActivity(this)
    }

    override fun onCreateEngine(position: Int, dartEntrypointName: String): FlutterViewEngine {
        val engine = flutterEngineGroup.createAndRunEngine(
            this, DartExecutor.DartEntrypoint(
                FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                "showCell"
            )
        )
        val flutterViewEngine = FlutterViewEngine(engine)
        flutterViewEngine.attachToActivity(this)
        return flutterViewEngine
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.progressLayout.visibility = View.VISIBLE
            binding.button.visibility = View.GONE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.progressLayout.visibility = View.GONE
            binding.button.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }
}