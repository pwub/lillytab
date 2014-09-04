/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.collections;

import de.dhke.projects.cutil.IDecorator;
import java.util.Iterator;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.iterators.TransformIterator;


/**
 * Iterator that supports transformed iteration over another iterator.
 * < /p>
 * This only exists, because {@link TransformIterator} cannot be used with wildcard collections.
 * 
 * @author Peter Wullinger <java@dhke.de>
 */
public class ExtractorIterator<I, O>
	implements Iterator<O>, IDecorator<Iterator<? extends I>>
{
	private final Iterator<? extends I> _baseIter;
	private final Transformer<I, O> _transformer;

	protected ExtractorIterator(final Iterator<? extends I> baseIter, final Transformer<I, O> transformer)
	{
		_baseIter = baseIter;
		_transformer = transformer;
	}

	public static <I, O> ExtractorIterator<I, O> decorate(final Iterator<? extends I> baseIter,
														  Transformer<I, O> transformer)
	{
		return new ExtractorIterator<>(baseIter, transformer);
	}

	@Override
	public O next()
	{
		return _transformer.transform(_baseIter.next());
	}

	@Override
	public boolean hasNext()
	{
		return _baseIter.hasNext();
	}

	@Override
	public void remove()
	{
		_baseIter.remove();
	}

	@Override
	public Iterator<? extends I> getDecoratee()
	{
		return _baseIter;
	}
}
