package com.GoogleGlass.googleglasslevellingapp;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GlassGestureDetector implements GestureDetector.OnGestureListener {

        enum Gesture {
            TAP,
            SWIPE_FORWARD,
            SWIPE_BACKWARD,
            SWIPE_UP,
            SWIPE_DOWN,
        }

        interface OnGestureListener {
            boolean onGesture(Gesture gesture);
        }

        private static final int SWIPE_DISTANCE_THRESHOLD_PX = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD_PX = 100;
        private static final double TAN_60_DEGREES = Math.tan(Math.toRadians(60));

        private GestureDetector gestureDetector;
        private OnGestureListener onGestureListener;

   public GlassGestureDetector(Context context, OnGestureListener onGestureListener) {
            gestureDetector = new GestureDetector(context, this);
            this.onGestureListener = onGestureListener;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return onGestureListener.onGesture(Gesture.TAP);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        /**
         * Swipe detection depends on the:
         * - movement tan value,
         * - movement distance,
         * - movement velocity.
         *
         * To prevent unintentional SWIPE_DOWN and SWIPE_UP gestures, they are detected if movement
         * angle is only between 60 and 120 degrees.
         * Any other detected swipes, will be considered as SWIPE_FORWARD and SWIPE_BACKWARD, depends
         * on deltaX value sign.
         *
         *           ______________________________________________________________
         *          |                     \        UP         /                    |
         *          |                       \               /                      |
         *          |                         60         120                       |
         *          |                           \       /                          |
         *          |                             \   /                            |
         *          |  BACKWARD  <-------  0  ------------  180  ------>  FORWARD  |
         *          |                             /   \                            |
         *          |                           /       \                          |
         *          |                         60         120                       |
         *          |                       /               \                      |
         *          |                     /       DOWN        \                    |
         *           --------------------------------------------------------------
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            final float deltaX = e2.getX() - e1.getX();
            final float deltaY = e2.getY() - e1.getY();
            final double tan = deltaX != 0 ? Math.abs(deltaY/deltaX) : Double.MAX_VALUE;

            if (tan > TAN_60_DEGREES) {
                if (Math.abs(deltaY) < SWIPE_DISTANCE_THRESHOLD_PX || Math.abs(velocityY) < SWIPE_VELOCITY_THRESHOLD_PX) {
                    return false;
                } else if (deltaY < 0) {
                    return onGestureListener.onGesture(Gesture.SWIPE_UP);
                } else {
                    return onGestureListener.onGesture(Gesture.SWIPE_DOWN);
                }
            } else {
                if (Math.abs(deltaX) < SWIPE_DISTANCE_THRESHOLD_PX || Math.abs(velocityX) < SWIPE_VELOCITY_THRESHOLD_PX) {
                    return false;
                } else if (deltaX < 0) {
                    return onGestureListener.onGesture(Gesture.SWIPE_FORWARD);
                } else {
                    return onGestureListener.onGesture(Gesture.SWIPE_BACKWARD);
                }
            }
        }
}
