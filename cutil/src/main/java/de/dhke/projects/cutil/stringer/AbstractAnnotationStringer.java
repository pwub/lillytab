/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.stringer;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public abstract class AbstractAnnotationStringer
	extends AbstractToStringConverter {

	@Override
	public boolean canHandle(Class<?> objectType)
	{
		final Class<?>[] supportedClasses = getClass().getAnnotation(SupportsType.class).value();
		for (Class<?> supportedClass : supportedClasses) {
			if (supportedClass.isAssignableFrom(objectType))
				return true;
		}
		return false;
	}
}
