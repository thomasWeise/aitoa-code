package aitoa.algorithms.aco;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

/** Test the {@link NodeSet} */
public class TestNodeSet {

  /**
   * Test the node set for a given value of L
   *
   * @param L
   *          the length of the permutations
   */
  private static final void __testForL(final int L) {
    final NodeSet n = new NodeSet(L);
    final Random r = ThreadLocalRandom.current();

    final boolean[] avail = new boolean[L];

    // delete the last one
    n.fill(r);
    Arrays.fill(avail, true);
    for (int i = L; i > 0;) {
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.isNodeAvailable(j));
      }
      Assert.assertEquals(i, n.size());
      Assert.assertFalse(n.isEmpty());
      Assert.assertFalse(avail[n.deleteLast()] ^= true);
      Assert.assertEquals(--i, n.size());
      Assert.assertTrue((i <= 0) == n.isEmpty());
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.isNodeAvailable(j));
      }
    }
    for (final boolean b : avail) {
      Assert.assertFalse(b);
    }
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());

    // delete the nodes in forward direction
    n.fill(r);
    Arrays.fill(avail, true);
    for (int i = 0; i < L; i++) {
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.isNodeAvailable(j));
      }
      Assert.assertEquals(L - i, n.size());
      Assert.assertFalse(n.isEmpty());
      Assert.assertTrue(n.isNodeAvailable(i));
      avail[i] = false;
      n.deleteNode(i);
      Assert.assertFalse(n.isNodeAvailable(i));
      Assert.assertEquals(L - i - 1, n.size());
      Assert.assertTrue((i >= (L - 1)) == n.isEmpty());
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.isNodeAvailable(j));
      }
    }
    for (final boolean b : avail) {
      Assert.assertFalse(b);
    }
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());

    // delete the nodes in backwards direction
    n.fill(r);
    Arrays.fill(avail, true);
    for (int i = L; (--i) >= 0;) {
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.isNodeAvailable(j));
      }
      Assert.assertEquals(i + 1, n.size());
      Assert.assertFalse(n.isEmpty());
      Assert.assertTrue(n.isNodeAvailable(i));
      avail[i] = false;
      n.deleteNode(i);
      Assert.assertFalse(n.isNodeAvailable(i));
      Assert.assertEquals(i, n.size());
      Assert.assertTrue((i <= 0) == n.isEmpty());
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.isNodeAvailable(j));
      }
    }
    for (final boolean b : avail) {
      Assert.assertFalse(b);
    }
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());

    // delete a random node
    n.fill(r);
    Arrays.fill(avail, true);
    for (int i = L; i > 0;) {
      Assert.assertEquals(i, n.size());
      Assert.assertFalse(n.isEmpty());
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.isNodeAvailable(j));
      }

      final int del = n.deleteRandom(r);

      Assert.assertFalse(avail[del] ^= true);
      Assert.assertEquals(--i, n.size());
      Assert.assertTrue((i <= 0) == n.isEmpty());
      for (int j = avail.length; (--j) >= 0;) {
        Assert.assertTrue(avail[j] == n.isNodeAvailable(j));
      }
    }
    for (final boolean b : avail) {
      Assert.assertFalse(b);
    }
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());

    // check the nodes at the indices
    n.fill(r);
    boolean toggle = true;
    for (int i = L; i > 0; i--) {
      for (int j = i; (--j) >= 0;) {
        Assert.assertTrue(
            toggle == (avail[n.getNodeAt(j)] ^= true));
      }

      final int del = n.deleteRandom(r);
      toggle ^= true;
      Assert.assertTrue(toggle == (avail[del] ^= true));
    }
    Assert.assertTrue(n.isEmpty());
    Assert.assertEquals(0, n.size());
  }

  /** test the set */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testFor_2() {
    TestNodeSet.__testForL(2);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_3() {
    TestNodeSet.__testForL(3);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_4() {
    TestNodeSet.__testForL(4);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_5() {
    TestNodeSet.__testForL(5);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_10() {
    TestNodeSet.__testForL(10);
  }

  /** test the set */
  @Test(timeout = 3600000)
  @SuppressWarnings("static-method")
  public final void testFor_100() {
    TestNodeSet.__testForL(100);
  }
}
