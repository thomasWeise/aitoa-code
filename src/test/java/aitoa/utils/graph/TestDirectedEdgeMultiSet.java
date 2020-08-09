package aitoa.utils.graph;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.utils.RandomUtils;

/** Test the {@link DirectedEdgeMultiSet} */
@Ignore
public abstract class TestDirectedEdgeMultiSet {

  /**
   * Test the edge multi set of a given L-K combination
   *
   * @param e
   *          the set
   */
  private static final void
      testForLK(final DirectedEdgeMultiSet e) {
    e.clear();

    final ThreadLocalRandom r = ThreadLocalRandom.current();

    final int L = e.L;
    final int K = e.K;

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
      final int down = Math.max(0, L - K);
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
      for (int b = Math.min(L, K); (--b) >= 0;) {
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

  /**
   * Create the directed edge multiset
   *
   * @param L
   *          the highest node id
   * @param K
   *          the maximum number of edges per node
   * @return the set
   */
  protected abstract DirectedEdgeMultiSet create(final int L,
      final int K);

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor2x1() {
    TestDirectedEdgeMultiSet.testForLK(this.create(2, 1));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor2x2() {
    TestDirectedEdgeMultiSet.testForLK(this.create(2, 2));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor3x1() {
    TestDirectedEdgeMultiSet.testForLK(this.create(3, 1));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor3x2() {
    TestDirectedEdgeMultiSet.testForLK(this.create(3, 2));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor3x3() {
    TestDirectedEdgeMultiSet.testForLK(this.create(3, 3));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor4x1() {
    TestDirectedEdgeMultiSet.testForLK(this.create(4, 1));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor4x2() {
    TestDirectedEdgeMultiSet.testForLK(this.create(4, 2));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor4x3() {
    TestDirectedEdgeMultiSet.testForLK(this.create(4, 3));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor4x4() {
    TestDirectedEdgeMultiSet.testForLK(this.create(4, 4));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor10x2() {
    TestDirectedEdgeMultiSet.testForLK(this.create(10, 2));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor10x5() {
    TestDirectedEdgeMultiSet.testForLK(this.create(10, 5));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor100x30() {
    TestDirectedEdgeMultiSet.testForLK(this.create(100, 30));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor200x200() {
    TestDirectedEdgeMultiSet.testForLK(this.create(200, 200));
  }

  /** test the set */
  @Test(timeout = 3600000)
  public final void testFor70x200() {
    TestDirectedEdgeMultiSet.testForLK(this.create(70, 200));
  }
}
