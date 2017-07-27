package com.sqisland.friendspell.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.gson.Gson;
import com.sqisland.friendspell.R;
import com.sqisland.friendspell.api.GoogleApiClientBridge;
import com.sqisland.friendspell.storage.LetterSource;
import com.sqisland.friendspell.storage.NearbyPerson;
import com.sqisland.friendspell.storage.SpelledLetter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static com.sqisland.friendspell.util.CustomMatchers.atPosition;
import static com.sqisland.friendspell.util.CustomMatchers.hasChildCount;
import static com.sqisland.friendspell.util.CustomMatchers.withTextColor;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class TestUtil {
  public static final String ME_DISPLAY_NAME = "Cute Cat";
  public static final NearbyPerson ADAM = new NearbyPerson(
      "107708120842840792570", "A", "Adam Powell",
      "https://lh3.googleusercontent.com/-u6tKuQpTVNA/AAAAAAAAAAI/AAAAAAAAHO4/irJdIXh_XIs/s46-c-k-no/photo.jpg");
  public static final NearbyPerson CHET = new NearbyPerson(
      "104755487586666698979", "C", "Chet Haase",
      "https://lh3.googleusercontent.com/-alRF2kfXilM/AAAAAAAAAAI/AAAAAAAAz5I/dZZpm1WTwE4/s46-c-k-no/photo.jpg");
  public static final NearbyPerson COLT = new NearbyPerson(
      "105062545746290691206", "C", "Colt McAnlis",
      "https://lh3.googleusercontent.com/-NHsYU-OGvnQ/AAAAAAAAAAI/AAAAAAAAH2E/5FBt4OXZH80/s46-c-k-no/photo.jpg");
  public static final NearbyPerson DIANNE = new NearbyPerson(
      "105051985738280261832", "D", "Dianne Hackborn",
      "https://lh3.googleusercontent.com/-cyZL14lm8gk/AAAAAAAAAAI/AAAAAAAAIFY/4HCpUx9y2W4/s46-c-k-no/photo.jpg");
  public static final NearbyPerson KATHERINE = new NearbyPerson(
      "101483589011642884895", "K", "Katherine Kuan",
      "https://lh3.googleusercontent.com/-fF-088TJRMQ/AAAAAAAAAAI/AAAAAAAAABE/Qh-ekQUjrmg/s46-c-k-no/photo.jpg");
  public static final NearbyPerson RETO = new NearbyPerson(
      "111169963967137030210", "R", "Reto Meier",
      "https://lh3.googleusercontent.com/-FnQer4-BRHE/AAAAAAAAAAI/AAAAAAAA-hU/AJ4qvN5ammg/s46-c-k-no/photo.jpg");
  public static final NearbyPerson TOR = new NearbyPerson(
      "116539451797396019960", "T", "Tor Norbye",
      "https://lh3.googleusercontent.com/-LNdnF_dvn90/AAAAAAAAAAI/AAAAAAABcf0/CYW5PSDoTcM/s46-c-k-no/photo.jpg");
  public static final NearbyPerson XAVIER = new NearbyPerson(
      "109385828142935151413", "X", "Xavier Ducrohet",
      "https://lh3.googleusercontent.com/-TB5RgVFwuWE/AAAAAAAAAAI/AAAAAAAAD0o/NtNoQP9QdPw/s46-c-k-no/photo.jpg");

  private static final SimpleArrayMap<String, NearbyPerson> PEOPLE = new SimpleArrayMap<>();
  static {
    for (NearbyPerson person : new NearbyPerson[] {
        ADAM, CHET, COLT, DIANNE, KATHERINE, RETO, TOR, XAVIER
    }) {
      PEOPLE.put(person.googlePlusId, person);
    }
  }

  private static final Gson GSON = new Gson();

  public static ViewInteraction matchToolbarTitle(
      CharSequence title) {
    return onView(isAssignableFrom(Toolbar.class))
        .check(matches(withToolbarTitle(is(title))));
  }

  public static ViewInteraction matchToolbarTitle(@StringRes int resId) {
    CharSequence title = InstrumentationRegistry.getTargetContext().getString(resId);
    return matchToolbarTitle(title);
  }

  private static Matcher<Object> withToolbarTitle(
      final Matcher<CharSequence> textMatcher) {
    return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
      @Override public boolean matchesSafely(Toolbar toolbar) {
        return textMatcher.matches(toolbar.getTitle());
      }
      @Override public void describeTo(Description description) {
        description.appendText("with toolbar title: ");
        textMatcher.describeTo(description);
      }
    };
  }

  public static void clickActionBarHomeButton() {
    onView(allOf(
        withParent(isAssignableFrom(Toolbar.class)),
        isAssignableFrom(ImageButton.class)))
        .perform(click());
  }

  public static void setupGoogleApiClientBridgeForNearby(
      GoogleApiClientBridge googleApiClientBridge, final NearbyPerson... people) {
    final String token = "token";
    final ArgumentCaptor<GoogleApiClient.ConnectionCallbacks> connectedArgument
        = ArgumentCaptor.forClass(GoogleApiClient.ConnectionCallbacks.class);
    Mockito.when(googleApiClientBridge.init(Mockito.any(Activity.class),
        connectedArgument.capture(), Mockito.any(GoogleApiClient.OnConnectionFailedListener.class)))
        .thenReturn(token);
    Mockito.doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        connectedArgument.getValue().onConnected(null);
        return null;
      }
    }).when(googleApiClientBridge).connect(Mockito.anyString());
    Mockito.when(googleApiClientBridge.isConnected(Mockito.anyString())).thenReturn(true);

    GoogleSignInAccount account = Mockito.mock(GoogleSignInAccount.class);
    Mockito.when(account.getDisplayName()).thenReturn(ME_DISPLAY_NAME);
    Uri imageUri = Mockito.mock(Uri.class);
    Mockito.when(imageUri.toString()).thenReturn("http://lorempixel.com/50/50/cats");
    Mockito.when(account.getPhotoUrl()).thenReturn(imageUri);
    Mockito.when(googleApiClientBridge.getCurrentAccount()).thenReturn(account);

    final ArgumentCaptor<MessageListener> messageListenerArgument
        = ArgumentCaptor.forClass(MessageListener.class);
    Mockito.doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        for (NearbyPerson person : people) {
          Message message = Mockito.mock(Message.class);
          byte[] data = GSON.toJson(person).getBytes();
          Mockito.when(message.getContent()).thenReturn(data);
          messageListenerArgument.getValue().onFound(message);
        }
        return null;
      }
    }).when(googleApiClientBridge).subscribe(
        Mockito.anyString(), messageListenerArgument.capture(), Mockito.any(ResultCallback.class));

    final ArgumentCaptor<GoogleApiClientBridge.GetProfileImagesCallback> getProfileImagesCallbackArgument
        = ArgumentCaptor.forClass(GoogleApiClientBridge.GetProfileImagesCallback.class);
    Mockito.doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        getProfileImagesCallbackArgument.getValue().onSuccess();
        return null;
      }
    }).when(googleApiClientBridge).getProfileImages(
        Mockito.anyString(), Mockito.anyListOf(String.class), getProfileImagesCallbackArgument.capture());

    final ArgumentCaptor<String> userIdArgument = ArgumentCaptor.forClass(String.class);
    Mockito.doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        String userId = userIdArgument.getValue();
        return PEOPLE.get(userId).imageUrl;
      }
    }).when(googleApiClientBridge).getProfileImage(userIdArgument.capture());
  }

  public static void verifyViewGroupWordColors(@IdRes int viewGroupId, int... colors) {
    for (int i = 0; i < colors.length; ++i) {
      int color = colors[i];
      onView(allOf(
          withParent(withId(viewGroupId)), atPosition(i)))
          .check(matches(withTextColor(color)));
    }
  }

  public static void verifyViewGroupWordSources(@IdRes int viewGroupId, int... counts) {
    for (int i = 0; i < counts.length; ++i) {
      int count = counts[i];
      if (count > 1) {
        onView(allOf(
            withParent(withId(viewGroupId)), atPosition(i)))
            .check(matches(hasChildCount(count)));
      } else {
        onView(allOf(
            withParent(withId(viewGroupId)), atPosition(i)))
            .check(matches(isAssignableFrom(ImageView.class)));
      }
    }
  }

  public static void setSeenMultiSource(SharedPreferences pref) {
    SharedPreferences.Editor editor = pref.edit();
    editor.putBoolean(Constants.KEY_SEEN_MULTI_SOURCE, true);
    editor.apply();
  }

  public static void verifyMultiSourceTipShown() {
    onView(withId(R.id.multi_source_tip))
        .check(matches(isDisplayed()));
    onView(withId(R.id.multi_source_tip_arrow))
        .check(matches(isDisplayed()));
    onView(withId(R.id.multi_source_tip_arrow_handle))
        .check(matches(isDisplayed()));
  }

  public static void verifyMultiSourceTipHidden() {
    onView(withId(R.id.multi_source_tip))
        .check(matches(not(isDisplayed())));
    onView(withId(R.id.multi_source_tip_arrow))
        .check(matches(not(isDisplayed())));
    onView(withId(R.id.multi_source_tip_arrow_handle))
        .check(matches(not(isDisplayed())));
  }

  public static SpelledLetter createSpelledLetter(String letter, NearbyPerson... people) {
    SpelledLetter spelledLetter = new SpelledLetter(letter);
    for (NearbyPerson person : people) {
      spelledLetter.sources.add(new LetterSource(person));
    }
    return spelledLetter;
  }
}