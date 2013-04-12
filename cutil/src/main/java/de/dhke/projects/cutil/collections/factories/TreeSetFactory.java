/**
 * (c) 2009-2012 Peter Wullinger
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

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class TreeSetFactory<E>
	implements ICollectionFactory<E, Set<E>>
{
	private final Comparator<? super E> _comparator;

	public TreeSetFactory(final Comparator<? super E> comparator)
	{
		_comparator = comparator;
	}

	public TreeSetFactory()
	{
		_comparator = null;
	}

	@Override
	public Set<E> getInstance()
	{
		if (_comparator == null)
			return new TreeSet<>();
		else
			return new TreeSet<>(_comparator);
	}

	@Override
	public Set<E> getInstance(final Set<E> baseCollection)
	{
		if (_comparator == null)
			return new TreeSet<>(baseCollection);
		else {
			Set<E> treeSet = getInstance();
			treeSet.addAll(baseCollection);
			return treeSet;
		}
	}
}