package it.giovanni.arkivio.utils.typekit;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class Typekit {

    public enum Style {
        Normal,
        Bold,
        Italic,
        BoldItalic
    }

    private static Typekit mInstance = new Typekit();

    public static Typekit getInstance() {
        return mInstance;
    }

    private Map<String, Typeface> mFonts = new HashMap<>();

    private Typekit() {}

    public Typeface get(Style style) {
        return get(style.toString());
    }

    public Typeface get(String key) {
        return mFonts.get(key);
    }

    public Typekit addNormal(Typeface typeface) {
        mFonts.put(Style.Normal.toString(), typeface);
        return this;
    }

    public Typekit addItalic(Typeface typeface) {
        mFonts.put(Style.Italic.toString(), typeface);
        return this;
    }

    public Typekit addBold(Typeface typeface) {
        mFonts.put(Style.Bold.toString(), typeface);
        return this;
    }

    public Typekit addBoldItalic(Typeface typeface) {
        mFonts.put(Style.BoldItalic.toString(), typeface);
        return this;
    }

    public Typekit add(String key, Typeface typeface) {
        mFonts.put(key, typeface);
        return this;
    }

    public static Typeface createFromAsset(Context context, String path) {
        return Typeface.createFromAsset(context.getAssets(), path);
    }
}