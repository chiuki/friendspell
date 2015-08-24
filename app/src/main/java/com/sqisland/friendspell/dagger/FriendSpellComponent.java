package com.sqisland.friendspell.dagger;

import com.sqisland.friendspell.activity.BaseNearbyActivity;
import com.sqisland.friendspell.activity.MainActivity;
import com.sqisland.friendspell.activity.PeopleActivity;
import com.sqisland.friendspell.activity.SpellActivity;
import com.sqisland.friendspell.activity.WordSetActivity;

public interface FriendSpellComponent {
  void inject(MainActivity activity);
  void inject(WordSetActivity activity);
  void inject(SpellActivity activity);
  void inject(BaseNearbyActivity activity);
  void inject(PeopleActivity activity);
}