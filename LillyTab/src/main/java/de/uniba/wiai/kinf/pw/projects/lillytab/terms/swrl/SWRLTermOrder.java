/**
 * (c) 2009-2014 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public enum SWRLTermOrder {

	SWRL_BEFORE(null),
	SWRL_BEFORE_CLASS_ATOM(null),
	SWRL_CLASS_ATOM(ISWRLClassAtom.class),
	SWRL_AFTER_CLASS_ATOM(null),
	SWRL_BEFORE_DATA_RANGE_ATOM(null),
	SWRL_DATA_RANGE_ATOM(ISWRLDataRangeAtom.class),
	SWRL_AFTER_DATA_RANGE_ATOM(null),
	SWRL_BEFORE_OBJECT_ROLE_ATOM(null),
	SWRL_OBJECT_ROLE_ATOM(ISWRLObjectRoleAtom.class),
	SWRL_AFTER_OBJECT_ROLE_ATOM(null),
	SWRL_BEFORE_DATA_ROLE_ATOM(null),
	SWRL_DATA_ROLE_ATOM(ISWRLDataRoleAtom.class),
	SWRL_AFTER_DATA_ROLE_ATOM(null),
	SWRL_BEFORE_INTERSECTION(null),
	SWRL_INTERSECTION(ISWRLIntersection.class),
	SWRL_AFTER_INTERSECTION(null),
	SWRL_AFTER(null);
	/**
	 * The interface class associated with the enumeration item
	 *
	 */
	private final Class<? extends ISWRLTerm> _swrlTermClass;


	/**
	 * Create a new enumeration item wrapping the specified class.
	 *
	 * @param klass The wrapped class.
	 */
	SWRLTermOrder(final Class<? extends ISWRLTerm> klass)
	{
		_swrlTermClass = klass;
	}


	/**
	 * Compare the term orders of two {@link ISWRLTerm}s.
	 *
	 * @param t0 The first term
	 * @param t1 The second term
	 * <p/>
	 * @return {@literal -1}, if {@literal t0} has a lower term order than {@literal t1},
	 *	{@literal 0} if the term orders match, and {@literal 1}, if {@literal t0} has a higher term order than
	 *            {@literal t1}.
	 */
	public static int compareTermOrder(final ISWRLTerm t0, final ISWRLTerm t1)
	{
		return t0.getSWRLTermOrder().compareTo(t1);
	}


	/**
	 * Compare this term order to the term order of a {@link ISWRLTerm}.
	 *
	 * @param formula The term whose term order to compare.
	 * <p/>
	 * @return {@literal -1}, if {@literal t0} has a lower term order than {@literal this},
	 *	{@literal 0} if the term orders match, and {@literal 1}, if {@literal t0} has a higher term order than
	 *            {@literal this}.
	 */
	public int compareTo(final ISWRLTerm formula)
	{
		return compareTo(formula.getSWRLTermOrder());
	}


	/**
	 * Get the {@link IDLTerm} interface class wrapped by this enumeration item.
	 *
	 * @return The {@link IDLTerm} interface class
	 */
	public Class<? extends ISWRLTerm> getSWRLTermClass()
	{
		return _swrlTermClass;
	}
}
