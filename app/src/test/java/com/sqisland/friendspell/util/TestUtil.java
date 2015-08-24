package com.sqisland.friendspell.util;

import com.sqisland.friendspell.storage.LetterSource;
import com.sqisland.friendspell.storage.SpelledLetter;

public abstract class TestUtil {
  public static LetterSource createLetterSource(String letter) {
    LetterSource letterSource = new LetterSource();
    letterSource.letter = letter;
    letterSource.googlePlusId = "id_" + letter;
    letterSource.displayName = letter + letter;
    return letterSource;
  }

  public static SpelledLetter createSpelledLetter(String letter, String... sourceLetters) {
    SpelledLetter spelledLetter = new SpelledLetter(letter);
    for (String sourceLetter : sourceLetters) {
      spelledLetter.sources.add(TestUtil.createLetterSource(sourceLetter));
    }
    return spelledLetter;
  }
}
