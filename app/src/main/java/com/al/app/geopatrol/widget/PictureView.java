package com.al.app.geopatrol.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Dai Jingjing on 2016/3/18.
 */
public class PictureView extends ImageView {

    private GestureDetector gestureDetector;

    public PictureView(Context context) {
        super(context);
        init();
    }

    public PictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setScaleType(ScaleType.CENTER_INSIDE);
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            onViewDoubleTap();

            return true;
        }
    }

    protected void onViewDoubleTap() {
        this.setScaleType(getScaleType() == ScaleType.CENTER_INSIDE ? ScaleType.CENTER_CROP : ScaleType.CENTER_INSIDE);
    }
}
