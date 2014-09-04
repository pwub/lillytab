/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dhke.projects.cutil.lang;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class LangUtil {
	private LangUtil()
	{		
	}
	
	public static boolean isInstanceOneOf(Object obj, Class<?>... classes)
	{
		for (Class<?> klass: classes) {
			if (klass.isInstance(obj))
				return true;
		}
		return false;
	}
}
