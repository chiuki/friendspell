package com.sqisland.friendspell.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = AndroidModule.class)
public class SharedPreferencesModule {
  @Provides
  @Singleton
  SharedPreferences provideSharedPreferences(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }
}