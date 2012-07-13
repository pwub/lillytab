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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import javax.management.relation.Role;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLNothing<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IDLRestriction<Name, Klass, Role>, IAtom
{
	public DLNothing()
	{
	}

	public DLTermOrder getDLTermOrder()
	{
		return DLTermOrder.DL_NOTHING;
	}

	@Override
	public ITerm clone()
	{
		/* DLThing is a singleton */
		return this;
	}

	public int compareTo(final IDLTerm<Name, Klass, Role> o)
	{
		/* DLNothing is a singleton, order is only dependent on term order */
		return getDLTermOrder().compareTo(o);
	}

	@Override
	public int hashCode()
	{
		return getClass().hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		return obj instanceof DLNothing;
	}

	@Override
	public String toString()
	{
		return "_Nothing_";
	}
	
	public String toString(IToStringFormatter entityFormatter)
	{
		return toString();
	}

	public IDLRestriction<Name, Klass, Role> getBefore()
	{
		return new DLDummyDescription<Name, Klass, Role>(DLTermOrder.DL_BEFORE_NOTHING);
	}

	public IDLRestriction<Name, Klass, Role> getAfter()
	{
		return new DLDummyDescription<Name, Klass, Role>(DLTermOrder.DL_AFTER_NOTHING);
	}
}

