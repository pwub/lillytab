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

import java.util.Set;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @param <T> 
 * @param <S> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class GenericImmutableSet<T, S extends Set<T>>
	extends GenericImmutableCollection<T, S>
	implements Set<T>
{
	protected GenericImmutableSet(final S baseSet, Transformer<T, T> valueTransformer)
	{
		super(baseSet, valueTransformer);
	}

	public static <T, S extends Set<T>> Set<T> decorate(final S baseSet)
	{
		return new GenericImmutableSet<>(baseSet, null);
	}

	public static <T, S extends Set<T>> Set<T> decorate(final S baseSet, final Transformer<T, T> valueTransformer)
	{
		return new GenericImmutableSet<>(baseSet, valueTransformer);
	}
}
