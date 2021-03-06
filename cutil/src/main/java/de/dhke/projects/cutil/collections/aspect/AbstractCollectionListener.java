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
 * @author Peter Wullinger <java@dhke.de>
 */
public class AbstractCollectionListener<E, C>
	implements ICollectionListener<E, C>
{
	@Override
	public void beforeElementAdded(final CollectionItemEvent<E, C> e)
	{
	}


	@Override
	public void beforeElementRemoved(final CollectionItemEvent<E, C> e)
	{
	}


	@Override
	public void beforeElementReplaced(final CollectionItemReplacedEvent<E, C> e)
	{
	}


	@Override
	public void beforeCollectionCleared(final CollectionEvent<E, C> e)
	{
	}


	@Override
	public void afterElementAdded(final CollectionItemEvent<E, C> e)
	{
	}


	@Override
	public void afterElementRemoved(final CollectionItemEvent<E, C> e)
	{
	}


	@Override
	public void afterElementReplaced(final CollectionItemReplacedEvent<E, C> e)
	{
	}


	@Override
	public void afterCollectionCleared(final CollectionEvent<E, C> e)
	{
	}
}
