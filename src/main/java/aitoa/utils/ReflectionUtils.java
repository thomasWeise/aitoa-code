package aitoa.utils;

/** Some reflection utilities */
public final class ReflectionUtils {

  /**
   * Get the class name of an object
   *
   * @param object
   *          the object
   * @return its class name
   */
  public static String className(final Object object) {
    return (object != null)
        ? ReflectionUtils.className(object.getClass()) : "null"; //$NON-NLS-1$
  }

  /**
   * Get a class name, as fully-qualified as possible.
   *
   * @param clazz
   *          the class
   * @return its name
   */
  public static String className(final Class<?> clazz) {
    String s = clazz.getCanonicalName();
    if (s != null) {
      return s;
    }
    s = clazz.getName();
    if (s != null) {
      return s;
    }
    s = clazz.getSimpleName();
    if (s == null) {
      throw new IllegalArgumentException(
          "Cannot get name of class " //$NON-NLS-1$
              + clazz);
    }
    return s;
  }

  /** forbidden */
  private ReflectionUtils() {
    throw new UnsupportedOperationException();
  }
}
