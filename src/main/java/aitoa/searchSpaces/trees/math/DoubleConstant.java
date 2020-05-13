package aitoa.searchSpaces.trees.math;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;
import aitoa.searchSpaces.trees.NodeTypeSet;

/**
 * a constant number in {@code double} format
 *
 * @param <C>
 *          the basic context
 */
public final class DoubleConstant<C> extends NullaryFunction<C> {

  /** a set of default constants */
  private static final double[] DEFAULT_CONSTANTS = { //
      -1d, //
      1d / 3d, //
      0.5d, //
      0.618033988749894848204586834365638117720309179805762862135d, // golden_ratio
      0.707106781186547524400844362104849039284835937688474036588d, // 1/sqrt(2)
      1d, //
      1.41421356237309504880168872420969807856967187537694807317667973799d, // sqrt(2)
      2d, //
      Math.E, //
      3d, //
      Math.PI, //
      5d, //
      10d };

  /** the constant value */
  public final double value;

  /**
   * Create a double constant node
   *
   * @param type
   *          the node type record
   * @param _value
   *          the constant value
   */
  public DoubleConstant(final NodeType<DoubleConstant<C>> type,
      final double _value) {
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
    if (this.value >= Long.MAX_VALUE) {
      return Long.MAX_VALUE;
    }
    if (this.value <= Long.MIN_VALUE) {
      return Long.MIN_VALUE;
    }
    return ((long) (this.value));
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
    final String s = Double.toString(this.value);
    final int l = s.length();
    if (l > 2) {
      if ((s.charAt(l - 1) == '0') && (s.charAt(l - 2) == '.')) {
        out.append(s.subSequence(0, l - 2));
        return;
      }
    }
    out.append(s);
  }

  /** {@inheritDoc} */
  @Override
  public void asJavaPrintParameters(final Appendable out)
      throws IOException {
    out.append(',').append(' ')
        .append(Double.toString(this.value)).append('d');
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
    if (o instanceof DoubleConstant) {
      final double d = ((DoubleConstant) o).value;
      return ((d == this.value)
          || ((d != d) && (this.value != this.value)));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return (0x25438121 ^ Double.hashCode(this.value));
  }

  /**
   * Create a node type for constants with default privileged
   * values and the default range of (-10, 10)
   *
   * @return the node type
   */
  public static Function<NodeTypeSet<?>[], NodeType<?>> type() {
    return DoubleConstant.type(-10d, 10d,
        DoubleConstant.DEFAULT_CONSTANTS);
  }

  /**
   * Create a node type for constants with default privileged
   * values
   *
   * @param min
   *          the minimum of the valid range for constants
   * @param max
   *          the maximum of the valid range for constants
   * @return the node type
   */
  public static Function<NodeTypeSet<?>[], NodeType<?>>
      type(final double min, final double max) {
    final double[] p =
        new double[DoubleConstant.DEFAULT_CONSTANTS.length];
    int i = 0;
    for (final double d : DoubleConstant.DEFAULT_CONSTANTS) {
      if ((d >= min) && (d <= max)) {
        p[i++] = d;
      }
    }
    return DoubleConstant.type(min, max,
        (i < DoubleConstant.DEFAULT_CONSTANTS.length)
            ? Arrays.copyOf(p, i)
            : DoubleConstant.DEFAULT_CONSTANTS);
  }

  /**
   * Create a node type for constants
   *
   * @param min
   *          the minimum of the valid range for constants
   * @param max
   *          the maximum of the valid range for constants
   * @param privileged
   *          an array of values that should receive priority
   *          when instantiating new constants
   * @return the node type
   */
  public static final Function<NodeTypeSet<?>[], NodeType<?>>
      type(final double min, final double max,
          final double... privileged) {

    final double[] r =
        ((privileged != DoubleConstant.DEFAULT_CONSTANTS)
            ? privileged.clone() : privileged);
    final double span = max - min;

    if ((!(Double.isFinite(min) && Double.isFinite(max)
        && Double.isFinite(span))) || (span <= 0d)
        || (min >= max)) {
      throw new IllegalArgumentException(
          ((((("Invalid constance range [" + //$NON-NLS-1$
              min) + ',') + max) + ':') + span) + ']');
    }

    for (final double d : r) {
      if ((d < min) && (d > max) && (!(Double.isFinite(d)))) {
        throw new IllegalArgumentException(((((("value " //$NON-NLS-1$
            + d) + " is out of range [") + min) + //$NON-NLS-1$
            ',') + max) + ']');
      }
    }

    return a -> new __ConstantNodeType<>(a, min, max, span, r);
  }

  /**
   * a factory for constant node types
   *
   * @param <C>
   *          the parameter type
   */
  private final static class __ConstantNodeType<C>
      extends NodeType<DoubleConstant<C>> {

    /** the minimum value */
    private final double m_min;
    /** the maximum value */
    private final double m_max;
    /** the range */
    private final double m_span;
    /** a set of privileged values */
    private final DoubleConstant<C>[] m_privileged;

    /**
     * create the constant node factory
     *
     * @param children
     *          the child node types
     * @param min
     *          the minimum
     * @param max
     *          the maximum
     * @param span
     *          the span
     * @param privileged
     *          the privileged values
     */
    @SuppressWarnings("unchecked")
    __ConstantNodeType(final NodeTypeSet<?>[] children,
        final double min, final double max, final double span,
        final double[] privileged) {
      super(children);

      this.m_min = min;
      this.m_max = max;
      this.m_span = span;

      int i = privileged.length;
      this.m_privileged = new DoubleConstant[i];
      for (; (--i) >= 0;) {
        this.m_privileged[i] =
            new DoubleConstant<>(this, privileged[i]);
      }
    }

    /** {@inheritDoc} */
    @Override
    public DoubleConstant<C> instantiate(final Node[] children,
        final Random random) {
      final int l = this.m_privileged.length;
      final int i = random.nextInt(l + 1);
      double v;
      if (i >= l) {
        do {
          v = (this.m_min + (random.nextBoolean() //
              ? (this.m_span * random.nextDouble()) // uniform
              : ((0.1d * this.m_span * random.nextGaussian())
                  + (0.5d * this.m_span)))); // normal
        } while ((!(Double.isFinite(v))) || (v < this.m_min)
            || (v > this.m_max));
        return new DoubleConstant<>(this, v);
      }
      return this.m_privileged[i];
    }

    /** {@inheritDoc} */
    @Override
    public DoubleConstant<C> createModifiedCopy(
        final DoubleConstant<C> node, final Random random) {
      final double value = node.value;
      final double av = Math.abs(value);
      double v;
      do {
        v = value + (random.nextGaussian() * 0.1d
            * (((av > 1e-10d) && random.nextBoolean()) ? av
                : this.m_span));
      } while ((!(Double.isFinite(v))) || (v < this.m_min)
          || (v > this.m_max) || (v == value));
      return new DoubleConstant<>(this, v);
    }

    /** {@inheritDoc} */
    @Override
    public DoubleConstant<C> replaceChild(
        final DoubleConstant<C> original, final Node child,
        final int index) {
      throw new UnsupportedOperationException(
          "double constants have no children."); //$NON-NLS-1$
    }
  }
}
