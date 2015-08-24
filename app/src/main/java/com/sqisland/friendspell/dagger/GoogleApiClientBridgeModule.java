package com.sqisland.friendspell.dagger;

import com.sqisland.friendspell.api.GoogleApiClientBridge;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GoogleApiClientBridgeModule {
  @Provides
  @Singleton
  GoogleApiClientBridge provideGoogleApiClientBridge() {
    return new GoogleApiClientBridge();
  }
}