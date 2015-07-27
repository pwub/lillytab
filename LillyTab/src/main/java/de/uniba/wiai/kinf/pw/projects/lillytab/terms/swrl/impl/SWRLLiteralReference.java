/**
 * (c) 2009-2014 Otto-Friedrich-University Bamberg
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
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLVariable;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLLiteralReference<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements ISWRLLiteralReference<I, L, K, R>
{
	private final L _literal;

	protected SWRLLiteralReference(L literal)
	{
		_literal = literal;
	}

	@Override
	public ISWRLLiteralReference<I, L, K, R> clone()
	{
		return this;
	}

	@Override
	public L getObject()
	{
		return _literal;
	}

	@Override
	public int compareTo(final ISWRLArgument<I, L, K, R> o)
	{
		if (o instanceof ISWRLLiteralReference) {
			final ISWRLLiteralReference<I, L, K, R> other = (ISWRLLiteralReference<I, L, K, R>) o;
			return _literal.compareTo(other.getObject());
		} else if (o instanceof ISWRLVariable) {
			/* Order is: Literals -> Individuals -> Variables */
			return 1;
		} else if (o instanceof ISWRLArgument) {
			/* Order is: Literals -> Individuals -> Variables */
			return 1;
		} else {
			throw new IllegalArgumentException("Unknown SWRL individual type: " + o.getClass());
		}
	}

	@Override
	public String toString()
	{
		return _literal.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj instanceof ISWRLLiteralReference) {
			final ISWRLLiteralReference<?, ?, ?, ?> other = (ISWRLLiteralReference<?, ?, ?, ?>) obj;
			return _literal.equals(other.getObject());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return _literal.hashCode();
	}
}
