package it.giovanni.arkivio.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import it.giovanni.arkivio.R;
import it.giovanni.arkivio.utils.FontCache;

public class TextViewCustom extends AppCompatTextView {

    private static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public TextViewCustom(Context context) {
        super(context);
        applyCustomFont(context, null);
    }

    public TextViewCustom(Context context, Typeface typeface) {
        super(context);
        setTypeface(typeface);
    }

    public TextViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public TextViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTextAppearance(context, defStyle);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            int textStyle = (attrs != null) ? attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", 0) : 0;
            Typeface customFont = selectTypeface(context, textStyle);
            setTypeface(customFont);
            setPaintFlags(getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextViewCustom);
            boolean isUnderline = typedArray.getBoolean(R.styleable.TextViewCustom_underlineText, false);

            if (isUnderline)
                setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            typedArray.recycle();
        }
    }

    public static Typeface selectTypeface(Context context, int textStyle) {
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