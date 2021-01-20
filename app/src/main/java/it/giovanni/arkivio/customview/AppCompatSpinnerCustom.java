package it.giovanni.arkivio.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatSpinner;

public class AppCompatSpinnerCustom extends AppCompatSpinner {

    private static final String TAG = AppCompatSpinnerCustom.class.getSimpleName();
    private OnSpinnerEventsListener listener;
    private boolean openInitiated = false;

    public AppCompatSpinnerCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AppCompatSpinnerCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppCompatSpinnerCustom(Context context) {
        super(context);
    }

    public void setSpinnerEventsListener(OnSpinnerEventsListener onSpinnerEventsListener) {
        listener = onSpinnerEventsListener;
    }

    @Override
    public boolean performClick() {
        openInitiated = true;
        if (listener != null)
            listener.onSpinnerOpened(this);
        return super.performClick();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasBeenOpened() && hasWindowFocus) {
            Log.i(TAG, "Spinner Chiuso");
            performClosedEvent();
        } else
            Log.i(TAG, "Spinner Aperto");
    }

    public boolean hasBeenOpened() {
        return openInitiated;
    }

    public void performClosedEvent() {
        openInitiated = false;
        if (listener != null)
            listener.onSpinnerClosed(this);
    }

    public interface OnSpinnerEventsListener {
        void onSpinnerOpened(AppCompatSpinner spin);
        void onSpinnerClosed(AppCompatSpinner spin);
    }
}