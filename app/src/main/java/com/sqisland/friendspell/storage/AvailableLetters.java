package com.sqisland.friendspell.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AvailableLetters extends HashMap<String, List<LetterSource>> {
  private static final Comparator COMPARATOR = new Comparator<LetterSource>() {
    @Override
    public int compare(LetterSource lhs, LetterSource rhs) {
      return lhs.displayName.compareTo(rhs.displayName);
    }
  };

  public AvailableLetters(List<LetterSource> letters) {
    super();

    for (LetterSource letter : letters) {
      if (letter.available != 1) {
        continue;
      }

      List<LetterSource> list = get(letter.letter);
      if (list == null) {
        list = new ArrayList<>();
      }
      list.add(letter);

      put(letter.letter, list);
    }

    for (String key : keySet()) {
      List<LetterSource> value = get(key);
      Collections.sort(value, COMPARATOR);
      put(key, value);
    }
  }

  public List<LetterSource> getFlatList() {
    ArrayList<LetterSource> flattened = new ArrayList<>();
    for (List<LetterSource> letterSources : values()) {
      flattened.addAll(letterSources);
    }
    Collections.sort(flattened, COMPARATOR);
    return flattened;
  }
}
