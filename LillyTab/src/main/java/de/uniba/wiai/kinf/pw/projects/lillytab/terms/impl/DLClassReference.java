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
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;

/**
 *
 * @param <K> The type for DL classes
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLClassReference<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	implements IDLClassReference<I, L, K, R> {

	private final K _klass;


	public DLClassReference(final K klass)
	{
		_klass = klass;
	}


	@Override
	public K getElement()
	{
		return _klass;
	}


	@Override
	public int hashCode()
	{
		return _klass.hashCode();
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) {
			return true;
		}
		return ((obj instanceof IDLClassReference) && getElement().equals(((IDLClassReference) obj).getElement()));
	}


	@Override
	public String toString()
	{
		/* Cast: netbeans/compiler workaround */
		return _klass.toString();
	}


	@Override
	public String toString(IToStringFormatter entityFormatter)
	{
		return entityFormatter.toString(_klass);
	}


	@Override
	public DLClassReference<I, L, K, R> clone()
	{
		return this;
		// return new DLClassReference<I, L, K, R>(_klass);
	}


	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(final IDLTerm<I, L, K, R> o)
	{
		final int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLClassReference;
			return _klass.compareTo((K) (((IDLClassReference) o).getElement()));
		} else {
			return compare;
		}
	}


	@Override
	public DLTermOrder getDLTermOrder()
	{
		return DLTermOrder.DL_CLASS_REFERENCE;
	}


	@Override
	public IDLTerm<I, L, K, R> getBefore()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_BEFORE_CLASS_REFERENCE);
	}


	@Override
	public IDLTerm<I, L, K, R> getAfter()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_AFTER_CLASS_REFERENCE);
	}
}
