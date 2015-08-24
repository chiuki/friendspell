package com.sqisland.friendspell.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DatabaseApi {
  private final DatabaseUtility databaseUtility;
  private final SQLiteDatabase db;

  public DatabaseApi(Context context) {
    databaseUtility = new DatabaseUtility(context);
    db = databaseUtility.getWritableDatabase();
  }

  public DatabaseApi(Context context, String name) {
    databaseUtility = new DatabaseUtility(context, name);
    db = databaseUtility.getWritableDatabase();
  }

  public LetterSource loadLetter(String googlePlusId) {
    return cupboard().withDatabase(db)
        .query(LetterSource.class)
        .withSelection("googlePlusId = ?", googlePlusId)
        .get();
  }

  public void saveLetter(LetterSource item) {
    cupboard().withDatabase(db).put(item);
  }

  public List<LetterSource> getAvailableLetters() {
    return cupboard().withDatabase(db)
        .query(LetterSource.class)
        .withSelection("available = ?", "1")
        .orderBy("displayName")
        .list();
  }

  public WordSetItem loadWord(String wordset, String word) {
    return cupboard().withDatabase(db)
        .query(WordSetItem.class)
        .withSelection("wordset = ? AND word = ?", wordset, word)
        .get();
  }

  public void saveWord(WordSetItem item) {
    ArrayList<String> placeholder = new ArrayList<>();
    ArrayList<String> used = new ArrayList<>();
    for (SpelledLetter spelledLetter : item.spelledWord.letters) {
      if (spelledLetter != null) {
        for (LetterSource source : spelledLetter.sources) {
          used.add(source.googlePlusId);
          placeholder.add("?");
        }
      }
    }

    ContentValues values = new ContentValues(1);
    values.put("available", 0);
    cupboard()
        .withDatabase(db)
        .update(LetterSource.class, values,
            "googlePlusId IN ( " + TextUtils.join(",", placeholder) + ")",
            used.toArray(new String[] {}));

    cupboard().withDatabase(db).put(item);
  }

  public void clear() {
    cupboard().withDatabase(db)
        .delete(LetterSource.class, null);
    cupboard().withDatabase(db)
        .delete(WordSetItem.class, null);
  }
}