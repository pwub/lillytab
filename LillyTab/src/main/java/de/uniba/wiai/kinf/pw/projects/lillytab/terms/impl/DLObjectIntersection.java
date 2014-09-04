/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITermList;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import java.util.Collection;


/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLObjectIntersection<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractDLOperatorTerm<I, L, K, R, IDLClassExpression<I, L, K, R>>
	implements IDLObjectIntersection<I, L, K, R>
{
	public final static String OPERATOR_NAME = "AND";

	public DLObjectIntersection(final Collection<? extends IDLClassExpression<I, L, K, R>> ds)
	{
		super(DLTermOrder.DL_OBJECT_INTERSECTION, OPERATOR_NAME, ds.size());
		if (ds.size() < 2) {
			throw new IllegalArgumentException("DLIntersection needs at least two subterms");
		} else {
			int i = 0;
			for (IDLClassExpression<I, L, K, R> d : ds) {
				getModifiableTermList().set(i, d);
				++i;
			}
			sortAndEnsureUnique(this, 2);
		}
	}

	public DLObjectIntersection(final IDLClassExpression<I, L, K, R> d0, final IDLClassExpression<I, L, K, R> d1)
	{
		super(DLTermOrder.DL_OBJECT_INTERSECTION, OPERATOR_NAME, 2);
		getModifiableTermList().set(0, d0);
		getModifiableTermList().set(1, d1);
		/* ensure order */
		sortAndEnsureUnique(this, 2);
	}

	@Override
	public int hashCode()
	{
		/* no need to override hashcode */
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		} else /* intersection is a commutative operation, this equality needs to be as well */ {
			return ((obj instanceof IDLObjectIntersection) && containsAll((IDLObjectIntersection) obj));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public DLObjectIntersection<I, L, K, R> clone()
	{
		return this;
		// return new DLObjectIntersection<I, L, K, R>((IDLClassExpression<I, L, K, R>) getFirstTerm().clone(), (IDLClassExpression<I, L, K, R>) getSecondTerm().clone());
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(IDLTerm o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLObjectIntersection;
			compare = TermUtil.compareTermList(this, (IDLObjectIntersection<I, L, K, R>) o);
		}
		return compare;
	}

	@Override
	public IDLTerm<I, L, K, R> getBefore()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_BEFORE_OBJECT_INTERSECTION);
	}

	@Override
	public IDLTerm<I, L, K, R> getAfter()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_AFTER_OBJECT_INTERSECTION);
	}

	@Override
	public ITermList<? extends IDLNodeTerm<I, L, K, R>> getTerms()
	{
		return this;
	}
}
