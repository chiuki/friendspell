package com.sqisland.friendspell.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.model.people.Person;
import com.sqisland.friendspell.R;
import com.sqisland.friendspell.api.GoogleApiClientBridge;
import com.sqisland.friendspell.util.TestUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends BaseTest {
  @Rule
  public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(
      MainActivity.class,
      true,     // initialTouchMode
      false);   // launchActivity. False so we set up before activity launch

  @Test
  public void signIn() {
    setupGoogleApiClientBridge(googleApiClientBridge, false, true);

    Activity activity = activityRule.launchActivity(null);

    onView(withId(R.id.signed_out_pane))
        .check(matches(isDisplayed()));
    TestUtil.matchToolbarTitle(activity.getString(R.string.app_name));

    onView(withId(R.id.sign_in_button))
        .perform(click());

    onView(withId(R.id.signed_out_pane))
        .check(matches(not(isDisplayed())));
    TestUtil.matchToolbarTitle(activity.getString(R.string.title_word_sets));

    Mockito.verify(googleApiClientBridge, Mockito.times(2)).connect(Mockito.anyString());
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
      GoogleApiClientBridge googleApiClientBridge, final boolean... statuses) {
    final String token = "token";
    final ArgumentCaptor<GoogleApiClient.ConnectionCallbacks> connectedArgument
        = ArgumentCaptor.forClass(GoogleApiClient.ConnectionCallbacks.class);
    final ArgumentCaptor<GoogleApiClient.OnConnectionFailedListener> failedArgument
        = ArgumentCaptor.forClass(GoogleApiClient.OnConnectionFailedListener.class);
    Mockito.when(googleApiClientBridge.init(Mockito.any(Activity.class),
        connectedArgument.capture(), failedArgument.capture())).thenReturn(token);
    Mockito.doAnswer(new Answer() {
      int count = 0;
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        if (statuses[count]) {
          connectedArgument.getValue().onConnected(null);
        } else {
          PendingIntent pendingIntent = PendingIntent.getActivity(
              InstrumentationRegistry.getTargetContext(), 0, new Intent(), 0);
          ConnectionResult result = new ConnectionResult(
              ConnectionResult.SIGN_IN_REQUIRED, pendingIntent);
          failedArgument.getValue().onConnectionFailed(result);
        }
        count += 1;
        return null;
      }
    }).when(googleApiClientBridge).connect(Mockito.anyString());
    Mockito.when(googleApiClientBridge.isConnected(Mockito.anyString())).thenReturn(true);

    Person person = Mockito.mock(Person.class);
    Mockito.when(googleApiClientBridge.getCurrentPerson(Mockito.anyString())).thenReturn(person);
  }
}