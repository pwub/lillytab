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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyDescription;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IUnaryOperator;

/**
 *
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLSomeRestriction<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractDLRoleRestriction<Name, Klass, Role>
	implements IDLSomeRestriction<Name, Klass, Role>, IUnaryOperator<IDLRestriction<Name, Klass, Role>> {

	public final static String OPERATOR_NAME = "SOME";


	public DLSomeRestriction(final Role role, final IDLRestriction<Name, Klass, Role> d)
	{
		super(DLTermOrder.DL_SOME_RESTRICTION, OPERATOR_NAME, role, d);
	}


	@Override
	public DLSomeRestriction<Name, Klass, Role> clone()
	{
		return this;
		// return new DLSomeRestriction<Name, Klass, Role>(getRole(), getTerm());
	}


	@SuppressWarnings("unchecked")
	public int compareTo(final IDLTerm o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLSomeRestriction;
			@SuppressWarnings("unchecked")
			IDLSomeRestriction<Name, Klass, Role> other = (IDLSomeRestriction<Name, Klass, Role>) o;
			/**
			 * We could include <Role> in the above cast instead of using the erasure, but this version is actually less
			 * restrictive
			 */
			compare = getRole().compareTo(other.getRole());
			if (compare == 0) {
				ITerm otherTerm = other.getTerm();
				assert otherTerm instanceof IDLRestriction;
				compare = getTerm().compareTo((IDLRestriction<Name, Klass, Role>) otherTerm);
			}
		}
		return compare;
	}


	@Override
	public IDLRestriction<Name, Klass, Role> getBefore()
	{
		return new DLDummyDescription<>(DLTermOrder.DL_BEFORE_SOME_RESTRICTION);
	}


	@Override
	public IDLRestriction<Name, Klass, Role> getAfter()
	{
		return new DLDummyDescription<>(DLTermOrder.DL_AFTER_SOME_RESTRICTION);
	}
}
