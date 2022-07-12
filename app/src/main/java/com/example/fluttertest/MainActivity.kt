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
    private lateinit var initialEngine: FlutterEngine
    private lateinit var adapter: ListAdapter
    private val imageList = a(
        "https://images.unsplash.com/photo-1520342868574-5fa3804e551c?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=6ff92caffcdd63681a35134a6770ed3b&auto=format&fit=crop&w=1951&q=80",
        "https://images.unsplash.com/photo-1522205408450-add114ad53fe?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=368f45b0888aeb0b7b08e3a1084d3ede&auto=format&fit=crop&w=1950&q=80",
        "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=94a1e718d89ca60a6337a6008341ca50&auto=format&fit=crop&w=1950&q=80",
        "https://images.unsplash.com/photo-1523205771623-e0faa4d2813d?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=89719a0d55dd05e2deae4120227e6efc&auto=format&fit=crop&w=1953&q=80",
        "https://images.unsplash.com/photo-1508704019882-f9cf40e475b4?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=8c6e5e3aba713b17aa1fe71ab4f0ae5b&auto=format&fit=crop&w=1352&q=80",
        "https://images.unsplash.com/photo-1519985176271-adb1088fa94c?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=a0c8d632e977f94e5d312d9893258f59&auto=format&fit=crop&w=1355&q=80"
    )

    private fun <T> a(vararg items: T) = arrayListOf(*items)

    private val viewBinding: EngineBindings by lazy {
        EngineBindings(activity = this, delegate = this, entrypoint = "showCell")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DataModel.instance.addObserver(this)

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
        for (i in 0..6) {
            val engine1 = EngineManager.createViewEngine(this)
            engine1.attachToActivity(this)
        }
        Toast.makeText(
            this,
            "Took " + (System.currentTimeMillis() - init) / 1000F + " seconds to create " + EngineManager.availableFlutterEngines.size + " engines",
            Toast.LENGTH_LONG
        ).show()

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
        adapter =
            ListAdapter(this, this, items.toMutableList(), EngineManager.availableFlutterEngines)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // If the activity was restarted, keep track of the previous scroll
        // position and of the previous cell indices that were randomly selected
        // as Flutter cells to preserve immersion.
        layoutManager.onRestoreInstanceState(savedInstanceState?.getParcelable("layoutManager"))
        val previousFlutterCellsArray = savedInstanceState?.getIntegerArrayList("adapter")
        if (previousFlutterCellsArray != null) {
            //adapter.previousFlutterCells = TreeSet(previousFlutterCellsArray)
        }
        recyclerView.isNestedScrollingEnabled = false;


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
        /* val previousFlutterCells =
             (binding.recyclerView.adapter as? ListAdapter)?.previousFlutterCells
         if (previousFlutterCells != null) {
             outState.putIntegerArrayList(
                 "adapter",
                 ArrayList(previousFlutterCells)
             )
         }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        //flutterViewEngine.detachActivity()
        //flutterViewEngine.detachFlutterView()
        EngineManager.detachAllEngines(this)
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
                        getData(i),
                        null
                    )
                )
                isFirst = false
                count++
            } else {
                list.add(ListAdapter.Item("Android $i", false, i, getData(i), null))
            }
        }
        showProgress(false)
        Log.d("PerformTest", "Finalising List: ${System.currentTimeMillis()} , $count")
        return list
    }

    /*private fun addEngine(isFirst: Boolean, position: Int): FlutterViewEngine {
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
    }*/

    override fun onCountUpdate(newCount: Int) {
        TODO("Not yet implemented")
    }

    override fun onNext() {
        TODO("Not yet implemented")
    }

    override fun onEngineCreated(position: Int, flutterViewEngine: FlutterViewEngine) {
        flutterViewEngine.attachToActivity(this)
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

    private fun getData(position: Int): List<Map<String, Any>> {
        val list = mutableListOf<Map<String, Any>>()
        list.clear()
        if (position % 3 == 0) {
            imageList.forEachIndexed { index, s ->
                val map = mutableMapOf<String, Any>()
                map["image"] = s
                map["sectionPosition"] = position
                map["itemPosition"] = index
                list.add(map)
            }
        } else {
            val map = mutableMapOf<String, Any>()
            map["image"] = imageList[0]
            map["sectionPosition"] = position
            map["itemPosition"] = 0
            list.add(map)
        }

        return list
    }
}