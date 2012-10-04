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
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.aspect.ICollectionListener;
import de.dhke.projects.cutil.collections.factories.HashMapFactory;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.factories.IMapFactory;
import de.dhke.projects.cutil.collections.factories.IMultiMapFactory;
import de.dhke.projects.cutil.collections.factories.MultiTreeSetHashMapFactory;
import de.dhke.projects.cutil.collections.factories.TreeSetFactory;
import de.dhke.projects.cutil.collections.factories.TreeSortedSetFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.LinearSequenceNumberGenerator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import org.apache.commons.collections15.MultiMap;

/**
 * Wrapper class for various shared objects.
 *
 * This is not copied, but referenced during cloning,
 * conserving memory.
 **/
public final class ABoxCommon<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> {

	public ABoxCommon(IDLTermFactory<Name, Klass, Role> termFactory)
	{
		this._termFactory = termFactory;
	}

	/**
	 * @return the aboxIDFactory
	 */ public LinearSequenceNumberGenerator getAboxIDFactory()
	{
		return _aboxIDFactory;
	}

	/**
	 * @return the termFactory
	 */ public IDLTermFactory<Name, Klass, Role> getTermFactory()
	{
		return _termFactory;
	}

	/**
	 * @return the termSetFactory
	 */ public ICollectionFactory<IDLTerm<Name, Klass, Role>, SortedSet<IDLTerm<Name, Klass, Role>>> getTermSetFactory()
	{
		return _termSetFactory;
	}

	/**
	 * @return the nodeIDSetFactory
	 */ public ICollectionFactory<NodeID, Set<NodeID>> getNodeIDSetFactory()
	{
		return _nodeIDSetFactory;
	}

	/**
	 * @return the nodeSetFactory
	 */ public ICollectionFactory<IABoxNode<Name, Klass, Role>, SortedSet<IABoxNode<Name, Klass, Role>>> getNodeSetFactory()
	{
		return _nodeSetFactory;
	}

	/**
	 * @return the nodeMapFactory
	 */ public IMapFactory<Object, IABoxNode<Name, Klass, Role>, Map<Object, IABoxNode<Name, Klass, Role>>> getNodeMapFactory()
	{
		return _nodeMapFactory;
	}

	/**
	 * @return the linkMapListener
	 */ public ICollectionListener<Map.Entry<Role, NodeID>, MultiMap<Role, NodeID>> getLinkMapListener()
	{
		return _linkMapListener;
	}

	public TermEntryFactory<Name, Klass, Role> getTermEntryFactory()
	{
		return _termEntryFactory;
	}
	
