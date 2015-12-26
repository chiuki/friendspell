package com.sqisland.friendspell;

import com.sqisland.friendspell.dagger.DaggerTestComponent;
import com.sqisland.friendspell.dagger.FriendSpellComponent;
import com.sqisland.friendspell.dagger.MockDatabaseApiModule;
import com.sqisland.friendspell.dagger.MockGoogleApiClientBridgeModule;

public class MockFriendSpellApplication extends FriendSpellApplication {
  @Override
  protected FriendSpellComponent createComponent() {
    return DaggerTestComponent.builder()
            .mockDatabaseApiModule(new MockDatabaseApiModule(this))
            .mockGoogleApiClientBridgeModule(new MockGoogleApiClientBridgeModule())
            .build();
  }
}