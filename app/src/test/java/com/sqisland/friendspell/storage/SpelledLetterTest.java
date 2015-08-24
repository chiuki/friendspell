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
public class SpelledLetterTest {
  @Test
  public void emptySources() {
    SpelledLetter letter = new SpelledLetter("A");

    assertEquals("A", letter.letter);
    assertNotNull(letter.sources);
    assertEquals(0, letter.sources.size());
    assertFalse(letter.isSingleSource());
    assertFalse(letter.isComplete());
  }

  @Test
  public void singleSource() {
    SpelledLetter letter = new SpelledLetter("B");
    letter.sources = Arrays.asList(TestUtil.createLetterSource("B"));

    assertEquals("B", letter.letter);
    assertNotNull(letter.sources);
    assertTrue(letter.isSingleSource());
    assertTrue(letter.isComplete());
  }

  @Test
  public void multiSource() {
    SpelledLetter letter = new SpelledLetter("Z");
    letter.sources = Arrays.asList(
        TestUtil.createLetterSource("A"),
        TestUtil.createLetterSource("B"),
        TestUtil.createLetterSource("C"),
        TestUtil.createLetterSource("D"));

    assertEquals("Z", letter.letter);
    assertFalse(letter.isSingleSource());
    assertTrue(letter.isComplete());
  }

  @Test
  public void partial() {
    SpelledLetter letter = new SpelledLetter("X");
    letter.sources = Arrays.asList(TestUtil.createLetterSource("A"));

    assertEquals("X", letter.letter);
    assertFalse(letter.isSingleSource());
    assertFalse(letter.isComplete());
  }
}