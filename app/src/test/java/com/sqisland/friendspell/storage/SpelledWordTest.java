package com.sqisland.friendspell.storage;

import com.sqisland.friendspell.util.TestUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SpelledWordTest {
  @Test
  public void empty() {
    SpelledWord word = new SpelledWord();

    assertNotNull(word.letters);
    assertEquals(0, word.letters.size());
  }

  @Test
  public void singleSources() {
    SpelledWord word = new SpelledWord();
    word.letters = Arrays.asList(
        TestUtil.createSpelledLetter("O", "O"),
        TestUtil.createSpelledLetter("K", "K"));

    assertTrue(word.isComplete());
  }

  @Test
  public void singleAndMultiSource() {
    SpelledWord word = new SpelledWord();
    word.letters = Arrays.asList(
        TestUtil.createSpelledLetter("O", "O"),
        TestUtil.createSpelledLetter("K", "A", "B", "C", "D"));

    assertTrue(word.isComplete());
  }

  @Test
  public void multiSources() {
    SpelledWord word = new SpelledWord();
    word.letters = Arrays.asList(
        TestUtil.createSpelledLetter("O", "W", "X", "Y", "Z"),
        TestUtil.createSpelledLetter("K", "A", "B", "C", "D"));

    assertTrue(word.isComplete());
  }

  @Test
  public void incomplete() {
    SpelledWord word = new SpelledWord();
    word.letters = Arrays.asList(
        TestUtil.createSpelledLetter("O", "O"),
        TestUtil.createSpelledLetter("K", "A"));

    assertFalse(word.isComplete());
  }
}