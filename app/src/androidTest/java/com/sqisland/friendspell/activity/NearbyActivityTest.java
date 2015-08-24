package com.sqisland.friendspell.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sqisland.friendspell.R;
import com.sqisland.friendspell.storage.LetterSource;
import com.sqisland.friendspell.storage.NearbyPerson;
import com.sqisland.friendspell.util.TestUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.sqisland.friendspell.util.CustomMatchers.withCompoundDrawable;
import static com.sqisland.friendspell.util.CustomMatchers.withoutCompoundDrawable;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class NearbyActivityTest extends BaseTest {
  @Rule
  public ActivityTestRule<NearbyActivity> activityRule = new ActivityTestRule<>(
      NearbyActivity.class,
      true,     // initialTouchMode
      false);   // launchActivity. False so we set up before activity launch

  @Test
  public void newlyTagged() {
    TestUtil.setupGoogleApiClientBridgeForNearby(googleApiClientBridge, TestUtil.COLT);

    activityRule.launchActivity(null);

    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.COLT.displayName)))
        .check(matches(withCompoundDrawable(0)))
        .check(matches(withCompoundDrawable(2, R.drawable.ic_newly_tagged)));
  }

  @Test
  public void alreadyTagged() {
    databaseApi.saveLetter(new LetterSource(TestUtil.COLT));

    TestUtil.setupGoogleApiClientBridgeForNearby(googleApiClientBridge, TestUtil.COLT);

    activityRule.launchActivity(null);

    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.COLT.displayName)))
        .check(matches(withCompoundDrawable(0)))
        .check(matches(withoutCompoundDrawable(2)));
  }
}