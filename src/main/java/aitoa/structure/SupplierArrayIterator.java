package aitoa.structure;

import java.util.function.Supplier;

/**
 * an iterator over an array of suppliers, used by
 * {@link aitoa.structure.IModel}
 *
 * @param <X>
 *          the array type
 */
final class SupplierArrayIterator<X>
    extends IteratorBase<Supplier<X>, X> {

  /**
   * create
   *
   * @param pArray
   *          the array
   * @param pStart
   *          the start index
   * @param pEnd
   *          the end index
   */
  SupplierArrayIterator(final Supplier<X>[] pArray,
      final int pStart, final int pEnd) {
    super(pArray, pStart, pEnd);
  }

  /** {@inheritDoc} */
  @Override
  public X next() {
    final int i = this.mIndex;
    if (i >= this.mEnd) {
      IteratorBase.endError(i);
    }
    final X x = this.mArray[i].get();
    this.mIndex = (i + 1);
    return x;
  }
}
