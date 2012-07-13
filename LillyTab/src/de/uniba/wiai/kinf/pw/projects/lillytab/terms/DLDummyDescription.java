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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * <p>
 * A dummy {@link IDLClassExpression}  of the specified type. Should be used
 * for sorting, only.
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public final class DLDummyDescription<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IDLRestriction<Name, Klass, Role>
{
	private final DLTermOrder _termType;

	public DLDummyDescription(DLTermOrder termType)
	{
		_termType = termType;
	}

	public DLTermOrder getDLTermOrder()
	{
		return _termType;
	}

	@Override
	public ITerm clone()
	{
		return this;
	}

	public int compareTo(IDLTerm<Name, Klass, Role> o)
	{
		return getDLTermOrder().compareTo(o);
	}

	public IDLRestriction<Name, Klass, Role> getBefore()
	{
		return this;
	}

	public IDLRestriction<Name, Klass, Role> getAfter()
	{
		return this;
	}


	public String toString(IToStringFormatter entityFormatter)
	{
		return toString();
	}	
}
