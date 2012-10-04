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

import de.dhke.projects.cutil.collections.aspect.AspectSet;
import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemReplacedEvent;
import de.dhke.projects.cutil.collections.aspect.ICollectionListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

/**
 * <p>
 * Represents an DL TBox. A TBox is a set of global terms that define restrictions on
 * the ABox models, for example role hierachies, domain and range restrictions
 * for properties as well as more complex axioms.
 * </p><p>
 * The current TBox implementation only supports concept restriction axioms,
 * and most importantly concept inclusions and general concept inclusions (GCIs).
 * </p><p>
 * Concept restrictions are a set of axioms that are required to hold globally, i.e.
 * that need to hold for every instance in a tableaux.
 * </p><p>
 * The global axioms may be split into an unfoldable and a non-unfoldable set.
 * Descriptions of the form {@literal (CN subClassOf C)}, whereas {@literal CN}
 * is a primitive concept name and {@literal C} is an arbitrary concept may
 * be unfolded <emph>lazily</emph>, that is {@literal C} must only be added
 * to a node's term set if {@literal CN} is already present in the node's concept
 * set. Descriptions in other formats need to be added regardlessly. Since
 * unfoldable descriptions are quite common, lazy unfolding significantly improves performance.
 * </p>
 *
 * @param <Name>
 * @param <Klass>
 * @param <Role>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TBox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends TermSet<Name, Klass, Role>
	implements ITBox<Name, Klass, Role> {

	private final IDLTermFactory<Name, Klass, Role> _termFactory;
	/**
	 * The set of non-unfoldable global descriptions
	 **/
	private final Set<IDLRestriction<Name, Klass, Role>> _globalDescriptionSet = new HashSet<IDLRestriction<Name, Klass, Role>>();
	/**
	 * A map of unfoldable descriptions. The key is usually a named class reference and the value is
	 * the set of unfoldings associated with that class. The class reference itself is not
	 * included in the mapped set.
	 */
	private final MultiMap<IDLRestriction<Name, Klass, Role>, IDLRestriction<Name, Klass, Role>> _unfolding = new MultiHashMap<IDLRestriction<Name, Klass, Role>, IDLRestriction<Name, Klass, Role>>();
	private boolean _needRecalculate = true;
	// private final Set<
	/**
	 * The AssertedRBox associated with this TBox.
	 **/
	private final IAssertedRBox<Name, Klass, Role> _rbox = new AssertedRBox<Name, Klass, Role>(this);
	private final ICollectionListener<IDLTerm<Name,Klass,Role>, Collection<IDLTerm<Name,Klass,Role>>> _termSetListener
	= new ICollectionListener<IDLTerm<Name,Klass,Role>, Collection<IDLTerm<Name,Klass,Role>>>() {

		public void beforeElementAdded(CollectionItemEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
		{
		}

		public void beforeElementRemoved(CollectionItemEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
		{
		}

		public void beforeElementReplaced(CollectionItemReplacedEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
		{
		}

		public void beforeCollectionCleared(CollectionEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
		{
		}

		@SuppressWarnings("unchecked")
		public void afterElementAdded(CollectionItemEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
		{
			((TBox<Name, Klass, Role>)e.getSource())._needRecalculate = true;
		}

		@SuppressWarnings("unchecked")
		public void afterElementRemoved(CollectionItemEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
		{
			((TBox<Name, Klass, Role>)e.getSource())._needRecalculate = true;
		}

		@SuppressWarnings("unchecked")
		public void afterElementReplaced(CollectionItemReplacedEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
		{
			((TBox<Name, Klass, Role>)e.getSource())._needRecalculate = true;
		}

		@SuppressWarnings("unchecked")
		public void afterCollectionCleared(CollectionEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> e)
		{
			((TBox<Name, Klass, Role>)e.getSource())._needRecalculate = true;
		}
	};

	public TBox(final IDLTermFactory<Name, Klass, Role> termFactory)
	{
		super();
		_termFactory = termFactory;
		getListeners().add(_termSetListener);
	}

	/**
	 * @return The set of non-unfoldable descriptions.
	 */
	public Set<IDLRestriction<Name, Klass, Role>> getGlobalDescriptions()
	{
		if (_needRecalculate)
			recalculate();
		return Collections.unmodifiableSet(_globalDescriptionSet);
	}

	/**
	 * @param unfoldee The description to unfold.
	 * @return The unfolding for {@literal unfoldee}.
	 */
	public Collection<IDLRestriction<Name, Klass, Role>> getUnfolding(final IDLRestriction<Name, Klass, Role> unfoldee)
	{
		if (_needRecalculate)
			recalculate();
		if (_unfolding.containsKey(unfoldee))
			return _unfolding.get(unfoldee);
		else
			return Collections.emptySet();
	}

	/**
	 * <p>
	 * Recalculate the internal state from the term set.
	 * </p><p>
	 * Should only be called, when needed, i.e. when the term set was changed since
	 * the last access.
	 * </p>
	 */
	private void recalculate()
	{
		_unfolding.clear();
		_globalDescriptionSet.clear();

		/* first, we unfold any top level intersections recursively */
		Set<IDLTerm<Name, Klass, Role>> termSet = TermUtil.unfoldIntersections(this, _termFactory);

		Iterator<IDLTerm<Name, Klass, Role>> iter = termSet.iterator();

		while (iter.hasNext()) {
			IDLTerm<Name, Klass, Role> term = iter.next();
			if (term instanceof IDLRestriction) {
				/* pick up descriptions only */
				IDLRestriction<Name, Klass, Role> desc = (IDLRestriction<Name, Klass, Role>) term;
				/* simplify description */
				desc = TermUtil.simplify(desc, _termFactory);
				if (desc instanceof IDLImplies) {
					handleImplication(desc);
				} else if (desc instanceof IDLIntersection) {
					/**
					 * if the description is an intersection, look at it's parts
					 * and treat them individually.
					 **/
					IDLIntersection<Name, Klass, Role> intersection = (IDLIntersection<Name, Klass, Role>)desc;
					for (IDLRestriction<Name, Klass, Role> subTerm: intersection) {
						if (subTerm instanceof IDLImplies)
							handleImplication(subTerm);
						else
							_globalDescriptionSet.add(TermUtil.toNNF(subTerm, _termFactory));
					}
				} else
					_globalDescriptionSet.add(TermUtil.toNNF(desc, _termFactory));
			}
		}
		/* don't forget to add _Thing_ */
		_globalDescriptionSet.add(_termFactory.getDLThing());
		_needRecalculate = false;
	}


	private void handleImplication(IDLRestriction<Name, Klass, Role> desc)
	{
		/**
		 * if description is an implication AND the left hand side is a
		 * simple class or nominal reference, update the map of unfoldings,
		 * otherwise add the description to the global term set.
		 **/
		@SuppressWarnings("unchecked")
		IDLImplies<Name, Klass, Role> implies = (IDLImplies<Name, Klass, Role>) desc;
		IDLRestriction<Name, Klass, Role> subDesc = TermUtil.toNNF(implies.getSubDescription(), _termFactory);

		if ((subDesc instanceof IDLClassReference) || (subDesc instanceof IDLNominalReference)) {
			if (!subDesc.equals(implies.getSuperDescription())) {
				_unfolding.put(implies.getSubDescription(), TermUtil.toNNF(implies.getSuperDescription(),
					_termFactory));
			}
		} else
			_globalDescriptionSet.add(TermUtil.toNNF(desc, _termFactory));
	}

	public IAssertedRBox<Name, Klass, Role> getRBox()
	{
		return _rbox;
	}

	public String toString(String prefix)
	{
		// XXX - rewrite
		return toString();
	}
}
