package aitoa.algorithms.aco;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.utils.RandomUtils;

/** Test the {@link EdgeMultiSetLK} */
public class TestEdgeMultiSetLK {

  /**
   * Test the edge multi set of a given L-K combination
   *
   * @param L
   *          the length of the permutations
   * @param K
   *          the size of the per-node set
   */
  private static final void __testForLK(final int L,
      final int K) {
    final EdgeMultiSetLK e = new EdgeMultiSetLK(L, K);
    e.clear();

// check for each individual edge
    for (int a = -1; a < L; a++) {
      for (int b = 0; b < L; b++) {
        Assert.assertEquals(0, e.getEdgeCount(a, b));
        e.addEdge(a, b);
        Assert.assertEquals(1, e.getEdgeCount(a, b));
        e.removeEdge(a, b);
        Assert.assertEquals(0, e.getEdgeCount(a, b));
        for (int i = 1; i <= K; i++) {
          e.addEdge(a, b);
          Assert.assertEquals(i, e.getEdgeCount(a, b));
        }
        for (int i = K; i > 0; i--) {
          Assert.assertEquals(i, e.getEdgeCount(a, b));
          e.removeEdge(a, b);
        }
        Assert.assertEquals(0, e.getEdgeCount(a, b));
      }

      // now deterministic upwards
      for (int b = 0; b < L; b++) {
        if (b >= K) {
          Assert.assertEquals(1, e.getEdgeCount(a, b - K));
          e.removeEdge(a, b - K);
          Assert.assertEquals(0, e.getEdgeCount(a, b - K));
        }
        Assert.assertEquals(0, e.getEdgeCount(a, b));
        e.addEdge(a, b);
        Assert.assertEquals(1, e.getEdgeCount(a, b));
      }

      // remove the last K added edges again
      final int down = L - K;
      for (int b = L; (--b) >= down;) {
        Assert.assertEquals(1, e.getEdgeCount(a, b));
        e.removeEdge(a, b);
        Assert.assertEquals(0, e.getEdgeCount(a, b));
      }

      // now adding edges backwards deterministically
      for (int b = L; (--b) >= 0;) {
        if (b < down) {
          Assert.assertEquals(1, e.getEdgeCount(a, b + K));
          e.removeEdge(a, b + K);
          Assert.assertEquals(0, e.getEdgeCount(a, b + K));
        }
        Assert.assertEquals(0, e.getEdgeCount(a, b));
        e.addEdge(a, b);
        Assert.assertEquals(1, e.getEdgeCount(a, b));
      }

      // finally, remove last K edges again
      for (int b = K; (--b) >= 0;) {
        Assert.assertEquals(1, e.getEdgeCount(a, b));
        e.removeEdge(a, b);
        Assert.assertEquals(0, e.getEdgeCount(a, b));
      }
    }

// there should not be any edge left
    for (int a = -1; a < L; a++) {
      for (int b = 0; b < L; b++) {
        Assert.assertEquals(0, e.getEdgeCount(a, b));
      }
    }

// check for random edges
    final Random r = ThreadLocalRandom.current();
    final int[][] matrix = new int[L + 1][L + 1];

    for (int i = 100000; (--i) >= 0;) {
      final int a = r.nextInt(L + 1);
      final int[] ax = matrix[a];
      if ((ax[0] >= K) || ((ax[0] > 0) && r.nextBoolean())) {
        // remove random edge
        int b = r.nextInt(L);
        while (ax[b + 1] <= 0) {
          b = (b + 1) % L;
        }
        --ax[0];
        Assert.assertEquals(ax[b + 1], e.getEdgeCount(a - 1, b));
        e.removeEdge(a - 1, b);
        Assert.assertEquals(--ax[b + 1],
            e.getEdgeCount(a - 1, b));
      } else {
        // add random edge
        final int b = r.nextInt(L);
        Assert.assertEquals(ax[b + 1], e.getEdgeCount(a - 1, b));
        e.addEdge(a - 1, b);
        Assert.assertEquals(++ax[b + 1],
            e.getEdgeCount(a - 1, b));
        ++ax[0];
      }
    }

    // now remove all edges again
    for (int a = -1; a < L; a++) {
      final int[] ax = matrix[a + 1];
      for (int b = 0; b < (ax.length - 1); b++) {
        for (int c = ax[b + 1]; c > 0; c--) {
          Assert.assertEquals(c, e.getEdgeCount(a, b));
          e.removeEdge(a, b);
        }
        Assert.assertEquals(0, e.getEdgeCount(a, b));
      }
    }

    // there should not be any edge left
    for (int a = -1; a < L; a++) {
      for (int b = 0; b < L; b++) {
        Assert.assertEquals(0, e.getEdgeCount(a, b));
      }
    }

    // now test it with a random permutation
    final int[] pi = new int[L];
    for (int i = L; (--i) >= 0;) {
      pi[i] = i;
    }
    RandomUtils.shuffle(r, pi, 0, L);

    e.addPermutation(pi);
    int last = -1;
    for (final int i : pi) {
      Assert.assertEquals(1, e.getEdgeCount(last, i));
      last = i;
    }

    e.removePermutation(pi);

    // there should not be any edge left
    for (int a = -1; a < L; a++) {
      for (int b = 0; b < L; b++) {
        Assert.assertEquals(0, e.getEdgeCount(a, b));
      }
    }
  }

  /** test the set */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testFor_2_1() {
    TestEdgeMultiSetLK.__testForLK(2, 1);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_2_2() {
    TestEdgeMultiSetLK.__testForLK(2, 2);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_3_1() {
    TestEdgeMultiSetLK.__testForLK(3, 1);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_3_2() {
    TestEdgeMultiSetLK.__testForLK(3, 2);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_3_3() {
    TestEdgeMultiSetLK.__testForLK(3, 3);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_4_1() {
    TestEdgeMultiSetLK.__testForLK(4, 1);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_4_2() {
    TestEdgeMultiSetLK.__testForLK(4, 2);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_4_3() {
    TestEdgeMultiSetLK.__testForLK(4, 3);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_4_4() {
    TestEdgeMultiSetLK.__testForLK(4, 4);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_10_2() {
    TestEdgeMultiSetLK.__testForLK(10, 2);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_10_5() {
    TestEdgeMultiSetLK.__testForLK(10, 5);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_100_30() {
    TestEdgeMultiSetLK.__testForLK(100, 30);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_200_200() {
    TestEdgeMultiSetLK.__testForLK(200, 200);
  }
}
