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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 *
 * @param <Name> 
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLIndividual<Name extends Comparable<Name>>
	implements IDLIndividual<Name>
{
	private final SortedSet<Name> _names = new TreeSet<Name>();
	private final int _id;

	public DLIndividual(final int id)
	{
		_id = id;
	}

	public DLIndividual(final int id, final Name name)
	{
		_id = id;
		_names.add(name);
	}

	public DLIndividual(final int id, final Collection<Name> names)
	{
		_id = id;
		_names.addAll(names);
	}

	public int getID()
	{
		return _id;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj instanceof IDLIndividual) {
			final IDLIndividual other = (IDLIndividual) obj;
			return _id == other.getID();
		} else
			return false;
	}

	@Override
	public int hashCode()
	{
		return 23 + getID() * 13;
	}

	public int compareTo(IDLIndividual<Name> o)
	{
		return _id - o.getID();
	}

	public Set<Name> getNames()
	{
		return _names;
	}

	public Name getPrimaryName()
	{
		if (_names.isEmpty())
			return null;
		else
			return _names.first();
	}

	public boolean isAnonymous()
	{
		return _names.isEmpty();
	}

	@Override
	public String toString()
	{
		return _id + ": " + _names.toString();
	}
}
