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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.EInvalidTermException;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.AbstractFixedTermList;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLAtomicTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLVariable;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.SWRLTermOrder;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLIntersection<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractFixedTermList<ISWRLAtomicTerm<Name, Klass, Role>>
	implements ISWRLIntersection<Name, Klass, Role> {

	public final static String OPERATOR_NAME = "AND";


	protected SWRLIntersection(final Collection<? extends ISWRLAtomicTerm<Name, Klass, Role>> as)
	{
		super(as.size());
		if (as.size() < 2) {
			throw new EInvalidTermException("Intersection needs at least two subterms");
		} else {
			int i = 0;
			for (ISWRLAtomicTerm<Name, Klass, Role> d : as) {
				getModifiableTermList().set(i, d);
				++i;
			}
			Collections.sort(getModifiableTermList());
		}
	}


	public SWRLIntersection(final ISWRLAtomicTerm<Name, Klass, Role> a0, final ISWRLAtomicTerm<Name, Klass, Role> a1)
	{
		super(2);
		getModifiableTermList().set(0, a0);
		getModifiableTermList().set(1, a1);
		/* ensure order */
		Collections.sort(getModifiableTermList());
	}


	public SWRLTermOrder getSWRLTermOrder()
	{
		return SWRLTermOrder.SWRL_INTERSECTION;
	}


	@Override
	public ISWRLIntersection<Name, Klass, Role> clone()
	{
		return this;
	}


	public int compareTo(final ISWRLTerm<Name, Klass, Role> o)
	{
		int compare = getSWRLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof ISWRLIntersection;
			compare = TermUtil.compareTermList(this, (ISWRLIntersection<Name, Klass, Role>) o);
		}
		return compare;
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(OPERATOR_NAME);

		for (ISWRLAtomicTerm<Name, Klass, Role> term : this) {
			sb.append(" ");
			sb.append(term.toString());
		}
		sb.append(")");
		return sb.toString();
	}


	public String toString(IToStringFormatter formatter)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(OPERATOR_NAME);

		for (ISWRLAtomicTerm<Name, Klass, Role> term : this) {
			sb.append(" ");
			sb.append(term.toString(formatter));
		}
		sb.append(")");
		return sb.toString();
	}
}
