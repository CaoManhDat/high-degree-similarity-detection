package com.viettel.hdsimilar;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IndexerTest {

  String[] doc1 = new String[]{"a","b","c","d","e","f","g","h","i","j","k"};
  String[] doc2 = new String[]{"l","b","c","d","e","f","g","h","i","j","k"}; // doc1 and doc2 have jaccard = 10 / 12 = 0.83
  String[] doc3 = new String[]{"i","j","k"}; // doc3 and doc1 hava jaccard = 3 / 11 = 0.27
  String[] doc4 = new String[]{"a","l","m","n","o","p","q","r","s","t","u"}; // doc1 and doc4 have jaccard = 1 / 21 = 0.04
  String[] doc5 = new String[]{"g","b","c","d","e","f","g","h","i","j","k"}; // doc1 and doc5 have jaccard = 10 / 12 = 0.83

  @Test
  public void testGetSimilarWithoutCheck() {
    Indexer indexer = new Indexer(0.8f);
    indexer.index(1,doc1);
    indexer.index(2,doc2);
    indexer.index(3,doc3);
    indexer.index(4,doc4);
    indexer.index(5,doc5);
//    System.out.println(indexer.getSimilarWithoutCheck(doc2));
    Assert.assertTrue(indexer.getSimilarWithoutCheck(doc2).contains(1));
    Assert.assertFalse(indexer.getSimilarWithoutCheck(doc2).contains(4));
    Assert.assertTrue(indexer.getSimilarWithoutCheck(doc4).contains(1));
  }

  @Test
  public void testGetSimilarWithCheck() {
    Indexer indexer = new Indexer(0.8f);
    indexer.index(1,doc1);
    indexer.index(2,doc2);
    indexer.index(3,doc3);
    indexer.index(4,doc4);
    indexer.index(5,doc5);
//    System.out.println(indexer.getSimilar(doc2));
    Assert.assertTrue(indexer.getSimilar(doc2).contains(1));
    Assert.assertFalse(indexer.getSimilar(doc2).contains(4));
    Assert.assertFalse(indexer.getSimilar(doc4).contains(1));
  }

  @Test
  public void testMinHash() {
    Indexer indexer = new Indexer(0.8f);
    Set<String> set1 = indexer.toSet(doc1);
    Set<String> set2 = indexer.toSet(doc2);
    MinHash mh = new MinHash(2147482949, 200);
    int[] minHashDoc1 = mh.GetMinHashStr(set1);
    int[] minHashDoc2 = mh.GetMinHashStr(set2);
    System.out.println(MinHash.Similarity(minHashDoc1,minHashDoc2));
  }
}
