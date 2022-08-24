package com.example.fluttertest

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.fluttertest.databinding.AdapterItemBinding
import io.flutter.plugin.common.MethodChannel
import java.util.*
import kotlin.collections.HashMap

//To optimise the performance, we could try calculating the number of flutter views from the recycler view
// and attach their engine to activity before initialising the adapter

class ListAdapter(
    context: Context,
    private val activity: ComponentActivity,
    private val items: MutableList<Item>,
    private val availableFlutterEngines: Queue<FlutterViewEngine>
) : RecyclerView.Adapter<ListAdapter.CellViewHolder>(), EngineBindingsDelegate {
    private val viewBinding: EngineBindings by lazy {
        EngineBindings(activity = context as Activity, delegate = this, entrypoint = "showCell")
    }

    // Save the previous cells determined to be Flutter cells to avoid a confusing visual effect
    // that the Flutter cells change position when scrolling back.
    var engineMap = HashMap<Int, FlutterViewEngine>()
    private lateinit var engineHandler: EngineHandler

    private val matchParentLayout = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    //private val flutterView = FlutterView(context)
    //private val flutterChannel = MethodChannel(flutterViewEngine.engine.dartExecutor, "my_cell")

    inner class CellViewHolder(val binding: AdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    fun setItems(items: List<Item>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val binding = AdapterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CellViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        val item = items[position]
        Log.d("ItemTest", "Print: $item")
        //Log.d("ItemTest", "Print: $item ${previousFlutterCells.contains(position)} ${previousFlutterCells.isEmpty()} ${position > previousFlutterCells.last()} ${viewHolder == null} ${item.isFlutter}")
        // While scrolling forward, if no Flutter is presently showing, let the next one have a 1/3
        // chance of being Flutter.
        //
        // While scrolling backward, let it be deterministic, and only show cells that were
        // previously Flutter cells as Flutter cells.
        if (item.isFlutter) {
            // If we're restoring a cell at a previous location, the current cell may not have
            // recycled yet since that JVM timing is Nondeterministic. Yank it from the current one.
            //
            // This shouldn't produce any visual glitches since in the forward direction,
            // Flutter cells were only introduced once the previous Flutter cell recycled.
            /*if (viewHolder != null) {
                Log.d("FeedAdapter", "While restoring a previous Flutter cell, a current "
                        + "yet to be recycled Flutter cell was detached.")
                viewHolder!!.binding.root.removeView(flutterView)
                flutterViewEngine.detachFlutterView()
                viewHolder = null
            }*/

            // Add the Flutter card and hide the Android card for the cells chosen to be Flutter
            // cells.
            //holder.binding.root.addView(flutterView, matchParentLayout)
            holder.binding.androidCard.visibility = View.GONE
            holder.binding.flutterView.visibility = View.VISIBLE
            holder.binding.flutterView.tag = "Item $position"

            /*if (!engineMap.containsKey(position)) {
                val engine = FlutterViewEngine(viewBinding.engine)
                engine.attachToActivity(activity)
                engineMap[position] = engine
            }*/

            if (item.engine == null) {
                item.engine = availableFlutterEngines.poll()
            }

            //FlutterEngineCache.getInstance().put(position.toString(), item.engine?.engine)
            // This is what makes the Flutter cell start rendering.
            holder.binding.flutterView.isScrollable = item.data.size > 1
            item.engine?.attachFlutterView(holder.binding.flutterView)
            val flutterChannel =
                MethodChannel(item.engine?.engine?.dartExecutor!!, "my_cell")
           /* holder.binding.flutterView.layoutManager = LinearLayoutManager(holder.binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            holder.binding.flutterView.adapter = FlutterViewAdapter(item.engine!!, flutterChannel, item.data)
            holder.binding.flutterView.isNestedScrollingEnabled = true*/
            /*Log.d(
                "ItemTest",
                "Flutter view engineId: ${holder.binding.flutterView.isAttachedToFlutterEngine} ${holder.binding.flutterView.tag}"
            )*/
            // Tell Flutter which index it's at so Flutter could show the cell number too in its
            // own widget tree.
            /*val flutterChannel =
                MethodChannel(item.engine?.engine?.dartExecutor!!, "my_cell")*/
            flutterChannel.invokeMethod("setCellData", item.data)
        } else {
            // If it's not selected as a Flutter cell, just show the Android card.
            holder.binding.androidCard.visibility = View.VISIBLE
            holder.binding.flutterView.visibility = View.GONE
            holder.binding.cellNumber.text = item.text
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].isFlutter) {
            true -> 1
            else -> 0
        }
    }

    override fun getItemCount() = items.size

    override fun onViewRecycled(cell: CellViewHolder) {
        val item = items[cell.adapterPosition]
        if (item.isFlutter) {
            Log.d("ItemTest", "Item at position ${cell.adapterPosition} is being recycled")
            item.engine?.detachFlutterView()
            availableFlutterEngines.add(item.engine)
            item.engine = null
            //item.engine?.detachActivity()
            //engineMap.remove(cell.adapterPosition)
            //FlutterEngineCache.getInstance().remove(cell.adapterPosition.toString())
            Log.d(
                "ItemTest",
                "Engine at position ${cell.adapterPosition} is ${engineMap.containsKey(cell.adapterPosition)}"
            )
            //viewHolder = null
        }
        super.onViewRecycled(cell)
    }

    data class Item(
        val text: String,
        val isFlutter: Boolean,
        val position: Int,
        val data: List<Map<String, Any>>,
        var engine: FlutterViewEngine?
    )

    override fun onNext() {
        TODO("Not yet implemented")
    }

    interface EngineHandler {
        fun onEngineCreated(position: Int, flutterViewEngine: FlutterViewEngine)
    }

    data class FlutterSectionItem(
        val image: String,
        val sectionPosition: Int,
        val itemPosition: Int
    )
}