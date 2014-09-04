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
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.cow.CopyOnWriteSortedSet;
import de.dhke.projects.cutil.collections.factories.TreeSetFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @param <I> The type for nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * <p/>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ABoxNodeTermSet<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends TermSet<I, L, K, R> {

	public ABoxNodeTermSet(final ABoxNode<?, I, L, K, R> sender)
	{
		this(
			CopyOnWriteSortedSet.decorate(new TreeSet<IDLTerm<I, L, K, R>>(), new TreeSetFactory<IDLTerm<I, L, K, R>>()),
			sender);
	}


	public ABoxNodeTermSet(final SortedSet<IDLTerm<I, L, K, R>> baseSet, final ABoxNode<?, I, L, K, R> sender)
	{
		super(TermTypes.ANY, baseSet, sender);
	}


		public ABoxNodeTermSet<I, L, K, R> clone(final ABoxNode<?, I, L, K, R> newNode)
	{
		final CopyOnWriteSortedSet<IDLTerm<I, L, K, R>> klonedSet = ((CopyOnWriteSortedSet<IDLTerm<I, L, K, R>>) getDecoratee()).clone();
		return new ABoxNodeTermSet<>(klonedSet, newNode);
	}


	@Override
	public void notifyAfterElementAdded(final CollectionItemEvent<IDLTerm<I, L, K, R>, Collection<IDLTerm<I, L, K, R>>> e)
	{
		final ABoxNode<?, I, L, K, R> source = getNode();
		final ABox<I, L, K, R> abox = source.getABox();

		if (abox != null) {
			if (e.getItem() instanceof IDLIndividualReference) {
				final IDLIndividualReference<I, L, K, R> iRef = (IDLIndividualReference<I, L, K, R>) e.getItem();
				assert source instanceof IndividualABoxNode;
				@SuppressWarnings("unchecked")
				final IndividualABoxNode<I, L, K, R> iNode = (IndividualABoxNode<I, L, K, R>) source;

				iNode._names.add(iRef.getIndividual());
			} else if (e.getItem() instanceof IDLLiteralReference) {
				final IDLLiteralReference<I, L, K, R> lRef = (IDLLiteralReference<I, L, K, R>) e.getItem();
				assert source instanceof LiteralABoxNode;
				@SuppressWarnings("unchecked")
				final LiteralABoxNode<I, L, K, R> iNode = (LiteralABoxNode<I, L, K, R>) source;

				iNode._names.add(lRef.getLiteral());
			}
			abox.notifyTermAdded(source, e.getItem());
		}
		super.notifyAfterElementAdded(e);
	}


	@Override
	protected void notifyBeforeElementAdded(
		CollectionItemEvent<IDLTerm<I, L, K, R>, Collection<IDLTerm<I, L, K, R>>> ev)
	{
		super.notifyBeforeElementAdded(ev);
		final IABoxNode<I, L, K, R> node = getNode();
		final IABox<I, L, K, R> abox = node.getABox();
		if (abox != null) {
			if (ev.getItem() instanceof IDLIndividualReference) {
				final IDLIndividualReference<I, L, K, R> indRef = (IDLIndividualReference<I, L, K, R>) ev.getItem();
				if ((abox.getIndividualNode(indRef.getIndividual()) != null)
					&& (!abox.getIndividualNode(indRef.getIndividual()).equals(node))) {
					throw new IllegalArgumentException(
						"Different node with name " + indRef.getIndividual() + " already in node map.");
				} else if (ev.getItem() instanceof IDLLiteralReference) {
					final IDLLiteralReference<I, L, K, R> litRef = (IDLLiteralReference<I, L, K, R>) ev.getItem();
					if ((abox.getDatatypeNode(litRef.getLiteral()) != null)
						&& (!abox.getDatatypeNode(litRef.getLiteral()).equals(node))) {
						throw new IllegalArgumentException(
							"Different node with value " + litRef.getLiteral() + " already in node map.");
					}
				}
			}
		}
	}


	@Override
	protected void notifyAfterElementRemoved(
		CollectionItemEvent<IDLTerm<I, L, K, R>, Collection<IDLTerm<I, L, K, R>>> ev)
	{
		final ABoxNode<?, I, L, K, R> node = getNode();
		final ABox<I, L, K, R> abox = node.getABox();

		if (abox != null) {
			if (ev.getItem() instanceof IDLIndividualReference) {
				final IDLIndividualReference<I, L, K, R> nRef = (IDLIndividualReference<I, L, K, R>) ev.getItem();

				node._names.remove(nRef.getIndividual());
			}
			abox.notifyTermRemoved(node, ev.getItem());
		}
		super.notifyAfterElementRemoved(ev);
	}


	@Override
	protected void notifyAfterCollectionCleared(
		CollectionEvent<IDLTerm<I, L, K, R>, Collection<IDLTerm<I, L, K, R>>> ev)
	{
		final ABoxNode<?, I, L, K, R> node = getNode();
		final ABox<I, L, K, R> abox = node.getABox();

		boolean isAnonNode = node.isAnonymous();

		node._names.clear();
		if (abox != null) {
			if (!isAnonNode) {
			}
			abox.notifyTermSetCleared(node);
		}
		super.notifyAfterCollectionCleared(ev);
	}


	@SuppressWarnings("unchecked")
	 ABoxNode<?, I, L, K, R> getNode(
		)
	{
		return (ABoxNode<?, I, L, K, R>) getSender();
	}
}
