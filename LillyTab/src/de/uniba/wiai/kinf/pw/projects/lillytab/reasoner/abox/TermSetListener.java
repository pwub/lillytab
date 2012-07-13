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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.collections.aspect.AbstractCollectionListener;
import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.aspect.ICollectionListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collection;

/**
 * <p>
 * Implement a concept set listener, that maintains
 * the proper entries of the current node within the
 * associated ABox's node map.
 * </p><p>
 * Note, that we currently update the ABox by removing and
 * re-adding a node if a new nominal (= name) was added to
 * the current node's concept set.
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
class TermSetListener<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCollectionListener<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>>
	implements ICollectionListener<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> {

	protected TermSetListener()
	{
	}

	@Override
	public void beforeElementAdded(
		final CollectionItemEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
	{
		assert e.getSource() instanceof ABoxNode;
		@SuppressWarnings("unchecked")
		final ABoxNode<Name, Klass, Role> source = (ABoxNode<Name, Klass, Role>) e.getSource();
		final ABox<Name, Klass, Role> abox = source.getABox();
		if ((abox != null) && (e.getItem() instanceof IDLNominalReference)) {
			IDLNominalReference<Name, Klass, Role> nRef = (IDLNominalReference<Name, Klass, Role>) e.getItem();
			if ((abox.getNode(nRef.getIndividual()) != null)
				&& (!abox.getNode(nRef.getIndividual()).equals(source)))
				throw new IllegalArgumentException(
					"Different node with name " + nRef.getIndividual() + " already in node map.");
		}
	}

	@Override
	public void afterElementAdded(
		final CollectionItemEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
	{
		assert e.getSource() instanceof ABoxNode;
		@SuppressWarnings("unchecked")
		final ABoxNode<Name, Klass, Role> source = (ABoxNode<Name, Klass, Role>) e.getSource();
		final ABox<Name, Klass, Role> abox = source.getABox();

		if (abox != null) {
			if (e.getItem() instanceof IDLNominalReference) {
				IDLNominalReference<Name, Klass, Role> nRef = (IDLNominalReference<Name, Klass, Role>) e.getItem();

				/* XXX - removing/re-adding is only the most simple way to update the abox */
				boolean wasRemoved = abox.remove(source);
				assert wasRemoved;
				// XXX - make this less ugly
				source._names.add(nRef.getIndividual());
				abox.add(source);
			}
			abox.notifyTermAdded(source, e.getItem());
		}
	}

	@Override
	public void afterElementRemoved(
		final CollectionItemEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
	{
		assert e.getSource() instanceof ABoxNode;
		@SuppressWarnings("unchecked")
		final ABoxNode<Name, Klass, Role> source = (ABoxNode<Name, Klass, Role>) e.getSource();
		final ABox<Name, Klass, Role> abox = source.getABox();

		if (abox != null) {
			if (e.getItem() instanceof IDLNominalReference) {
				IDLNominalReference<Name, Klass, Role> nRef = (IDLNominalReference<Name, Klass, Role>) e.getItem();

				/* XXX - this is the most simple way to update the abox */
				boolean wasRemoved = abox.remove(source);
				assert wasRemoved;
				source._names.remove(nRef.getIndividual());
				abox.add(source);
			}	
			abox.notifyTermRemoved(source, e.getItem());
		}
	}

	@Override
	public void afterCollectionCleared(
		final CollectionEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
	{
		assert e.getSource() instanceof ABoxNode;
		@SuppressWarnings("unchecked")
		final ABoxNode<Name, Klass, Role> source = (ABoxNode<Name, Klass, Role>) e.getSource();
		final ABox<Name, Klass, Role> abox = source.getABox();

		// XXX - make this less ugly
		source._names.clear();
		if (abox != null) {
			abox.remove(source);
			abox.add(source);
			abox.notifyTermSetCleared(source);
		}
	}
}
