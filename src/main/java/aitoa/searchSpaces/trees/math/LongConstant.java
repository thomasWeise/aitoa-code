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
   * @param pType
   *          the node type record
   * @param pValue
   *          the constant value
   */
  public LongConstant(final NodeType<LongConstant<C>> pType,
      final long pValue) {
    super(pType);
    this.value = pValue;
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
    return a -> new ConstantNodeType<>(a, min, max);
  }

  /**
   * a factory for constant node types
   *
   * @param <C>
   *          the parameter type
   */
  private final static class ConstantNodeType<C>
      extends NodeType<LongConstant<C>> {

    /** the minimum value */
    private final long mMin;
    /** the maximum value */
    private final long mMax;
    /** a set of privileged values */
    private final LongConstant<C>[] mPrivileged;
    /** the offset */
    private final long mStart;
    /** the offset */
    private final long mEnd;
    /** the span */
    private final double mSpan;

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
    ConstantNodeType(final NodeTypeSet<?>[] children,
        final long min, final long max) {
      super(children);

      this.mMin = min;
      this.mMax = max;

      final long r = max - min;
// HD 2-12 Overflow iff the arguments have different signs and
// the sign of the result is different from the sign of x
      if (((max ^ min) & (max ^ r)) < 0) {
        this.mSpan = r;
      } else {
        this.mSpan = ((double) max) - ((double) min);
      }

      if ((max - min) <= 101L) {
        this.mStart = min;
        this.mEnd = max;
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
        this.mStart = Math.max(min, center - 50L);
        this.mEnd = Math.min(max, center + 50L);
      }

      this.mPrivileged =
          new LongConstant[((int) (this.mEnd - this.mStart))
              + 1];
      for (int i = this.mPrivileged.length; (--i) >= 0;) {
        this.mPrivileged[i] =
            new LongConstant<>(this, this.mStart + i);
      }
    }

    /**
     * create
     *
     * @param l
     *          the long value
     * @return the constant
     */
    private LongConstant<C> forLong(final long l) {
      if ((l >= this.mStart) && (l <= this.mEnd)) {
        return this.mPrivileged[(int) (l - this.mStart)];
      }
      return new LongConstant<>(this, l);
    }

    /** {@inheritDoc} */
    @Override
    public LongConstant<C> instantiate(final Node[] children,
        final Random random) {
      if (random.nextInt(3) <= 0) {
        return this.mPrivileged[random
            .nextInt(this.mPrivileged.length)];
      }
      return this.forLong(RandomUtils.uniformFromMtoN(random,
          this.mMin, this.mMax));
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
                : this.mSpan));
        if ((add < Long.MIN_VALUE) || (add > Long.MAX_VALUE)) {
          continue;
        }
        final long addl = ((long) add);
        if (addl == 0L) {
          continue;
        }
        v = (node.value + addl);
        if ((v >= this.mMin) && (v <= this.mMax)) {
          break;
        }
      }
      return this.forLong(v);
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
