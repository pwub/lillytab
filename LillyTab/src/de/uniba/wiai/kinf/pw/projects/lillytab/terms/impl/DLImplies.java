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
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLImplies<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractBinaryDLOperator<IDLRestriction<Name, Klass, Role>>
	implements IDLImplies<Name, Klass, Role>
{
	public final static String OPERATOR_NAME = "IMPLIES";

	public DLImplies(final IDLRestriction<Name, Klass, Role> sub, final IDLRestriction<Name, Klass, Role> sup)
	{
		super(DLTermOrder.DL_IMPLIES, OPERATOR_NAME, sub, sup);
	}

	public IDLRestriction<Name, Klass, Role> getSubDescription()
	{
		return get(0);
	}

	public IDLRestriction<Name, Klass, Role> getSuperDescription()
	{
		return get(1);
	}

	public boolean isDefinitorial()
	{
		return getSubDescription() instanceof IDLClassReference;
	}

	@Override
	public DLImplies<Name, Klass, Role> clone()
	{
		return this;
		// return new DLImplies<Name, Klass, Role>(getSubDescription(), getSuperDescription());
	}

	public int compareTo(final IDLTerm<Name, Klass, Role> o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLImplies;
			compare = TermUtil.compareTermList(this, (IDLImplies<Name, Klass, Role>) o);
		}
		return compare;
	}

	public IDLRestriction<Name, Klass, Role> getBefore()
	{
		return new DLDummyDescription<Name, Klass, Role>(DLTermOrder.DL_BEFORE_IMPLIES);
	}

	public IDLRestriction<Name, Klass, Role> getAfter()
	{
		return new DLDummyDescription<Name, Klass, Role>(DLTermOrder.DL_AFTER_IMPLIES);
	}

}
