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

import java.util.Collection;


/**
 * 
 * Implements a the event interface for all aspect collections
 * starting with {@link AspectCollection}. Specialized variations
 * of {@link ICollectionListener}s are supported by all
 * aspect collections.
 * <p />
 * Listeners may be registered with aspect collections and
 * are informed before and after the contents of the associated
 * collection change.
 * <p />
 * Listeners are able to veto changes to collections by raising
 * an exception from on of the {@literal before*} handlers.
 * 
 *
 * @author Peter Wullinger <java@dhke.de>
 * @param <E> The element type
 * @param <C> The actual collection type.
 */
public interface ICollectionListener<E, C>
{
	/**
	 * 
	 * Called, when an element is about to be added to a collection.
	 * <p />
	 * Invocation of this function signals the <em>intention</em> to add an item
	 * to the collection. It does <em>not</em> signal the appropriate operation will
	 * actually take place and modify the collection.
	 * <p />
	 * Implementations should
	 * make sure, that
	 * {@link #beforeElementAdded(de.dhke.projects.cutil.collections.aspect.CollectionItemEvent) }
	 * is called only for elements that will really be added, but this is not part
	 * of the required protocol.
	 * <p />
	 * For example, when adding an already existing item to a set,
	 * {@link #beforeElementAdded(de.dhke.projects.cutil.collections.aspect.CollectionItemEvent)}
	 * may be called for the item, but the collection will not be actually modified
	 * and no corresponding call to {@link #afterElementAdded(de.dhke.projects.cutil.collections.aspect.CollectionItemEvent) }
	 * will follow.
	 * <p />
	 * Raising an exception from the handler will prevent further processing
	 * and the exception will be propagated to the caller.
	 * <p />
	 * Aspect collections make sure that batch add operations {@link java.util.Collection#addAll(java.util.Collection)}
	 * are done atomically. This means, that all before add handlers will
	 * be called before actual mofications take place.
	 * 
	 * @param e The event
	 */
	void beforeElementAdded(CollectionItemEvent<E, C> e);

	/**
	 * 
	 * Called, when an element is about to be removed from a collection.
	 * <p />
	 * Invocation of this function signals the <em>intention</em> to remove an item
	 * from the collection. It does <em>not</em> signal the appropriate operation will
	 * actually take place and modify the collection.
	 * <p />
	 * Implementations should make sure, that
	 * {@link #beforeElementReplaced(de.dhke.projects.cutil.collections.aspect.CollectionItemReplacedEvent)}
	 * is called only for elements that will really be replaced later, but this is not part
	 * of the required protocol.
	 * <p />
	 * Raising an exception from the handler will prevent further processing
	 * and the exception will be propagated to the caller.
	 * <p />
	 * Aspect collections make sure that batch remove operations (@link Collection#addAll(Collection<?>)}
	 * are done atomically. This means, that all before add handlers will
	 * be called before actual mofications take place.
	 * 
	 * @param e The event
	 */
	void beforeElementRemoved(CollectionItemEvent<E, C> e);

	/**
	 * 
	 * Called, when an existing element is about to be replaced with another item.
	 * <p />
	 * Invocation of this function signals the <em>intention</em> to replace an item
	 * in the collection. It does <em>not</em> signal the appropriate operation will
	 * actually take place and modify the collection.
	 * <p />
	 * For example, when updating a map {@link java.util.Map#put(java.lang.Object, java.lang.Object) }, with an
	 * key-value pair that already exists in the collection,
	 * {@link #beforeElementReplaced(de.dhke.projects.cutil.collections.aspect.CollectionItemReplacedEvent) }
	 * will be called, but no modification will happen later on.
	 * <p />
	 * Implementations should make sure, that
	 * {@link #beforeElementReplaced(de.dhke.projects.cutil.collections.aspect.CollectionItemReplacedEvent)}
	 * is called only for elements that will really be replaced later, but this is not part
	 * of the required protocol.
	 * <p />
	 * Raising an exception from the handler will prevent further processing
	 * and the exception will be propagated to the caller.
	 * <p />
	 * Aspect collections make sure that batch replace operations {@link java.util.Map#putAll(java.util.Map)}.
	 * are done atomically. This means, that all before replace handlers will
	 * be called before actual mofications take place.
	 * 
	 * @param e The event
	 */
	void beforeElementReplaced(CollectionItemReplacedEvent<E, C> e);

	/**
	 * 
	 * Called to signal the intention to remove all elements from a collection.
	 * <p />
	 * Raising an exception from the handler will prevent further processing
	 * and the exception will be propagated to the caller.
	 * 
	 * @param e The event
	 */
	void beforeCollectionCleared(CollectionEvent<E, C> e);

	/**
	 * 
	 * Called, when a new element has been added to the wrapped collection.
	 * <p />
	 * This method is called if any only if an item was added to the collection.
	 * <p />
	 * Exceptions raised in the listener methods are ignored.
	 * 
	 * @param e The event.
	 **/
	void afterElementAdded(CollectionItemEvent<E, C> e);

	/**
	 * 
	 * Called, when an element has been removed from the wrapped collection.
	 * <p />
	 * This method is called if any only if an item was removed from the collection.
	 * <p />
	 * Exceptions raised in the listener methods are ignored.
	 * 
	 * @param e The event.
	 **/
	void afterElementRemoved(CollectionItemEvent<E, C> e);

	/**
	 * 
	 * Called, when an existing element has been replaced by another element.
	 * <p />
	 * This method is called if any only if an item was replaced by another element
	 * inside the collection.
	 * <p />
	 * Exceptions raised in the listener methods are ignored.
	 * 
	 * @param e The event.
	 **/
	void afterElementReplaced(CollectionItemReplacedEvent<E, C> e);

	/**
	 * 
	 * Called, after a collection was cleared of all its elements.
	 * <p />
	 * Exceptions raised in the listener methods are ignored.
	 * 
	 * @param e The event.
	 */
	void afterCollectionCleared(CollectionEvent<E, C> e);
}
