/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dhke.projects.cutil.collections.immutable;

import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class ImmutableTransformer<T extends IImmutable<T>>
	implements Transformer<T ,T>
{
	public ImmutableTransformer()
	{
	}
	
	public static <T extends IImmutable<T>> ImmutableTransformer<T> create()
	{
		return new ImmutableTransformer<T>();
	}

	@Override
	public T transform(final T input)
	{
		return input.getImmutable();
	}

}
