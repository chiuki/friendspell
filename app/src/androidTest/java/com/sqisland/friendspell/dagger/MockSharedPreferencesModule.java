package com.sqisland.friendspell.dagger;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MockSharedPreferencesModule {
  @Provides
  @Singleton
  SharedPreferences provideSharedPreferences() {
    return new FakeSharedPreferences();
  }
}