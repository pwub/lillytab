/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.dhke.projects.lutil.LoggingClass;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public abstract class AbstractReasoner<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends LoggingClass
	implements IReasoner<Name, Klass, Role> {

	@Override
	public boolean isSubClassOf(IABox<Name, Klass, Role> abox, Klass presumedSub, Klass presumedSuper)
		throws EReasonerException, EInconsistencyException
	{
		final IDLClassReference<Name, Klass, Role> subRef = abox.getDLTermFactory().getDLClassReference(presumedSub);
		final IDLClassReference<Name, Klass, Role> superRef = abox.getDLTermFactory().getDLClassReference(presumedSuper);
		return isSubClassOf(abox, presumedSub, presumedSuper);
	}


	@Override
	public boolean isConsistent(final IDLRestriction<Name, Klass, Role> concept,
								final IABoxFactory<Name, Klass, Role> aboxFactory)
		throws EReasonerException, EInconsistencyException
	{
		IABox<Name, Klass, Role> abox = aboxFactory.createABox();
		return isConsistent(abox, concept);
	}


	@Override
	public Collection<? extends IReasonerResult<Name, Klass, Role>> checkConsistency(final IABox<Name, Klass, Role> abox)
		throws EReasonerException, EInconsistencyException
	{
		return checkConsistency(abox, true);
	}


	@Override
	public boolean isConsistent(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> concept)
		throws EReasonerException, EInconsistencyException
	{
		final Collection<? extends IReasonerResult<Name, Klass, Role>> results = checkConsistency(abox, concept, true);
		return !results.isEmpty();
	}


	@Override
	public boolean isConsistent(final IABox<Name, Klass, Role> abox)
		throws EReasonerException, EInconsistencyException
	{
		final Collection<? extends IReasonerResult<Name, Klass, Role>> results = checkConsistency(abox, true);
		return !results.isEmpty();
	}


	private void propagateClassification(final byte cfMatrix[][])
	{
		/**
		 * (cgMatrix[i][j] == 1) ==> (i ⊑ j) (cgMatrix[i][j] == -1) ==> ¬(i ⊑ j)
		 *
		 */
		for (int i = 0; i < cfMatrix.length; ++i) {
			for (int j = 0; j < cfMatrix.length; ++j) {
				if (cfMatrix[i][j] == 1) {
					for (int k = 0; k < cfMatrix.length; ++k) {
						if (cfMatrix[j][k] == 1) {
							/* i ⊑ j => ∀ k . j ⊑ k => i ⊑ k */
							cfMatrix[i][k] = 1;
						}

						if (cfMatrix[k][i] == 1) {
							/* i ⊑ j => ∀ k . k ⊑ i => k ⊑ j */
							cfMatrix[k][j] = 1;
						}
					}
				}
			}
		}
	}


	@Override
	public final Collection<IDLImplies<Name, Klass, Role>> classify(
		final IABox<Name, Klass, Role> abox)
		throws EReasonerException, EInconsistencyException
	{
		final ITBox<Name, Klass, Role> tbox = abox.getTBox();
		final Set<Klass> classes = abox.getClassesInSignature();
		final List<Klass> classList = new ArrayList<>(classes);
		Collections.sort(classList);
		final byte[][] scMatrix = createSquareMatrixArray(classList);

		final Set<IDLImplies<Name, Klass, Role>> classifications = new TreeSet<>();

		for (int i = 0; i < scMatrix.length; ++i) {
			scMatrix[i][i] = 1;

			final IDLClassReference<Name, Klass, Role> subClass = abox.getDLTermFactory().
				getDLClassReference(classList.get(i));
			final IABox<Name, Klass, Role> kloneABox = abox.clone();
			final IABoxNode<Name, Klass, Role> node = kloneABox.createNode(false);
			node.addUnfoldedDescription(subClass);
			checkConsistency(kloneABox);
		}

		for (IDLTerm<Name, Klass, Role> desc : tbox.subSet(DLTermOrder.DL_IMPLIES)) {
			final IDLImplies<Name, Klass, Role> imp = (IDLImplies<Name, Klass, Role>) desc;
			if ((imp.getSubDescription() instanceof IDLClassReference)
				&& (imp.getSuperDescription() instanceof IDLClassReference)) {
				int i = Collections.binarySearch(classList, ((IDLClassReference<Name, Klass, Role>) imp.
					getSubDescription()).getElement());
				int j = Collections.binarySearch(classList, ((IDLClassReference<Name, Klass, Role>) imp.
					getSuperDescription()).getElement());
				scMatrix[i][j] = 1;
			}
		}

		for (int i = 0; i < scMatrix.length; ++i) {
			for (int j = 0; j < scMatrix.length; ++j) {
				if (scMatrix[i][j] == 0) {
					final IDLClassReference<Name, Klass, Role> subClass = abox.getDLTermFactory().
						getDLClassReference(classList.get(i));
					final IDLClassReference<Name, Klass, Role> superClass = abox.getDLTermFactory().
						getDLClassReference(classList.get(j));
					boolean isSub = isSubClassOf(abox, subClass, superClass);
					if (isSub) {
						scMatrix[i][j] = 1;
					} else {
						scMatrix[i][j] = -1;
					}

					propagateClassification(scMatrix);
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
		final IABox<Name, Klass, Role> classifiedABox = abox.clone();
		final ITBox<Name, Klass, Role> classifiedTBox = classifiedABox.getTBox();
		for (int i = 0; i < dsMatrix.length; ++i) {
			for (int j = 0; j < dsMatrix.length; ++j) {
				if ((i != j) && (dsMatrix[i][j] == 1)) {
					final IDLClassReference<Name, Klass, Role> subClass = classifiedABox.getDLTermFactory().
						getDLClassReference(classList.get(i));
					final IDLClassReference<Name, Klass, Role> superClass = classifiedABox.getDLTermFactory().
						getDLClassReference(classList.get(j));
					final IDLImplies<Name, Klass, Role> imp = classifiedABox.getDLTermFactory().getDLImplies(subClass,
																											 superClass);
					classifications.add(imp);
				}
			}
		}

		return classifications;
	}


	private byte[][] createSquareMatrixArray(final List<Klass> classList)
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
