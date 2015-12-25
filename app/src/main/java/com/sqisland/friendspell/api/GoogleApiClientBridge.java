package com.sqisland.friendspell.api;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class GoogleApiClientBridge {
  private final SimpleArrayMap<String, GoogleApiClient> clients = new SimpleArrayMap<>();
  private final SimpleArrayMap<String, String> profileImages = new SimpleArrayMap<>();
  private GoogleSignInAccount currentAccount;

  public GoogleApiClientBridge() {
  }

  public String init(
      Activity activity,
      GoogleApiClient.ConnectionCallbacks connectedListener,
      GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
    // Configure sign-in to request the user's ID, email address, and basic profile. ID and
    // basic profile are included in DEFAULT_SIGN_IN.
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestId()
            .requestProfile()
            .build();

    GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
        .addConnectionCallbacks(connectedListener)
        .addOnConnectionFailedListener(connectionFailedListener)
        .addApi(Plus.API)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .addApi(Nearby.MESSAGES_API)
        .build();
    String token = UUID.randomUUID().toString();
    clients.put(token, googleApiClient);
    return token;
  }

  public void destroy(String token) {
    clients.remove(token);
  }

  public void connect(String token) {
    GoogleApiClient googleApiClient = clients.get(token);
    googleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
  }

  public boolean isConnected(String token) {
    GoogleApiClient googleApiClient = clients.get(token);
    return googleApiClient.isConnected();
  }

  public void disconnect(String token) {
    GoogleApiClient googleApiClient = clients.get(token);
    if (googleApiClient.isConnected()) {
      googleApiClient.disconnect();
    }
  }

  public boolean isSignedIn() {
    return currentAccount != null;
  }

  public void setCurrentAccount(GoogleSignInAccount account) {
    currentAccount = account;
  }

  public Intent getSignInIntent(String token) {
    GoogleApiClient googleApiClient = clients.get(token);
    return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
  }

  public GoogleSignInResult getSignInResultFromIntent(Intent intent) {
    return Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
  }

  public OptionalPendingResult<GoogleSignInResult> silentSignIn(String token) {
    GoogleApiClient googleApiClient = clients.get(token);
    return Auth.GoogleSignInApi.silentSignIn(googleApiClient);
  }

  public void signOut(String token) {
    currentAccount = null;
    GoogleApiClient googleApiClient = clients.get(token);
    final PendingResult<Status> statusPendingResult =
            Auth.GoogleSignInApi.signOut(googleApiClient);
    statusPendingResult.setResultCallback(new ResultCallback<Status>() {
      @Override
      public void onResult(@NonNull Status status) {
        Timber.d("Sign out: %s", status);
      }
    });
  }

  public GoogleSignInAccount getCurrentAccount() {
    if (currentAccount == null) {
      return null;
    }
    profileImages.put(currentAccount.getId(), getCurrentAccountImageUrl());
    return currentAccount;
  }

  public String getProfileImage(String userId) {
    return profileImages.get(userId);
  }

  public void putProfileImage(String userId, String url) {
    profileImages.put(userId, url);
  }

  public void getProfileImages(
      String token, final List<String> userIds, final GetProfileImagesCallback callback) {
    ArrayList<String> toFetch = new ArrayList<>();
    for (int i = 0; i < userIds.size(); ++i) {
      String userId = userIds.get(i);
      if (!profileImages.containsKey(userId)) {
        toFetch.add(userId);
      }
    }

    GoogleApiClient googleApiClient = clients.get(token);
    if (googleApiClient == null) {
      return;
    }

    Plus.PeopleApi.load(googleApiClient, userIds).setResultCallback(
        new ResultCallback<People.LoadPeopleResult>() {
      @Override
      public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
          PersonBuffer personBuffer = peopleData.getPersonBuffer();
          try {
            int count = personBuffer.getCount();
            for (int i = 0; i < count; ++i) {
              Person person = personBuffer.get(i);
              profileImages.put(person.getId(), getProfileImageUrl(person));
            }
            callback.onSuccess();
          } finally {
            personBuffer.close();
          }
        } else {
          Timber.e("Error requesting people data: %s", peopleData.getStatus());
        }
      }
    });
  }

  public Message publish(String token, byte[] data, ResultCallback<Status> callback) {
    Message message = new Message(data);
    GoogleApiClient googleApiClient = clients.get(token);
    if (googleApiClient.isConnected()) {
      Nearby.Messages.publish(googleApiClient, message).setResultCallback(callback);
    }
    return message;
  }

  public void unpublish(String token, Message message, ResultCallback<Status> callback) {
    GoogleApiClient googleApiClient = clients.get(token);
    if (googleApiClient.isConnected()) {
      Nearby.Messages.unpublish(googleApiClient, message)
          .setResultCallback(callback);
    }
  }

  public void subscribe(String token, MessageListener listener, ResultCallback<Status> callback) {
    GoogleApiClient googleApiClient = clients.get(token);
    Nearby.Messages.subscribe(googleApiClient, listener)
        .setResultCallback(callback);
  }

  public void unsubscribe(String token, MessageListener listener, ResultCallback<Status> callback) {
    GoogleApiClient googleApiClient = clients.get(token);
    if (googleApiClient.isConnected()) {
      Nearby.Messages.unsubscribe(googleApiClient, listener)
          .setResultCallback(callback);
    }
  }

  private String getCurrentAccountImageUrl() {
    return currentAccount.getPhotoUrl() != null ? currentAccount.getPhotoUrl().toString() : null;
  }

  private String getProfileImageUrl(Person person) {
    Person.Image image = person.getImage();
    return image.hasUrl() ? image.getUrl() : null;
  }

  public interface GetProfileImagesCallback {
    void onSuccess();
  }
}
