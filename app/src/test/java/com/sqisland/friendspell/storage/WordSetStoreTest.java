package com.sqisland.friendspell.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(JUnit4.class)
public class WordSetStoreTest {
  @Test public void seasons() throws IOException {
    InputStreamReader reader = new InputStreamReader(
        WordSetStoreTest.class.getResourceAsStream("/word_sets.csv"));
    WordSetStore store = new WordSetStore(reader);

    List<WordSet> sets = store.getWordSets();
    assertEquals(2, sets.size());

    assertEquals("newyork", sets.get(0).name);
    assertEquals("New York", sets.get(0).title);
    assertEquals(5, sets.get(0).words.length);
    assertEquals("APPLE", sets.get(0).words[0]);
    assertEquals("BAGEL", sets.get(0).words[1]);
    assertEquals("PIZZA", sets.get(0).words[2]);
    assertEquals("TAXI", sets.get(0).words[3]);
    assertEquals("LIBERTY", sets.get(0).words[4]);

    assertEquals("seasons", sets.get(1).name);
    assertEquals("Seasons", sets.get(1).title);
    assertEquals(4, sets.get(1).words.length);
    assertEquals("SPRING", sets.get(1).words[0]);
    assertEquals("SUMMER", sets.get(1).words[1]);
    assertEquals("AUTUMN", sets.get(1).words[2]);
    assertEquals("WINTER", sets.get(1).words[3]);

    WordSet newyork = store.getWordSet("newyork");
    assertNotNull(newyork);
    assertEquals("New York", newyork.title);

    assertNull(store.getWordSet("what"));
  }
}