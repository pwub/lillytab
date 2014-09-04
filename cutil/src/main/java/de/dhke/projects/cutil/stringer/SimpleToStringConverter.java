/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.stringer;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class SimpleToStringConverter
	extends AbstractToStringConverter {

	@Override
	public void append(StringBuilder sb, Object obj, final IToStringConverter backStringer)
	{
		sb.append(obj);
	}


	@Override
	public boolean canHandle(
		Class<?> objectType)
	{
		return true;
	}
}
