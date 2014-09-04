/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.stringer;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class LazyStringer {

	private final Object _obj;
	private final IToStringConverter _stringer;
	private final IToStringConverter _backStringer;


	public LazyStringer(final Object obj, final IToStringConverter stringer, final IToStringConverter backStringer)
	{
		_obj = obj;
		_stringer = stringer;
		_backStringer = backStringer;
	}


	@Override
	public String toString()
	{
		return _stringer.toString(_obj, _backStringer);
	}
}
