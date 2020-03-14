package it.giovanni.kotlin.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class FontCache {

    private static final boolean ENABLE_OLD_VERSION_CONTROL = false;
    private static final String FOLDER_FONTS = "fonts/";
    public static final String NORMAL_FONT = "fira_sans_regular.ttf";
    public static final String ITALIC_FONT = "fira_sans_light.ttf";
    public static final String BOLD_FONT = "fira_sans_medium.ttf";

    private static Map<String, Typeface> fontMap = new HashMap<>();

    @SuppressLint("ObsoleteSdkInt")
    public static Typeface getFont(Context context, String fontName) {

        if (ENABLE_OLD_VERSION_CONTROL && android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.KITKAT) {
            if (fontName.equals(ITALIC_FONT))
                fontName = NORMAL_FONT;
            else if (fontName.equals(NORMAL_FONT))
                fontName = BOLD_FONT;
        }
        if (fontMap.containsKey(fontName)) {
            return fontMap.get(fontName);
        } else {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), FOLDER_FONTS + fontName);
            fontMap.put(fontName, typeface);
            return typeface;
        }
    }
}