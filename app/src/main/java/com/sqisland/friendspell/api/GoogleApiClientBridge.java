package com.sqisland.friendspell.api;

import android.app.Activity;
import android.support.v4.util.SimpleArrayMap;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
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

  public GoogleApiClientBridge() {
  }

  public String init(
      Activity activity,
      GoogleApiClient.ConnectionCallbacks connectedListener,
      GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
    GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
        .addConnectionCallbacks(connectedListener)
        .addOnConnectionFailedListener(connectionFailedListener)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .addScope(Plus.SCOPE_PLUS_PROFILE)
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
    googleApiClient.connect();
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

  public void signOut(String token) {
    GoogleApiClient googleApiClient = clients.get(token);
    if (googleApiClient.isConnected()) {
      Plus.AccountApi.clearDefaultAccount(googleApiClient);
      googleApiClient.disconnect();
    }
  }

  public Person getCurrentPerson(String token) {
    GoogleApiClient googleApiClient = clients.get(token);
    if (googleApiClient == null) {
      return null;
    }
    Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
    profileImages.put(person.getId(), getProfileImageUrl(person));
    return person;
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
          Timber.e("Error requesting people data: " + peopleData.getStatus());
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

  private String getProfileImageUrl(Person person) {
    Person.Image image = person.getImage();
    return image.hasUrl() ? image.getUrl() : null;
  }

  public interface GetProfileImagesCallback {
    void onSuccess();
  }
}
