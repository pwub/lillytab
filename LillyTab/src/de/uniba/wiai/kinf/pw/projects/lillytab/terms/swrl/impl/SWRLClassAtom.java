/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IToStringFormatter;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLClassAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIndividual;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.SWRLTermOrder;

/**
 *
 * @param <Name>
 * @param <Klass>
 * @param <Role> 
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLClassAtom<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements ISWRLClassAtom<Name, Klass, Role>
{
	private final Klass _klass;
	private final ISWRLIndividual<Name, Klass, Role> _individual;

	protected SWRLClassAtom(final Klass klass, final ISWRLIndividual<Name, Klass, Role> individual)
	{
		_klass = klass;
		_individual = individual;
	}

	@Override
	public ISWRLClassAtom<Name, Klass, Role> clone()
	{
		return this;
	}

	public Klass getKlass()
	{
		return _klass;
	}

	public ISWRLIndividual<Name, Klass, Role> getIndividual()
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
		if (this == obj)
			return true;
		if (obj instanceof ISWRLClassAtom) {
			@SuppressWarnings("unchecked")
			final ISWRLClassAtom<Name, Klass, Role> atom = (ISWRLClassAtom<Name, Klass, Role>)obj;
			return (_klass.equals(atom.getKlass()) && _individual.equals(atom.getIndividual()));
		} else
			return false;

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
	
	

	public SWRLTermOrder getSWRLTermOrder()
	{
		return SWRLTermOrder.SWRL_AFTER_CLASS_ATOM;
	}

	public int compareTo(final ISWRLTerm<Name, Klass, Role> o)
	{
		int compare = getSWRLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof ISWRLClassAtom;
			final ISWRLClassAtom<Name, Klass, Role> other = (ISWRLClassAtom<Name, Klass, Role>)o;
			/**
			 * lexicographically compare klass first and then individual.
			 **/
			compare = _klass.compareTo(other.getKlass());
			if (compare == 0)
				compare = _individual.compareTo(other.getIndividual());
		}
		return compare;
	}

}
