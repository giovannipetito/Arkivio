package it.giovanni.kotlin.customview;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import it.giovanni.kotlin.utils.FontCache;

public class ButtonCustom extends AppCompatButton {

    private static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public ButtonCustom(Context context) {
        super(context);
        applyCustomFont(context, null);
    }

    public ButtonCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public ButtonCustom(Context context, Typeface typeface) {
        super(context);
        setTypeface(typeface);
    }

    public ButtonCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            int textStyle = (attrs != null) ? attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", 0) : 0;
            Typeface customFont = selectTypeface(context, textStyle);
            setTypeface(customFont);
            setPaintFlags(getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        }
    }

    private Typeface selectTypeface(Context context, int textStyle) {
        /*
         * information about the TextView textStyle: http://developer.android.com/reference/android/R.styleable.html#TextView_textStyle
         */
        switch (textStyle) {
            case 1: // bold
                return FontCache.getFont(context, FontCache.BOLD_FONT);

            case 2: // italic
                return FontCache.getFont(context, FontCache.ITALIC_FONT);

            case 0: // regular
            default:
                return FontCache.getFont(context, FontCache.NORMAL_FONT);
        }
    }
}