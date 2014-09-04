/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.stringer;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public interface IToStringConverter {

	void append(final StringBuilder sb,
				final Object obj, final IToStringConverter backStringer);


	void append(final StringBuilder sb, final Object obj);


	Object lazy(final Object obj, final IToStringConverter backStringer);


	Object lazy(final Object obj);


	boolean canHandle(final Class<?> objectType);


	String toString(final Object obj, final IToStringConverter backStringer);


	String toString(final Object obj);
}
