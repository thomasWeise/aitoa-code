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
abstract class _StructureTest<T> extends ObjectTest<T> {

  /** the done objects */
  private final __Checker m_done;

  /**
   * create the structured test
   *
   * @param owner
   *          the owner
   */
  _StructureTest(final _StructureTest<?> owner) {
    super();
    this.m_done =
        ((owner != null) ? owner.m_done : (new __Checker()));
  }

  /**
   * do the test
   *
   * @param o
   *          the object
   * @param r
   *          the runnable
   */
  final void _test(final Object o, final Runnable r) {
    this.m_done.accept(o, r);
  }

  /** the internal checker */
  private static final class __Checker
      implements BiConsumer<Object, Runnable> {

    /** the objects which are done */
    private final HashSet<Object> m_done;

    /** the depth */
    private int m_depth;

    /** create */
    __Checker() {
      super();
      this.m_done = new HashSet<>();
    }

    /** do the test */
    @Override
    public final void accept(final Object t, final Runnable u) {
      if (this.m_depth < 20) {
        if ((t == null) || this.m_done.add(t)) {
          ++this.m_depth;
          try {
            u.run();
          } finally {
            --this.m_depth;
          }
        }
      }
    }

  }
}
