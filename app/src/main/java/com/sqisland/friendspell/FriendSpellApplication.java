package com.sqisland.friendspell;

import android.app.Application;
import android.util.Log;

import com.sqisland.friendspell.dagger.AndroidModule;
import com.sqisland.friendspell.dagger.DaggerApplicationComponent;
import com.sqisland.friendspell.dagger.FriendSpellComponent;
import com.sqisland.friendspell.dagger.GoogleApiClientBridgeModule;

import timber.log.Timber;

public class FriendSpellApplication extends Application {
  private FriendSpellComponent component = null;

  @Override
  public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      Timber.plant(new ErrorTree());
    }

    if (component == null) {
      component = DaggerApplicationComponent.builder()
          .androidModule(new AndroidModule(this))
          .googleApiClientBridgeModule(new GoogleApiClientBridgeModule())
          .build();
    }
  }

  public void setComponent(FriendSpellComponent component) {
    this.component = component;
  }

  public FriendSpellComponent component() {
    return component;
  }

  private static class ErrorTree extends Timber.Tree {
    private static final String TAG = "FriendSpell";

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
      if (priority == Log.ERROR) {
        Log.e(TAG, message, t);
      }
    }
  }
}