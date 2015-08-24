package com.sqisland.friendspell.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.graphics.drawable.VectorDrawableCompat;

import com.sqisland.friendspell.R;

public abstract class ImageUtil {
  public static Bitmap createWordImageBitmap(Resources res, int resId) {
    int size = res.getDimensionPixelSize(R.dimen.word_image_size);
    return ImageUtil.createBitmapFromVector(res, resId, size);
  }

  public static Bitmap createWordThumbnailBitmap(Resources res, int resId) {
    int size = res.getDimensionPixelSize(R.dimen.word_image_thumbnail_size);
    return ImageUtil.createBitmapFromVector(res, resId, size);
  }

  public static Bitmap createBitmapFromVector(Resources res, int resId, int size) {
    float viewportSize = res.getDimension(R.dimen.vector_drawable_viewport_size);
    return createBitmapFromVector(res, resId, size, size, viewportSize, viewportSize);
  }

  public static Bitmap createBitmapFromVector(
      Resources res, int resId, int width, int height, float viewportWidth, float viewportHeight) {
    VectorDrawableCompat drawable = VectorDrawableCompat.createFromResource(
        res, resId);

    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);

    float scaleX = width / viewportWidth;
    float scaleY = height / viewportHeight;
    canvas.scale(scaleX, scaleY);
    drawable.draw(canvas);
    canvas.scale(1/scaleX, 1/scaleY);

    return bitmap;
  }
}
