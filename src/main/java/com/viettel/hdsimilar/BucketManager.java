package com.viettel.hdsimilar;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Objects;

/**
 * @author Cao Manh Dat
 */
public class BucketManager {
  private Multimap<Key, Integer> buckets = ArrayListMultimap.create();

  public void add(Key key, int docIndex) {
    buckets.put(key, docIndex);
  }

  public Collection<Integer> get(Key key) {
    return buckets.get(key);
  }


  public static class Key<T> {
    public final T word;
    public final int index;
    public final int suffixLength;

    public Key(T word, int index, int suffixLength) {
      this.word = word;
      this.index = index;
      this.suffixLength = suffixLength;
    }

    @Override public int hashCode() {
      return Objects.hash(word, index, suffixLength);
    }

    @Override public boolean equals(Object obj) {
      if (obj == this)
        return true;
      if (obj instanceof Key) {
        Key key = (Key) obj;
        return Objects.equals(word, key.word) && Objects.equals(index, key.index) && Objects
            .equals(suffixLength, key.suffixLength);
      }
      return false;
    }

    @Override public String toString() {
      return "(" + word + "," + index + "," + suffixLength + ")";
    }
  }


  public static class HashKey extends Key<Integer> {
    public HashKey(String word, int index, int suffixLength) {
      super(word.hashCode(), index, suffixLength);
    }

    public HashKey(Integer word, int index, int suffixLength) {
      super(word, index, suffixLength);
    }
  }


  public static class WordKey extends Key<String> {

    public WordKey(String word, int index, int suffixLength) {
      super(word, index, suffixLength);
    }
  }

}
