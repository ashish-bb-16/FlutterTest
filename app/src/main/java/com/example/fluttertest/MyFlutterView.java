package com.example.fluttertest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.android.FlutterImageView;
import io.flutter.embedding.android.FlutterSurfaceView;
import io.flutter.embedding.android.FlutterTextureView;
import io.flutter.embedding.android.FlutterView;

public class MyFlutterView extends FlutterView {

    public MyFlutterView(@NonNull Context context) {
        super(context);
    }

    public MyFlutterView(@NonNull Context context, @NonNull FlutterSurfaceView flutterSurfaceView) {
        super(context, flutterSurfaceView);
    }

    public MyFlutterView(@NonNull Context context, @NonNull FlutterTextureView flutterTextureView) {
        super(context, flutterTextureView);
    }

    public MyFlutterView(@NonNull Context context, @NonNull FlutterImageView flutterImageView) {
        super(context, flutterImageView);
    }

    public MyFlutterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        Log.d("TouchResult", "Received result : " + result);
        return result;
    }
}
