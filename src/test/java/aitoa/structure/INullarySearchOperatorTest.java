package aitoa.structure;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;

import aitoa.ObjectTest;
import aitoa.TestTools;

/**
 * This is a base class for testing nullary search operators.
 *
 * @param <X>
 *          the data structure
 */
@Ignore
public abstract class INullarySearchOperatorTest<X>
    extends ObjectTest<INullarySearchOperator<X>> {

  /**
   * get an instance of the space backing the operator
   *
   * @return the space
   */
  protected abstract ISpace<X> getSpace();

  /**
   * Get the nullary operator corresponding to the space
   *
   * @param space
   *          the space
   * @return the operator
   * @see #getSpace()
   */
  protected abstract INullarySearchOperator<X>
      getOperator(final ISpace<X> space);

  /** {@inheritDoc} */
  @Override
  protected INullarySearchOperator<X> getInstance() {
    return this.getOperator(this.getSpace());
  }

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
   * test that the
   * {@link INullarySearchOperator#apply(Object, Random)} method
   * works and produces at least two different results within 100
   * calls
   */
  @Test(timeout = 3600000)
  public final void testCreateValidAndDifferent() {
    final ISpace<X> space = this.getSpace();
    final INullarySearchOperator<X> op = this.getOperator(space);
    final Random random = ThreadLocalRandom.current();
    final ArrayList<X> list = new ArrayList<>();

    outer: for (int i = 100; (--i) >= 0;) {
      final X dest = space.create();
      op.apply(dest, random);
      space.check(dest);
      for (final X x : list) {
        if (this.equals(x, dest)) {
          continue outer;
        }
      }
      list.add(dest);
    }

    TestTools.assertGreater(list.size(), 1);
    TestTools.assertLessOrEqual(list.size(), 100);
  }
}
