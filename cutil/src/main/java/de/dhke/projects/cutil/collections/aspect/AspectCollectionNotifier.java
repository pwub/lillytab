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

import java.util.LinkedList;
import java.util.List;


/**
 *
 * @param <E> Collection element type.
 * @param <C> Actual collection type.
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectCollectionNotifier<E, C>
{
	private final Object _sender;
	private List<ICollectionListener<E, C>> _listeners = new LinkedList<>();

	protected AspectCollectionNotifier()
	{
		_sender = this;
	}

	protected AspectCollectionNotifier(final Object sender)
	{
		_sender = sender;
	}

	public List<ICollectionListener<E, C>> getListeners()
	{
		return _listeners;
	}

	protected Object getSender()
	{
		return _sender;
	}

	protected void setListeners(final List<ICollectionListener<E, C>> listeners)
	{
		_listeners = listeners;
	}

	protected final CollectionItemEvent<E, C> notifyBeforeElementAdded(final C collection, final E item)
	{
		CollectionItemEvent<E, C> ev = new CollectionItemEvent<>(_sender, collection, item);
		notifyBeforeElementAdded(ev);
		return ev;
	}

	protected void notifyBeforeElementAdded(final CollectionItemEvent<E, C> ev)
	{
		if (!_listeners.isEmpty()) {
			for (ICollectionListener<E, C> listener : _listeners)
				listener.beforeElementAdded(ev);
		}
	}

	protected final CollectionItemEvent<E, C> notifyBeforeElementRemoved(final C collection, final E item)
	{
		final CollectionItemEvent<E, C> ev = new CollectionItemEvent<>(_sender, collection, item);
		notifyBeforeElementRemoved(ev);
		return ev;
	}

	protected void notifyBeforeElementRemoved(final CollectionItemEvent<E, C> ev)
	{
		if (!_listeners.isEmpty()) {
			for (ICollectionListener<E, C> listener : _listeners)
				listener.beforeElementRemoved(ev);
		}
	}

	protected final CollectionItemReplacedEvent<E, C> notifyBeforeElementReplaced(final C collection, final E oldItem,
																			   final E newItem)
	{
		CollectionItemReplacedEvent<E, C> ev = new CollectionItemReplacedEvent<>(_sender, collection, oldItem,
																					 newItem);
		notifyBeforeElementReplaced(ev);
		return ev;
	}

	protected void notifyBeforeElementReplaced(final CollectionItemReplacedEvent<E, C> ev)
	{
		for (ICollectionListener<E, C> listener : getListeners())
			listener.beforeElementReplaced(ev);

	}

	protected final CollectionEvent<E, C> notifyBeforeCollectionCleared(final C collection)
	{
		CollectionEvent<E, C> ev = new CollectionEvent<>(_sender, collection);
		notifyBeforeCollectionCleared(ev);
		return ev;
	}

	protected void notifyBeforeCollectionCleared(final CollectionEvent<E, C> ev)
	{
		for (ICollectionListener<E, C> listener : getListeners())
			listener.beforeCollectionCleared(ev);
	}

	protected final CollectionItemEvent<E, C> notifyAfterElementAdded(final C collection, final E item)
	{
		CollectionItemEvent<E, C> ev = new CollectionItemEvent<>(_sender, collection, item);
		notifyAfterElementAdded(ev);
		return ev;
	}

	protected void notifyAfterElementAdded(final CollectionItemEvent<E, C> ev)
	{
		RuntimeException lastException = null;
		for (ICollectionListener<E, C> listener : getListeners()) {
			try {
				listener.afterElementAdded(ev);
			} catch (RuntimeException ex) {
				lastException = ex;
			}
		}
		/*
		 if (lastException != null)
		 throw lastException;
		 */
	}

	protected final CollectionItemEvent<E, C> notifyAfterElementRemoved(final C collection, final E item)
	{
		CollectionItemEvent<E, C> ev = new CollectionItemEvent<>(_sender, collection, item);
		notifyAfterElementRemoved(ev);
		return ev;
	}

	protected void notifyAfterElementRemoved(final CollectionItemEvent<E, C> ev)
	{
		RuntimeException lastException = null;
		if (!_listeners.isEmpty()) {
			for (ICollectionListener<E, C> listener : _listeners) {
				try {
					listener.afterElementRemoved(ev);
				} catch (RuntimeException ex) {
					lastException = ex;
				}
			}
		}
		/*
		 if (lastException != null)
		 throw lastException;
		 */
	}

	protected final CollectionItemReplacedEvent<E, C> notifyAfterElementReplaced(final C collection, final E oldItem,
																			  final E newItem)
	{
		CollectionItemReplacedEvent<E, C> ev = new CollectionItemReplacedEvent<>(_sender, collection, oldItem,
																					 newItem);
		notifyAfterElementReplaced(ev);
		return ev;
	}

	protected void notifyAfterElementReplaced(final CollectionItemReplacedEvent<E, C> ev)
	{
		RuntimeException lastException = null;
		if (!_listeners.isEmpty()) {
			for (ICollectionListener<E, C> listener : _listeners) {
				try {
					listener.afterElementReplaced(ev);
				} catch (RuntimeException ex) {
					lastException = ex;
				}
			}
		}
		/*
		 if (lastException != null)
		 throw lastException;
		 */
	}

	protected final CollectionEvent<E, C> notifyAfterCollectionCleared(final C collection)
	{
		CollectionEvent<E, C> ev = new CollectionEvent<>(_sender, collection);
		notifyAfterCollectionCleared(ev);
		return ev;
	}

	protected void notifyAfterCollectionCleared(final CollectionEvent<E, C> ev)
	{
		RuntimeException lastException = null;
		if (!_listeners.isEmpty()) {
			for (ICollectionListener<E, C> listener : _listeners) {
				try {
					listener.afterCollectionCleared(ev);
				} catch (RuntimeException ex) {
					lastException = ex;
				}
			}
		}
		/*
		 if (lastException != null)
		 throw lastException;
		 */
	}
}
