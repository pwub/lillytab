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
package de.dhke.projects.cutil.collections.iterator;

import de.dhke.projects.cutil.collections.ExtractorCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @param <E> 
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ChainIterable<E>
	implements Iterable<E> {

	private final Queue<Iterable<? extends E>> _iterableList = new LinkedList<>();


	@SafeVarargs
	protected ChainIterable(final Iterable<? extends E>... iters)
	{
		_iterableList.addAll(Arrays.asList(iters));
	}


	protected ChainIterable(final Collection<? extends Iterable<? extends E>> iters)
	{
		_iterableList.addAll(iters);
	}


	@SafeVarargs
	public static <E> ChainIterable<E> decorate(final Iterable<E>... iters)
	{
		return new ChainIterable<>(iters);
	}


	public static <E> ChainIterable<E> decorate(final Collection<? extends Iterable<? extends E>> iters)
	{
		return new ChainIterable<>(iters);
	}


	@Override
	public Iterator<E> iterator()
	{
		final Collection<Iterator<? extends E>> iterCollection = ExtractorCollection.decorate(_iterableList,
																							  new Transformer<Iterable<? extends E>, Iterator<? extends E>>() {
			@Override
			public Iterator<? extends E> transform(Iterable<? extends E> input)
			{
				return input.iterator();
			}
		});
		return ChainIterator.decorate(iterCollection);
	}
}
