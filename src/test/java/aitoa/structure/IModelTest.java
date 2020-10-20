package aitoa.structure;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;

/**
 * This is a base class for testing models that are used in
 * estimation of distribution algorithms.
 *
 * @param <X>
 *          the data structure
 */
@Ignore
public abstract class IModelTest<X>
    extends INullarySearchOperatorTest<X> {

  /**
   * Get the model corresponding to the space
   *
   * @param space
   *          the space
   * @return the operator
   * @see #getSpace()
   */
  protected abstract IModel<X> getModel(final ISpace<X> space);

  /** {@inheritDoc} */
  @Override
  protected INullarySearchOperator<X>
      getOperator(final ISpace<X> space) {
    return this.getModel(space);
  }

  /** {@inheritDoc} */
  @Override
  protected IModel<X> getInstance() {
    return this.getModel(this.getSpace());
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
   * Test whether the model creates valid and different points
   * directly after initialization.
   */
  @Test(timeout = 3600000)
  public final void testValidAndDifferentAfterInitialize() {
    final ISpace<X> space = this.getSpace();
    final IModel<X> model = this.getModel(space);
    model.initialize();
    @SuppressWarnings("unchecked")
    final X[] array = ((X[]) (new Object[100]));
    final Random random = new Random();

    int diff = 0;
    outer: for (int i = array.length; (--i) >= 0;) {
      array[i] = space.create();
      model.apply(array[i], random);
      space.check(array[i]);
      for (int j = array.length; (--j) > i;) {
        if (this.equals(array[i], array[j])) {
          continue outer;
        }
      }
      ++diff;
    }

    TestTools.assertGreaterOrEqual(diff, array.length >>> 3);
  }

  /**
   * test that the
   * {@link INullarySearchOperator#apply(Object, Random)} method
   * works and produces at least two different results within 100
   * calls
   */
  @Test(timeout = 3600000)
  @Override
  public void testCreateValidAndDifferent() {
    this.getInstance().initialize();
    super.testCreateValidAndDifferent();
  }

  /**
   * Test whether the model creates valid points after update.
   */
  @Test(timeout = 3600000)
  public final void testValidAfterUpdate() {
    final ISpace<X> space = this.getSpace();
    final IModel<X> model = this.getModel(space);

    model.initialize();

    @SuppressWarnings("unchecked")
    final Individual<X>[] array = new Individual[48];
    @SuppressWarnings("unchecked")
    final X[] dest =
        ((X[]) (new Object[3 * (array.length + 1)]));
    final ThreadLocalRandom random = ThreadLocalRandom.current();

    int diff = 0;
    outer: for (int j = 0; j < dest.length; j++) {
      for (int i = array.length; (--i) >= 0;) {
        array[i] = new Individual<>(this.createValid(),
            random.nextDouble(1d, 100d));
      }

      final int selectedEnd = 1 + (j % array.length);
      model.update(IModel.use(array, 0, selectedEnd));

      dest[j] = space.create();
      model.apply(dest[j], random);
      space.check(dest[j]);

      for (int k = 0; k < j; ++k) {
        if (this.equals(dest[k], dest[j])) {
          continue outer;
        }
      }
      ++diff;
    }

    TestTools.assertGreaterOrEqual(diff, dest.length >>> 3);
  }
}
