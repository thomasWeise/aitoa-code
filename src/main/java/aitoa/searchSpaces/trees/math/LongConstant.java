package aitoa.searchSpaces.trees.math;

import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;
import aitoa.searchSpaces.trees.NodeTypeSet;
import aitoa.utils.RandomUtils;

/**
 * a constant number in {@code long} format
 *
 * @param <C>
 *          the basic context
 */
public final class LongConstant<C> extends NullaryFunction<C> {

  /** the constant value */
  public final long value;

  /**
   * Create a long constant node
   *
   * @param type
   *          the node type record
   * @param _value
   *          the constant value
   */
  public LongConstant(final NodeType<LongConstant<C>> type,
      final long _value) {
    super(type);
    this.value = _value;
  }

  /** {@inheritDoc} */
  @Override
  public double applyAsDouble(final C param) {
    return this.value;
  }

  /** {@inheritDoc} */
  @Override
  public long applyAsLong(final C param) {
    return this.value;
  }

  /** {@inheritDoc} */
  @Override
  public int applyAsInt(final C param) {
    if (this.value >= Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    if (this.value <= Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }
    return ((int) (this.value));
  }

  /** {@inheritDoc} */
  @Override
  public void asText(final Appendable out) throws IOException {
    out.append(Long.toString(this.value));
  }

  /** {@inheritDoc} */
  @Override
  public void asJavaPrintParameters(final Appendable out)
      throws IOException {
    out.append(',').append(' ').append(Long.toString(this.value))
        .append('L');
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(final Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (o instanceof LongConstant) {
      return (((LongConstant) o).value == this.value);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return (0x7645216 ^ Long.hashCode(this.value));
  }

  /**
   * Create a node type for constants for the default range of
   * [0, 100]
   *
   * @return the node type
   */
  public static Function<NodeTypeSet<?>[], NodeType<?>> type() {
    return LongConstant.type(0L, 100L);
  }

  /**
   * Create a node type for constants
   *
   * @param min
   *          the minimum of the valid range for constants
   * @param max
   *          the maximum of the valid range for constants
   * @return the node type
   */
  public static Function<NodeTypeSet<?>[], NodeType<?>>
      type(final long min, final long max) {

    if (min >= max) {
      throw new IllegalArgumentException(
          ((("Invalid constance range [" + //$NON-NLS-1$
              min) + ',') + max) + ']');
    }
    return a -> new __ConstantNodeType<>(a, min, max);
  }

  /**
   * a factory for constant node types
   *
   * @param <C>
   *          the parameter type
   */
  private final static class __ConstantNodeType<C>
      extends NodeType<LongConstant<C>> {

    /** the minimum value */
    private final long m_min;
    /** the maximum value */
    private final long m_max;
    /** a set of privileged values */
    private final LongConstant<C>[] m_privileged;
    /** the offset */
    private final long m_start;
    /** the offset */
    private final long m_end;
    /** the span */
    private final double m_span;

    /**
     * create the constant node factory
     *
     * @param children
     *          the child node types
     * @param min
     *          the minimum
     * @param max
     *          the maximum
     */
    @SuppressWarnings("unchecked")
    __ConstantNodeType(final NodeTypeSet<?>[] children,
        final long min, final long max) {
      super(children);

      this.m_min = min;
      this.m_max = max;

      final long r = max - min;
      // HD 2-12 Overflow iff the arguments have different signs
      // and
      // the sign of the result is different from the sign of x
      if (((max ^ min) & (max ^ r)) < 0) {
        this.m_span = r;
      } else {
        this.m_span = ((double) max) - ((double) min);
      }

      if ((max - min) <= 101L) {
        this.m_start = min;
        this.m_end = max;
      } else {
        long center;
        if ((min <= 0L) && (max >= 0L)) {
          center = 0L;
        } else {
          center = ((min + max) / 2L);
          if ((center < min) || (center > max)) {
            center = ((min / 2L) + (max / 2L));
            if ((center < min) || (center > max)) {
              center = min;
            }
          }
        }
        this.m_start = Math.max(min, center - 50L);
        this.m_end = Math.min(max, center + 50L);
      }

      this.m_privileged =
          new LongConstant[((int) (this.m_end - this.m_start))
              + 1];
      for (int i = this.m_privileged.length; (--i) >= 0;) {
        this.m_privileged[i] =
            new LongConstant<>(this, this.m_start + i);
      }
    }

    /**
     * create
     *
     * @param l
     *          the long value
     * @return the constant
     */
    private LongConstant<C> __for(final long l) {
      if ((l >= this.m_start) && (l <= this.m_end)) {
        return this.m_privileged[(int) (l - this.m_start)];
      }
      return new LongConstant<>(this, l);
    }

    /** {@inheritDoc} */
    @Override
    public LongConstant<C> instantiate(final Node[] children,
        final Random random) {
      if (random.nextInt(3) <= 0) {
        return this.m_privileged[random
            .nextInt(this.m_privileged.length)];
      }
      return this.__for(RandomUtils.uniformFromMtoN(random,
          this.m_min, this.m_max));
    }

    /** {@inheritDoc} */
    @Override
    public LongConstant<C> createModifiedCopy(
        final LongConstant<C> node, final Random random) {
      final long av = Math.abs(node.value);
      if (av < 0L) {
        return this.instantiate(null, random);
      }

      long v;
      for (;;) {
        final double add = (random.nextGaussian() * 0.1d
            * (((av > 1e-10d) && random.nextBoolean()) ? av
                : this.m_span));
        if ((add < Long.MIN_VALUE) || (add > Long.MAX_VALUE)) {
          continue;
        }
        final long addl = ((long) add);
        if (addl == 0L) {
          continue;
        }
        v = (node.value + addl);
        if ((v >= this.m_min) && (v <= this.m_max)) {
          break;
        }
      }
      return this.__for(v);
    }

    /** {@inheritDoc} */
    @Override
    public LongConstant<C> replaceChild(
        final LongConstant<C> original, final Node child,
        final int index) {
      throw new UnsupportedOperationException(
          "long constants have no children."); //$NON-NLS-1$
    }
  }
}
