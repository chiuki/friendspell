package com.sqisland.friendspell.storage;

import java.util.ArrayList;
import java.util.List;

public class SpelledWord {
  public Long _id;
  public List<SpelledLetter> letters = new ArrayList<>();

  public boolean isComplete() {
    for (int i = 0; i < letters.size(); ++i) {
      SpelledLetter spelledLetter = letters.get(i);
      if (spelledLetter == null || !spelledLetter.isComplete()) {
        return false;
      }
    }
    return true;
  }
}