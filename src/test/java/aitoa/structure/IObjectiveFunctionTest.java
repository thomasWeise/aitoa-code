package aitoa.structure;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.ObjectTest;
import aitoa.TestTools;

/**
 * This is a base class for testing objective functions.
 *
 * @param <Y>
 *          the data structure
 */
@Ignore
public abstract class IObjectiveFunctionTest<Y>
    extends ObjectTest<IObjectiveFunction<Y>> {

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
  @Test(timeout = 3600000)
  public void testApplyValidAndDifferent() {
    final IObjectiveFunction<Y> f = this.getInstance();
    final double lb = f.lowerBound();
    final double ub = f.upperBound();
    for (int i = 100; (--i) >= 0;) {
      final double d = f.evaluate(this.createValid());
      Assert.assertTrue(Double.isFinite(d));
      TestTools.assertGreaterOrEqual(d, lb);
      TestTools.assertLessOrEqual(d, ub);
    }
  }
}
