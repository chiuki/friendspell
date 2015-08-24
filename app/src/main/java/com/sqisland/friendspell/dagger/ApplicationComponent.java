package com.sqisland.friendspell.dagger;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    AndroidModule.class,
    DatabaseApiModule.class,
    GoogleApiClientBridgeModule.class,
    SharedPreferencesModule.class
})
public interface ApplicationComponent extends FriendSpellComponent {
}