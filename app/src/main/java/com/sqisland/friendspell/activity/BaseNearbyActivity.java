package com.sqisland.friendspell.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.gson.Gson;
import com.sqisland.friendspell.FriendSpellApplication;
import com.sqisland.friendspell.R;
import com.sqisland.friendspell.api.GoogleApiClientBridge;
import com.sqisland.friendspell.storage.DatabaseApi;
import com.sqisland.friendspell.storage.LetterSource;
import com.sqisland.friendspell.storage.NearbyPerson;
import com.sqisland.friendspell.ui.CircleTransform;
import com.sqisland.friendspell.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public abstract class BaseNearbyActivity extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
  private static final Gson GSON = new Gson();

  @Inject
  DatabaseApi databaseApi;

  @Inject
  GoogleApiClientBridge googleApiClientBridge;
  private String googleApiClientToken;

  private CircleTransform circleTransform;

  private Message message;
  private MessageListener messageListener = new MessageListener() {
    @Override
    public void onFound(final Message message) {
      String json = new String(message.getContent());
      final NearbyPerson person = GSON.fromJson(json, NearbyPerson.class);
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          addPerson(person);
          updateList();
        }
      });
    }
  };

  private ErrorCheckingCallback callback = new ErrorCheckingCallback();

  @BindView(R.id.list)
  ListView listView;
  @BindView(R.id.empty)
  View emptyView;
  private NearbyAdapter adapter;
  private HashMap<String, NearbyPerson> people = new HashMap<>();
  private Comparator<? super NearbyPerson> comparator = new Comparator<NearbyPerson>() {
    @Override
    public int compare(NearbyPerson lhs, NearbyPerson rhs) {
      return lhs.displayName.compareTo(rhs.displayName);
    }
  };

  private TextView numAddedView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nearby);

    ((FriendSpellApplication) getApplication()).component().inject(this);
    ButterKnife.bind(this);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    googleApiClientToken = googleApiClientBridge.init(this, this, this);
    circleTransform = new CircleTransform();

    NearbyPerson me = getMe();
    if (me != null) {
      setupListView(me);
    }
  }

  @Override public void onStart() {
    super.onStart();
    googleApiClientBridge.connect(googleApiClientToken);
  }

  @Override public void onStop() {
    googleApiClientBridge.unpublish(googleApiClientToken, message, callback);
    googleApiClientBridge.unsubscribe(googleApiClientToken, messageListener, callback);

    googleApiClientBridge.disconnect(googleApiClientToken);
    super.onStop();
  }

  @Override public void onDestroy() {
    googleApiClientBridge.destroy(googleApiClientToken);
    super.onDestroy();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      setResult(RESULT_OK);
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case Constants.REQUEST_CODE_RESOLVE_ERROR:
        if (resultCode == RESULT_OK) {
          publishAndSubscribe();
        }
        break;
      default:
        super.onActivityResult(requestCode, resultCode, data);
    }
  }

  private void setupListView(NearbyPerson person) {
    if (listView.getHeaderViewsCount() == 0) {
      View header = LayoutInflater.from(this).inflate(
          R.layout.nearby_header, listView, false);
      setupHeader(header, person);
      numAddedView = (TextView) header.findViewById(R.id.num_added);
      listView.addHeaderView(header);
    }
    View emptyHeader = emptyView.findViewById(R.id.nearby_header);
    setupHeader(emptyHeader, person);

    adapter = new NearbyAdapter(this, R.layout.nearby_person);
    listView.setAdapter(adapter);
    listView.setEmptyView(emptyView);
  }

  private void setupHeader(View header, NearbyPerson person) {
    TextView meView = (TextView) header.findViewById(R.id.me);
    meView.setText(person.displayName);
    if (!TextUtils.isEmpty(person.imageUrl)) {
      Picasso.with(this)
          .load(person.imageUrl)
          .placeholder(R.drawable.nearby_person_thumbnail_placeholder)
          .transform(circleTransform)
          .resizeDimen(R.dimen.nearby_person_thumbnail_size, R.dimen.nearby_person_thumbnail_size)
          .into(new TextViewTarget(meView));
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    publishAndSubscribe();
  }

  @Override
  public void onConnectionSuspended(int i) {
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
  }

  protected void addPerson(NearbyPerson person) {
    GoogleSignInAccount me = googleApiClientBridge.getCurrentAccount();
    if (me != null && person.googlePlusId.equals(me.getId())) {
      return;
    }

    if (!people.containsKey(person.googlePlusId)) {
      LetterSource source = databaseApi.loadLetter(person.googlePlusId);
      person.updateState(source);
      people.put(person.googlePlusId, person);
      if (source == null) {
        source = new LetterSource(person);
      }
      source.available = 1;
      databaseApi.saveLetter(source);
      googleApiClientBridge.putProfileImage(person.googlePlusId, person.imageUrl);
    }
  }

  protected void updateList() {
    ArrayList<NearbyPerson> list = new ArrayList<>(people.values());
    Collections.sort(list, comparator);
    adapter.clear();
    for (NearbyPerson person : list) {
      adapter.add(person);
    }
    if (adapter.getCount() > 0) {
      numAddedView.setText(
          getResources().getQuantityString(
              R.plurals.friend_added, adapter.getCount(), adapter.getCount()));
      numAddedView.setVisibility(View.VISIBLE);
    } else {
    }
  }

  private NearbyPerson getMe() {
    GoogleSignInAccount me = googleApiClientBridge.getCurrentAccount();
    if (me == null) {
      return null;
    }

    String displayName = me.getDisplayName();
    if (displayName == null) {
      displayName = "null";
    }
    Uri imageUri = googleApiClientBridge.getCurrentAccount().getPhotoUrl();
    String imageUrl = imageUri != null ? imageUri.toString() : null;

    return new NearbyPerson(
        me.getId(), displayName.substring(0, 1), displayName, imageUrl);
  }

  private void publishAndSubscribe() {
    final NearbyPerson me = getMe();
    if (me == null) {
      return;
    }
    byte[] data = GSON.toJson(me).getBytes();

    Timber.d("Publishing: " + me.googlePlusId);
    message = googleApiClientBridge.publish(googleApiClientToken, data, callback);
    googleApiClientBridge.subscribe(googleApiClientToken, messageListener, callback);
  }

  private class ErrorCheckingCallback implements ResultCallback<Status> {
    private final Runnable runOnSuccess;
    private boolean resolvingError;

    private ErrorCheckingCallback() {
      this(null);
    }

    private ErrorCheckingCallback(@Nullable Runnable runOnSuccess) {
      this.runOnSuccess = runOnSuccess;
    }

    @Override
    public void onResult(@NonNull Status status) {
      if (status.isSuccess()) {
        Timber.d("succeeded.");
        if (runOnSuccess != null) {
          runOnSuccess.run();
        }
      } else {
        // Currently, the only resolvable error is that the device is not opted
        // in to Nearby. Starting the resolution displays an opt-in dialog.
        if (status.hasResolution()) {
          if (!resolvingError) {
            try {
              status.startResolutionForResult(BaseNearbyActivity.this,
                  Constants.REQUEST_CODE_RESOLVE_ERROR);
              resolvingError = true;
            } catch (IntentSender.SendIntentException e) {
              Timber.e("failed with exception: " + e);
            }
          } else {
            // This will be encountered on initial startup because we do
            // both publish and subscribe together.  So having a toast while
            // resolving dialog is in progress is confusing, so just log it.
            Timber.e("failed with status: " + status
                + " while resolving error.");
          }
        } else {
          Timber.e("failed with : " + status + " resolving error: " + resolvingError);
        }
      }
    }
  }
}