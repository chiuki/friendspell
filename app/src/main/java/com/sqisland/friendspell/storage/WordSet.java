package com.sqisland.friendspell.storage;

public class WordSet {
  public String name;
  public String title;
  public String[] words;

  public WordSet(String line) {
    if (line == null) {
      throw new IllegalArgumentException("Line cannot be null");
    }

    String[] fields = line.split("\t");
    if (fields.length != 3) {
      throw new IllegalArgumentException("WordSet needs 3 fields, found: " + fields.length);
    }

    this.name = fields[0];
    this.title = fields[1];
    this.words = fields[2].split(",");
  }

  @Override
  public String toString() {
    return title;
  }
}