package com.sqisland.friendspell.storage;

import com.sqisland.friendspell.util.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class NearbyPersonTest {
  private NearbyPerson person;

  @Before
  public void setUp() {
    person = new NearbyPerson("googlePlusId", "A", "Alphabet", null);
  }

  @Test
  public void fromLetterSource() {
    LetterSource source = TestUtil.createLetterSource("L");
    NearbyPerson nearbyPerson = new NearbyPerson(source);
    assertEquals("L", nearbyPerson.letter);
    assertEquals("id_L", nearbyPerson.googlePlusId);
    assertEquals("LL", nearbyPerson.displayName);
    assertEquals(NearbyPerson.State.UNKNOWN, person.state);
  }

  @Test
  public void stateUnknown() {
    assertEquals(NearbyPerson.State.UNKNOWN, person.state);
  }

  @Test
  public void stateNew() {
    person.updateState(null);
    assertEquals(NearbyPerson.State.NEW, person.state);
  }

  @Test
  public void stateExisting() {
    LetterSource source = new LetterSource();
    source.available = 1;
    person.updateState(source);
    assertEquals(NearbyPerson.State.EXISTING, person.state);
  }

  @Test
  public void stateRediscovered() {
    LetterSource source = new LetterSource();
    source.available = 0;
    person.updateState(source);
    assertEquals(NearbyPerson.State.REDISCOVERED, person.state);
  }
}