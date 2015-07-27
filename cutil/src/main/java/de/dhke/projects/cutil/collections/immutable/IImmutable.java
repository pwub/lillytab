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
package de.dhke.projects.cutil.collections.immutable;

/**
 * 
 * Interface to an object that supports to be converted into a 
 * an immutable proxy.
 * <p />
 * Call {@link #getImmutable() } for an object implementing
 * {@link IImmutable} should create a proxy object that prevents
 * direct modifications to the initial object.
 * <p />
 * Implementations may choose between three types of dynamic behaviour models:
 * <dl>
 *   <dd>Unmodifiable Proxy with unmodifiable source</dd>
 *   <dt>
 *     In this case, modifications through the immutable object are prevented.
 *     Modifications to the underlying object are unsupported. The behaviour
 *	   of the immutable is undefined, when the underlying object is changed.
 *     This model is usually the cheapest to implement, but 
 *     it leaves it to the responsibility of the caller
 *     not to modify the original object.
 *   </dt>
 *   <dd>Unmodifiable Proxy with mutable source</dd>
 *   <dt>
 *     In this case, modifications through the immutable object are prevented,
 *     but modifications to the underlying object are still possible and
 *     are visible through the proxy object.
 *   </dt>
 *   <dd>Unmodifiable Clone</dd>
 *   <dt>
 *     In this case, modifications through the immutable object are prevented
 *     and modifications to the initial object do not (directly) influence
 *	   the state of the immutable object. This is usually implemented by
 *     creating an internal copy of the initial object.
 *   </dt>
 * <p />
 * The interface contract specifies that direct modifications to the initial object
 * are prevented by the immutable proxy. How this also applies to related objects
 * is up the actual implementation.
 * <p />
 * Note that an immutable object that implements the {@link Cloneable} interface
 * should return mutable clones unless otherwise noted.
 * 
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public interface IImmutable<T extends IImmutable<? super T>>
{
	/**
	 * Convert the current state of the current object into
	 * an immutable proxy object.
	 *
	 * @return An immutable proxy object for {@literal this}.
	 **/
	T getImmutable();
}
