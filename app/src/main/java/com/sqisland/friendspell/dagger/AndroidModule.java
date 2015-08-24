package com.sqisland.friendspell.dagger;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {
  private final Application application;

  public AndroidModule(Application application) {
    this.application = application;
  }

  @Provides
  @Singleton
  Context provideContext() {
    return application;
  }
}