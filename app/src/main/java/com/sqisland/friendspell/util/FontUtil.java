package com.sqisland.friendspell.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.SimpleArrayMap;

public abstract class FontUtil {
  private static SimpleArrayMap<String, Typeface> fonts = new SimpleArrayMap<String, Typeface>();

  public static Typeface load(Context context, String path) {
    if (fonts.containsKey(path)) {
      return fonts.get(path);
    }
    Typeface font = Typeface.createFromAsset(context.getAssets(), path);
    fonts.put(path, font);
    return font;
  }
}