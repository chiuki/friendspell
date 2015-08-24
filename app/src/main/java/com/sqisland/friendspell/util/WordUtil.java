package com.sqisland.friendspell.util;

import com.sqisland.friendspell.storage.AvailableLetters;
import com.sqisland.friendspell.storage.LetterSource;
import com.sqisland.friendspell.storage.SpelledLetter;
import com.sqisland.friendspell.storage.SpelledWord;

import java.util.HashSet;
import java.util.List;

public abstract class WordUtil {
  public static final int COLOR_MISSING = rgb(0xBD, 0xBD, 0xBD);
  public static final int COLOR_ONE = rgb(0x4C, 0xAF, 0x50);
  public static final int COLOR_COMBO = rgb(0x03, 0xA9, 0xF4);

  public static SpelledWord spell(List<LetterSource> letters, String word) {
    AvailableLetters availableLetters = new AvailableLetters(letters);
    return spell(availableLetters, word);
  }

  public static int[] getWordColors(List<LetterSource> letters, String word) {
    int[] colors = new int[word.length()];

    if (letters == null) {
      for (int i = 0; i < colors.length; ++i) {
        colors[i] = COLOR_MISSING;
      }
      return colors;
    }

    SpelledWord spelledWord = WordUtil.spell(letters, word);
    return getWordColors(spelledWord.letters);
  }

  public static int[] getWordColors(List<SpelledLetter> letters) {
    int[] colors = new int[letters.size()];

    for (int i = 0; i < letters.size(); ++i) {
      SpelledLetter spelledLetter = letters.get(i);

      int color = COLOR_MISSING;
      if (spelledLetter != null) {
        if (spelledLetter.isSingleSource()) {
          color = COLOR_ONE;
        }
        if (spelledLetter.sources.size() == 4) {
          color = COLOR_COMBO;
        }
      }

      colors[i] = color;
    }

    return colors;
  }

  private static SpelledWord spell(AvailableLetters availableLetters, String word) {
    SpelledWord spelledWord = new SpelledWord();

    HashSet<String> used = new HashSet<>();
    int missing = 0;
    for (int i = 0; i < word.length(); ++i) {
      String letter = word.substring(i, i + 1);

      if (availableLetters.containsKey(letter)) {
        SpelledLetter spelledLetter = new SpelledLetter(letter);
        List<LetterSource> sources = availableLetters.get(letter);
        LetterSource source = sources.get(0);
        spelledLetter.sources.add(source);

        if (sources.size() == 1) {
          availableLetters.remove(letter);
        } else {
          sources.remove(0);
          availableLetters.put(letter, sources);
        }

        spelledWord.letters.add(spelledLetter);
        used.add(source.googlePlusId);
      } else {
        spelledWord.letters.add(null);
        missing += 1;
      }
    }

    if (missing == 0) {
      return spelledWord;
    }

    List<LetterSource> remaining = availableLetters.getFlatList();
    int remainingPosition = 0;
    for (int i = 0; i < spelledWord.letters.size(); ++i) {
      SpelledLetter spelledLetter = spelledWord.letters.get(i);
      if (spelledLetter == null) {
        String letter = word.substring(i, i + 1);
        spelledLetter = new SpelledLetter(letter);
        for (; remainingPosition < remaining.size(); ++remainingPosition) {
          LetterSource source = remaining.get(remainingPosition);
          if (!used.contains(source.googlePlusId)) {
            spelledLetter.sources.add(source);
            if (spelledLetter.sources.size() == 4) {
              remainingPosition += 1;
              break;
            }
          }
        }
        if (spelledLetter.sources.size() > 0) {
          spelledWord.letters.set(i, spelledLetter);
        }
      }
    }

    return spelledWord;
  }

  // Copy from Color.rgb() to avoid Android dependency
  private static int rgb(int red, int green, int blue) {
    return (0xFF << 24) | (red << 16) | (green << 8) | blue;
  }
}