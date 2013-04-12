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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLDataAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <I>
 * @param <K>
 * @param <R> The type of the referenced role.
 */
public class DLDataAllRestriction<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractDLRoleRestriction<I, L, K, R, IDLDataRange<I, L, K, R>>
	implements IDLDataAllRestriction<I, L, K, R> {

	public final static String OPERATOR_NAME = "ONLY";


	public DLDataAllRestriction(final R role, final IDLDataRange<I, L, K, R> d)
	{
		super(DLTermOrder.DL_OBJECT_ALL_RESTRICTION, OPERATOR_NAME, role, d);
	}


	@Override
	@SuppressWarnings("unchecked")
	public DLDataAllRestriction<I, L, K, R> clone()
	{
		return this;
		// return new DLDataAllRestriction<I, L, K, R>(getRole(), (IDLClassExpression<I, L, K, R>) getTerm().clone());
	}


	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(final IDLTerm<I, L, K, R> o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLDataAllRestriction;
			final IDLDataAllRestriction<?, ?, ?, ?> other = (IDLDataAllRestriction<?, ?, ?, ?>) o;
			compare = getRole().compareTo((R) other.getRole());
			if (compare == 0) {
				ITerm otherTerm = other.getTerm();
				assert otherTerm instanceof IDLDataRange;
				compare = getTerm().compareTo((IDLDataRange<I, L, K, R>) otherTerm);
			}
		}
		return compare;
	}


	@Override
	public IDLTerm<I, L, K, R> getBefore()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_BEFORE_DATA_ALL_RESTRICTION);
	}


	@Override
	public IDLTerm<I, L, K, R> getAfter()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_AFTER_DATA_ALL_RESTRICTION);
	}
}
