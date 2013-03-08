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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyDescription;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLIntersection<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractOperatorTerm<IDLRestriction<Name, Klass, Role>>
	implements IDLIntersection<Name, Klass, Role> {

	public final static String OPERATOR_NAME = "AND";


	public DLIntersection(final Collection<? extends IDLRestriction<Name, Klass, Role>> ds)
	{
		super(DLTermOrder.DL_INTERSECTION, OPERATOR_NAME, ds.size());
		if (ds.size() < 2) {
			throw new IllegalArgumentException("DLIntersection needs at least two subterms");
		} else {
			int i = 0;
			for (IDLRestriction<Name, Klass, Role> d : ds) {
				getModifiableTermList().set(i, d);
				++i;
			}
			Collections.sort(getModifiableTermList());
		}
	}


	public DLIntersection(final IDLRestriction<Name, Klass, Role> d0, final IDLRestriction<Name, Klass, Role> d1)
	{
		super(DLTermOrder.DL_INTERSECTION, OPERATOR_NAME, 2);
		getModifiableTermList().set(0, d0);
		getModifiableTermList().set(1, d1);
		/* ensure order */
		Collections.sort(getModifiableTermList());
	}


	@Override
	public int hashCode()
	{
		/* no need to override hashcode */
		return super.hashCode();
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		} else /* intersection is a commutative operation, this equality needs to be as well */ {
			return ((obj instanceof IDLIntersection) && containsAll((IDLIntersection) obj));
		}
	}


	@Override
	@SuppressWarnings("unchecked")
	public DLIntersection<Name, Klass, Role> clone()
	{
		return this;
		// return new DLIntersection<Name, Klass, Role>((IDLClassExpression<Name, Klass, Role>) getFirstTerm().clone(), (IDLClassExpression<Name, Klass, Role>) getSecondTerm().clone());
	}


	@SuppressWarnings("unchecked")
	public int compareTo(IDLTerm o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLIntersection;
			compare = TermUtil.compareTermList(this, (IDLIntersection<Name, Klass, Role>) o);
		}
		return compare;
	}


	@Override
	public IDLRestriction<Name, Klass, Role> getBefore()
	{
		return new DLDummyDescription<>(DLTermOrder.DL_BEFORE_INTERSECTION);
	}


	@Override
	public IDLRestriction<Name, Klass, Role> getAfter()
	{
		return new DLDummyDescription<>(DLTermOrder.DL_AFTER_INTERSECTION);
	}
}