	public NodeTermSetListener<Name, Klass, Role> getNodeTermSetListener()
	{
		return _nodeTermSetListener;
	}
	 
//	/**
//	 * @return the unfoldListener
//	 */ public IUnfoldListener<Name, Klass, Role> getUnfoldListener()
//	{
//		return unfoldListener;
//	}

//	/// <editor-fold defaultstate="collapsed" desc="class UnfoldListener">
//	/**
//	 *
//	 *
//	 **/
//	final class UnfoldListener
//		implements IUnfoldListener<Name, Klass, Role> {
//
//		public void beforeConceptUnfold(IABoxNode<Name, Klass, Role> node, IDLClassExpression<Name, Klass, Role> initial, Collection<IDLClassExpression<Name, Klass, Role>> unfolds)
//		{
//			final IABox<Name, Klass, Role> abox = node.getABox();
//			final TermEntry<Name, Klass, Role> iTermEntry = TermEntry.wrap(node, initial);
//			for (IDLClassExpression<Name, Klass, Role> unfoldee : unfolds) {
//				final TermEntry<Name, Klass, Role> uTermEntry = TermEntry.wrap(node, unfoldee);
//				/**
//				 * update parent dependency information,
//				 * but only if no dependency information was already present
//				 **/
//				if (!abox.getDependencyMap().containsKey(uTermEntry))
//					abox.getDependencyMap().put(uTermEntry, iTermEntry);
//			}
//		}
//	}
//	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="class LinkMapListener">
	/**
	 * <p>
	 * Helper class for {@link ABoxNode}. Updates the entwined predecessor and successor maps if one of them
	 * is modified.
	 * </p><p>
	 * This is made available via {@link ABoxCommon.getLinkMapListener()} because only a single instance is needed
	 * per ABox type.
	 * </p>
	 **/
	final class LinkMapListener<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
		extends AbstractCollectionListener<Map.Entry<Role, NodeID>, MultiMap<Role, NodeID>> {

		@Override
		public void afterElementAdded(final CollectionItemEvent<Entry<Role, NodeID>, MultiMap<Role, NodeID>> e)
		{
			assert e.getSource() instanceof ABoxNode;
			@SuppressWarnings("unchecked")
			final IABoxNode<Name, Klass, Role> source = (IABoxNode<Name, Klass, Role>) e.getSource();
			final IABox<Name, Klass, Role> abox = source.getABox();
			assert abox != null;
			final Map.Entry<Role, NodeID> entry = e.getItem();
			final IABoxNode<Name, Klass, Role> target = abox.getNode(entry.getValue());
			assert target != null;
			if (e.getCollection() == source.getLinkMap().getAssertedSuccessors()) {
				if (!target.getLinkMap().getAssertedPredecessors().containsValue(entry.getKey(), source.getNodeID()))
					target.getLinkMap().getAssertedPredecessors().put(entry.getKey(), source.getNodeID());
			} else if (e.getCollection() == source.getLinkMap().getAssertedPredecessors()) {
				if (!target.getLinkMap().getAssertedSuccessors().containsValue(entry.getKey(), source.getNodeID()))
					target.getLinkMap().getAssertedSuccessors().put(entry.getKey(), source.getNodeID());
			}
		}

		@Override
		public void afterElementRemoved(final CollectionItemEvent<Entry<Role, NodeID>, MultiMap<Role, NodeID>> e)
		{
			assert e.getSource() instanceof ABoxNode;
			@SuppressWarnings("unchecked")
			final IABoxNode<Name, Klass, Role> source = (IABoxNode<Name, Klass, Role>) e.getSource();
			final IABox<Name, Klass, Role> abox = source.getABox();
			assert abox != null;
			final Map.Entry<Role, NodeID> entry = e.getItem();
			final IABoxNode<Name, Klass, Role> target = abox.getNode(entry.getValue());

			if (target != null) {
				if (e.getCollection() == source.getLinkMap().getAssertedSuccessors()) {
					target.getLinkMap().getAssertedPredecessors().remove(entry.getKey(), source.getNodeID());
				} else if (e.getCollection() == source.getLinkMap().getAssertedPredecessors()) {
					if (!target.getLinkMap().getAssertedSuccessors().containsValue(entry.getKey(), source.getNodeID()))
						target.getLinkMap().getAssertedSuccessors().remove(entry.getKey(), source.getNodeID());
				}
			}
		}
	}
	/// </editor-fold>

	private final LinearSequenceNumberGenerator _aboxIDFactory = new LinearSequenceNumberGenerator();
	private final IDLTermFactory<Name, Klass, Role> _termFactory;
	private final ICollectionFactory<IDLTerm<Name, Klass, Role>, SortedSet<IDLTerm<Name, Klass, Role>>> _termSetFactory = new TreeSortedSetFactory<IDLTerm<Name, Klass, Role>>();
	private final ICollectionFactory<NodeID, Set<NodeID>> _nodeIDSetFactory = new TreeSetFactory<NodeID>();
	private final ICollectionFactory<IABoxNode<Name, Klass, Role>, SortedSet<IABoxNode<Name, Klass, Role>>> _nodeSetFactory = new TreeSortedSetFactory<IABoxNode<Name, Klass, Role>>();
	private final IMapFactory<Object, IABoxNode<Name, Klass, Role>, Map<Object, IABoxNode<Name, Klass, Role>>> _nodeMapFactory = new HashMapFactory<Object, IABoxNode<Name, Klass, Role>>();
	private final ICollectionListener<Map.Entry<Role, NodeID>, MultiMap<Role, NodeID>> _linkMapListener = new LinkMapListener<Name, Klass, Role>();
	private final TermEntryFactory<Name, Klass, Role> _termEntryFactory = new TermEntryFactory<Name, Klass, Role>();
	private final NodeTermSetListener<Name, Klass, Role> _nodeTermSetListener = new NodeTermSetListener<Name, Klass, Role>();
}
