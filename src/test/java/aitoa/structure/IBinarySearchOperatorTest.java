package aitoa.structure;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;

/**
 * This is a base class for testing binary search operators.
 *
 * @param <X>
 *          the data structure
 */
@Ignore
public abstract class IBinarySearchOperatorTest<X> {

  /**
   * get an instance of the space backing the operator
   *
   * @return the space
   */
  protected abstract ISpace<X> getSpace();

  /**
   * Get the binary operator corresponding to the space
   *
   * @param space
   *          the space
   * @return the operator
   * @see #getSpace()
   */
  protected abstract IBinarySearchOperator<X>
      getOperator(final ISpace<X> space);

  /**
   * check if two instances of the data structure {@code X} are
   * equal or not
   *
   * @param a
   *          the first instance
   * @param b
   *          the second instance
   * @return {@code true} if they are equal, {@code false} if not
   */
  protected boolean equals(final X a, final X b) {
    return Objects.deepEquals(a, b);
  }

  /**
   * Create a valid point in the search space, i.e., instances of
   * the data structure {@code X} . Ideally, this method should
   * return a different a valid point in the search space every
   * time it is called.
   *
   * @return a valid instance
   */
  protected abstract X createValid();

  /**
   * test that the
   * {@link IUnarySearchOperator#apply(Object, Object, Random)}
   * method works and produces different, valid results while not
   * altering the source objects
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testApplyValidAndDifferent() {
    final ISpace<X> space = this.getSpace();
    final IBinarySearchOperator<X> op = this.getOperator(space);
    final Random random = ThreadLocalRandom.current();

    final X copy1 = space.create();
    final X copy2 = space.create();
    final X dest = space.create();

    int count = 0;
    int different = 0;
    for (int i = 0; (++i) <= 512;) {
      final X src1 = this.createValid();
      space.check(src1);
      space.copy(src1, copy1);
      final X src2 = this.createValid();
      space.check(src2);
      space.copy(src2, copy2);

      op.apply(src1, src2, dest, random);
      Assert.assertTrue(this.equals(src1, copy1));
      Assert.assertTrue(this.equals(src2, copy2));
      space.check(dest);

      if (!this.equals(src1, src2)) {
        count++;
        if ((!(this.equals(src1, dest)))
            && (!(this.equals(src2, dest)))) {
          different++;
        }
      }
    }

    TestTools.assertGreater(count, 32);
    TestTools.assertGreaterOrEqual(different, 1 + (count >>> 5));
  }
}
