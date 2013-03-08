/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIndividual;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLVariable;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLVariable<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements ISWRLVariable<Name, Klass, Role> {

	private final String _name;


	protected SWRLVariable(final String name)
	{
		_name = name;
	}


	public String getVariableName()
	{
		return _name;
	}


	public String getIndividual()
	{
		return getVariableName();
	}


	@Override
	public ISWRLVariable<Name, Klass, Role> clone()
	{
		return this;
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj instanceof ISWRLVariable) {
			@SuppressWarnings("unchecked")
			final ISWRLVariable<Name, Klass, Role> variable = (ISWRLVariable<Name, Klass, Role>) obj;
			return _name.equals(variable.getVariableName());
		} else {
			return false;
		}
	}


	@Override
	public int hashCode()
	{
		return _name.hashCode();
	}


	public int compareTo(final ISWRLIndividual<Name, Klass, Role> o)
	{
		if (o instanceof ISWRLVariable) {
			final ISWRLVariable<Name, Klass, Role> other = (ISWRLVariable<Name, Klass, Role>) o;
			return _name.compareTo(other.getVariableName());
		} else if (o instanceof ISWRLNominalReference) {
			/* Order is: Nominals -> Variables */
			return -1;
		} else {
			throw new IllegalArgumentException("Unknown SWRL individual type: " + o.getClass());
		}
	}


	@Override
	public String toString()
	{
		return "?" + _name;
	}


	public String toString(IToStringFormatter formatter)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("?");
		formatter.append(sb, _name);
		return sb.toString();
	}
}
