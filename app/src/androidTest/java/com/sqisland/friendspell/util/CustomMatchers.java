package com.sqisland.friendspell.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.DrawableRes;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v4.content.ContextCompat;
import android.text.SpannedString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public abstract class CustomMatchers {
  public static Matcher<View> withColors(final int... colors) {
    return new BoundedMatcher<View, TextView>(TextView.class) {
      @Override public boolean matchesSafely(TextView textView) {
        SpannedString text = (SpannedString) textView.getText();
        ForegroundColorSpan[] spans = text.getSpans(0, text.length(), ForegroundColorSpan.class);
        if (spans.length != colors.length) {
          return false;
        }
        for (int i = 0; i < colors.length; ++i) {
          if (spans[i].getForegroundColor() != colors[i]) {
            return false;
          }
        }
        return true;
      }
      @Override public void describeTo(Description description) {
        description.appendText("has colors:");
        for (int color : colors) {
          description.appendText(" " + getHexColor(color));
        }
      }
    };
  }

  public static Matcher<View> withTextColor(final int color) {
    return new BoundedMatcher<View, TextView>(TextView.class) {
      @Override public boolean matchesSafely(TextView textView) {
        return (textView.getCurrentTextColor() == color);
      }
      @Override public void describeTo(Description description) {
        description.appendText("has colors: " + getHexColor(color));
      }
    };
  }

  public static Matcher<View> atPosition(final int position) {
    return new TypeSafeMatcher<View>() {
      @Override public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        if (!(parent instanceof ViewGroup)) {
          return false;
        }
        ViewGroup parentGroup = (ViewGroup) parent;
        if (parentGroup.getChildCount() < position) {
          return false;
        }
        return (view == parentGroup.getChildAt(position));
      }
      @Override public void describeTo(Description description) {
        description.appendText("at position: " + position);
      }
    };
  }

  public static Matcher<View> hasChildCount(final int count) {
    return new BoundedMatcher<View, ViewGroup>(ViewGroup.class) {
      @Override public boolean matchesSafely(ViewGroup viewGroup) {
        return (viewGroup.getChildCount() == count);
      }
      @Override public void describeTo(Description description) {
        description.appendText("has child count: " + count);
      }
    };
  }

  public static Matcher<View> withCompoundDrawable(
      final int position, @DrawableRes final int resId) {
    return new BoundedMatcher<View, TextView>(TextView.class) {
      @Override public void describeTo(Description description) {
        description.appendText(
            "has compound drawable resource " + resId + " at position " + position);
      }
      @Override public boolean matchesSafely(TextView textView) {
        Drawable drawables[] = textView.getCompoundDrawables();
        return sameBitmap(textView.getContext(), drawables[position], resId);
      }
    };
  }

  public static Matcher<View> withCompoundDrawable(final int position) {
    return new BoundedMatcher<View, TextView>(TextView.class) {
      @Override public void describeTo(Description description) {
        description.appendText(
            "has compound drawable resource at position " + position);
      }
      @Override public boolean matchesSafely(TextView textView) {
        Drawable drawables[] = textView.getCompoundDrawables();
        return (drawables[position] != null);
      }
    };
  }

  public static Matcher<View> withoutCompoundDrawable(final int position) {
    return new BoundedMatcher<View, TextView>(TextView.class) {
      @Override public void describeTo(Description description) {
        description.appendText(
            "does not have compound drawable at position " + position);
      }
      @Override public boolean matchesSafely(TextView textView) {
        Drawable drawables[] = textView.getCompoundDrawables();
        return (drawables[position] == null);
      }
    };
  }

  private static String getHexColor(int color) {
    return String.format("#%06X", 0xFFFFFF & color);
  }

  private static boolean sameBitmap(Context context, Drawable drawable, int resourceId) {
    if (resourceId == 0) {
      return (drawable == null);
    }
    Drawable otherDrawable = ContextCompat.getDrawable(context, resourceId);
    if (drawable == null || otherDrawable == null) {
      return false;
    }
    if (drawable instanceof StateListDrawable) {
      drawable = drawable.getCurrent();
    }
    if (otherDrawable instanceof StateListDrawable) {
      otherDrawable = otherDrawable.getCurrent();
    }
    if (drawable instanceof BitmapDrawable) {
      Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
      Bitmap otherBitmap = ((BitmapDrawable) otherDrawable).getBitmap();
      return bitmap.sameAs(otherBitmap);
    }
    return false;
  }
}