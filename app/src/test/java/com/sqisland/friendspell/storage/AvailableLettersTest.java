package com.sqisland.friendspell.util;

import com.sqisland.friendspell.storage.AvailableLetters;
import com.sqisland.friendspell.storage.LetterSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(JUnit4.class)
public class AvailableLettersTest {
  @Test
  public void oneAvailableOneUnavailable() {
    LetterSource letterW = TestUtil.createLetterSource("W");
    letterW.available = 0;

    List<LetterSource> sources = Arrays.asList(
        TestUtil.createLetterSource("O"),
        TestUtil.createLetterSource("Z"),
        letterW);
    AvailableLetters availableLetters = new AvailableLetters(sources);

    assertEquals(2, availableLetters.size());
    assertNotNull(availableLetters.get("O"));
    assertNotNull(availableLetters.get("Z"));
    assertNull(availableLetters.get("W"));
  }

  @Test
  public void flatten() {
    LetterSource letterA1 = TestUtil.createLetterSource("A");
    letterA1.displayName = "Alice";
    LetterSource letterA2 = TestUtil.createLetterSource("A");
    letterA2.displayName = "Amy";
    LetterSource letterB = TestUtil.createLetterSource("B");
    letterB.displayName = "Belinda";

    List<LetterSource> sources = Arrays.asList(letterA2, letterB, letterA1);
    AvailableLetters availableLetters = new AvailableLetters(sources);

    assertEquals(2, availableLetters.size());
    assertNotNull(availableLetters.get("A"));
    assertEquals(2, availableLetters.get("A").size());
    assertNotNull(availableLetters.get("B"));
    assertEquals(1, availableLetters.get("B").size());
    assertNull(availableLetters.get("C"));

    List<LetterSource> flattened = availableLetters.getFlatList();
    assertEquals(3, flattened.size());
    assertEquals(letterA1, flattened.get(0));
    assertEquals(letterA2, flattened.get(1));
    assertEquals(letterB, flattened.get(2));
  }
}
