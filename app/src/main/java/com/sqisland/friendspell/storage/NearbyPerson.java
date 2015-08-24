package com.sqisland.friendspell.storage;

public class NearbyPerson {
  public final String googlePlusId;
  public final String letter;
  public final String displayName;
  public String imageUrl;

  public enum State {
    UNKNOWN, NEW, REDISCOVERED, EXISTING
  }
  public State state = State.UNKNOWN;

  public NearbyPerson(
      String googlePlusId, String letter, String displayName, String imageUrl) {
    this.googlePlusId = googlePlusId;
    this.letter = letter;
    this.displayName = displayName;
    this.imageUrl = imageUrl;
  }

  public NearbyPerson(LetterSource source) {
    this.googlePlusId = source.googlePlusId;
    this.letter = source.letter;
    this.displayName = source.displayName;
  }

  public void updateState(LetterSource source) {
    if (source == null) {
      state = State.NEW;
      return;
    }
    state = (source.available == 1) ? State.EXISTING : State.REDISCOVERED;
  }
}