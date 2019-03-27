package aitoa.structure;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.ObjectTest;
import aitoa.TestTools;

/**
 * A test for an instance of {@link ISpace}
 *
 * @param <X>
 *          the data structure used in the space
 */
@Ignore
public abstract class ISpaceTest<X>
    extends ObjectTest<ISpace<X>> {

  /**
   * fill the given object with (random?) data
   *
   * @param dest
   *          the destination
   */
  protected void fillWithRandomData(final X dest) {
    TestTools.fillWithRandomData(dest);
  }

  /**
   * Create a valid instance of the data structure {@code X}.
   * Ideally, this method should return a different instance
   * every time it is called.
   *
   * @return a valid instance
   */
  protected abstract X createValid();

  /**
   * Create an invalid instance of the data structure {@code X},
   * or {@code null} if no instance of {@code X} can be invalid.
   * Ideally, this method should return a different instance
   * every time it is called.
   *
   * @return the instance
   */
  protected X createInvalid() {
    return null;
  }

  /**
   * Check that an instance of the space is valid. This method
   * allows us to re-implement {@link ISpace#check(Object)} to
   * check whether we may have overlooked some issues there.
   *
   * @param a
   *          the instance
   */
  protected void assertValid(final X a) {
    Assert.assertNotNull(a);
  }

  /**
   * check if two instances of the data structure are equal or
   * not
   *
   * @param a
   *          the first instance
   * @param b
   *          the second instance
   */
  protected void assertEquals(final X a, final X b) {
    TestTools.assertEquals(a, b);
  }

  /**
   * test that the {@link ISpace#create()} method does not return
   * {@code null}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 10000)
  public final void testCreateNotNull() {
    Assert.assertNotNull(this.getInstance().create());
  }

  /**
   * test that the {@link ISpace#create()} method returns objects
   * of the same class
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 10000)
  public final void testCreateTwiceSameClass() {
    final ISpace<X> space = this.getInstance();
    Assert.assertEquals(space.create().getClass(),
        space.create().getClass());
  }

  /**
   * test that the {@link ISpace#create()} method returns objects
   * different objects when called twice
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 10000)
  public final void testCreateNotSameInstance() {
    final ISpace<X> space = this.getInstance();
    Assert.assertNotSame(space.create(), space.create());
  }

  /**
   * test that the {@link ISpace#copy(Object, Object)} method
   * works
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public final void testCopy() {
    final ISpace<X> space = this.getInstance();
    final X i1 = space.create();
    final X i2 = space.create();

    for (int i = 10; (--i) >= 0;) {
      this.fillWithRandomData(i1);
      space.copy(i1, i2);
      this.assertEquals(i1, i2);
    }
  }

  /**
   * test that the {@link ISpace#copy(Object, Object)} method
   * works with the instance returned by {@link #createValid()}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public final void testCopyValid() {
    final ISpace<X> space = this.getInstance();
    final X i1 = this.createValid();
    final X i2 = space.create();

    for (int i = 10; (--i) >= 0;) {
      this.fillWithRandomData(i1);
      space.copy(i1, i2);
      this.assertEquals(i1, i2);
    }
  }

  /**
   * a self-test to check whether our test here makes the same
   * assumptions and checks as the {@link ISpace#check(Object)}
   * method of the space
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 1000000)
  public final void testCheckAgreesWithAssertValid() {
    X d;

    for (int i = 100; (--i) >= 0;) {
      d = this.createValid();
      this.assertValid(d);

      d = this.createInvalid();
      boolean error = false;
      try {
        this.assertValid(d);
        error = true;
      } catch (@SuppressWarnings("unused") final AssertionError expected) {
        // ignore
      }
      if (error) {
        Assert.fail("expected error did not occur"); //$NON-NLS-1$
      }
    }
  }

  /**
   * test that the {@link ISpace#check(Object)} method works with
   * the valid instance
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public final void testCheckValid() {
    this.getInstance().check(this.createValid());
  }

  /**
   * test that the {@link ISpace#check(Object)} method works with
   * the invalid instance
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 1000000)
  public final void testCheckInvalid() {
    final ISpace<X> space = this.getInstance();
    boolean error = false;

    for (int j = 100; (--j) >= 0;) {
      final X x = this.createInvalid();
      try {
        space.check(x);
        error = true;
      } catch (@SuppressWarnings("unused") final Exception e) {
        //
      }
      if (error) {
        Assert.fail(//
            "No exception thrown when checking " //$NON-NLS-1$
                + Objects.toString(x));
      }
    }

    try {
      space.check(null);
      error = true;
    } catch (@SuppressWarnings("unused") final Exception e) {
      //
    }
    if (error) {
      Assert.fail(//
          "No exception thrown when checking null!"); //$NON-NLS-1$
    }
  }

  /**
   * test that the {@link ISpace#print(Object, Appendable)}
   * method works with the valid instance
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public final void testPrintValid() {
    final StringBuilder sb = new StringBuilder();
    this.getInstance().print(this.createValid(), sb);
    TestTools.assertGreater(sb.length(), 0);
  }
}
