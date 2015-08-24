package com.sqisland.friendspell.util;

import com.sqisland.friendspell.storage.LetterSource;
import com.sqisland.friendspell.storage.SpelledWord;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class WordUtilTest {
  @Test
  public void spellComplete() {
    LetterSource letterD = TestUtil.createLetterSource("D");
    letterD.available = 0;

    List<LetterSource> availableLetters = Arrays.asList(
        TestUtil.createLetterSource("O"),
        TestUtil.createLetterSource("A"),
        TestUtil.createLetterSource("B"),
        TestUtil.createLetterSource("C"),
        letterD,
        TestUtil.createLetterSource("E"));
    SpelledWord spelledWord = WordUtil.spell(availableLetters, "OK");

    assertTrue(spelledWord.isComplete());
    assertNotNull(spelledWord.letters);
    assertEquals(2, spelledWord.letters.size());

    assertEquals("O", spelledWord.letters.get(0).letter);
    assertTrue(spelledWord.letters.get(0).isSingleSource());
    assertTrue(spelledWord.letters.get(0).isComplete());
    assertNotNull(spelledWord.letters.get(0).sources);
    assertEquals(1, spelledWord.letters.get(0).sources.size());
    assertEquals("O", spelledWord.letters.get(0).sources.get(0).letter);
    assertEquals(1, spelledWord.letters.get(0).sources.get(0).available);

    assertEquals("K", spelledWord.letters.get(1).letter);
    assertFalse(spelledWord.letters.get(1).isSingleSource());
    assertTrue(spelledWord.letters.get(1).isComplete());
  }

  @Test
  public void spellIncomplete() {
    List<LetterSource> availableLetters = Arrays.asList(
        TestUtil.createLetterSource("Z"), TestUtil.createLetterSource("Y"));
    SpelledWord spelledWord = WordUtil.spell(availableLetters, "OK");

    assertFalse(spelledWord.isComplete());
    assertNotNull(spelledWord.letters);
    assertEquals(2, spelledWord.letters.size());

    assertEquals("O", spelledWord.letters.get(0).letter);
    assertFalse(spelledWord.letters.get(0).isSingleSource());
    assertFalse(spelledWord.letters.get(0).isComplete());
    assertNotNull(spelledWord.letters.get(0).sources);
    assertEquals(2, spelledWord.letters.get(0).sources.size());
    assertEquals("Y", spelledWord.letters.get(0).sources.get(0).letter);
    assertEquals(1, spelledWord.letters.get(0).sources.get(0).available);
    assertEquals("Z", spelledWord.letters.get(0).sources.get(1).letter);
    assertEquals(1, spelledWord.letters.get(0).sources.get(1).available);

    assertNull(spelledWord.letters.get(1));
  }

  @Test
  public void spellPartial() {
    LetterSource letterW = TestUtil.createLetterSource("W");
    letterW.available = 0;

    List<LetterSource> availableLetters = Arrays.asList(
        TestUtil.createLetterSource("O"),
        TestUtil.createLetterSource("Z"),
        letterW);
    SpelledWord spelledWord = WordUtil.spell(availableLetters, "OK");

    assertFalse(spelledWord.isComplete());
    assertNotNull(spelledWord.letters);
    assertEquals(2, spelledWord.letters.size());

    assertNotNull(spelledWord.letters.get(0));
    assertEquals("O", spelledWord.letters.get(0).letter);
    assertTrue(spelledWord.letters.get(0).isSingleSource());
    assertTrue(spelledWord.letters.get(0).isComplete());
    assertNotNull(spelledWord.letters.get(0).sources);
    assertEquals(1, spelledWord.letters.get(0).sources.size());
    assertEquals("O", spelledWord.letters.get(0).sources.get(0).letter);

    assertNotNull(spelledWord.letters.get(1));
    assertEquals("K", spelledWord.letters.get(1).letter);
    assertFalse(spelledWord.letters.get(1).isSingleSource());
    assertFalse(spelledWord.letters.get(1).isComplete());
    assertNotNull(spelledWord.letters.get(1).sources);
    assertEquals(1, spelledWord.letters.get(1).sources.size());
    assertEquals("Z", spelledWord.letters.get(1).sources.get(0).letter);
  }

  @Test
  public void spellAllSingleSource() {
    List<LetterSource> availableLetters = Arrays.asList(
        TestUtil.createLetterSource("O"),
        TestUtil.createLetterSource("K"));
    SpelledWord spelledWord = WordUtil.spell(availableLetters, "OK");

    assertTrue(spelledWord.isComplete());
    assertNotNull(spelledWord.letters);
    assertEquals(2, spelledWord.letters.size());

    assertEquals("O", spelledWord.letters.get(0).letter);
    assertTrue(spelledWord.letters.get(0).isSingleSource());
    assertTrue(spelledWord.letters.get(0).isComplete());
    assertNotNull(spelledWord.letters.get(0).sources);
    assertEquals(1, spelledWord.letters.get(0).sources.size());
    assertEquals("O", spelledWord.letters.get(0).sources.get(0).letter);
    assertEquals(1, spelledWord.letters.get(0).sources.get(0).available);

    assertEquals("K", spelledWord.letters.get(1).letter);
    assertTrue(spelledWord.letters.get(1).isSingleSource());
    assertTrue(spelledWord.letters.get(1).isComplete());
  }

  @Test
  public void spellRepeatedLetter() {
    LetterSource alice = TestUtil.createLetterSource("A");
    alice.displayName = "Alice";
    LetterSource amy = TestUtil.createLetterSource("A");
    amy.displayName = "Amy";
    LetterSource belinda = TestUtil.createLetterSource("B");
    belinda.displayName = "Belinda";

    List<LetterSource> availableLetters = Arrays.asList(amy, belinda, alice);
    SpelledWord spelledWord = WordUtil.spell(availableLetters, "BAA");

    assertTrue(spelledWord.isComplete());
    assertNotNull(spelledWord.letters);
    assertEquals(3, spelledWord.letters.size());

    assertEquals("B", spelledWord.letters.get(0).letter);
    assertTrue(spelledWord.letters.get(0).isSingleSource());
    assertTrue(spelledWord.letters.get(0).isComplete());
    assertNotNull(spelledWord.letters.get(0).sources);
    assertEquals(1, spelledWord.letters.get(0).sources.size());
    assertEquals("B", spelledWord.letters.get(0).sources.get(0).letter);
    assertEquals("Belinda", spelledWord.letters.get(0).sources.get(0).displayName);
    assertEquals(1, spelledWord.letters.get(0).sources.get(0).available);

    assertEquals("A", spelledWord.letters.get(1).letter);
    assertTrue(spelledWord.letters.get(1).isSingleSource());
    assertTrue(spelledWord.letters.get(1).isComplete());
    assertNotNull(spelledWord.letters.get(1).sources);
    assertEquals(1, spelledWord.letters.get(1).sources.size());
    assertEquals("A", spelledWord.letters.get(1).sources.get(0).letter);
    assertEquals("Alice", spelledWord.letters.get(1).sources.get(0).displayName);
    assertEquals(1, spelledWord.letters.get(1).sources.get(0).available);

    assertEquals("A", spelledWord.letters.get(2).letter);
    assertTrue(spelledWord.letters.get(2).isSingleSource());
    assertTrue(spelledWord.letters.get(2).isComplete());
    assertNotNull(spelledWord.letters.get(2).sources);
    assertEquals(1, spelledWord.letters.get(2).sources.size());
    assertEquals("A", spelledWord.letters.get(2).sources.get(0).letter);
    assertEquals("Amy", spelledWord.letters.get(2).sources.get(0).displayName);
    assertEquals(1, spelledWord.letters.get(2).sources.get(0).available);
  }

  @Test
  public void colorsAvailableNull() {
    int[] colors = WordUtil.getWordColors(null, "NULL");

    assertEquals(4, colors.length);
    assertEquals(WordUtil.COLOR_MISSING, colors[0]);
    assertEquals(WordUtil.COLOR_MISSING, colors[1]);
    assertEquals(WordUtil.COLOR_MISSING, colors[2]);
    assertEquals(WordUtil.COLOR_MISSING, colors[3]);
  }

  @Test
  public void colorsAvailableComplete() {
    List<LetterSource> availableLetters = Arrays.asList(
        TestUtil.createLetterSource("O"),
        TestUtil.createLetterSource("A"),
        TestUtil.createLetterSource("B"),
        TestUtil.createLetterSource("C"),
        TestUtil.createLetterSource("D"));
    int[] colors = WordUtil.getWordColors(availableLetters, "OK");

    assertEquals(2, colors.length);
    assertEquals(WordUtil.COLOR_ONE, colors[0]);
    assertEquals(WordUtil.COLOR_COMBO, colors[1]);
  }

  @Test
  public void colorsAvailableIncomplete() {
    List<LetterSource> availableLetters = Arrays.asList(
        TestUtil.createLetterSource("Z"),
        TestUtil.createLetterSource("Y"));
    int[] colors = WordUtil.getWordColors(availableLetters, "OK");

    assertEquals(2, colors.length);
    assertEquals(WordUtil.COLOR_MISSING, colors[0]);
    assertEquals(WordUtil.COLOR_MISSING, colors[1]);
  }

  @Test
  public void colorsAvailablePartial() {
    List<LetterSource> availableLetters = Arrays.asList(
        TestUtil.createLetterSource("Z"),
        TestUtil.createLetterSource("Y"),
        TestUtil.createLetterSource("X"),
        TestUtil.createLetterSource("W"),
        TestUtil.createLetterSource("V"));
    int[] colors = WordUtil.getWordColors(availableLetters, "OK");

    assertEquals(2, colors.length);
    assertEquals(WordUtil.COLOR_COMBO, colors[0]);
    assertEquals(WordUtil.COLOR_MISSING, colors[1]);
  }

  @Test
  public void colorsSpelledComplete() {
    LetterSource letterO = TestUtil.createLetterSource("O");
    LetterSource letterA = TestUtil.createLetterSource("A");
    LetterSource letterB = TestUtil.createLetterSource("B");
    LetterSource letterC = TestUtil.createLetterSource("C");
    LetterSource letterD = TestUtil.createLetterSource("D");
    letterD.available = 0;
    LetterSource letterE = TestUtil.createLetterSource("E");

    List<LetterSource> letters = Arrays.asList(
        letterO, letterA, letterB, letterC, letterD, letterE);
    int[] colors = WordUtil.getWordColors(letters, "OK");

    assertEquals(2, colors.length);
    assertEquals(WordUtil.COLOR_ONE, colors[0]);
    assertEquals(WordUtil.COLOR_COMBO, colors[1]);
  }

  @Test
  public void colorsSpelledIncomplete() {
    LetterSource letterZ = TestUtil.createLetterSource("Z");
    LetterSource letterY = TestUtil.createLetterSource("Y");

    List<LetterSource> letters = Arrays.asList(letterZ, letterY);
    int[] colors = WordUtil.getWordColors(letters, "OK");

    assertEquals(2, colors.length);
    assertEquals(WordUtil.COLOR_MISSING, colors[0]);
    assertEquals(WordUtil.COLOR_MISSING, colors[1]);
  }

  @Test
  public void colorsSpelledPartial() {
    LetterSource letterO = TestUtil.createLetterSource("O");
    LetterSource letterA = TestUtil.createLetterSource("A");
    LetterSource letterB = TestUtil.createLetterSource("B");
    LetterSource letterC = TestUtil.createLetterSource("C");
    LetterSource letterD = TestUtil.createLetterSource("D");
    letterD.available = 0;
    LetterSource letterE = TestUtil.createLetterSource("E");
    letterE.available = 0;

    List<LetterSource> letters = Arrays.asList(
        letterO, letterA, letterB, letterC, letterD, letterE);
    int[] colors = WordUtil.getWordColors(letters, "OK");

    assertEquals(2, colors.length);
    assertEquals(WordUtil.COLOR_ONE, colors[0]);
    assertEquals(WordUtil.COLOR_MISSING, colors[1]);
  }
}