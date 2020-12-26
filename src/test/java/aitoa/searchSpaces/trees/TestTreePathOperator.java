package aitoa.searchSpaces.trees;

import java.util.IdentityHashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.ObjectTest;
import aitoa.TestTools;
import aitoa.searchSpaces.trees.math.TestFunctionNodeTypeSet;
import aitoa.utils.math.Statistics;

/** A test for the tree path operator */
@Ignore
public class TestTreePathOperator
    extends ObjectTest<TreePathOperator> {

  /** the type set */
  private final NodeTypeSet<?> mTypeSet;
  /** the nullary operator */
  private final TreeNullaryOperator mNullary;
  /** the unary operator */
  private final TreePathOperator mOp;

  /**
   * create the unary operator
   *
   * @param pMaxDepth
   *          the maximum depth
   */
  public TestTreePathOperator(final int pMaxDepth) {
    super();

    this.mTypeSet =
        TestFunctionNodeTypeSet.makeMathNodeTypeSet(true);
    this.mNullary =
        new TreeNullaryOperator(this.mTypeSet, pMaxDepth);
    this.mOp = new TreePathOperator(pMaxDepth);
  }

  /** {@inheritDoc} */
  @Override
  protected TreePathOperator getInstance() {
    return this.mOp;
  }

  /**
   * create a valid tree
   *
   * @return the tree
   */
  private Node createValid() {
    final Node[] res = new Node[1];
    this.mNullary.apply(res, ThreadLocalRandom.current());
    return res[0];
  }

  /**
   * insert tree via the map
   *
   * @param n
   *          the node
   * @param map
   *          the map
   */
  private static final void index(final Node n,
      final IdentityHashMap<Node, Integer> map) {
    if (map.put(n, Integer.valueOf(map.size())) != null) {
      Assert.fail("node type " //$NON-NLS-1$
          + n.getClass().getSimpleName() + //
          " already exists.");//$NON-NLS-1$
    }
    for (int i = n.getChildCount(); (--i) >= 0;) {
      TestTreePathOperator.index(n.getChild(i), map);
    }
  }

  /**
   * Ensure that sufficient different nodes are created and that
   * the depth of the trees is correct
   */
  @Test(timeout = 3600000)
  public void testPathDistribution() {
    final TreePathOperator op = this.getInstance();
    final Random random = ThreadLocalRandom.current();
    final IdentityHashMap<Node, Integer> map =
        new IdentityHashMap<>();

    for (int i = 50; (--i) >= 0;) {
// create, validate, and flatten tree
      final Node tree = this.createValid();
      Assert.assertNotNull(tree);

      final int w = tree.weight();
      TestTools.assertGreater(w, 0);
      TestTools.assertGreater(tree.depth(), 0);
      TestTools.assertGreaterOrEqual(w, tree.depth());

      TestTreePathOperator.index(tree, map);
      Assert.assertEquals(w, map.size());

      final long[] counts = new long[w];

// sample many paths
      final int samples =
          Math.max(10000, (3333 * tree.weight()));
      for (int j = samples; (--j) >= 0;) {
        final int length = op.randomPath(tree, random);
        TestTools.assertGreater(length, 0);
        TestTools.assertLessOrEqual(length, tree.depth());
        ++counts[map.get(op.getEnd()).intValue()];
      }
      map.clear();

// trivial case
      if (w <= 1) {
        Assert.assertEquals(samples, counts[0]);
        continue;
      }

// counts now holds, for each node, how often it was sampled.
// we can now check for uniformity distribution.
      final Number[] meanSd =
          Statistics.sampleMeanAndStandardDeviation(counts);
      final double mean = samples / ((double) w);
      Assert.assertEquals(mean, meanSd[0].doubleValue(), 1e-14d);
      final double sd = meanSd[1].doubleValue();
      TestTools.assertGreaterOrEqual(sd, 0d);
      final double sdLimit = Math.floor(Math.max(2d,
          Math.min(2.75 * Math.sqrt(mean), mean / 3d)));
      TestTools.assertLessOrEqual(sd, sdLimit);

      final double difLimit = Math.floor(Math.max(2d, Math
          .min(0.5 * mean, 3d * Math.min(sdLimit, sd * 3d))));
      for (final long c : counts) {
        TestTools.assertGreater(c, 0);
        TestTools.assertLess(c, samples);
        TestTools.assertGreaterOrEqual(c, mean - difLimit);
        TestTools.assertLessOrEqual(c, mean + difLimit);
      }
    }
  }
}
