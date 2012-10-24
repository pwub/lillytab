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
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;


/**
 *
 * @param <Klass> The type for DL classes
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLClassReference<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IDLClassReference<Name, Klass, Role>
{
	private final Klass _klass;

	public DLClassReference(final Klass klass)
	{
		_klass = klass;
	}

	@Override
	public Klass getElement()
	{
		return _klass;
	}

	@Override
	public int hashCode()
	{
		return _klass.hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		return ((obj instanceof IDLClassReference) && getElement().equals(((IDLClassReference) obj).getElement()));
	}

	@Override
	public String toString()
	{
		/* Cast: netbeans/compiler workaround */
		return _klass.toString();
	}


	@Override
	public String toString(IToStringFormatter entityFormatter)
	{
		return entityFormatter.toString(_klass);
	}

	@Override
	public DLClassReference<Name, Klass, Role> clone()
	{
		return this;
		// return new DLClassReference<Name, Klass, Role>(_klass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(final IDLTerm<Name, Klass, Role> o)
	{
		final int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLClassReference;
			return _klass.compareTo((Klass)(((IDLClassReference)o).getElement()));
		} else
			return compare;
	}

	@Override
	public DLTermOrder getDLTermOrder()
	{
		return DLTermOrder.DL_CLASS_REFERENCE;
	}

	@Override
	public IDLRestriction<Name, Klass, Role> getBefore()
	{
		return new DLDummyDescription<Name, Klass, Role>(DLTermOrder.DL_BEFORE_CLASS_REFERENCE);
	}

	@Override
	public IDLRestriction<Name, Klass, Role> getAfter()
	{
		return new DLDummyDescription<Name, Klass, Role>(DLTermOrder.DL_AFTER_CLASS_REFERENCE);
	}
}
