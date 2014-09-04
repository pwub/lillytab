/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLDArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLDataRangeAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.SWRLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.visitor.ISWRLTermVisitor;

/**
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * <p/>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLDataRangeAtom<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements ISWRLDataRangeAtom<I, L, K, R> {

	private final IDLDataRange<I, L, K, R> _range;
	private final ISWRLDArgument<I, L, K, R> _individual;


	public SWRLDataRangeAtom(final IDLDataRange<I, L, K, R> range, final ISWRLDArgument<I, L, K, R> individual)
	{
		_range = range;
		_individual = individual;
	}


	@Override
	public ISWRLDArgument<I, L, K, R> getIndividual()
	{
		return _individual;
	}


	@Override
	public IDLDataRange<I, L, K, R> getDataRange()
	{
		return _range;
	}


	@Override
	public SWRLTermOrder getSWRLTermOrder()
	{
		return SWRLTermOrder.SWRL_DATA_RANGE_ATOM;
	}


	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(_range.toString());
		sb.append(" ");
		sb.append(_individual.toString());
		sb.append(")");
		return sb.toString();
	}


	@Override
	public int compareTo(ISWRLTerm<I, L, K, R> o)
	{
		int compare = getSWRLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof ISWRLDataRangeAtom;
			final ISWRLDataRangeAtom<I, L, K, R> other = (ISWRLDataRangeAtom<I, L, K, R>) o;
			compare = getDataRange().compareTo(other.getDataRange());
			if (compare == 0)
				compare = getIndividual().compareTo(other.getIndividual());
		}
		return compare;
	}


	@Override
	public void accept(final ISWRLTermVisitor<I, L, K, R> visitor)
	{
		visitor.visit(this);
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		else if (obj instanceof ISWRLDataRangeAtom) {
			final ISWRLDataRangeAtom<?, ?, ?, ?> other = (ISWRLDataRangeAtom<?, ?, ?, ?>) obj;
			return getDataRange().equals(other.getDataRange()) && getIndividual().equals(other.getIndividual());
		} else
			return false;
	}


	@Override
	public int hashCode()
	{
		return 2417 * getDataRange().hashCode() + 1049 * getIndividual().hashCode();
	}
}
