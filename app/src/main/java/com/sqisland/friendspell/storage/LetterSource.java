package com.sqisland.friendspell.storage;

public class LetterSource {
  public Long _id;
  public String googlePlusId;
  public String letter;
  public String displayName;
  public int available = 1;

  public LetterSource() {
  }

  public LetterSource(NearbyPerson other) {
    this.googlePlusId = other.googlePlusId;
    this.letter = other.letter;
    this.displayName = other.displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}