/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.collections.immutable;

import de.dhke.projects.cutil.IDecorator;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections15.Transformer;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class GenericImmutableIterable<T, I extends Iterable<T>>
	implements Iterable<T>, IDecorator<I>
{
	private final I _backIterable;
	private final Transformer<T, T> _valueTransformer;

	public Transformer<T, T> getValueTransformer()
	{
		return _valueTransformer;
	}

	@Override
	public I getDecoratee()
	{
		return _backIterable;
	}

	protected GenericImmutableIterable(final I backIterable, final Transformer<T, T> valueTransformer)
	{
		assert backIterable != null;
		_backIterable = backIterable;
		_valueTransformer = valueTransformer;
	}

	public static <T, C extends Iterable<T>> GenericImmutableIterable<T, C> decorate(final C backColl)
	{
		return new GenericImmutableIterable<T, C>(backColl, null);
	}

	public static <T, C extends Iterable<T>> GenericImmutableIterable<T, C> decorate(final C backColl,
																					 final Transformer<T, T> valueTransformer)
	{
		return new GenericImmutableIterable<T, C>(backColl, valueTransformer);
	}

	@Override
	public Iterator<T> iterator()
	{
		return ImmutableIterator.decorate(_backIterable.iterator(), _valueTransformer);
	}
}
