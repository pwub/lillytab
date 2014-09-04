/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.collections.factories;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class EnumSetFactory<E extends Enum<E>>
	implements ICollectionFactory<E, Set<E>> {

	private final Class<E> _enumClass;


	public EnumSetFactory(final Class<E> enumClass)
	{
		_enumClass = enumClass;
	}


	@Override
	public Set<E> getInstance()
	{
		return EnumSet.noneOf(_enumClass);
	}


	@Override
	public Set<E> getInstance(
		Collection<? extends E> baseCollection)
	{
		final Set<E> set = EnumSet.noneOf(_enumClass);
		set.addAll(set);
		return set;
	}
}
