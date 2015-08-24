package com.sqisland.friendspell.storage;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.sqisland.friendspell.dagger.MockDatabaseApiModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseApiTest {
  protected DatabaseApi databaseApi;

  @Before
  public void setUp() {
    databaseApi = new DatabaseApi(
        InstrumentationRegistry.getTargetContext(), MockDatabaseApiModule.DATABASE_NAME);
  }

  @Test
  public void empty() {
    List<LetterSource> availableLetters = databaseApi.getAvailableLetters();
    assertEquals(0, availableLetters.size());
  }

  @Test
  public void saveAndLoadLetter() {
    LetterSource saved = new LetterSource();
    saved.googlePlusId = "someId";
    saved.letter = "T";
    saved.displayName = "Test";
    databaseApi.saveLetter(saved);

    List<LetterSource> availableLetters = databaseApi.getAvailableLetters();
    assertEquals(1, availableLetters.size());
    assertEquals(saved.googlePlusId, availableLetters.get(0).googlePlusId);
    assertEquals(saved.letter, availableLetters.get(0).letter);
    assertEquals(saved.displayName, availableLetters.get(0).displayName);
    assertEquals(1, availableLetters.get(0).available);

    LetterSource loaded = databaseApi.loadLetter(saved.googlePlusId);
    assertNotNull(loaded);
    assertEquals(saved.googlePlusId, loaded.googlePlusId);
    assertEquals(saved.letter, loaded.letter);
    assertEquals(saved.displayName, loaded.displayName);
    assertEquals(1, loaded.available);

    databaseApi.clear();
    availableLetters = databaseApi.getAvailableLetters();
    assertEquals(0, availableLetters.size());
  }

  @Test
  public void loadNonExistentLetter() {
    LetterSource loaded = databaseApi.loadLetter("nobody");
    assertNull(loaded);
  }

  @Test
  public void saveAndLoadWord() {
    LetterSource savedO = createLetterSource("O");
    LetterSource savedA = createLetterSource("A");
    LetterSource savedB = createLetterSource("B");
    LetterSource savedC = createLetterSource("C");
    LetterSource savedD = createLetterSource("D");

    WordSetItem savedWord = new WordSetItem();
    savedWord.wordset = "test";
    savedWord.word = "OK";

    savedWord.spelledWord = new SpelledWord();
    SpelledLetter spelledO = new SpelledLetter("O");
    spelledO.sources = Arrays.asList(savedO);
    SpelledLetter spelledK = new SpelledLetter("K");
    spelledK.sources = Arrays.asList(savedA, savedB, savedC, savedD);
    savedWord.spelledWord.letters = Arrays.asList(spelledO, spelledK);

    databaseApi.saveWord(savedWord);

    WordSetItem loadedWord = databaseApi.loadWord("test", "OK");
    assertEquals("test", loadedWord.wordset);
    assertEquals("OK", loadedWord.word);
    assertNotNull(loadedWord.spelledWord);
    assertNotNull(loadedWord.spelledWord.letters);
    assertEquals(2, loadedWord.spelledWord.letters.size());
    assertEquals("O", loadedWord.spelledWord.letters.get(0).letter);
    assertTrue(loadedWord.spelledWord.letters.get(0).isSingleSource());
    assertTrue(loadedWord.spelledWord.letters.get(0).isComplete());
    assertEquals("K", loadedWord.spelledWord.letters.get(1).letter);
    assertFalse(loadedWord.spelledWord.letters.get(1).isSingleSource());

    for (String letter : new String[] { "O", "A", "B", "C", "D" }) {
      loadAndVerifyUnavailableLetterSource(letter);
    }
  }

  private LetterSource createLetterSource(String letter) {
    LetterSource letterSource = new LetterSource();
    letterSource.letter = letter;
    letterSource.googlePlusId = "id_" + letter;
    letterSource.displayName = letter + letter;

    assertEquals(1, letterSource.available);
    databaseApi.saveLetter(letterSource);

    return letterSource;
  }

  private void loadAndVerifyUnavailableLetterSource(String letter) {
    LetterSource loaded = databaseApi.loadLetter("id_" + letter);
    assertNotNull(loaded);
    assertEquals(0, loaded.available);
  }

  @After
  public void tearDown() {
    InstrumentationRegistry.getInstrumentation().getTargetContext().deleteDatabase
        (MockDatabaseApiModule.DATABASE_NAME);
  }
}