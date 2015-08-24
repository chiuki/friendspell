package com.sqisland.friendspell.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(JUnit4.class)
public class LetterSourceTest {
  @Test
  public void empty() {
    LetterSource source = new LetterSource();

    assertNull(source.letter);
    assertNull(source.displayName);
    assertNull(source.googlePlusId);
    assertEquals(1, source.available);
    assertNull(source.toString());
  }

  @Test
  public void fromNearbyPerson() {
    NearbyPerson person = new NearbyPerson("googlePlusId", "N", "Name", null);
    LetterSource source = new LetterSource(person);

    assertEquals("N", source.letter);
    assertEquals("Name", source.displayName);
    assertEquals("googlePlusId", source.googlePlusId);
    assertEquals(1, source.available);
    assertEquals("Name", source.toString());
  }
}