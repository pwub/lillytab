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


/**
 *
 * @param <E> The item type
 * @param <C> The actual collection type.
 * @author Peter Wullinger <java@dhke.de>
 */
public class CollectionItemEvent<E, C>
	extends CollectionEvent<E, C>
{
	private static final long serialVersionUID = -3505485141130709499L;
	private final E _item;


	public CollectionItemEvent(final Object sender, final C collection, final E item)
	{
		super(sender, collection);
		_item = item;
	}

	public CollectionItemEvent(final C collection, final E item)
	{
		super(collection);
		_item = item;
	}

	public final E getItem() {
		return _item;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " on " + getCollection().toString() + ": " + _item.toString();
	}


}
