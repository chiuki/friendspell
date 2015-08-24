package com.sqisland.friendspell.activity;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sqisland.friendspell.R;
import com.sqisland.friendspell.storage.LetterSource;
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
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.sqisland.friendspell.util.CustomMatchers.withColors;
import static com.sqisland.friendspell.util.CustomMatchers.withWordImage;
import static com.sqisland.friendspell.util.CustomMatchers.withWordThumbnail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class WordSetActivityTest extends BaseTest {
  private static final String[] WORDS = { "APPLE", "PIZZA", "TAXI", "LIBERTY" };

  @Rule
  public ActivityTestRule<WordSetActivity> activityRule = new ActivityTestRule<>(
      WordSetActivity.class,
      true,     // initialTouchMode
      false);   // launchActivity. False so we set up before activity launch

  @Test
  public void peopleNearbyAddTip() {
    Intent intent = new Intent();
    intent.putExtra(Constants.KEY_NAME, "newyork");
    activityRule.launchActivity(intent);

    TestUtil.matchToolbarTitle("New York");

    onView(withId(R.id.action_people))
        .perform(click());

    TestUtil.matchToolbarTitle(R.string.title_people);
    onView(withId(R.id.nearby_tip))
        .check(matches(isDisplayed()));
  }

  @Test
  public void addAdam() {
    TestUtil.setupGoogleApiClientBridgeForNearby(googleApiClientBridge, TestUtil.ADAM);

    Intent intent = new Intent();
    intent.putExtra(Constants.KEY_NAME, "newyork");
    activityRule.launchActivity(intent);

    TestUtil.matchToolbarTitle("New York");

    for (String word : WORDS) {
      verifyMissingColor(word);
    }

    onView(withId(R.id.action_nearby_add))
        .perform(click());
    onView(allOf(isDescendantOfA(withId(R.id.nearby_header)), withId(R.id.me)))
        .check(matches(withText(TestUtil.ME_DISPLAY_NAME)));

    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.ADAM.displayName)));

    TestUtil.clickActionBarHomeButton();

    onData(is("APPLE"))
        .check(matches(withColors(
            WordUtil.COLOR_ONE,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING
        )));
    onData(is("PIZZA"))
        .check(matches(withColors(
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_ONE
        )));
    onData(is("TAXI"))
        .check(matches(withColors(
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_ONE,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING
        )));
    onData(is("LIBERTY"))
        .check(matches(withColors(
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING
        )));
  }

  @Test
  public void multiSourceOnePersonFirstTime() {
    multiSourceOnePerson(true);
  }

  @Test
  public void multiSourceOnePersonReturning() {
    multiSourceOnePerson(false);
  }

  private void multiSourceOnePerson(boolean first) {
    TestUtil.setupGoogleApiClientBridgeForNearby(googleApiClientBridge, TestUtil.KATHERINE);

    if (!first) {
      TestUtil.setSeenMultiSource(pref);
    }

    Intent intent = new Intent();
    intent.putExtra(Constants.KEY_NAME, "newyork");
    activityRule.launchActivity(intent);

    TestUtil.matchToolbarTitle("New York");

    // Go to SpellActivity with APPLE
    onData(is("APPLE"))
        .perform(click());

    TestUtil.matchToolbarTitle("APPLE");

    // Go to NearbyActivity
    onView(withId(R.id.nearby_tip))
        .check(matches(isDisplayed()));

    TestUtil.verifyMultiSourceTipHidden();

    onView(withId(R.id.action_nearby_add))
        .perform(click());
    onView(allOf(isDescendantOfA(withId(R.id.nearby_header)), withId(R.id.me)))
        .check(matches(withText(TestUtil.ME_DISPLAY_NAME)));

    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.KATHERINE.displayName)));

    // Back to SpellActivity
    Espresso.pressBack();

    onView(withId(R.id.nearby_tip))
        .check(matches(not(isDisplayed())));

    if (first) {
      TestUtil.verifyMultiSourceTipShown();
    } else {
      TestUtil.verifyMultiSourceTipHidden();
    }

    // Back to WordSetActivity
    TestUtil.clickActionBarHomeButton();

    // Go to SpellActivity with a different word
    onData(is("LIBERTY"))
        .perform(click());
    TestUtil.verifyMultiSourceTipHidden();
  }

  @Test
  public void spell() {
    TestUtil.setSeenMultiSource(pref);
    for (NearbyPerson person : new NearbyPerson[] {
        TestUtil.TOR, TestUtil.ADAM, TestUtil.DIANNE, TestUtil.KATHERINE,
        TestUtil.COLT, TestUtil.CHET, TestUtil.RETO
    }) {
      databaseApi.saveLetter(new LetterSource(person));
    }
    TestUtil.setupGoogleApiClientBridgeForNearby(googleApiClientBridge, TestUtil.XAVIER);

    Intent intent = new Intent();
    intent.putExtra(Constants.KEY_NAME, "newyork");
    activityRule.launchActivity(intent);

    TestUtil.matchToolbarTitle("New York");

    onData(is("APPLE"))
        .check(matches(withColors(
            WordUtil.COLOR_ONE,
            WordUtil.COLOR_COMBO,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING
        )))
        .check(matches(withWordThumbnail(R.raw.word_image_placeholder)));
    onData(is("PIZZA"))
        .check(matches(withColors(
            WordUtil.COLOR_COMBO,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_ONE
        )))
        .check(matches(withWordThumbnail(R.raw.word_image_placeholder)));
    onData(is("TAXI"))
        .check(matches(withColors(
            WordUtil.COLOR_ONE,
            WordUtil.COLOR_ONE,
            WordUtil.COLOR_COMBO,
            WordUtil.COLOR_MISSING
        )))
        .check(matches(withWordThumbnail(R.raw.word_image_placeholder)));
    onData(is("LIBERTY"))
        .check(matches(withColors(
            WordUtil.COLOR_COMBO,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_ONE,
            WordUtil.COLOR_ONE,
            WordUtil.COLOR_MISSING
        )))
        .check(matches(withWordThumbnail(R.raw.word_image_placeholder)));

    // Go to SpellActivity with TAXI
    onData(is("TAXI"))
        .perform(click());

    onView(withId(R.id.word_image))
        .check(matches(withWordImage(R.raw.word_image_placeholder)));

    TestUtil.verifyViewGroupWordColors(
        R.id.word,
        WordUtil.COLOR_ONE,
        WordUtil.COLOR_ONE,
        WordUtil.COLOR_COMBO,
        WordUtil.COLOR_MISSING);
    TestUtil.verifyViewGroupWordSources(R.id.sources, 1, 1, 4, 4);

    onView(withId(R.id.spell_button))
        .check(matches(not(isEnabled())));

    onView(withId(R.id.nearby_tip))
        .check(matches(not(isDisplayed())));

    TestUtil.verifyMultiSourceTipHidden();

    // Go to NearbyActivity
    onView(withId(R.id.action_nearby_add))
        .perform(click());
    onView(allOf(isDescendantOfA(withId(R.id.nearby_header)), withId(R.id.me)))
        .check(matches(withText(TestUtil.ME_DISPLAY_NAME)));

    onData(is(instanceOf(NearbyPerson.class)))
        .atPosition(0)
        .check(matches(withText(TestUtil.XAVIER.displayName)));

    // Back to SpellActivity
    Espresso.pressBack();

    TestUtil.verifyViewGroupWordColors(
        R.id.word,
        WordUtil.COLOR_ONE,
        WordUtil.COLOR_ONE,
        WordUtil.COLOR_ONE,
        WordUtil.COLOR_COMBO);
    TestUtil.verifyViewGroupWordSources(R.id.sources, 1, 1, 1, 4);

    onView(withId(R.id.word_image))
        .check(matches(withWordImage(R.raw.word_image_placeholder)));

    onView(withId(R.id.spell_button))
        .check(matches(isEnabled()))
        .perform(click())
        .check(matches(not(isDisplayed())));

    onView(withId(R.id.word_image))
        .check(matches(withWordImage(R.raw.taxi)));

    // Back to WordSetActivity
    TestUtil.clickActionBarHomeButton();

    onData(is("APPLE"))
        .check(matches(withColors(
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING
        )))
        .check(matches(withWordThumbnail(R.raw.word_image_placeholder)));
    onData(is("PIZZA"))
        .check(matches(withColors(
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING
        )))
        .check(matches(withWordThumbnail(R.raw.word_image_placeholder)));
    onData(is("TAXI"))
        .check(matches(withColors(
            WordUtil.COLOR_ONE,
            WordUtil.COLOR_ONE,
            WordUtil.COLOR_ONE,
            WordUtil.COLOR_COMBO
        )))
        .check(matches(withWordThumbnail(R.raw.taxi)));
    onData(is("LIBERTY"))
        .check(matches(withColors(
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_ONE,
            WordUtil.COLOR_MISSING,
            WordUtil.COLOR_MISSING
        )))
        .check(matches(withWordThumbnail(R.raw.word_image_placeholder)));
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
    activityRule.launchActivity(intent);

    TestUtil.matchToolbarTitle("Chinese Zodiac");

    onData(equalTo("OX"))
        .check(matches(withColors(
            WordUtil.COLOR_COMBO,
            WordUtil.COLOR_ONE
        )))
        .check(matches(withWordThumbnail(R.raw.ox)));
  }

  private void verifyMissingColor(String word) {
    int[] colors = new int[word.length()];
    for (int i = 0; i < word.length(); ++i) {
      colors[i] = WordUtil.COLOR_MISSING;
    }
    onData(is(word))
        .check(matches(withColors(colors)));
  }
}