package com.viettel.hdsimilar;

import java.util.Random;
import java.util.Set;

/**
 * @author minhtrung
 */
public class MinHash {

  private final HashFunction[] hashFunctions;
  private final int numHashFunctions;
  private final int featureSize;

  public MinHash(int featureSizes, int numHashFunctions) {
    this.numHashFunctions = numHashFunctions;
    this.hashFunctions = new HashFunction[numHashFunctions];

    this.featureSize = featureSizes;
    int m = (int) (Math.floor(Math.log(featureSizes) / Math.log(2.0)) + 1);
    GenerateHashFunctions(m);
  }

  private void GenerateHashFunctions(int m) {

    Random r = new Random();

    for (int i = 0; i < numHashFunctions; i++) {
      int a = 0;

      while (a % 2 == 1 || a <= 0) {
        a = r.nextInt();
      }
      int b = 0;
      int maxb = 1 << (32 - m);

      while (b <= 0) {
        b = (int) r.nextInt(maxb);
      }
      hashFunctions[i] = new HashFunction(a, b, m);
    }
  }

  public static double Similarity(int[] sig1, int[] sig2) {
    if (sig1.length != sig2.length) {
      throw new IllegalArgumentException("Size of signatures should be the same");
    }

    double sim = 0;
    for (int i = 0; i < sig1.length; i++) {
      if (sig1[i] == sig2[i]) {
        sim += 1;
      }
    }
    return sim / sig1.length;
  }

  public int[] GetMinHashStr(Set<String> words) {
    int[] minHashes = new int[numHashFunctions];
    for (int h = 0; h < numHashFunctions; h++) {
      minHashes[h] = Integer.MAX_VALUE;
    }
    for (int h = 0; h < numHashFunctions; h++) {
      for (String word : words) {
        int hash = this.hashFunctions[h].UHash(word.hashCode());
        minHashes[h] = Math.min(minHashes[h], hash);
      }
    }
    return minHashes;
  }

  public int[] GetMinHash(Set<Integer> wordIds) {
    int[] minHashes = new int[numHashFunctions];
    for (int h = 0; h < numHashFunctions; h++) {
      minHashes[h] = Integer.MAX_VALUE;
    }
    for (int h = 0; h < numHashFunctions; h++) {
      for (Integer id : wordIds) {
        int hash = this.hashFunctions[h].UHash(id);
        minHashes[h] = Math.min(minHashes[h], hash);
      }
    }
    return minHashes;
  }


  public class HashFunction {

    int a, b, u;

    public HashFunction(int a, int b, int u) {
      this.a = a;
      this.b = b;
      this.u = u;
    }

    public int UHash(int x) {
      return (a * x + b) >> (32 - u);
      //return (a * x + b) % u;
    }
  }
}

