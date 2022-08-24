package com.example.fluttertest

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import io.flutter.embedding.android.FlutterImageView
import io.flutter.embedding.android.FlutterSurfaceView
import io.flutter.embedding.android.FlutterTextureView
import io.flutter.embedding.android.FlutterView
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.atan2

class MyFlutterView : FlutterView {
    private var lastX = 0F
    private var lastY = 0F
    var isScrollable = false
    private var minVerticalSwipeableAngle = 10

    constructor(context: Context) : super(context)

    constructor(context: Context, flutterSurfaceView: FlutterSurfaceView) : super(
        context,
        flutterSurfaceView
    )

    constructor(context: Context, flutterTextureView: FlutterTextureView) : super(
        context,
        flutterTextureView
    )

    constructor(context: Context, flutterImageView: FlutterImageView) : super(
        context,
        flutterImageView
    )

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val result = super.onTouchEvent(event)
        if (!isScrollable) {
            return result
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                val currentX: Float = event.x
                val currentY: Float = event.y
                val dx: Float = currentX - lastX
                val dy: Float = currentY - lastY

                var verticalAngle = Math.toDegrees(atan2(dy, dx).toDouble())
                if (verticalAngle < 0) {
                    verticalAngle = abs(verticalAngle)
                }

                if (verticalAngle < 90) {
                    verticalAngle = 90 - verticalAngle
                } else {
                    verticalAngle -= 90
                }

                if (verticalAngle > minVerticalSwipeableAngle) {
                    requestDisallowInterceptTouchEvent(true)
                } else {
                    requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        return result
    }
}