package com.sqisland.friendspell.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.sqisland.friendspell.FriendSpellApplication;
import com.sqisland.friendspell.R;
import com.sqisland.friendspell.api.GoogleApiClientBridge;
import com.sqisland.friendspell.storage.DatabaseApi;
import com.sqisland.friendspell.storage.WordSet;
import com.sqisland.friendspell.storage.WordSetStore;
import com.sqisland.friendspell.util.Constants;
import com.sqisland.friendspell.util.FontUtil;
import com.sqisland.friendspell.util.ImageUtil;
import com.sqisland.friendspell.util.ViewUtil;
import com.sqisland.friendspell.util.WordUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
  @BindView(R.id.signed_out_pane)
  View signedOutPane;
  @BindView(R.id.sign_in_button)
  View signInButton;

  @BindView(R.id.word)
  LinearLayout wordContainer;
  @BindView(R.id.word_image)
  ImageView wordImageView;
  @BindView(R.id.friend_c)
  ImageView friendCView;
  @BindView(R.id.friend_a)
  ImageView friendAView;
  @BindView(R.id.friend_r)
  ImageView friendRView;
  @BindView(R.id.word_sets)
  ListView wordSetsListView;

  private Menu menu;

  @Inject
  DatabaseApi databaseApi;

  @Inject
  GoogleApiClientBridge googleApiClientBridge;
  private String googleApiClientToken;

  @Inject
  SharedPreferences pref;

  private boolean shouldAutoLogin = true;

  private WordSetStore store;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ((FriendSpellApplication) getApplication()).component().inject(this);
    ButterKnife.bind(this);

    googleApiClientToken = googleApiClientBridge.init(this, this, this);

    setTitle(null);

    try {
      init();
    } catch (IOException e) {
      Timber.e(Log.getStackTraceString(e));
    }
  }

  private void init() throws IOException {
    store = new WordSetStore(
        new InputStreamReader(getResources().openRawResource(R.raw.word_sets)));
    final List<WordSet> wordSets = store.getWordSets();

    ArrayAdapter<WordSet> adapter = new ArrayAdapter<>(
        this, android.R.layout.simple_list_item_1, wordSets);
    wordSetsListView.setAdapter(adapter);
    wordSetsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WordSet set = wordSets.get(position);
        Intent intent = new Intent(MainActivity.this, WordSetActivity.class);
        intent.putExtra(Constants.KEY_NAME, set.name);
        startActivityForResult(intent, Constants.REQUEST_CODE_WORD_SET);
      }
    });

    String word = "CAR";
    Typeface typeface = FontUtil.load(this, "fonts/SyncopateBold.ttf");
    int colors[] = new int[] { WordUtil.COLOR_ONE };
    for (int i = 0; i < word.length(); ++i) {
      String letter = word.substring(i, i+1);
      TextView textView = (TextView) wordContainer.getChildAt(i);
      textView.setTypeface(typeface);
      textView.setText(ViewUtil.getColoredWord(colors, letter));
    }

    loadWordImage();
    showFriend(friendCView, "kameleon_man_9_", R.dimen.spell_letter_box_size);
    showFriend(friendAView, "kameleon_woman_9", R.dimen.spell_letter_box_size);
    showFriend(friendRView, "kameleon_woman_15", R.dimen.spell_letter_box_size);
  }

  private void loadWordImage() {
    int resId = ViewUtil.getWordImageResId(this, "automobile");
    int width = getResources().getDimensionPixelSize(R.dimen.automobile_width);
    int height = getResources().getDimensionPixelSize(R.dimen.automobile_height);
    float viewportWidth = getResources().getDimension(R.dimen.automobile_viewport_width);
    float viewportHeight = getResources().getDimension(R.dimen.automobile_viewport_height);
    Bitmap bitmap = ImageUtil.createBitmapFromVector(
        getResources(), resId, width, height, viewportWidth, viewportHeight);
    wordImageView.setImageBitmap(bitmap);
  }

  private void showFriend(ImageView imageView, String name, @DimenRes int sizeResId) {
    int resId = ViewUtil.getWordImageResId(this, name);
    int size = getResources().getDimensionPixelSize(sizeResId);
    Bitmap bitmap = ImageUtil.createBitmapFromVector(getResources(), resId, size);
    imageView.setImageBitmap(bitmap);
  }

  @Override public void onStart() {
    super.onStart();
    googleApiClientBridge.connect(googleApiClientToken);
  }

  @Override public void onStop() {
    googleApiClientBridge.disconnect(googleApiClientToken);
    super.onStop();
  }

  @Override public void onDestroy() {
    googleApiClientBridge.destroy(googleApiClientToken);
    super.onDestroy();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Timber.d("onActivityResult:%d:%d:%s", requestCode, resultCode, data);

    switch (requestCode) {
      case Constants.REQUEST_CODE_GOOGLE_PLUS_SIGN_IN:
        GoogleSignInResult result = googleApiClientBridge.getSignInResultFromIntent(data);
        handleSignInResult(result);
        break;
      default:
        super.onActivityResult(requestCode, resultCode, data);
    }
  }

  public void handleSignInResult(GoogleSignInResult result) {
    Timber.d("handleSignInResult status: %s", result.getStatus());
    if (result.isSuccess()) {
      googleApiClientBridge.setCurrentAccount(result.getSignInAccount());
      showAutoSignedInUI();
    } else {
      googleApiClientBridge.setCurrentAccount(null);
      showSignedOutUI();
    }
  }

  @Override
  public void onConnected(Bundle connectionHint) {
    Timber.d("onConnected");
    if (shouldAutoLogin) {
      OptionalPendingResult<GoogleSignInResult> pendingResult =
              googleApiClientBridge.silentSignIn(googleApiClientToken);
      if (pendingResult.isDone()) {
        // There's immediate result available.
        handleSignInResult(pendingResult.get());
      } else {
        // There's no immediate result ready
        pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
          @Override
          public void onResult(@NonNull GoogleSignInResult result) {
            handleSignInResult(result);
          }
        });
      }
    }
  }

  @Override
  public void onConnectionSuspended(int cause) {
    Timber.d("onConnectionSuspended: %d", cause);
    googleApiClientBridge.connect(googleApiClientToken);
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    // Could not connect to Google Play Services.  The user needs to select an account,
    // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
    // ConnectionResult to see possible error codes.
    Timber.d("onConnectionFailed: %s", connectionResult);
    showSignedOutUI();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main, menu);

    this.menu = menu;
    updateMenuItems();

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_sign_out:
        signOut();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @OnClick(R.id.sign_in_button)
  void signIn() {
    signInButton.setEnabled(false);
    Intent signInIntent = googleApiClientBridge.getSignInIntent(googleApiClientToken);
    startActivityForResult(signInIntent, Constants.REQUEST_CODE_GOOGLE_PLUS_SIGN_IN);
  }

  private void signOut() {
    googleApiClientBridge.signOut(googleApiClientToken);
    databaseApi.clear();
    SharedPreferences.Editor editor = pref.edit();
    editor.clear();
    editor.apply();
    showSignedOutUI();
  }

  private void showSignedOutUI() {
    setTitle(R.string.app_name);

    wordSetsListView.setVisibility(View.GONE);

    signInButton.setEnabled(true);
    signedOutPane.setVisibility(View.VISIBLE);

    updateMenuItems();
  }

  private void showAutoSignedInUI() {
    setTitle(R.string.title_word_sets);

    signedOutPane.setVisibility(View.GONE);
    wordSetsListView.setVisibility(View.VISIBLE);
    updateMenuItems();
  }

  private void updateMenuItems() {
    if (menu == null) {
      return;
    }

    boolean visible = (googleApiClientBridge.isSignedIn() &&
        wordSetsListView.getVisibility() == View.VISIBLE);
    for (int i = 0; i < menu.size(); ++i) {
      menu.getItem(i).setVisible(visible);
    }
  }
}