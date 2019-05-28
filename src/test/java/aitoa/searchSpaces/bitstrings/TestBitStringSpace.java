package aitoa.searchSpaces.bitstrings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;

import aitoa.structure.ISpace;
import aitoa.structure.ISpaceTest;

/** Test the bit string space problem */
@Ignore
public class TestBitStringSpace extends ISpaceTest<boolean[]> {

  /** the space */
  private final BitStringSpace m_space;

  /**
   * create
   *
   * @param length
   *          the length
   */
  protected TestBitStringSpace(final int length) {
    super();
    this.m_space = new BitStringSpace(length);
  }

  /** {@inheritDoc} */
  @Override
  protected ISpace<boolean[]> getInstance() {
    return this.m_space;
  }

  /** {@inheritDoc} */
  @Override
  protected void assertValid(final boolean[] a) {
    Assert.assertNotNull(a);
    Assert.assertEquals(this.m_space.length, a.length);
  }

  /** {@inheritDoc} */
  @Override
  protected void fillWithRandomData(final boolean[] dest) {
    final Random random = ThreadLocalRandom.current();
    for (int i = dest.length; (--i) >= 0;) {
      dest[i] = random.nextBoolean();
    }
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] b = new boolean[this.m_space.length];
    this.fillWithRandomData(b);
    return b;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createInvalid() {
    final Random random = ThreadLocalRandom.current();

    if (random.nextBoolean()) {
      return null;
    }

    for (;;) {
      final int len =
          random.nextInt(2 + (2 * this.m_space.length));
      if (len != this.m_space.length) {
        return new boolean[len];
      }
    }
  }
}
