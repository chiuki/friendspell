package com.sqisland.friendspell.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sqisland.friendspell.R;
import com.sqisland.friendspell.storage.DatabaseApi;
import com.sqisland.friendspell.storage.LetterSource;
import com.sqisland.friendspell.storage.WordSetItem;
import com.sqisland.friendspell.util.FontUtil;
import com.sqisland.friendspell.util.ImageUtil;
import com.sqisland.friendspell.util.ViewUtil;
import com.sqisland.friendspell.util.WordUtil;

import java.util.List;

public class WordAdapter extends ArrayAdapter<String> {
  private final String name;

  private final Typeface typeface;

  DatabaseApi databaseApi;
  private List<LetterSource> availableLetters;

  public WordAdapter(
      Context context, int resource, String[] objects, DatabaseApi databaseApi, String name) {
    super(context, resource, objects);
    typeface = FontUtil.load(context, "fonts/SyncopateBold.ttf");
    this.databaseApi = databaseApi;
    this.name = name;
    updateLetters();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TextView textView = (TextView) super.getView(position, convertView, parent);
    textView.setTypeface(typeface);
    String word = this.getItem(position);

    WordSetItem item = databaseApi.loadWord(name, word);

    int resId = R.raw.word_image_placeholder;

    if (item == null) {
      if (availableLetters == null) {
        textView.setText(word);
      } else {
        int[] colors = WordUtil.getWordColors(availableLetters, word);
        textView.setText(ViewUtil.getColoredWord(colors, word));
      }
    } else {
      int[] colors = WordUtil.getWordColors(item.spelledWord.letters);
      textView.setText(ViewUtil.getColoredWord(colors, word));
      resId = ViewUtil.getWordImageResId(getContext(), word);
    }

    Resources res = getContext().getResources();
    Bitmap bitmap = ImageUtil.createWordThumbnailBitmap(res, resId);
    Drawable thumbnail = new BitmapDrawable(res, bitmap);
    textView.setCompoundDrawablesWithIntrinsicBounds(thumbnail, null, null, null);

    return textView;
  }

  public void updateLetters() {
    availableLetters = databaseApi.getAvailableLetters();
  }
}