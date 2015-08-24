package com.sqisland.friendspell.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class WordSetTest {
  @Test(expected = IllegalArgumentException.class)
  public void nullLine() {
    new WordSet(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyLine() {
    new WordSet("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void tooFewFields() {
    new WordSet("0\t1");
  }

  @Test(expected = IllegalArgumentException.class)
  public void tooManyFields() {
    new WordSet("0\t1\t2\t3");
  }

  @Test
  public void seasons() {
    WordSet wordSet = new WordSet("seasons\tSeasons\tSPRING,SUMMER,AUTUMN,WINTER");
    assertEquals("seasons", wordSet.name);
    assertEquals("Seasons", wordSet.title);
    assertEquals(4, wordSet.words.length);
    assertEquals("SPRING", wordSet.words[0]);
    assertEquals("SUMMER", wordSet.words[1]);
    assertEquals("AUTUMN", wordSet.words[2]);
    assertEquals("WINTER", wordSet.words[3]);
    assertEquals("Seasons", wordSet.toString());
  }
}