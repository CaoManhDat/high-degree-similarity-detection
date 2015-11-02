package com.viettel.hdsimilar;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Cao Manh Dat
 */
public class Indexer {

  private Comparator<String> comparator;
  private HashMap<Integer, int[]> corpusSign = new HashMap<Integer, int[]>(100000);
  private MinHash mh = new MinHash(2147482949, 2000);
  private BucketManager bucketManager = new BucketManager();
  private float minJaccard;

  public Indexer(float minJaccard) {
    this.minJaccard = minJaccard;
    comparator = String.CASE_INSENSITIVE_ORDER;
  }

  public Indexer(float minJaccard, Comparator<String> comparator) {
    this.minJaccard = minJaccard;
    this.comparator = comparator;
  }

  public void index(int docIndex, String[] doc) {
    TreeSet<String> set = toSet(doc);
    int numPrefix = Math.round((1 - minJaccard) * set.size()) + 1;
    Iterator<String> iterator = set.iterator();
    for (int i = 1; i <= numPrefix && iterator.hasNext(); i++) {
      BucketManager.Key key = createKey(iterator.next(), i, set.size() - 1);
      bucketManager.add(key, docIndex);
    }
    corpusSign.put(docIndex, mh.GetMinHashStr(set));
  }

  private BucketManager.Key createKey(String word, int position, int suffixLength) {
    return new BucketManager.HashKey(word, position, suffixLength);
  }

  public TreeSet<String> toSet(String[] doc) {
    TreeSet<String> set = new TreeSet<String>(comparator);
    Collections.addAll(set, doc);
    return set;
  }

  public Set<Integer> getSimilarWithoutCheck(String[] doc) {
    TreeSet<String> set = toSet(doc);
    Set<Integer> result = getSimilarWithoutCheck(set);
    return result;
  }

  private Set<Integer> getSimilarWithoutCheck(TreeSet<String> set) {
    Set<Integer> result = new HashSet<Integer>();
    int l = set.size();
    int numPrefix = Math.round((1 - minJaccard) * set.size()) + 1;
    String[] prefixes = new String[numPrefix];
    Iterator<String> iterator = set.iterator();
    for (int i = 0; i < numPrefix && iterator.hasNext(); i++) {
      prefixes[i] = iterator.next();
    }

    for (int i = 1; i <= numPrefix && iterator.hasNext(); i++) {
      int p = set.size() - i;
      // q have length < p
      boolean isContinue = true;
      for (int q = p; isContinue; q--) {
        isContinue = false;
        for (int j = 1; (float) (q + 1) / (l - 1 + j) >= minJaccard; j++) {
          isContinue = true;
          BucketManager.Key key = createKey(prefixes[i - 1], j, q);
          result.addAll(bucketManager.get(key));
        }
      }
      isContinue = true;
      for (int q = p + 1; isContinue; q++) {
        isContinue = false;
        for (int j = 1; (float) (l - i + 1) / (i + j - 1 + q) >= minJaccard; j++) {
          isContinue = true;
          BucketManager.Key key = createKey(prefixes[i - 1], j, q);
          result.addAll(bucketManager.get(key));
        }
      }
    }

    return result;
  }

  public Set<Integer> getSimilar(String[] doc) {
    TreeSet<String> set = toSet(doc);
    int[] docMinHash = mh.GetMinHashStr(set);
    Set<Integer> result = getSimilarWithoutCheck(set);
    Iterator<Integer> iterator = result.iterator();
    while (iterator.hasNext()) {
      int[] doc2MinHash = corpusSign.get(iterator.next());
      if (MinHash.Similarity(docMinHash, doc2MinHash) < (minJaccard - 0.05)) {
        //        System.out.println(MinHash.Similarity(docMinHash,doc2MinHash));
        iterator.remove();
      }
    }
    return result;
  }

}
