package com.sqisland.friendspell.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.Locale;

public abstract class ViewUtil {
  public static CharSequence getColoredWord(int[] colors, String word) {
    SpannableString spannableString = new SpannableString(word);
    for (int i = 0; i < word.length(); ++i) {
      setLetterColor(spannableString, i, colors[i]);
    }
    return spannableString;
  }

  public static void setDrawable(TextView textView, Drawable drawable, int position) {
    Drawable[] drawables = textView.getCompoundDrawables();
    textView.setCompoundDrawablesWithIntrinsicBounds(
        (position == 0) ? drawable : drawables[0],
        (position == 1) ? drawable : drawables[1],
        (position == 2) ? drawable : drawables[2],
        (position == 3) ? drawable : drawables[3]);
  }

  public static int getWordImageResId(Context context, String word) {
    return context.getResources().getIdentifier(
        word.toLowerCase(Locale.ENGLISH), "raw", context.getPackageName());
  }

  private static void setLetterColor(SpannableString string, int position, int color) {
    string.setSpan(
        new ForegroundColorSpan(color),
        position, position + 1, 0);
  }
}
