package it.giovanni.arkivio.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class FontCache {

    private static final String FOLDER_FONTS = "fonts/";
    public static final String NORMAL_FONT = "fira_regular.ttf";
    public static final String ITALIC_FONT = "fira_light.ttf";
    public static final String BOLD_FONT = "fira_medium.ttf";

    private static final Map<String, Typeface> fontMap = new HashMap<>();

    public static Typeface getFont(Context context, String fontName) {
        if (fontMap.containsKey(fontName)) {
            return fontMap.get(fontName);
        } else {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), FOLDER_FONTS + fontName);
            fontMap.put(fontName, typeface);
            return typeface;
        }
    }
}