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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITermList;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.collections15.SetUtils;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class DLDataUnion<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractOperatorTerm<IDLDataRange<I, L, K, R>>
	implements IDLDataUnion<I, L, K, R>
{
	public static final String OPERATOR_NAME = "OR";

	public DLDataUnion(final Collection<? extends IDLDataRange<I, L, K, R>> ds)
	{
		super(DLTermOrder.DL_DATA_UNION, OPERATOR_NAME, ds.size());
		if (ds.size() < 2) {
			throw new IllegalArgumentException("DLDataUnion needs at least two subterms");
		} else {
			int i = 0;
			for (IDLDataRange<I, L, K, R> d : ds) {
				getModifiableTermList().set(i, d);
				++i;
			}
			Collections.sort(getModifiableTermList());
		}
	}

	public DLDataUnion(final IDLDataRange<I, L, K, R> d0, final IDLDataRange<I, L, K, R> d1)
	{
		super(DLTermOrder.DL_DATA_UNION, OPERATOR_NAME, 2);
		getModifiableTermList().set(0, d0);
		getModifiableTermList().set(1, d1);
		/* ensure order */
		Collections.sort(getModifiableTermList());
	}

	@Override
	public ITermList<IDLDataRange<I, L, K, R>> getTerms()
	{
		return this;
	}

	@Override
	public int hashCode()
	{
		/* no need to override hashcode */
		return super.hashCode() + SetUtils.hashCodeForSet(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(final Object obj)
	{
		if (this == obj) {
			return true;
		} if (obj instanceof IDLDataUnion) {
			final IDLDataUnion<I, L, K, R> other = (IDLDataUnion<I, L, K, R>) obj;
			return (obj instanceof IDLDataUnion) && (size() == other.size()) && containsAll(other);
		
		} else
			return false;
	}

	@Override
	public DLDataUnion<I, L, K, R> clone()
	{
		return this;
	}

	@Override
	public int compareTo(final IDLTerm<I, L, K, R> o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLDataUnion;
			compare = TermUtil.compareTermList(this, (IDLDataUnion<I, L, K, R>) o);
		}
		return compare;
	}

	@Override
	public IDLTerm<I, L, K, R> getBefore()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_BEFORE_DATA_UNION);
	}

	@Override
	public IDLTerm<I, L, K, R> getAfter()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_AFTER_DATA_UNION);
	}
}
