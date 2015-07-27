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
package de.uniba.wiai.kinf.pw.projects.lillytab.cache;

import de.dhke.projects.cutil.IDecorator;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.factories.TreeSetFactory;
import de.dhke.projects.cutil.collections.map.GenericMultiHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.AbstractReasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import java.util.Collection;
import java.util.SortedSet;
import java.util.WeakHashMap;
import org.apache.commons.collections15.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ReasonerCache<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractReasoner<I, L, K, R>
	implements IReasoner<I, L, K, R>, IDecorator<IReasoner<I, L, K, R>> {

	static final Logger _logger = LoggerFactory.getLogger(ReasonerCache.class);
	private IReasoner<I, L, K, R> _baseReasoner;
	private WeakHashMap<IABox<I, L, K, R>, Cache> _cacheMap = new WeakHashMap<>();


	public ReasonerCache(final IReasoner<I, L, K, R> reasoner)
	{
		_baseReasoner = reasoner;
	}


	@Override
	public IReasoner<I, L, K, R> getDecoratee()
	{
		return _baseReasoner;
	}


	@Override
	public Collection<? extends IReasonerResult<I, L, K, R>> checkConsistency(IABox<I, L, K, R> abox,
																			  IDLClassExpression<I, L, K, R> concept,
																			  boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		return _baseReasoner.checkConsistency(abox, concept, stopAtFirstModel);
	}


	@Override
	public Collection<? extends IReasonerResult<I, L, K, R>> checkConsistency(IABox<I, L, K, R> abox,
																			  boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		return _baseReasoner.checkConsistency(abox, stopAtFirstModel);
	}


	@Override
	public boolean isSubClassOf(IABox<I, L, K, R> abox,
								IDLClassExpression<I, L, K, R> presumedSub,
								IDLClassExpression<I, L, K, R> presumedSuper)
		throws EReasonerException, EInconsistencyException
	{
		Cache cache = _cacheMap.get(abox);
		if (cache == null) {
			cache = new Cache();
			_cacheMap.put(abox, cache);
		}

		if (cache._subClassCache.containsValue(presumedSub, presumedSuper)) {
			return true;
		} else if (cache._negSubClassCache.containsValue(presumedSub, presumedSuper)) {
			return false;
		} else if ((cache._disjointCache.containsValue(presumedSub, presumedSuper))
			|| (cache._disjointCache.containsValue(presumedSuper, presumedSub))) {
			return false;
		} else {
			boolean isSubClass = _baseReasoner.isSubClassOf(abox, presumedSub, presumedSuper);
			if (isSubClass) {
				cache._subClassCache.put(presumedSub, presumedSuper);
			} else {
				cache._negSubClassCache.put(presumedSub, presumedSuper);
			}
			return isSubClass;
		}
	}


	@Override
	public boolean isInDomain(IABox<I, L, K, R> abox,
							  IDLClassExpression<I, L, K, R> desc, R role)
		throws EReasonerException, EInconsistencyException
	{
		return _baseReasoner.isInDomain(abox, desc, role);
	}


	@Override
	public boolean isInRange(IABox<I, L, K, R> abox,
							 IDLClassExpression<I, L, K, R> desc, R role)
		throws EReasonerException, EInconsistencyException
	{
		return _baseReasoner.isInRange(abox, desc, role);
	}


	@Override
	public boolean isDisjoint(IABox<I, L, K, R> abox,
							  IDLClassExpression<I, L, K, R> desc1,
							  IDLClassExpression<I, L, K, R> desc2)
		throws EReasonerException, EInconsistencyException
	{
		Cache cache = _cacheMap.get(abox);
		if (cache == null) {
			cache = new Cache();
			_cacheMap.put(abox, cache);
		}
		if (cache._disjointCache.containsValue(desc1, desc2) || cache._disjointCache.containsValue(desc2, desc1)) {
			return true;
		} else {
			boolean isDisjoint = _baseReasoner.isDisjoint(abox, desc1, desc2);
			if (isDisjoint) {
				cache._disjointCache.put(desc2, desc1);
				cache._disjointCache.put(desc2, desc1);
			}
			return isDisjoint;
		}
	}

	class Cache {
		final MultiMap<IDLClassExpression<I, L, K, R>, IDLClassExpression<I, L, K, R>> _subClassCache;
		final MultiMap<IDLClassExpression<I, L, K, R>, IDLClassExpression<I, L, K, R>> _negSubClassCache;
		final MultiMap<IDLClassExpression<I, L, K, R>, IDLClassExpression<I, L, K, R>> _disjointCache;

		Cache()
		{
			final ICollectionFactory<IDLClassExpression<I, L, K, R>, SortedSet<IDLClassExpression<I, L, K, R>>> collectionFactory =
				new TreeSetFactory<>();
			_subClassCache = new GenericMultiHashMap<>(collectionFactory);
			_negSubClassCache = new GenericMultiHashMap<>(collectionFactory);
			_disjointCache = new GenericMultiHashMap<>(collectionFactory);
		}
	}
}
