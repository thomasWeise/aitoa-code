package aitoa.structure;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;

/**
 * This is a base class for testing objective functions.
 *
 * @param <Y>
 *          the data structure
 */
@Ignore
public abstract class IObjectiveFunctionTest<Y> {

  /**
   * get the objective function
   *
   * @return the objective function
   */
  protected abstract IObjectiveFunction<Y> getObjective();

  /**
   * Create a valid instance. Ideally, this method should return
   * a different instance every time it is called.
   *
   * @return a valid instance
   */
  protected abstract Y createValid();

  /**
   * test that the {@link IObjectiveFunction#evaluate(Object)}
   * method works
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testApplyValidAndDifferent() {
    final IObjectiveFunction<Y> f = this.getObjective();
    final double lb = f.lowerBound();
    for (int i = 100; (--i) >= 0;) {
      final double d = f.evaluate(this.createValid());
      Assert.assertTrue(Double.isFinite(d));
      TestTools.assertGreaterOrEqual(d, lb);
    }
  }
}
