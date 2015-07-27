/**
 * (c) 2009-2014 Peter Wullinger
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
package de.dhke.projects.cutil.collections.factories;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @param <E> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class HashSetFactory<E>
	implements ICollectionFactory<E, Set<E>>
{
	private final int _initialCapacity;

	public HashSetFactory()
	{
		_initialCapacity = -1;
	}

	public HashSetFactory(final int initialCapacity)
	{
		_initialCapacity = initialCapacity;
	}


	@Override
	public Set<E> getInstance()
	{
		if (_initialCapacity < 0)
			return new HashSet<>();
		else
			return new HashSet<>(_initialCapacity);
	}

	@Override
	public Set<E> getInstance(final Collection<? extends E> baseCollection)
	{
		return new HashSet<>(baseCollection);
	}

}
