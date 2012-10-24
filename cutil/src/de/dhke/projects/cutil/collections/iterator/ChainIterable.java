/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ChainIterable<E>
	implements Iterable<E> {

	private final Queue<Iterable<? extends E>> _iterableList = new LinkedList<Iterable<? extends E>>();


	protected ChainIterable(final Iterable<? extends E>... iters)
	{
		_iterableList.addAll(Arrays.asList(iters));
	}


	protected ChainIterable(final Collection<? extends Iterable<? extends E>> iters)
	{
		_iterableList.addAll(iters);
	}


	public static <E> ChainIterable<E> decorate(final Iterable<E>... iters)
	{
		return new ChainIterable<E>(iters);
	}


	public static <E> ChainIterable<E> decorate(final Collection<? extends Iterable<? extends E>> iters)
	{
		return new ChainIterable<E>(iters);
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
