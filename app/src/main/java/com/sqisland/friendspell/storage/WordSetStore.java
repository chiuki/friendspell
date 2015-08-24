package com.sqisland.friendspell.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WordSetStore {
  private final ArrayList<WordSet> sets = new ArrayList<>();

  public WordSetStore(InputStreamReader reader) throws IOException {
    String line;
    BufferedReader bufferedReader = new BufferedReader(reader);
    while ((line = bufferedReader.readLine()) != null) {
      WordSet set = new WordSet(line);
      sets.add(set);
    }
  }

  public List<WordSet> getWordSets() {
    return sets;
  }

  public WordSet getWordSet(String name) {
    for (WordSet set : sets) {
      if (set.name.equals(name)) {
        return set;
      }
    }
    return null;
  }
}
