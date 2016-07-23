package com.sqisland.friendspell.activity;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sqisland.friendspell.R;
import com.sqisland.friendspell.storage.NearbyPerson;
import com.sqisland.friendspell.storage.SpelledWord;
import com.sqisland.friendspell.storage.WordSetItem;
import com.sqisland.friendspell.util.Constants;
import com.sqisland.friendspell.util.TestUtil;
import com.sqisland.friendspell.util.WordUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class SpellActivityTest extends BaseTest {
  @Rule
  public ActivityTestRule<SpellActivity> activityRule = new ActivityTestRule<>(
      SpellActivity.class,
      true,     // initialTouchMode
      false);   // launchActivity. False so we set up before activity launch

  @Test
  public void nearbyAddTip() {
    Intent intent = new Intent();
    intent.putExtra(Constants.KEY_NAME, "newyork");
    intent.putExtra(Constants.KEY_WORD, "PIZZA");
    activityRule.launchActivity(intent);

    TestUtil.matchToolbarTitle("PIZZA");
    onView(withId(R.id.nearby_tip))
        .check(matches(isDisplayed()));
  }

  @Test
  public void nearbyAddViaImage() {
    TestUtil.setupGoogleApiClientBridgeForNearby(googleApiClientBridge);

    Intent intent = new Intent();
    intent.putExtra(Constants.KEY_NAME, "zodiac");
    intent.putExtra(Constants.KEY_WORD, "TIGER");
    activityRule.launchActivity(intent);

    TestUtil.matchToolbarTitle("TIGER");

    onView(withId(R.id.word_image))
        .perform(click());
    TestUtil.matchToolbarTitle(R.string.title_nearby);
  }

  @Test
  public void nearbyAddViaWord() {
    TestUtil.setupGoogleApiClientBridgeForNearby(googleApiClientBridge);

    Intent intent = new Intent();
    intent.putExtra(Constants.KEY_NAME, "zodiac");
    intent.putExtra(Constants.KEY_WORD, "SHEEP");
    activityRule.launchActivity(intent);

    TestUtil.matchToolbarTitle("SHEEP");

    onView(withId(R.id.word_image))
        .perform(click());
    TestUtil.matchToolbarTitle(R.string.title_nearby);
  }

  @Test
  public void people() {
    Intent intent = new Intent();
    intent.putExtra(Constants.KEY_NAME, "zodiac");
    intent.putExtra(Constants.KEY_WORD, "SNAKE");
    activityRule.launchActivity(intent);

    TestUtil.matchToolbarTitle("SNAKE");

    onView(withId(R.id.action_people))
        .perform(click());
    TestUtil.matchToolbarTitle(R.string.title_people);
  }

  @Test
  public void multiSource() {
    TestUtil.setupGoogleApiClientBridgeForNearby(googleApiClientBridge,
        TestUtil.DIANNE, TestUtil.KATHERINE, TestUtil.CHET, TestUtil.COLT, TestUtil.ADAM, TestUtil.RETO);

    Intent intent = new Intent();
    intent.putExtra(Constants.KEY_NAME, "newyork");
    intent.putExtra(Constants.KEY_WORD, "PIZZA");
    activityRule.launchActivity(intent);

    TestUtil.matchToolbarTitle("PIZZA");

    TestUtil.verifyViewGroupWordColors(
        R.id.word,
        WordUtil.COLOR_MISSING,
        WordUtil.COLOR_MISSING,
        WordUtil.COLOR_MISSING,
        WordUtil.COLOR_MISSING,
        WordUtil.COLOR_MISSING);
    TestUtil.verifyViewGroupWordSources(R.id.sources, 1, 1, 1, 1, 1);

    onView(withId(R.id.nearby_tip))
        .check(matches(isDisplayed()));

    TestUtil.verifyMultiSourceTipHidden();

    onView(withId(R.id.action_nearby_add))
        .perform(click());
    onView(allOf(isDescendantOfA(withId(R.id.nearby_header)), withId(R.id.me)))
        .check(matches(withText(TestUtil.ME_DISPLAY_NAME)));

    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.ADAM.displayName)));
    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(1)
        .check(matches(withText(TestUtil.CHET.displayName)));
    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(2)
        .check(matches(withText(TestUtil.COLT.displayName)));
    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(3)
        .check(matches(withText(TestUtil.DIANNE.displayName)));
    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(4)
        .check(matches(withText(TestUtil.KATHERINE.displayName)));

    Espresso.pressBack();

    TestUtil.verifyViewGroupWordColors(
        R.id.word,
        WordUtil.COLOR_COMBO,
        WordUtil.COLOR_MISSING,
        WordUtil.COLOR_MISSING,
        WordUtil.COLOR_MISSING,
        WordUtil.COLOR_ONE);
    TestUtil.verifyViewGroupWordSources(R.id.sources, 4, 4, 1, 1, 1);

    onView(withId(R.id.nearby_tip))
        .check(matches(not(isDisplayed())));

    TestUtil.verifyMultiSourceTipShown();
  }

  @Test
  public void loadSpelledWord() {
    WordSetItem item = new WordSetItem();
    item.wordset = "zodiac";
    item.word = "OX";
    item.spelledWord = new SpelledWord();
    item.spelledWord.letters = Arrays.asList(
        TestUtil.createSpelledLetter("O",
            TestUtil.ADAM, TestUtil.CHET, TestUtil.COLT, TestUtil.TOR),
        TestUtil.createSpelledLetter("X", TestUtil.XAVIER)
    );
    databaseApi.saveWord(item);

    Intent intent = new Intent();
    intent.putExtra(Constants.KEY_NAME, item.wordset);
    intent.putExtra(Constants.KEY_WORD, item.word);
    activityRule.launchActivity(intent);

    TestUtil.matchToolbarTitle(item.word);

    TestUtil.verifyViewGroupWordColors(
        R.id.word,
        WordUtil.COLOR_COMBO,
        WordUtil.COLOR_ONE);
    TestUtil.verifyViewGroupWordSources(R.id.sources, 4, 1);

    onView(withId(R.id.spell_button))
        .check(matches(not(isDisplayed())));
  }
}