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
package de.dhke.projects.cutil.collections.aspect;

import java.util.EventObject;

/**
 *
 * @param <E> The item type
 * @param <C> The actual collection type.
 * @author Peter Wullinger <java@dhke.de>
 */
public class CollectionEvent<E, C>
	extends EventObject
{
	private static final long serialVersionUID = -9004104280689782368L;
	/* don't serialize the whole collection */
	private final transient C _collection;

	public CollectionEvent(final C collection)
	{
		super(collection);
		_collection = collection;
	}

	public CollectionEvent(final Object sender, final C collection)
	{
		super(sender);
		_collection = collection;
	}

	/**
	 * @return the collection
	 */
	public final C getCollection()
	{
		return _collection;
	}

	@Override
	public String toString()
	{
		return getClass().getName() + " on " + getCollection().toString();
	}
}
