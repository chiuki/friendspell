package com.sqisland.friendspell.activity;

import android.app.Instrumentation;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import com.sqisland.friendspell.FriendSpellApplication;
import com.sqisland.friendspell.api.GoogleApiClientBridge;
import com.sqisland.friendspell.dagger.TestComponent;
import com.sqisland.friendspell.storage.DatabaseApi;

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
    TestComponent component = (TestComponent) app.component();
    component.inject(this);

    Mockito.reset(googleApiClientBridge);

    databaseApi.clear();

    SharedPreferences.Editor editor = pref.edit();
    editor.clear();
    editor.apply();
  }
}