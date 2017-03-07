package com.sqisland.friendspell.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sqisland.friendspell.FriendSpellApplication;
import com.sqisland.friendspell.R;
import com.sqisland.friendspell.api.GoogleApiClientBridge;
import com.sqisland.friendspell.storage.DatabaseApi;
import com.sqisland.friendspell.storage.LetterSource;
import com.sqisland.friendspell.storage.SpelledLetter;
import com.sqisland.friendspell.storage.SpelledWord;
import com.sqisland.friendspell.storage.WordSetItem;
import com.sqisland.friendspell.ui.CircleTransform;
import com.sqisland.friendspell.util.Constants;
import com.sqisland.friendspell.util.FontUtil;
import com.sqisland.friendspell.util.NavigationUtil;
import com.sqisland.friendspell.util.ViewUtil;
import com.sqisland.friendspell.util.WordUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpellActivity extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
  @BindView(R.id.nearby_tip)
  View nearbyTip;

  @BindView(R.id.word_image)
  ImageView wordImageView;

  @BindView(R.id.word)
  LinearLayout wordContainer;

  @BindView(R.id.sources)
  LinearLayout sourcesContainer;

  @BindView(R.id.multi_source_tip)
  View multiSourceTip;
  @BindView(R.id.multi_source_tip_arrow)
  View multiSourceTipArrow;
  @BindView(R.id.multi_source_tip_arrow_handle)
  View multiSourceTipArrowHandle;

  @BindView(R.id.spell_button)
  View spellButton;

  @Inject
  DatabaseApi databaseApi;
  
  @Inject
  GoogleApiClientBridge googleApiClientBridge;
  private String googleApiClientToken;

  @Inject
  SharedPreferences pref;

  private SpelledWord spelledWord;

  private CircleTransform circleTransform;
  private boolean seenMultiSourceTip = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_spell);

    ButterKnife.bind(this);
    ((FriendSpellApplication) getApplication()).component().inject(this);

    // Allows older platform to use vector drawables within DrawableContainer resources.
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    // home button goes up by a single level instead of going back to the top level or front page.
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    circleTransform = new CircleTransform();

    seenMultiSourceTip = pref.getBoolean(Constants.KEY_SEEN_MULTI_SOURCE, false);

    refreshUI();

    googleApiClientToken = googleApiClientBridge.init(this, this, this);
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
    // Recompute the page info if the user requests or just do the parent's implementation
    switch (requestCode) {
      case Constants.REQUEST_CODE_NEARBY:
        refreshUI();
        break;
      default:
        super.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_common, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Allows the user to navigate through the app with the menu
    switch (item.getItemId()) {
      case R.id.action_nearby_add:
        NavigationUtil.goToNearbyActivity(this);
        return true;
      case R.id.action_people:
        NavigationUtil.goToPeopleActivity(this);
        return true;
      case android.R.id.home:
        // Create a new app page (intent) and start it up, finishing the current page
        Intent intent = new Intent(this, WordSetActivity.class);
        intent.putExtra(Constants.KEY_NAME, getIntent().getStringExtra(Constants.KEY_NAME));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    getProfileImageUrls();
  }

  @Override
  public void onConnectionSuspended(int i) {
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
  }

  @OnClick({ R.id.word_image, R.id.word })
  void goToNearbyActivity() {
    NavigationUtil.goToNearbyActivity(this);
  }

  private void refreshUI() {
    // Get the list of available letters from the database
    List<LetterSource> letters = databaseApi.getAvailableLetters();

    // Get the strings from the current intent 
    String name = getIntent().getStringExtra(Constants.KEY_NAME);
    String word = getIntent().getStringExtra(Constants.KEY_WORD);
    // Load the name and word from the database
    WordSetItem wordSetItem = databaseApi.loadWord(name, word);
    if (wordSetItem == null) {  // if its not in the database
      spelledWord = WordUtil.spell(letters, word);  // create a new spelling
    } else {
      spelledWord = wordSetItem.spelledWord;  // efficiently load in a spelling we've done already
    }

    // Either set a placeholder or show the word if it exists
    @ColorRes int resId = (wordSetItem == null) ?
        R.drawable.word_image_placeholder : ViewUtil.getWordImageResId(this, word);
    wordImageView.setImageResource(resId);

    // If the word was not in the database then get the colour from either the db or intent
    int[] colors = (wordSetItem == null) ?
        WordUtil.getWordColors(letters, word) :
        WordUtil.getWordColors(wordSetItem.spelledWord.letters);
    LayoutInflater inflater = LayoutInflater.from(this);
    Typeface typeface = FontUtil.load(this, "fonts/SyncopateBold.ttf");

    // If there are no children then this is the initial intent
    boolean initial = (wordContainer.getChildCount() == 0);

    sourcesContainer.removeAllViews();   // so it can fresh easier
    boolean hasSource = false;
    boolean hasMultiSource = false;
    for (int i = 0; i < spelledWord.letters.size(); ++i) {
      // Either create a label from the children or get it from the inflater
      TextView textView = (TextView) (initial ?
          inflater.inflate(R.layout.letter, wordContainer, false) :
          (TextView) wordContainer.getChildAt(i));
      textView.setTypeface(typeface);
      // set the label to the individual letters in the word
      textView.setText(word.substring(i, i + 1));
      @ColorInt int color = colors[i];
      textView.setTextColor(color);
      // if there are no children add the view
      if (initial) {
        wordContainer.addView(textView);
      }

      SpelledLetter spelledLetter = spelledWord.letters.get(i);
      // bitwise inclusive OR and assignment operator
      hasSource |= (spelledLetter != null);
      // Set the layout based on if the spelled letter doesn't exist or if its from a single source
      int layoutId = (spelledLetter == null || spelledLetter.isSingleSource()) ?
          R.layout.single_source : R.layout.multi_source;
      hasMultiSource |= (layoutId == R.layout.multi_source);
      View child = inflater.inflate(layoutId, sourcesContainer, false);
      sourcesContainer.addView(child);

      refreshProfileImages();
    }

    setTitle(word);

    // Show or hide button if its initialized and then init the button
    spellButton.setVisibility(wordSetItem == null ? View.VISIBLE : View.GONE);
    spellButton.setEnabled(spelledWord.isComplete());
    spellButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        spell();
      }
    });

    nearbyTip.setVisibility(hasSource ? View.GONE : View.VISIBLE);

    // Show or hide the tips for which word it is
    boolean showMultiSourceTip = hasMultiSource && !seenMultiSourceTip;
    multiSourceTip.setVisibility(showMultiSourceTip ? View.VISIBLE : View.GONE);
    multiSourceTipArrow.setVisibility(showMultiSourceTip ? View.VISIBLE : View.GONE);
    multiSourceTipArrowHandle.setVisibility(showMultiSourceTip ? View.VISIBLE : View.GONE);
    if (showMultiSourceTip) {
      // Save current state of the editor to the shared preferences
      SharedPreferences.Editor editor = pref.edit();
      editor.putBoolean(Constants.KEY_SEEN_MULTI_SOURCE, true);
      editor.apply();
    }
  }

  private void getProfileImageUrls() {
    List<String> userIds = new ArrayList<>();
    // goes through each word and gets the user IDs
    for (int i = 0; i < spelledWord.letters.size(); ++i) {
      SpelledLetter spelledLetter = spelledWord.letters.get(i);
      if (spelledLetter == null) {
        continue;
      }
      List<LetterSource> sources = spelledLetter.sources;
      for (int j = 0; j < sources.size(); ++j) {
        LetterSource source = sources.get(j);
        userIds.add(source.googlePlusId);
      }
    }
    // If there are user IDS get the profile image url from a GET requet to Google's API
    if (!userIds.isEmpty()) {
      googleApiClientBridge.getProfileImages(
          googleApiClientToken, userIds, new GoogleApiClientBridge.GetProfileImagesCallback() {
            @Override
            public void onSuccess() {
              refreshProfileImages();
            }
          });
    }
  }

  private void refreshProfileImages() {
    // Don't refresh if the number of children is different
    if (sourcesContainer.getChildCount() != spelledWord.letters.size()) {
      return;
    }
    // For each word get the image from Google and replace it
    for (int i = 0; i < spelledWord.letters.size(); ++i) {
      SpelledLetter spelledLetter = spelledWord.letters.get(i);
      if (spelledLetter == null) {
        continue;
      }
      List<LetterSource> sources = spelledLetter.sources;
      if (spelledLetter.isSingleSource()) {
        String url = googleApiClientBridge.getProfileImage(sources.get(0).googlePlusId);
        ImageView imageView = (ImageView) sourcesContainer.getChildAt(i);
        loadImage(url, imageView);
      } else {
        // Go through the sources and replace the image of each
        ViewGroup viewGroup = (ViewGroup) sourcesContainer.getChildAt(i);
        for (int j = 0; j < sources.size(); ++j) {
          LetterSource source = sources.get(j);
          String url = googleApiClientBridge.getProfileImage(source.googlePlusId);
          ImageView imageView = (ImageView) viewGroup.getChildAt(j);
          loadImage(url, imageView);
        }
      }
    }
  }

  private void loadImage(String url, ImageView imageView) {
    // Take the image from the request and translate it into an app friendly form
    if (!TextUtils.isEmpty(url)) {
      Picasso.with(this)
          .load(url)
          .transform(circleTransform)
          .into(imageView);
    }
  }

  private void spell() {
    // Get the word and save it to the database
    WordSetItem item = new WordSetItem();
    item.wordset = getIntent().getStringExtra(Constants.KEY_NAME);
    item.word = getIntent().getStringExtra(Constants.KEY_WORD);
    item.spelledWord = spelledWord;
    databaseApi.saveWord(item);
    // Set the image view to the word and remove the spell button from the view
    wordImageView.setImageResource(ViewUtil.getWordImageResId(this, item.word));
    spellButton.setVisibility(View.GONE);
  }
}