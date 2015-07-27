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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.visitor.IDLTermVisitor;


/**
 *
 * A dummy {@link IDLClassExpression} of the specified type. Should be used for sorting, only.
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public final class DLDummyTerm<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IDLTerm<I, L, K, R>
{
	private final DLTermOrder _termType;

	public DLDummyTerm(DLTermOrder termType)
	{
		_termType = termType;
	}

	@Override
	public DLTermOrder getDLTermOrder()
	{
		return _termType;
	}

	@Override
	public DLDummyTerm<I, L, K, R> clone()
	{
		return this;
	}

	@Override
	public int compareTo(IDLTerm<I, L, K, R> o)
	{
		return getDLTermOrder().compareTo(o);
	}

	@Override
	public IDLTerm<I, L, K, R> getBefore()
	{
		return this;
	}

	@Override
	public IDLTerm<I, L, K, R> getAfter()
	{
		return this;
	}

	@Override
	public void accept(IDLTermVisitor<I, L, K, R> visitor)
	{
		visitor.visit(this);
	}
}
