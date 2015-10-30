/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.dhke.projects.cutil.IDecorator;
import de.dhke.projects.cutil.collections.map.DefaultMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TracingReasoner<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IReasoner<I, L, K, R>, IDecorator<IReasoner<I, L, K, R>> {

	public static final String CHECK_CONSISTENCY_CALLS = "calls.checkConsistency";
	public static final String SUBCLASS_OF_CALLS = "calls.subClassOf";
	public static final String IS_CONSISTENT_CALLS = "calls.isConsistent";
	public static final String IS_IN_DOMAIN_CALLS = "calls.isInDomain";
	public static final String IS_IN_RANGE_CALLS = "calls.isInRange";
	public static final String IS_DISJOINT_CALLS = "calls.isDisjoint";
	public static final String CLASSIFY_CALLS = "calls.classify";

	private final IReasoner<I, L, K, R> _backend;
	private final Map<String, Integer> _statistics;


	protected TracingReasoner(final IReasoner<I, L, K, R> backend)
	{
		_backend = backend;
		_statistics = DefaultMap.decorate(new TreeMap<String, Integer>(), 0);
	}


	private void incrementStat(final String statName, int increment)
	{
		_statistics.put(statName, _statistics.get(statName) + increment);
	}


	private void incrementStat(final String statName)
	{
		incrementStat(statName, 1);
	}


	@Override
	public Collection<? extends IReasonerResult<I, L, K, R>> checkConsistency(IABox<I, L, K, R> abox)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(CHECK_CONSISTENCY_CALLS);
		return _backend.checkConsistency(abox);
	}


	@Override
	public Collection<? extends IReasonerResult<I, L, K, R>> checkConsistency(IABox<I, L, K, R> abox,
																			  IDLClassExpression<I, L, K, R> concept,
																			  boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(CHECK_CONSISTENCY_CALLS);
		return _backend.checkConsistency(abox, concept, stopAtFirstModel);
	}


	@Override
	public Collection<? extends IReasonerResult<I, L, K, R>> checkConsistency(IABox<I, L, K, R> abox,
																			  boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(CHECK_CONSISTENCY_CALLS);
		return _backend.checkConsistency(abox, stopAtFirstModel);
	}


	@Override
	public boolean isSubClassOf(IABox<I, L, K, R> abox, IDLClassExpression<I, L, K, R> presumedSub,
								IDLClassExpression<I, L, K, R> presumedSuper)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(SUBCLASS_OF_CALLS);
		return _backend.isSubClassOf(abox, presumedSub, presumedSuper);
	}


	@Override
	public boolean isSubClassOf(IABox<I, L, K, R> abox, K presumedSub, K presumedSuper)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(SUBCLASS_OF_CALLS);
		return _backend.isSubClassOf(abox, presumedSub, presumedSuper);
	}


	@Override
	public boolean isConsistent(IABox<I, L, K, R> abox)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(IS_CONSISTENT_CALLS);
		return _backend.isConsistent(abox);
	}


	@Override
	public boolean isConsistent(IABox<I, L, K, R> abox, IDLClassExpression<I, L, K, R> concept)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(IS_CONSISTENT_CALLS);
		return _backend.isConsistent(abox, concept);
	}


	@Override
	public boolean isConsistent(IDLClassExpression<I, L, K, R> concept, IABoxFactory<I, L, K, R> aboxFactory)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(IS_CONSISTENT_CALLS);
		return _backend.isConsistent(concept, aboxFactory);
	}


	@Override
	public boolean isInDomain(IABox<I, L, K, R> abox, IDLClassExpression<I, L, K, R> desc, R role)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(IS_IN_DOMAIN_CALLS);
		return _backend.isInDomain(abox, desc, role);
	}


	@Override
	public boolean isInRange(IABox<I, L, K, R> abox, IDLClassExpression<I, L, K, R> desc, R role)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(IS_IN_RANGE_CALLS);
		return _backend.isInRange(abox, desc, role);
	}


	@Override
	public boolean isDisjoint(IABox<I, L, K, R> abox, IDLClassExpression<I, L, K, R> desc1,
							  IDLClassExpression<I, L, K, R> desc2)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(IS_DISJOINT_CALLS);
		return _backend.isDisjoint(abox, desc1, desc2);
	}


	@Override
	public Collection<IDLImplies<I, L, K, R>> classify(IABox<I, L, K, R> abox)
		throws EReasonerException, EInconsistencyException
	{
		incrementStat(CLASSIFY_CALLS);
		return _backend.classify(abox);
	}


	@Override
	public IReasoner<I, L, K, R> getDecoratee()
	{
		return _backend;
	}


	public void clearStats()
	{
		_statistics.clear();;
	}


	public Map<String, Integer> getStatistics()
	{
		return Collections.unmodifiableMap(_statistics);
	}

}
