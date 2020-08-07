package aitoa.searchSpaces.trees;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

import aitoa.utils.ReflectionUtils;

/**
 * This is an internal factory function that can create node
 * types that are based on reflection. These types may not be the
 * most efficient ones, but they will work in cases where all the
 * information is stored in the tree structure.
 *
 * @param <T>
 *          the node type
 */
final class ReflectiveNodeTypes<T extends Node>
    implements Function<NodeTypeSet<?>[], NodeType<T>> {

  /** the class */
  private final Class<T> m_clazz;

  /**
   * create
   *
   * @param clazz
   *          the class
   */
  ReflectiveNodeTypes(final Class<T> clazz) {
    super();
    this.m_clazz = Objects.requireNonNull(clazz);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public NodeType<T> apply(final NodeTypeSet<?>[] t) {
    final boolean hasChildren = (t.length > 0);
    try {
      final Constructor<T>[] cs =
          (Constructor<T>[]) (this.m_clazz.getConstructors());
      if (cs == null) {
        throw new IllegalArgumentException("class " + //$NON-NLS-1$
            ReflectionUtils.className(this.m_clazz)
            + " has no public constructors."); //$NON-NLS-1$
      }

      outer: for (final Constructor<T> cx : cs) {
        final Class<?>[] p = cx.getParameterTypes();
        if (p == null) {
          continue outer;
        }

        if (!NodeType.class.isAssignableFrom(p[0])) {
          continue outer;
        }

        if (p.length != (hasChildren ? 2 : 1)) {
          continue outer;
        }
        if (hasChildren) {
          if (!(Node[].class.isAssignableFrom(p[1]))) {
            continue outer;
          }
          return new ReflectiveNodeType1<>(t, cx);
        }
        return new ReflectiveNodeType0<>(t, cx);
      }

      throw new IllegalArgumentException("class " + //$NON-NLS-1$
          ReflectionUtils.className(this.m_clazz)//
          + " has no fitting public constructor."); //$NON-NLS-1$
    } catch (final SecurityException se) {
      throw new IllegalArgumentException(
          "cannot get constructors of class "//$NON-NLS-1$
              + ReflectionUtils.className(this.m_clazz),
          se);
    }
  }

  /**
   * a reflective node factory
   *
   * @param <T>
   *          the node type
   */
  private abstract static class ReflectiveNodeType<
      T extends Node> extends NodeType<T> {

    /** the constructor */
    final Constructor<T> m_constructor;

    /**
     * create the node factory
     *
     * @param constr
     *          the constructor
     * @param childTypes
     *          the child types
     */
    ReflectiveNodeType(final NodeTypeSet<?>[] childTypes,
        final Constructor<T> constr) {
      super(childTypes);
      this.m_constructor = Objects.requireNonNull(constr);
    }

    /**
     * re-throw an error
     *
     * @param error
     *          the error
     * @return the error
     */
    final IllegalStateException doThrow(final Throwable error) {
      return new IllegalStateException(//
          "reflective node instantiation failed to invoke " + //$NON-NLS-1$
              this.m_constructor,
          error);
    }
  }

  /**
   * a reflective node factory
   *
   * @param <T>
   *          the node type
   */
  private static final class ReflectiveNodeType0<T extends Node>
      extends ReflectiveNodeType<T> {

    /** the parameters */
    private final Object[] m_params;

    /**
     * create the node factory
     *
     * @param constr
     *          the constructor
     * @param childTypes
     *          the child types
     */
    ReflectiveNodeType0(final NodeTypeSet<?>[] childTypes,
        final Constructor<T> constr) {
      super(childTypes, constr);
      this.m_params = new Object[] { this };
    }

    /** {@inheritDoc} */
    @Override
    public T instantiate(final Node[] children,
        final Random random) {
      try {
        return this.m_constructor.newInstance(this.m_params);
      } catch (final Throwable error) {
        throw this.doThrow(error);
      }
    }
  }

  /**
   * a reflective node factory
   *
   * @param <T>
   *          the node type
   */
  private static final class ReflectiveNodeType1<T extends Node>
      extends ReflectiveNodeType<T> {

    /**
     * create the node factory
     *
     * @param constr
     *          the constructor
     * @param childTypes
     *          the child types
     */
    ReflectiveNodeType1(final NodeTypeSet<?>[] childTypes,
        final Constructor<T> constr) {
      super(childTypes, constr);
    }

    /** {@inheritDoc} */
    @Override
    public T instantiate(final Node[] children,
        final Random random) {
      try {
        return this.m_constructor.newInstance(this, children);
      } catch (final Throwable error) {
        throw this.doThrow(error);
      }
    }
  }
}
