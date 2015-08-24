package com.sqisland.friendspell.dagger;

import com.sqisland.friendspell.activity.BaseTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    MockDatabaseApiModule.class,
    MockGoogleApiClientBridgeModule.class,
    MockSharedPreferencesModule.class
})
public interface TestComponent extends FriendSpellComponent {
  void inject(BaseTest test);
}