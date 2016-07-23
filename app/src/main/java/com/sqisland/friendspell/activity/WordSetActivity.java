package com.sqisland.friendspell.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sqisland.friendspell.FriendSpellApplication;
import com.sqisland.friendspell.R;
import com.sqisland.friendspell.storage.DatabaseApi;
import com.sqisland.friendspell.storage.WordSet;
import com.sqisland.friendspell.storage.WordSetStore;
import com.sqisland.friendspell.util.Constants;
import com.sqisland.friendspell.util.NavigationUtil;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class WordSetActivity extends AppCompatActivity {
  @Inject
  DatabaseApi databaseApi;

  @BindView(R.id.words)
  ListView listView;

  private WordSetStore store;
  private WordAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_word_set);

    ButterKnife.bind(this);
    ((FriendSpellApplication) getApplication()).component().inject(this);

    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    try {
      init();
    } catch (IOException e) {
      Timber.e(Log.getStackTraceString(e));
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_common, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_nearby_add:
        NavigationUtil.goToNearbyActivity(this);
        return true;
      case R.id.action_people:
        NavigationUtil.goToPeopleActivity(this);
        return true;
      case android.R.id.home:
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    adapter.updateLetters();
    adapter.notifyDataSetChanged();
  }

  private void init() throws IOException {
    store = new WordSetStore(
        new InputStreamReader(getResources().openRawResource(R.raw.word_sets)));
    final String name = getIntent().getStringExtra(Constants.KEY_NAME);
    final WordSet set = store.getWordSet(name);

    // TODO: Show error for invalid word set

    adapter = new WordAdapter(
        this, R.layout.word_item, set.words, databaseApi, name);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(WordSetActivity.this, SpellActivity.class);
        intent.putExtra(Constants.KEY_NAME, name);
        intent.putExtra(Constants.KEY_WORD, set.words[position]);
        startActivityForResult(intent, Constants.REQUEST_CODE_SPELL);
      }
    });

    setTitle(set.title);
  }
}