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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyDescription;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param  <Role> The type of the referenced role.
 */
public class DLAllRestriction<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractDLRoleRestriction<Name, Klass, Role>
	implements IDLAllRestriction<Name, Klass, Role>
{
	public final static String OPERATOR_NAME = "ONLY";

	public DLAllRestriction(final Role role, final IDLRestriction<Name, Klass, Role> d)
	{
		super(DLTermOrder.DL_ALL_RESTRICTION, OPERATOR_NAME, role, d);
	}

	@Override
	@SuppressWarnings("unchecked")
	public DLAllRestriction<Name, Klass, Role> clone()
	{
		return this;
		// return new DLAllRestriction<Name, Klass, Role>(getRole(), (IDLClassExpression<Name, Klass, Role>) getTerm().clone());
	}


	@SuppressWarnings("unchecked")
	public int compareTo(final IDLTerm<Name, Klass, Role> o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLAllRestriction;
			IDLAllRestriction other = (IDLAllRestriction)o;
			compare = getRole().compareTo((Role)other.getRole());
			if (compare == 0) {
				ITerm otherTerm = other.getTerm();
				assert otherTerm instanceof IDLRestriction;
				compare = getTerm().compareTo((IDLRestriction)otherTerm);
			}
		}
		return compare;
	}

	public IDLRestriction<Name, Klass, Role> getBefore()
	{
		return new DLDummyDescription<Name, Klass, Role>(DLTermOrder.DL_BEFORE_ALL_RESTRICTION);
	}

	public IDLRestriction<Name, Klass, Role> getAfter()
	{
		return new DLDummyDescription<Name, Klass, Role>(DLTermOrder.DL_AFTER_ALL_RESTRICTION);
	}
}
