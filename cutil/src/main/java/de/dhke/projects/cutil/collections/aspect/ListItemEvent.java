/**
 * (c) 2009-2013 Peter Wullinger
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

import java.util.List;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 * @param <E> The element type
 * @param <L> The actual list type.
 */
public class ListItemEvent<E, L extends List<E>>
	extends CollectionItemEvent<E, L>
{
	private static final long serialVersionUID = -947692844701970686L;
	private final int _index;

	public ListItemEvent(final Object sender, final L l, final E item, final int index)
	{
		super(sender, l, item);
		_index = index;

	}

	public ListItemEvent(final L l, final E item, final int index)
	{
		super(l, l, item);
		_index = index;
	}

	public final int getIndex()
	{
		return _index;
	}
}
