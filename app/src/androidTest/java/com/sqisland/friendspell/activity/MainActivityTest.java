package com.sqisland.friendspell.activity;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.sqisland.friendspell.R;
import com.sqisland.friendspell.api.GoogleApiClientBridge;
import com.sqisland.friendspell.util.TestUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends BaseTest {
  @Rule
  public IntentsTestRule<MainActivity> activityRule = new IntentsTestRule<>(
      MainActivity.class,
      true,     // initialTouchMode
      false);   // launchActivity. False so we set up before activity launch

  @Test
  public void signIn() {
    setupGoogleApiClientBridge(googleApiClientBridge, false);

    Activity activity = activityRule.launchActivity(null);

    ActivityResult result = createSignInResult();
    intending(hasAction("com.google.android.gms.auth.GOOGLE_SIGN_IN")).respondWith(result);

    onView(withId(R.id.signed_out_pane))
        .check(matches(isDisplayed()));
    TestUtil.matchToolbarTitle(activity.getString(R.string.app_name));

    onView(withId(R.id.sign_in_button))
        .perform(click());

    onView(withId(R.id.signed_out_pane))
        .check(matches(not(isDisplayed())));
    TestUtil.matchToolbarTitle(activity.getString(R.string.title_word_sets));

    Mockito.verify(googleApiClientBridge, Mockito.times(2)).isSignedIn();
  }

  private ActivityResult createSignInResult() {
    return new ActivityResult(Activity.RESULT_OK, null);
  }

  @Test
  public void signOut() {
    setupGoogleApiClientBridge(googleApiClientBridge, true);

    Activity activity = activityRule.launchActivity(null);

    onView(withId(R.id.signed_out_pane))
        .check(matches(not(isDisplayed())));
    TestUtil.matchToolbarTitle(activity.getString(R.string.title_word_sets));

    onView(withId(R.id.action_sign_out))
        .check(matches(isDisplayed()))
        .perform(click());

    onView(withId(R.id.signed_out_pane))
        .check(matches(isDisplayed()));
    TestUtil.matchToolbarTitle(activity.getString(R.string.app_name));

    Mockito.verify(googleApiClientBridge, Mockito.times(1)).connect(Mockito.anyString());
    Mockito.verify(googleApiClientBridge, Mockito.times(1)).signOut(Mockito.anyString());
  }

  protected static void setupGoogleApiClientBridge(
      GoogleApiClientBridge googleApiClientBridge, final boolean initialStatus) {
    final String token = "token";
    final ArgumentCaptor<GoogleApiClient.ConnectionCallbacks> connectedArgument
        = ArgumentCaptor.forClass(GoogleApiClient.ConnectionCallbacks.class);
    final ArgumentCaptor<GoogleApiClient.OnConnectionFailedListener> failedArgument
        = ArgumentCaptor.forClass(GoogleApiClient.OnConnectionFailedListener.class);
    Mockito.when(googleApiClientBridge.init(Mockito.any(Activity.class),
        connectedArgument.capture(), failedArgument.capture())).thenReturn(token);
    Mockito.doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
          connectedArgument.getValue().onConnected(null);
          return null;
      }
    }).when(googleApiClientBridge).connect(Mockito.anyString());

    GoogleSignInAccount account = Mockito.mock(GoogleSignInAccount.class);
    Mockito.when(googleApiClientBridge.getCurrentAccount()).thenReturn(account);

    @SuppressWarnings("unchecked") OptionalPendingResult<GoogleSignInResult> mockPendingResult =
            Mockito.mock(OptionalPendingResult.class);
    GoogleSignInResult mockInitialSignInResult = Mockito.mock(GoogleSignInResult.class);
    Mockito.when(mockInitialSignInResult.isSuccess()).thenReturn(initialStatus);
    Mockito.when(mockInitialSignInResult.getSignInAccount()).thenReturn(account);
    GoogleSignInResult mockSuccessfulSignInResult = Mockito.mock(GoogleSignInResult.class);
    Mockito.when(mockSuccessfulSignInResult.isSuccess()).thenReturn(true);
    Mockito.when(mockSuccessfulSignInResult.getSignInAccount()).thenReturn(account);
    Mockito.when(mockPendingResult.isDone()).thenReturn(true);
    Mockito.when(mockPendingResult.get()).thenReturn(mockInitialSignInResult);
    Mockito.when(googleApiClientBridge.silentSignIn(Mockito.anyString()))
        .thenReturn(mockPendingResult);
    Mockito.when(googleApiClientBridge.isConnected(Mockito.anyString())).thenReturn(true);
    Mockito.when(googleApiClientBridge.isSignedIn()).thenReturn(initialStatus);
    Mockito.when(googleApiClientBridge
        .getSignInResultFromIntent(ArgumentMatchers.isNull(Intent.class)))
        .thenReturn(mockSuccessfulSignInResult);
    Mockito.when(googleApiClientBridge.getSignInIntent(Mockito.anyString()))
        .thenReturn(new Intent("com.google.android.gms.auth.GOOGLE_SIGN_IN"));
  }
}