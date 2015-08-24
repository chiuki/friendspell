package com.sqisland.friendspell.dagger;

import android.content.Context;

import com.sqisland.friendspell.storage.DatabaseApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MockDatabaseApiModule {
  public static final String DATABASE_NAME = "test.db";
  private final Context context;

  public MockDatabaseApiModule(Context context) {
    this.context = context;
  }
  @Provides
  @Singleton
  DatabaseApi provideDatabaseApi() {
    return new DatabaseApi(context, DATABASE_NAME);
  }
}