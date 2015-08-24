package com.sqisland.friendspell.storage;

import java.util.ArrayList;
import java.util.List;

public class SpelledLetter {
  public final String letter;
  public List<LetterSource> sources = new ArrayList<>();

  public SpelledLetter(String letter) {
    this.letter = letter;
  }

  public boolean isSingleSource() {
    return (sources.size() == 1 && sources.get(0).letter.equals(letter));
  }

  public boolean isComplete() {
    return (isSingleSource() || sources.size() == 4);
  }
}