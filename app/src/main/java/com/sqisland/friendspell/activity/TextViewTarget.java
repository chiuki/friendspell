package com.sqisland.friendspell.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.sqisland.friendspell.util.ViewUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class TextViewTarget implements Target {
  private final TextView textView;

  public TextViewTarget(TextView textView) {
    this.textView = textView;
  }

  @Override
  public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
    BitmapDrawable thumbnail = new BitmapDrawable(textView.getContext().getResources(), bitmap);
    ViewUtil.setDrawable(textView, thumbnail, 0);
  }
  @Override
  public void onBitmapFailed(Drawable errorDrawable) {
    textView.setCompoundDrawablesWithIntrinsicBounds(errorDrawable, null, null, null);
  }
  @Override
  public void onPrepareLoad(Drawable placeHolderDrawable) {
    textView.setCompoundDrawablesWithIntrinsicBounds(placeHolderDrawable, null, null, null);
  }
}