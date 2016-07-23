package com.sqisland.friendspell.util;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnitRunner;

import com.sqisland.friendspell.MockFriendSpellApplication;
import com.sqisland.friendspell.dagger.MockDatabaseApiModule;

public class MockTestRunner extends AndroidJUnitRunner {
  @Override
  public void finish(int resultCode, Bundle results) {
    InstrumentationRegistry.getInstrumentation().getTargetContext().deleteDatabase
        (MockDatabaseApiModule.DATABASE_NAME);
    super.finish(resultCode, results);
  }

  @Override
  public Application newApplication(ClassLoader cl, String className, Context context)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    return super.newApplication(cl, MockFriendSpellApplication.class.getName(), context);
  }
}