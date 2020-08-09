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
   * @param pL
   *          the length of the permutations
   */
  private static final void testForL(final int pL) {
    final IntSet n = new IntSet(pL);
    final Random r = ThreadLocalRandom.current();

    final boolean[] avail = new boolean[pL];

    // delete the last one
    n.fill();
    n.shuffle(r);
    Arrays.fill(avail, true);
    for (int i = pL; i > 0;) {
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
    n.shuffle(r);
    Arrays.fill(avail, true);
    for (int i = 0; i < pL; i++) {
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.has(j));
      }
      Assert.assertEquals(pL - i, n.size());
      Assert.assertFalse(n.isEmpty());
      Assert.assertTrue(n.has(i));
      avail[i] = false;
      n.delete(i);
      Assert.assertFalse(n.has(i));
      Assert.assertEquals(pL - i - 1, n.size());
      Assert.assertTrue((i >= (pL - 1)) == n.isEmpty());
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
    n.shuffle(r);
    Arrays.fill(avail, true);
    for (int i = pL; (--i) >= 0;) {
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
    n.shuffle(r);
    Arrays.fill(avail, true);
    for (int i = pL; i > 0;) {
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
    n.shuffle(r);
    boolean toggle = true;
    for (int i = pL; i > 0; i--) {
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
    for (int i = 0; i < pL; i++) {
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
    Assert.assertEquals(pL, n.size());
    n.clear();
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());

    // add in random order
    final int[] values = new int[pL];
    for (int i = pL; (--i) >= 0;) {
      values[i] = i;
    }
    RandomUtils.shuffle(r, values, 0, pL);

    // check adding of nodes
    for (int i = 0; i < pL; i++) {
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
  public final void testFor2() {
    TestIntSet.testForL(2);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor3() {
    TestIntSet.testForL(3);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor4() {
    TestIntSet.testForL(4);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor5() {
    TestIntSet.testForL(5);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor10() {
    TestIntSet.testForL(10);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor100() {
    TestIntSet.testForL(100);
  }
}
