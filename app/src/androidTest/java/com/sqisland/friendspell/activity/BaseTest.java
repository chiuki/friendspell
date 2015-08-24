package com.sqisland.friendspell.activity;

import android.app.Instrumentation;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import com.sqisland.friendspell.FriendSpellApplication;
import com.sqisland.friendspell.api.GoogleApiClientBridge;
import com.sqisland.friendspell.dagger.DaggerTestComponent;
import com.sqisland.friendspell.dagger.MockDatabaseApiModule;
import com.sqisland.friendspell.dagger.MockGoogleApiClientBridgeModule;
import com.sqisland.friendspell.dagger.TestComponent;
import com.sqisland.friendspell.storage.DatabaseApi;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import javax.inject.Inject;

public class BaseTest {
  @Inject
  protected GoogleApiClientBridge googleApiClientBridge;

  @Inject
  protected DatabaseApi databaseApi;

  @Inject
  protected SharedPreferences pref;

  @Before
  public void setUp() {
    Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
    FriendSpellApplication app
        = (FriendSpellApplication) instrumentation.getTargetContext().getApplicationContext();
    TestComponent component = DaggerTestComponent.builder()
        .mockDatabaseApiModule(new MockDatabaseApiModule(app))
        .mockGoogleApiClientBridgeModule(new MockGoogleApiClientBridgeModule())
        .build();
    app.setComponent(component);
    component.inject(this);

    Mockito.reset(googleApiClientBridge);
  }

  @After
  public void tearDown() {
    InstrumentationRegistry.getInstrumentation().getTargetContext().deleteDatabase
        (MockDatabaseApiModule.DATABASE_NAME);
  }
}