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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.cow.CopyOnWriteSortedSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ABoxNodeTermSet<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends TermSet<Name, Klass, Role> {

	public ABoxNodeTermSet(final ABoxNode<Name, Klass, Role> sender)
	{
		this(CopyOnWriteSortedSet.decorate(new TreeSet<IDLTerm<Name, Klass, Role>>()), sender);
	}


	public ABoxNodeTermSet(final SortedSet<IDLTerm<Name, Klass, Role>> baseSet, final ABoxNode<Name, Klass, Role> sender)
	{
		super(baseSet, sender);
	}


	public ABoxNodeTermSet<Name, Klass, Role> clone(final ABoxNode<Name, Klass, Role> newNode)
	{
		final CopyOnWriteSortedSet<IDLTerm<Name, Klass, Role>> klonedSet =
			((CopyOnWriteSortedSet<IDLTerm<Name, Klass, Role>>) getDecoratee()).clone();
		return new ABoxNodeTermSet<>(klonedSet, newNode);
	}


	@Override
	protected void notifyBeforeElementAdded(
		CollectionItemEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> ev)
	{
		super.notifyBeforeElementAdded(ev);
		final ABoxNode<Name, Klass, Role> node = getNode();
		final ABox<Name, Klass, Role> abox = node.getABox();
		if ((abox != null) && (ev.getItem() instanceof IDLNominalReference)) {
			IDLNominalReference<Name, Klass, Role> nRef = (IDLNominalReference<Name, Klass, Role>) ev.getItem();
			if ((abox.getNode(nRef.getIndividual()) != null)
				&& (!abox.getNode(nRef.getIndividual()).equals(node))) {
				throw new IllegalArgumentException(
					"Different node with name " + nRef.getIndividual() + " already in node map.");
			}
		}
	}


	@Override
	public void notifyAfterElementAdded(
		final CollectionItemEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
	{
		final ABoxNode<Name, Klass, Role> source = getNode();
		final ABox<Name, Klass, Role> abox = source.getABox();

		if (abox != null) {
			if (e.getItem() instanceof IDLNominalReference) {
				final IDLNominalReference<Name, Klass, Role> nRef = (IDLNominalReference<Name, Klass, Role>) e.getItem();

				abox.removeNoUnlink(source);
				source._names.add(nRef.getIndividual());
				abox.addNoUnlink(source);
			}
			abox.notifyTermAdded(source, e.getItem());
		}
		super.notifyAfterElementAdded(e);
	}


	@Override
	protected void notifyAfterElementRemoved(
		CollectionItemEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> ev)
	{
		final ABoxNode<Name, Klass, Role> node = getNode();
		final ABox<Name, Klass, Role> abox = node.getABox();

		if (abox != null) {
			if (ev.getItem() instanceof IDLNominalReference) {
				final IDLNominalReference<Name, Klass, Role> nRef = (IDLNominalReference<Name, Klass, Role>) ev.getItem();

				abox.removeNoUnlink(node);
				node._names.remove(nRef.getIndividual());
				abox.addNoUnlink(node);
			}
			abox.notifyTermRemoved(node, ev.getItem());
		}
		super.notifyAfterElementRemoved(ev);
	}


	@Override
	protected void notifyAfterCollectionCleared(
		CollectionEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> ev)
	{
		final ABoxNode<Name, Klass, Role> node = getNode();
		final ABox<Name, Klass, Role> abox = node.getABox();

		boolean isAnonNode = node.isAnonymous();
		if ((abox != null) && (!isAnonNode)) {
			boolean removed = abox.removeNoUnlink(node);
			assert removed;
		}

		node._names.clear();
		if (abox != null) {
			if (!isAnonNode) {
				abox.addNoUnlink(node);
			}
			abox.notifyTermSetCleared(node);
		}
		super.notifyAfterCollectionCleared(ev);
	}
}
