/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.stringer;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public abstract class AbstractToStringConverter
	implements IToStringConverter {

	@Override
	public Object lazy(Object obj, final IToStringConverter backStringer)
	{
		return new LazyStringer(obj, this, backStringer);
	}


	@Override
	public Object lazy(Object obj)
	{
		return lazy(obj, this);
	}


	@Override
	public String toString(Object obj)
	{
		return toString(obj, this);
	}


	@Override
	public void append(StringBuilder sb, Object obj)
	{
		append(sb, obj, this);
	}


	@Override
	public String toString(Object obj, final IToStringConverter backStringer)
	{
		final StringBuilder sb = new StringBuilder();
		append(sb, obj, backStringer);
		return sb.toString();
	}
}
