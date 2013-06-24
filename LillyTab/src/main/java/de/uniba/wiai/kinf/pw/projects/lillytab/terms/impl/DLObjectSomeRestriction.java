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
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLObjectSomeRestriction<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends AbstractDLRoleRestriction<I, L, K, R, IDLClassExpression<I, L, K, R>>
	implements IDLObjectSomeRestriction<I, L, K, R> {

	public final static String OPERATOR_NAME = "SOME";


	public DLObjectSomeRestriction(final R role, final IDLClassExpression<I, L, K, R> d)
	{
		super(DLTermOrder.DL_OBJECT_SOME_RESTRICTION, OPERATOR_NAME, role, d);
	}


	@Override
	public DLObjectSomeRestriction<I, L, K, R> clone()
	{
		return this;
		// return new DLObjectSomeRestriction<I, L, K, R>(getRole(), getTerm());
	}


	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(final IDLTerm o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLObjectSomeRestriction;
			@SuppressWarnings("unchecked")
			IDLObjectSomeRestriction<I, L, K, R> other = (IDLObjectSomeRestriction<I, L, K, R>) o;
			/**
			 * We could include <R> in the above cast instead of using the erasure, but this version is actually less
			 * restrictive
			 */
			compare = getRole().compareTo(other.getRole());
			if (compare == 0) {
				ITerm otherTerm = other.getTerm();
				assert otherTerm instanceof IDLClassExpression;
				compare = getTerm().compareTo((IDLClassExpression<I, L, K, R>) otherTerm);
			}
		}
		return compare;
	}


	@Override
	public IDLTerm<I, L, K, R> getBefore()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_BEFORE_OBJECT_SOME_RESTRICTION);
	}


	@Override
	public IDLTerm<I, L, K, R> getAfter()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_AFTER_OBJECT_SOME_RESTRICTION);
	}
}
