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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLClassAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.SWRLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;

/**
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLClassAtom<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements ISWRLClassAtom<I, L, K, R> {

	private final K _klass;
	private final ISWRLIArgument<I, L, K, R> _individual;


	protected SWRLClassAtom(final K klass, final ISWRLIArgument<I, L, K, R> individual)
	{
		_klass = klass;
		_individual = individual;
	}


	@Override
	public ISWRLClassAtom<I, L, K, R> clone()
	{
		return this;
	}


	@Override
	public K getKlass()
	{
		return _klass;
	}


	@Override
	public ISWRLIArgument<I, L, K, R> getIndividual()
	{
		return _individual;
	}


	@Override
	public int hashCode()
	{
		return 7 * _klass.hashCode() + _individual.hashCode();
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj instanceof ISWRLClassAtom) {
			@SuppressWarnings("unchecked")
			final ISWRLClassAtom<I, L, K, R> atom = (ISWRLClassAtom<I, L, K, R>) obj;
			return (_klass.equals(atom.getKlass()) && _individual.equals(atom.getIndividual()));
		} else {
			return false;
		}

	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(_klass);
		sb.append(" ");
		sb.append(getIndividual());
		sb.append(")");
		return sb.toString();
	}


	@Override
	public String toString(IToStringFormatter formatter)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(formatter.toString(_klass));
		sb.append(" ");
		sb.append(_individual.toString(formatter));
		sb.append(")");
		return sb.toString();
	}


	@Override
	public SWRLTermOrder getSWRLTermOrder()
	{
		return SWRLTermOrder.SWRL_AFTER_CLASS_ATOM;
	}


	@Override
	public int compareTo(final ISWRLTerm<I, L, K, R> o)
	{
		int compare = getSWRLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof ISWRLClassAtom;
			final ISWRLClassAtom<I, L, K, R> other = (ISWRLClassAtom<I, L, K, R>) o;
			/**
			 * lexicographically compare klass first and then individual.
			 *
			 */
			compare = _klass.compareTo(other.getKlass());
			if (compare == 0) {
				compare = _individual.compareTo(other.getIndividual());
			}
		}
		return compare;
	}
}
