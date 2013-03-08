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
package de.uniba.wiai.kinf.pw.projects.lillytab.cache;

import de.dhke.projects.cutil.IDecorator;
import de.dhke.projects.cutil.collections.map.MultiTreeSetHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.AbstractReasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;
import java.util.WeakHashMap;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ReasonerCache<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractReasoner<Name, Klass, Role>
	implements IReasoner<Name, Klass, Role>, IDecorator<IReasoner<Name, Klass, Role>> {

	class Cache {

		MultiMap<IDLRestriction<Name, Klass, Role>, IDLRestriction<Name, Klass, Role>> _subClassCache = new MultiTreeSetHashMap<>();
		MultiMap<IDLRestriction<Name, Klass, Role>, IDLRestriction<Name, Klass, Role>> _negSubClassCache = new MultiTreeSetHashMap<>();
		MultiMap<IDLRestriction<Name, Klass, Role>, IDLRestriction<Name, Klass, Role>> _disjointCache = new MultiTreeSetHashMap<>();
	}
	private IReasoner<Name, Klass, Role> _baseReasoner;
	private WeakHashMap<IABox<Name, Klass, Role>, Cache> _cacheMap = new WeakHashMap<>();


	@Override
	public IReasoner<Name, Klass, Role> getDecoratee()
	{
		return _baseReasoner;
	}


	public ReasonerCache(final IReasoner<Name, Klass, Role> reasoner)
	{
		_baseReasoner = reasoner;
	}


	@Override
	public Collection<? extends IReasonerResult<Name, Klass, Role>> checkConsistency(IABox<Name, Klass, Role> abox,
																					 IDLRestriction<Name, Klass, Role> concept,
																					 boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		return _baseReasoner.checkConsistency(abox, concept, stopAtFirstModel);
	}


	@Override
	public Collection<? extends IReasonerResult<Name, Klass, Role>> checkConsistency(IABox<Name, Klass, Role> abox,
																					 boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		return _baseReasoner.checkConsistency(abox, stopAtFirstModel);
	}


	@Override
	public boolean isSubClassOf(IABox<Name, Klass, Role> abox,
								IDLRestriction<Name, Klass, Role> presumedSub,
								IDLRestriction<Name, Klass, Role> presumedSuper)
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
	public boolean isInDomain(IABox<Name, Klass, Role> abox,
							  IDLRestriction<Name, Klass, Role> desc, Role role)
		throws EReasonerException, EInconsistencyException
	{
		return _baseReasoner.isInDomain(abox, desc, role);
	}


	@Override
	public boolean isInRange(IABox<Name, Klass, Role> abox,
							 IDLRestriction<Name, Klass, Role> desc, Role role)
		throws EReasonerException, EInconsistencyException
	{
		return _baseReasoner.isInRange(abox, desc, role);
	}


	@Override
	public boolean isDisjoint(IABox<Name, Klass, Role> abox,
							  IDLRestriction<Name, Klass, Role> desc1,
							  IDLRestriction<Name, Klass, Role> desc2)
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
}
