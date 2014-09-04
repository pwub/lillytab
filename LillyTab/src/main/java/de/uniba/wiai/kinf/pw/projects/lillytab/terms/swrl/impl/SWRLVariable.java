/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the
 * terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named
 * "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT
 * HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT ARE DISCLAIMED TO THE
 * EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO
 * COPYRIGHT HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY WAY OUT
 * OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 **/
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLVariable;


/**
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLVariable<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements ISWRLVariable<I, L, K, R>
{
	private final String _name;

	protected SWRLVariable(final String name)
	{
		_name = name;
	}

	@Override
	public String getVariableName()
	{
		return _name;
	}

	public String getObject()
	{
		return getVariableName();
	}

	@Override
	public ISWRLVariable<I, L, K, R> clone()
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
			final ISWRLVariable<I, L, K, R> variable = (ISWRLVariable<I, L, K, R>) obj;
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

	@Override
	public int compareTo(final ISWRLArgument<I, L, K, R> o)
	{
		if (o instanceof ISWRLVariable) {
			final ISWRLVariable<I, L, K, R> other = (ISWRLVariable<I, L, K, R>) o;
			return _name.compareTo(other.getVariableName());
		} else if (o instanceof ISWRLIndividualReference) {
			/* Order is: Literals -> Individuals -> Variables */
			return -1;
		} else if (o instanceof ISWRLLiteralReference) {
			return -1;
			/* Order is: Literals -> Individuals -> Variables */
		} else {
			throw new IllegalArgumentException("Unknown SWRL individual type: " + o.getClass());
		}
	}

	@Override
	public String toString()
	{
		return "?" + _name;
	}
}
