package aitoa;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * The base class for objects that are tested
 *
 * @param <T>
 *          the object type
 */
@Ignore
public abstract class ObjectTest<T> {

  /**
   * get the object instance
   *
   * @return the instance
   */
  protected abstract T getInstance();

  /**
   * test whether the instance is {@code null} - it should not
   * be.
   */
  @Test(timeout = 3600000)
  public void testInstanceNotNull() {
    Assert.assertNotNull(this.getInstance());
  }

  /** test whether the instance equals method */
  @Test(timeout = 3600000)
  public void testEquals() {
    final T instance;

    instance = this.getInstance();
    Assert.assertNotNull(instance);
    Assert.assertTrue(instance.equals(instance));
    Assert.assertEquals(instance, instance);
    Assert.assertFalse(instance.equals(null));
    Assert.assertFalse(instance.equals(new Object()));
  }

  /**
   * test whether we can invoke
   * {@link java.lang.Object#toString()} and that it does not
   * return {@code null} or an empty string
   */
  @Test(timeout = 3600000)
  public void testToString() {
    final Object instance = this.getInstance();
    Assert.assertNotNull(instance);
    final int h = instance.hashCode();
    final String s = instance.toString();
    Assert.assertNotNull(s);
    TestTools.assertGreater(s.length(), 0);
    Assert.assertEquals(s, instance.toString());
    Assert.assertEquals(h, instance.hashCode());
  }

  /**
   * test whether we can invoke
   * {@link java.lang.Object#hashCode()} several times and get
   * the same result
   */
  @Test(timeout = 3600000)
  public void testHashCode() {
    final T instance;

    instance = this.getInstance();
    final String s = instance.toString();
    Assert.assertNotNull(instance);
    Assert.assertEquals(instance.hashCode(),
        instance.hashCode());
    Assert.assertEquals(s, instance.toString());
  }

  /**
   * Test whether the object can be cloned
   *
   * @throws Throwable
   *           if something with the clones fails
   */
  @SuppressWarnings("unchecked")
  @Test(timeout = 3600000)
  public void testCloneIfCloneable() throws Throwable {
    final T instance1 = this.getInstance();
    Assert.assertNotNull(instance1);
    if (instance1 instanceof Cloneable) {
      final T instance2 =
          ((T) (instance1.getClass().getMethod("clone") //$NON-NLS-1$
              .invoke(instance1)));
      Assert.assertNotNull(instance2);

      Assert.assertEquals(instance1, instance2);
      Assert.assertEquals(instance1.hashCode(),
          instance2.hashCode());
      Assert.assertEquals(instance1.toString(),
          instance2.toString());
    }
  }

  /**
   * This method can be used to recursively invoke all test
   * methods
   */
  protected void runAllTests() {
    final Method[] methods = this.getClass().getMethods();
    final Random r = ThreadLocalRandom.current();
    for (int size = methods.length; size > 0;) {
      final int choice = r.nextInt(size);
      final Method method = methods[choice];
      methods[choice] = methods[--size];
      // method.getName().startsWith("test"))//$NON-NLS-1$
      if (method.getAnnotation(org.junit.Test.class) != null) {
        if (method.getParameterCount() <= 0) {
          try {
            method.invoke(this);
          } catch (IllegalAccessException
              | IllegalArgumentException
              | InvocationTargetException e) {
            throw new AssertionError(//
                "error when calling " //$NON-NLS-1$
                    + method,
                e);
          }
        }
      }
    }
  }
}
