package aitoa.structure;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;

import aitoa.ObjectTest;

/**
 * A test for an instance of {@link IRepresentationMapping}
 *
 * @param <X>
 *          the data structure used in the search space
 * @param <Y>
 *          the data structure used in the solution space
 */
@Ignore
public abstract class IRepresentationMappingTest<X, Y>
    extends ObjectTest<IRepresentationMapping<X, Y>> {

  /**
   * Create a valid instance of the search space. Ideally, this
   * method should return a different instance every time it is
   * called.
   *
   * @return a valid instance of the search space
   */
  protected abstract X createValidX();

  /**
   * Create an instance of the solution space.
   *
   * @return a valid instance of the solution space
   */
  protected abstract Y createY();

  /**
   * Assert that an instance of the solution space is valid
   *
   * @param y
   *          the instance
   */
  protected abstract void assertValid(final Y y);

  /**
   * test applying the map function
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testMap() {
    final Y dest = this.createY();
    final IRepresentationMapping<X, Y> map = this.getInstance();
    final Random random = ThreadLocalRandom.current();
    for (int i = 100; (--i) >= 0;) {
      map.map(random, this.createValidX(), dest);
      this.assertValid(dest);
    }
  }
}
