package aitoa.utils.graph;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.utils.RandomUtils;

/** Test the {@link IntSet} */
public class TestIntSet {

  /**
   * Test the node set for a given value of L
   *
   * @param L
   *          the length of the permutations
   */
  private static final void __testForL(final int L) {
    final IntSet n = new IntSet(L);
    final Random r = ThreadLocalRandom.current();

    final boolean[] avail = new boolean[L];

    // delete the last one
    n.fill();
    n.randomize(r);
    Arrays.fill(avail, true);
    for (int i = L; i > 0;) {
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.has(j));
      }
      Assert.assertEquals(i, n.size());
      Assert.assertFalse(n.isEmpty());
      Assert.assertFalse(avail[n.deleteLast()] ^= true);
      Assert.assertEquals(--i, n.size());
      Assert.assertTrue((i <= 0) == n.isEmpty());
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.has(j));
      }
    }
    for (final boolean b : avail) {
      Assert.assertFalse(b);
    }
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());

    // delete the nodes in forward direction
    n.fill();
    n.randomize(r);
    Arrays.fill(avail, true);
    for (int i = 0; i < L; i++) {
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.has(j));
      }
      Assert.assertEquals(L - i, n.size());
      Assert.assertFalse(n.isEmpty());
      Assert.assertTrue(n.has(i));
      avail[i] = false;
      n.delete(i);
      Assert.assertFalse(n.has(i));
      Assert.assertEquals(L - i - 1, n.size());
      Assert.assertTrue((i >= (L - 1)) == n.isEmpty());
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.has(j));
      }
    }
    for (final boolean b : avail) {
      Assert.assertFalse(b);
    }
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());

    // delete the nodes in backwards direction
    n.fill();
    n.randomize(r);
    Arrays.fill(avail, true);
    for (int i = L; (--i) >= 0;) {
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.has(j));
      }
      Assert.assertEquals(i + 1, n.size());
      Assert.assertFalse(n.isEmpty());
      Assert.assertTrue(n.has(i));
      avail[i] = false;
      n.delete(i);
      Assert.assertFalse(n.has(i));
      Assert.assertEquals(i, n.size());
      Assert.assertTrue((i <= 0) == n.isEmpty());
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.has(j));
      }
    }
    for (final boolean b : avail) {
      Assert.assertFalse(b);
    }
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());

    // delete a random node
    n.fill();
    n.randomize(r);
    Arrays.fill(avail, true);
    for (int i = L; i > 0;) {
      Assert.assertEquals(i, n.size());
      Assert.assertFalse(n.isEmpty());
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.has(j));
      }

      final int del = n.deleteRandom(r);

      Assert.assertFalse(avail[del] ^= true);
      Assert.assertEquals(--i, n.size());
      Assert.assertTrue((i <= 0) == n.isEmpty());
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.has(j));
      }
    }
    for (final boolean b : avail) {
      Assert.assertFalse(b);
    }
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());

    // check the nodes at the indices
    n.fill();
    n.randomize(r);
    boolean toggle = true;
    for (int i = L; i > 0; i--) {
      for (int j = i; (--j) >= 0;) {
        Assert.assertTrue(toggle == (avail[n.get(j)] ^= true));
      }

      final int del = n.deleteRandom(r);
      toggle ^= true;
      Assert.assertTrue(toggle == (avail[del] ^= true));
    }
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());

    // check adding of nodes
    for (int i = 0; i < L; i++) {
      Assert.assertFalse(n.has(i));
      Assert.assertEquals(i, n.size());
      n.add(i);
      Assert.assertTrue(n.has(i));
      Assert.assertEquals(i + 1, n.size());
      n.delete(i);
      Assert.assertFalse(n.has(i));
      Assert.assertEquals(i, n.size());
      n.add(i);
      Assert.assertTrue(n.has(i));
      Assert.assertEquals(i + 1, n.size());
    }

    Assert.assertFalse(n.isEmpty());
    Assert.assertEquals(L, n.size());
    n.clear();
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());

    // add in random order
    final int[] values = new int[L];
    for (int i = L; (--i) >= 0;) {
      values[i] = i;
    }
    RandomUtils.shuffle(r, values, 0, L);

    // check adding of nodes
    for (int i = 0; i < L; i++) {
      final int v = values[i];
      Assert.assertFalse(n.has(v));
      Assert.assertEquals(i, n.size());
      n.add(v);
      Assert.assertTrue(n.has(v));
      Assert.assertEquals(i + 1, n.size());
      n.delete(v);
      Assert.assertFalse(n.has(v));
      Assert.assertEquals(i, n.size());
      n.add(v);
      Assert.assertTrue(n.has(v));
      Assert.assertEquals(i + 1, n.size());
    }
  }

  /** test the set */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testFor_2() {
    TestIntSet.__testForL(2);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_3() {
    TestIntSet.__testForL(3);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_4() {
    TestIntSet.__testForL(4);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_5() {
    TestIntSet.__testForL(5);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_10() {
    TestIntSet.__testForL(10);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_100() {
    TestIntSet.__testForL(100);
  }
}
