/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the
 * terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named
 * "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT
 * HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT ARE DISCLAIMED TO THE
 * EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO
 * COPYRIGHT HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY WAY OUT
 * OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 **/
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.dhke.projects.lutil.LoggingClass;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public abstract class AbstractReasoner<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends LoggingClass
	implements IReasoner<I, L, K, R> {

	@Override
	public boolean isSubClassOf(IABox<I, L, K, R> abox, K presumedSub, K presumedSuper)
		throws EReasonerException, EInconsistencyException
	{
		final IDLClassReference<I, L, K, R> subRef = abox.getDLTermFactory().getDLClassReference(presumedSub);
		final IDLClassReference<I, L, K, R> superRef = abox.getDLTermFactory().getDLClassReference(presumedSuper);
		return isSubClassOf(abox, subRef, superRef);
	}


	@Override
	public boolean isConsistent(final IDLClassExpression<I, L, K, R> concept,
								final IABoxFactory<I, L, K, R> aboxFactory)
		throws EReasonerException, EInconsistencyException
	{
		IABox<I, L, K, R> abox = aboxFactory.createABox();
		return isConsistent(abox, concept);
	}


	@Override
	public Collection<? extends IReasonerResult<I, L, K, R>> checkConsistency(final IABox<I, L, K, R> abox)
		throws EReasonerException, EInconsistencyException
	{
		return checkConsistency(abox, true);
	}


	@Override
	public boolean isConsistent(final IABox<I, L, K, R> abox, final IDLClassExpression<I, L, K, R> concept)
		throws EReasonerException, EInconsistencyException
	{
		try {
			final Collection<? extends IReasonerResult<I, L, K, R>> results = checkConsistency(abox, concept, true);
			return true;
		} catch (EInconsistencyException ex) {
			return false;
		}
	}


	@Override
	public boolean isConsistent(final IABox<I, L, K, R> abox)
		throws EReasonerException, EInconsistencyException
	{
		try {
			final Collection<? extends IReasonerResult<I, L, K, R>> results = checkConsistency(abox, true);
			return true;
		} catch (EInconsistencyException ex) {
			return false;
		}
	}


	private void propagateClassification(final byte cfMatrix[][], final int changedRow, final int changedCol)
	{
		/**
		 * (cgMatrix[i][j] == 1) ==> (i ⊑ j) (cgMatrix[i][j] == -1) ==> ¬(i ⊑ j)
		 */
		final Queue<Point> queue = new LinkedList<>();
		queue.add(new Point(changedRow, changedCol));

		while (!queue.isEmpty()) {
			final Point item = queue.remove();
			logFinest("Propagating from change at %s", item);
			final int i = item.x;
			final int j = item.y;
			final int ij = cfMatrix[i][j];
			for (int s = 0; s < cfMatrix.length; ++s) {
				if (ij == 1) {
					if ((cfMatrix[s][i] == 1) && (cfMatrix[s][j] == 0)) {
						/* i ⊑ j, s ⊑ i => s ⊑ j */
						cfMatrix[s][j] = 1;
						queue.add(new Point(s, j));
					}
				} else if (ij == -1) {
					if ((cfMatrix[s][j] == 1) && (cfMatrix[i][s] == 0)) {
						cfMatrix[i][s] = -1;
						queue.add(new Point(s, j));
					}
				}
			}
		}
	}


	@Override
	public final Collection<IDLImplies<I, L, K, R>> classify(
		final IABox<I, L, K, R> abox)
		throws EReasonerException, EInconsistencyException
	{
		final Set<K> classes = abox.getClassesInSignature();
		final List<K> classList = new ArrayList<>(classes);
		Collections.sort(classList);
		final byte[][] scMatrix = createSquareMatrixArray(classList);

		final Set<IDLImplies<I, L, K, R>> classifications = new TreeSet<>();

		final IABox<I, L, K, R> baseABox = abox;
		final ITBox<I, L, K, R> tbox = baseABox.getTBox();

		for (IABoxNode<I, L, K, R> node : baseABox) {
			for (IDLTerm<I, L, K, R> clsTerm : node.getTerms().subSet(DLTermOrder.DL_CLASS_REFERENCE)) {
				final IDLClassReference<I, L, K, R> clsRef = (IDLClassReference<I, L, K, R>) clsTerm;
				int clsPos = Collections.binarySearch(classList, clsRef.getElement());
				assert clsPos >= 0;
				logFiner("%s is in initial ABox, marking.", clsRef);
				scMatrix[clsPos][clsPos] = 1;
			}
		}

		for (int i = 0; i < scMatrix.length; ++i) {
			if (scMatrix[i][i] == 0) {
				scMatrix[i][i] = 1;
				final IDLClassReference<I, L, K, R> klass = abox.getDLTermFactory().
					getDLClassReference(classList.get(i));
				logFiner("Testing satisfiability of %s", klass);
				final Collection<? extends IReasonerResult<I, L, K, R>> results = checkConsistency(abox, klass, true);
			}
		}

		for (IDLTerm<I, L, K, R> desc : tbox.subSet(DLTermOrder.DL_IMPLIES)) {
			final IDLImplies<I, L, K, R> imp = (IDLImplies<I, L, K, R>) desc;
			if ((imp.getSubDescription() instanceof IDLClassReference)
				&& (imp.getSuperDescription() instanceof IDLClassReference)) {
				int i = Collections.binarySearch(classList, ((IDLClassReference<I, L, K, R>) imp.
					getSubDescription()).getElement());
				int j = Collections.binarySearch(classList, ((IDLClassReference<I, L, K, R>) imp.
					getSuperDescription()).getElement());
				logFiner("%s implicitly asserted, marking", imp);
				scMatrix[i][j] = 1;
			}
		}

		for (int row = 0; row < scMatrix.length; ++row) {
			for (int col = 0; col < scMatrix.length; ++col) {
				if (scMatrix[row][col] == 0) {
					final IDLClassReference<I, L, K, R> subClass = baseABox.getDLTermFactory().
						getDLClassReference(classList.get(row));
					final IDLClassReference<I, L, K, R> superClass = baseABox.getDLTermFactory().
						getDLClassReference(classList.get(col));
					logFiner("Testing if (subClassOf %s %s)", subClass, superClass);
					boolean isSub = isSubClassOf(baseABox, subClass, superClass);

					if (isSub) {
						scMatrix[row][col] = 1;
					} else {
						scMatrix[row][col] = -1;
					}

					propagateClassification(scMatrix, row, col);
				}
			}
		}

		/**
		 * The dsMatrix contains the direct subclasses, only.
		 *
		 */
		byte[][] dsMatrix = copy(scMatrix);

		for (int i = 0; i < dsMatrix.length; ++i) {
			for (int j = 0; j < dsMatrix[i].length; ++j) {
				if (dsMatrix[i][j] == 1) {
					for (int k = 0; k < dsMatrix.length; ++k) {
						/* find a k, with i ⊑ k and k ⊑ j */
						if ((i != k) && (j != k) && (dsMatrix[i][k] == 1) && (dsMatrix[k][j] == 1)) {
							/* not a direct subclass */
							dsMatrix[i][j] = 0;
							break;
						}
					}
				}
			}
		}

		/**
		 * clone the input ABox and add all direct subclass assertions to the clone
		 *
		 */
		final IABox<I, L, K, R> classifiedABox = abox.clone();
		final ITBox<I, L, K, R> classifiedTBox = classifiedABox.getTBox();
		for (int i = 0; i < dsMatrix.length; ++i) {
			for (int j = 0; j < dsMatrix.length; ++j) {
				if ((i != j) && (dsMatrix[i][j] == 1)) {
					final IDLClassReference<I, L, K, R> subClass = classifiedABox.getDLTermFactory().
						getDLClassReference(classList.get(i));
					final IDLClassReference<I, L, K, R> superClass = classifiedABox.getDLTermFactory().
						getDLClassReference(classList.get(j));
					final IDLImplies<I, L, K, R> imp = classifiedABox.getDLTermFactory().getDLImplies(subClass,
																									  superClass);
					classifications.add(imp);
				}
			}
		}

		return classifications;
	}


	private byte[][] createSquareMatrixArray(final List<K> classList)
	{
		byte[][] scMatrix = new byte[classList.size()][];
		for (int i = 0; i < scMatrix.length; ++i) {
			scMatrix[i] = new byte[classList.size()];
			Arrays.fill(scMatrix[i], (byte) 0);
		}
		return scMatrix;
	}


	private byte[][] copy(final byte[][] src)
	{
		byte[][] tgt = new byte[src.length][];
		for (int i = 0; i < tgt.length; ++i) {
			tgt[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return tgt;
	}
}
