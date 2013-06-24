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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import java.util.SortedSet;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ImmutableIndividualABoxNode<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends ImmutableABoxNode<I, I, L, K, R>
	implements IIndividualABoxNode<I, L, K, R> {

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ImmutableIndividualABoxNode<I, L, K, R> decorate(
		final IIndividualABoxNode<I, L, K, R> baseNode, final IABox<I, L, K, R> abox)
	{
		return new ImmutableIndividualABoxNode<>(baseNode, abox);
	}
	 ImmutableIndividualABoxNode(final IIndividualABoxNode<I, L, K, R> baseNode, final IABox<I, L, K, R> abox)
	{
		super(baseNode, abox);
	}
	


	@Override
	public SortedSet<I> getNames()
	{
		return ((IIndividualABoxNode<I, L, K, R>) getBaseNode()).getNames();
	}


	@Override
	public I getPrimaryName()
	{
		return ((IIndividualABoxNode<I, L, K, R>) getBaseNode()).getPrimaryName();

	}

	@Override
	public NodeMergeInfo<I, L, K, R> addClassTerm(
														   IDLClassExpression<I, L, K, R> desc) throws ENodeMergeException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABoxNode.");
	}

	@Override
	public NodeMergeInfo<I, L, K, R> addClassTerm(
														   Iterable<? extends IDLClassExpression<I, L, K, R>> descs) throws ENodeMergeException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABoxNode.");
	}



	
}
