package com.sqisland.friendspell.activity;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sqisland.friendspell.R;
import com.sqisland.friendspell.api.GoogleApiClientBridge;
import com.sqisland.friendspell.storage.LetterSource;
import com.sqisland.friendspell.storage.NearbyPerson;
import com.sqisland.friendspell.util.TestUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.sqisland.friendspell.util.CustomMatchers.withCompoundDrawable;
import static com.sqisland.friendspell.util.CustomMatchers.withoutCompoundDrawable;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class PeopleActivityTest extends BaseTest {
  @Rule
  public ActivityTestRule<PeopleActivity> activityRule = new ActivityTestRule<>(
      PeopleActivity.class,
      true,     // initialTouchMode
      false);   // launchActivity. False so we set up before activity launch

  @Test
  public void tagTwice() {
    TestUtil.setupGoogleApiClientBridgeForNearby(googleApiClientBridge, TestUtil.COLT);

    activityRule.launchActivity(null);

    // Go to NearbyActivity
    onView(withId(R.id.nearby_tip))
        .check(matches(isDisplayed()));
    onView(withId(R.id.action_nearby_add))
        .perform(click());

    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.COLT.displayName)))
        .check(matches(withCompoundDrawable(0)))
        .check(matches(withCompoundDrawable(2, R.drawable.ic_newly_tagged)));

    // Back to PeopleActivity
    Espresso.pressBack();
    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.COLT.displayName)))
        .check(matches(withCompoundDrawable(0)))
        .check(matches(withoutCompoundDrawable(2)));

    // Go to NearbyActivity again
    onView(withId(R.id.nearby_tip))
        .check(matches(not(isDisplayed())));
    onView(withId(R.id.action_nearby_add))
        .perform(click());

    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.COLT.displayName)))
        .check(matches(withCompoundDrawable(0)))
        .check(matches(withoutCompoundDrawable(2)));

    Mockito.verify(googleApiClientBridge, Mockito.times(1)).getProfileImage(TestUtil.COLT.googlePlusId);
    Mockito.verify(googleApiClientBridge, Mockito.times(1)).getProfileImages(
        Mockito.anyString(),
        Mockito.anyList(),
        Mockito.any(GoogleApiClientBridge.GetProfileImagesCallback.class));
  }

  @Test
  public void tagNewAndExisting() {
    databaseApi.saveLetter(new LetterSource(TestUtil.KATHERINE));

    TestUtil.setupGoogleApiClientBridgeForNearby(
        googleApiClientBridge, TestUtil.KATHERINE, TestUtil.CHET);

    activityRule.launchActivity(null);

    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.KATHERINE.displayName)))
        .check(matches(withCompoundDrawable(0)));

    // Go to NearbyActivity
    onView(withId(R.id.nearby_tip))
        .check(matches(not(isDisplayed())));
    onView(withId(R.id.action_nearby_add))
        .perform(click());

    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.CHET.displayName)))
        .check(matches(withCompoundDrawable(0)))
        .check(matches(withCompoundDrawable(2, R.drawable.ic_newly_tagged)));
    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(1)
        .check(matches(withText(TestUtil.KATHERINE.displayName)))
        .check(matches(withCompoundDrawable(0)))
        .check(matches(withoutCompoundDrawable(2)));

    // Back to PeopleActivity
    TestUtil.clickActionBarHomeButton();
    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.CHET.displayName)))
        .check(matches(withCompoundDrawable(0)))
        .check(matches(withoutCompoundDrawable(2)));
    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(1)
        .check(matches(withText(TestUtil.KATHERINE.displayName)))
        .check(matches(withCompoundDrawable(0)))
        .check(matches(withoutCompoundDrawable(2)));

    Mockito.verify(googleApiClientBridge, Mockito.times(2)).getProfileImage(TestUtil.KATHERINE.googlePlusId);
    Mockito.verify(googleApiClientBridge, Mockito.times(1)).getProfileImage(TestUtil.CHET.googlePlusId);
    Mockito.verify(googleApiClientBridge, Mockito.times(2)).getProfileImages(
        Mockito.anyString(),
        Mockito.anyList(),
        Mockito.any(GoogleApiClientBridge.GetProfileImagesCallback.class));
  }
}