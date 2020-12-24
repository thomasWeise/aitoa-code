package aitoa.searchSpaces.trees;

import java.util.Objects;
import java.util.Random;

import aitoa.structure.IUnarySearchOperator;

/**
 * This mutation operator tries to apply one of four possible
 * modifications. It first selects one node {@code e} in the
 * parent tree and then, either:
 * <ol>
 * <li>uses the
 * {@linkplain NodeType#createModifiedCopy(Node, Random)
 * modification operator} of the node type to modify it,</li>
 * <li>if {@code e} is not a terminal node and the node types
 * permit it, replaces it with one of its children,</li>
 * <li>if the the tree depth and node types permit it, replaces
 * {@code e} with a new non-terminal node which has {@code e} as
 * one of its children, or</li>
 * <li>replaces {@code e} with a randomly generated new
 * sub-tree.</li>
 * </ol>
 */
public final class TreeUnaryOperatorMRSL extends TreePathOperator
    implements IUnarySearchOperator<Node[]> {

  /** a selection of integers */
  private int[] mIntSel1;
  /** a selection of integers */
  private int[] mIntSel2;

  /**
   * Create a new tree mutation operation
   *
   * @param md
   *          the maximum tree depth
   */
  public TreeUnaryOperatorMRSL(final int md) {
    super(md);
    this.mIntSel1 = new int[(md + 1) << 2];
    this.mIntSel2 = new int[(md + 1) << 2];
  }

  /**
   * get a selection of integers
   *
   * @param size
   *          the size
   * @return the selection
   */
  private int[] getIntSel1(final int size) {
    int[] res = this.mIntSel1;
    if (size >= res.length) {
      this.mIntSel1 = res = new int[(size + 1) << 1];
    }
    for (int i = size; (--i) >= 0;) {
      res[i] = i;
    }
    return res;
  }

  /**
   * get a selection of integers
   *
   * @param size
   *          the size
   * @return the selection
   */
  private int[] getIntSel2(final int size) {
    int[] res = this.mIntSel2;
    if (size >= res.length) {
      this.mIntSel2 = res = new int[(size + 1) << 1];
    }
    for (int i = size; (--i) >= 0;) {
      res[i] = i;
    }
    return res;
  }

  /** {@inheritDoc} */
  @Override
  public void apply(final Node[] x, final Node[] dest,
      final Random random) {
    final Node xx = x[0];

    outer: for (int trials = TreeOperator.MAX_TRIALS;
        (--trials) >= 0;) {
      final int length = this.randomPath(xx, random);

      final Node yy;
      switcher: switch (random.nextInt(4)) {
        case 0: { // modify
// try modifying the end node
          yy = this.tryModifyEnd(random);
          break switcher;
        }

        case 1: { // lift
// try lifting a child of the end node
          final Node e = this.getEnd();

// can only lift if not terminal
          int childrenSize = e.getChildCount();
          if (childrenSize <= 0) {
            continue outer;
          }

// the child indices
          final int[] children = this.getIntSel1(childrenSize);

// the set of allowed end node types
          final NodeTypeSet<?> possibleEnds =
              this.getEndChoices();

          while (childrenSize > 0) {
// randomly try a previously untested child
            final int i = random.nextInt(childrenSize);
// pick the child and check if it can be lifted
            final Node child = e.getChild(children[i]);
            children[i] = children[--childrenSize];
            if (possibleEnds.containsNode(child)) {
// yes, it can: replace the end node with the child node
              yy = this.replaceEnd(child);
              break switcher; // and break the switch
            }
          }
// if we get here, the end node could not be replaced by any of
// its children
          continue outer;
        }

        case 2: { // sink
// try sinking the end node, i.e., wrap the end node into another
// node
          final Node e = this.getEnd();
// it must be possible to insert at least one node between the
// current path end and its parent
          if ((length + e.depth()) >= this.mMaxDepth) {
            continue outer;
          }

// get the node type set of possible choices for the path end:
// only if we can insert a non-terminal node, this operator here
// can work
          final NodeTypeSet<?> nts = this.getEndChoices();
          int typeChoices = nts.getNonTerminalTypeCount();
          if (typeChoices <= 0) {
            continue outer;
          }

// get a random non-terminal type
          final int[] types = this.getIntSel1(typeChoices);
          while (typeChoices > 0) {
// pick a type that could be used to replace the current end
            final int i = random.nextInt(typeChoices);
            final NodeType<?> type =
                nts.getNonTerminalType(types[i]);
            types[i] = types[--typeChoices];

// it must be a non-terminal type, because it should wrap "e"
// it will have a number of possible offsprings, each associated
// with a type
            final int ofsChoices = type.getChildCount();
            if (ofsChoices <= 0) {
              throw new IllegalStateException(
                  "Non-terminal node type with no child choices?"); //$NON-NLS-1$
            }
            final int[] ofs = this.getIntSel2(ofsChoices);

// but now we need to find where we could place "e"
            int ii = ofsChoices;
            while (ii > 0) {
              final int j = random.nextInt(ii);
              final int eIdx = ofs[j];
              final NodeTypeSet<?> ntts =
                  type.getChildTypes(eIdx);
              ofs[j] = ofs[--ii];
              if (ntts.containsNode(e)) {
// ok, we have found a place
                final Node[] newOfs = new Node[ofsChoices];
                newOfs[eIdx] = e; // place e
// create the other nodes
                for (int jj = ofsChoices; (--jj) >= 0;) {
                  if (newOfs[jj] != null) {
                    continue;
                  }

                  final NodeTypeSet<?> nttts =
                      type.getChildTypes(jj);
// with a certain probability, use e again, if possible
                  if (nttts.containsNode(e) && (random
                      .nextInt(nttts.getTypeCount() + 2) <= 0)) {
                    newOfs[jj] = e;
                    continue;
                  }

// otherwise, create a new sub-tree
                  newOfs[jj] = TreeOperator.createTree(nttts,
                      this.mMaxDepth - length, random);
                }

// replace the end with the new node wrapping e
                yy = this.replaceEnd(
                    type.instantiate(newOfs, random));
                break switcher;
              }
            }

          }
          continue outer;
        }
        default: { // replace the node with a random sub-tree
          yy = this.replaceEnd(
              TreeOperator.createTree(this.getEndChoices(),
                  (this.mMaxDepth - length) + 1, random));
        }
      }

// if we get here, one of the four mutation choices has produced
// an output

      if ((yy != null) && (!Objects.equals(xx, yy))) {
        dest[0] = yy;
        return;
      }
    }

    throw new IllegalArgumentException(//
        "failed to modify tree " + //$NON-NLS-1$
            xx.toString() + " after trying "//$NON-NLS-1$
            + TreeOperator.MAX_TRIALS + " times!"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "mrsl" + this.mMaxDepth;//$NON-NLS-1$
  }
}
