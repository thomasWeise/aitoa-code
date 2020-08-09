package aitoa.searchSpaces.trees;

import java.util.HashSet;
import java.util.function.BiConsumer;

import org.junit.Ignore;

import aitoa.ObjectTest;

/**
 * an internal test of structured objects
 *
 * @param <T>
 *          the tested type
 */
@Ignore
abstract class StructureTest<T> extends ObjectTest<T> {

  /** the done objects */
  private final Checker mDone;

  /**
   * create the structured test
   *
   * @param pOwner
   *          the owner
   */
  StructureTest(final StructureTest<?> pOwner) {
    super();
    this.mDone =
        ((pOwner != null) ? pOwner.mDone : (new Checker()));
  }

  /**
   * do the test
   *
   * @param o
   *          the object
   * @param r
   *          the runnable
   */
  final void test(final Object o, final Runnable r) {
    this.mDone.accept(o, r);
  }

  /** the internal checker */
  private static final class Checker
      implements BiConsumer<Object, Runnable> {

    /** the objects which are done */
    private final HashSet<Object> mIsDone;

    /** the depth */
    private int mDepth;

    /** create */
    Checker() {
      super();
      this.mIsDone = new HashSet<>();
    }

    /** do the test */
    @Override
    public void accept(final Object t, final Runnable u) {
      if (this.mDepth < 20) {
        if ((t == null) || this.mIsDone.add(t)) {
          ++this.mDepth;
          try {
            u.run();
          } finally {
            --this.mDepth;
          }
        }
      }
    }

  }
}
