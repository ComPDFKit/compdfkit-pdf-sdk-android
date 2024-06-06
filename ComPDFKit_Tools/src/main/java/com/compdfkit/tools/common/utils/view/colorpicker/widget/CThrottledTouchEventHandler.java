package com.compdfkit.tools.common.utils.view.colorpicker.widget;


import android.view.MotionEvent;

import com.compdfkit.tools.common.utils.view.colorpicker.interfaces.CMotionEventUpdatable;

class CThrottledTouchEventHandler {

    /**
     * 16ms
     */
    private static final int EVENT_MIN_INTERVAL = 1000 / 60;

    private final int minInterval;
    private final CMotionEventUpdatable updatable;
    private long lastPassedEventTime;

    public CThrottledTouchEventHandler(int minInterval, CMotionEventUpdatable updatable) {
        this.minInterval = minInterval;
        this.updatable = updatable;
    }

    public CThrottledTouchEventHandler(CMotionEventUpdatable updatable) {
        this(EVENT_MIN_INTERVAL, updatable);
    }

    public void onTouchEvent(MotionEvent event) {
        long current = System.currentTimeMillis();
        if (current - lastPassedEventTime <= minInterval) {
            return;
        }
        lastPassedEventTime = current;
        updatable.update(event);
    }

}
