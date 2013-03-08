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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.IReasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITermList;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLAtomicTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLClassAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIndividual;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLVariable;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl.SWRLTermFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLTermUtil {

	private SWRLTermUtil()
	{
	}


	@Deprecated
	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> ISWRLTerm<Name, Klass, Role> joinIntoIntersection(
		Collection<? extends ISWRLTerm<Name, Klass, Role>> sourceTerms)
	{
		final ISWRLTermFactory<Name, Klass, Role> termFactory = new SWRLTermFactory<>();
		return joinIntoIntersection(sourceTerms, termFactory);
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> ISWRLTerm<Name, Klass, Role> joinIntoIntersection(
		Collection<? extends ISWRLTerm<Name, Klass, Role>> sourceTerms, ISWRLTermFactory<Name, Klass, Role> termFactory)
	{
		/**
		 * XXX - this should be done more elegantly
		 *
		 *
		 */
		/* create union of terms, join up */
		Set<ISWRLAtomicTerm<Name, Klass, Role>> terms = new TreeSet<>();
		if (sourceTerms.isEmpty()) {
			return null;
		} else if (sourceTerms.size() > 1) {
			for (ISWRLTerm<Name, Klass, Role> sourceTerm : sourceTerms) {
				if (sourceTerm == null) {
					/* skip */
				} else if (sourceTerm instanceof ISWRLIntersection) {
					terms.addAll((ISWRLIntersection<Name, Klass, Role>) sourceTerm);
				} else if (sourceTerm instanceof ISWRLAtomicTerm) {
					terms.add((ISWRLAtomicTerm<Name, Klass, Role>) sourceTerm);
				} else {
					throw new IllegalArgumentException("Unsupported term type: " + sourceTerm.getClass());
				}
			}
			/* handle empty terms, too */
			if (terms.size() == 1) {
				return terms.iterator().next();
			} else if (!terms.isEmpty()) {
				final ISWRLIntersection<Name, Klass, Role> intersection = termFactory.getSWRLIntersection(terms);
				return intersection;
			} else {
				return null;
			}
		} else {
			return sourceTerms.iterator().next();
		}
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> ISWRLTerm<Name, Klass, Role> joinIntoIntersection(
		final ISWRLTerm<Name, Klass, Role> first,
		final ISWRLTerm<Name, Klass, Role> second,
		final ISWRLTermFactory<Name, Klass, Role> termFactory)
	{
		if (first == null) {
			return second;
		} else if (second == null) {
			return first;
		} else {
			ArrayList<ISWRLTerm<Name, Klass, Role>> list = new ArrayList<>(2);
			list.add(first);
			list.add(second);
			list.trimToSize();
			return joinIntoIntersection(list, termFactory);
		}
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> ISWRLTerm<Name, Klass, Role> replaceVariables(
		final ISWRLTerm<Name, Klass, Role> term,
		final Map<ISWRLVariable<Name, Klass, Role>, ISWRLIndividual<Name, Klass, Role>> varMap)
	{
		final ISWRLTermFactory<Name, Klass, Role> termFactory = new SWRLTermFactory<>();

		if (term instanceof ISWRLClassAtom) {
			final ISWRLClassAtom<Name, Klass, Role> clsAtom = (ISWRLClassAtom<Name, Klass, Role>) term;
			return termFactory.getSWRLClassAtom(clsAtom.getKlass(), getMappedIndividual(clsAtom.getIndividual(), varMap));
		} else if (term instanceof ISWRLRoleAtom) {
			final ISWRLRoleAtom<Name, Klass, Role> roleAtom = (ISWRLRoleAtom<Name, Klass, Role>) term;
			return termFactory.getSWRLRoleAtom(roleAtom.getRole(), getMappedIndividual(roleAtom.getFirstIndividual(),
																					   varMap),
											   getMappedIndividual(roleAtom.
				getSecondIndividual(), varMap));
		} else if (term instanceof ITermList) {
			@SuppressWarnings("unchecked")
			final ITermList<ISWRLAtomicTerm<Name, Klass, Role>> list = (ITermList<ISWRLAtomicTerm<Name, Klass, Role>>) term;
			final Set<ISWRLTerm<Name, Klass, Role>> terms = new TreeSet<>();
			for (ISWRLTerm<Name, Klass, Role> subTerm : list) {
				terms.add(replaceVariables(subTerm, varMap));
			}
			return joinIntoIntersection(terms);
		} else {
			throw new IllegalArgumentException("Unknown SWRL Term type: " + term.getClass());
		}
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> ISWRLIndividual<Name, Klass, Role> getMappedIndividual(
		final ISWRLIndividual<Name, Klass, Role> ind,
		final Map<ISWRLVariable<Name, Klass, Role>, ISWRLIndividual<Name, Klass, Role>> varMap)
	{
		if (ind instanceof ISWRLVariable) {
			final ISWRLVariable<Name, Klass, Role> var = (ISWRLVariable<Name, Klass, Role>) ind;
			if (varMap.containsKey(var)) {
				return varMap.get(var);
			} else {
				return ind;
			}
		} else {
			return ind;
		}
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Collection<ISWRLIndividual<Name, Klass, Role>> getIndividuals(
		final Collection<? extends ISWRLTerm<Name, Klass, Role>> terms,
		Collection<ISWRLIndividual<Name, Klass, Role>> targetColl)
	{
		for (ISWRLTerm<Name, Klass, Role> term : terms) {
			getIndividuals(term, targetColl);
		}
		return targetColl;
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Collection<ISWRLIndividual<Name, Klass, Role>> getIndividuals(
		final ISWRLTerm<Name, Klass, Role> term, Collection<ISWRLIndividual<Name, Klass, Role>> targetColl)
	{
		if (term instanceof ISWRLClassAtom) {
			final ISWRLClassAtom<Name, Klass, Role> classAtom = (ISWRLClassAtom<Name, Klass, Role>) term;
			targetColl.add(classAtom.getIndividual());
		} else if (term instanceof ISWRLRoleAtom) {
			final ISWRLRoleAtom<Name, Klass, Role> roleAtom = (ISWRLRoleAtom<Name, Klass, Role>) term;
			targetColl.add(roleAtom.getFirstIndividual());
			targetColl.add(roleAtom.getSecondIndividual());
		} else if (term instanceof ITermList) {
			@SuppressWarnings("unchecked")
			final ITermList<ISWRLAtomicTerm<Name, Klass, Role>> list = (ITermList<ISWRLAtomicTerm<Name, Klass, Role>>) term;
			for (ISWRLAtomicTerm<Name, Klass, Role> subTerm : list) {
				getIndividuals(subTerm, targetColl);
			}
		} else {
			throw new IllegalArgumentException("Unknown SWRL Term type: " + term.getClass());
		}
		return targetColl;
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Collection<ISWRLVariable<Name, Klass, Role>> getVariables(
		final ISWRLTerm<Name, Klass, Role> term, Collection<ISWRLVariable<Name, Klass, Role>> targetColl)
	{
		if (term instanceof ISWRLClassAtom) {
			final ISWRLClassAtom<Name, Klass, Role> classAtom = (ISWRLClassAtom<Name, Klass, Role>) term;
			if (classAtom.getIndividual() instanceof ISWRLVariable) {
				targetColl.add((ISWRLVariable<Name, Klass, Role>) classAtom.getIndividual());
			}
		} else if (term instanceof ISWRLRoleAtom) {
			final ISWRLRoleAtom<Name, Klass, Role> roleAtom = (ISWRLRoleAtom<Name, Klass, Role>) term;
			if (roleAtom.getFirstIndividual() instanceof ISWRLVariable) {
				targetColl.add((ISWRLVariable<Name, Klass, Role>) roleAtom.getFirstIndividual());
			}
			if (roleAtom.getSecondIndividual() instanceof ISWRLVariable) {
				targetColl.add((ISWRLVariable<Name, Klass, Role>) roleAtom.getSecondIndividual());
			}
		} else if (term instanceof ITermList) {
			@SuppressWarnings("unchecked")
			final ITermList<ISWRLAtomicTerm<Name, Klass, Role>> list = (ITermList<ISWRLAtomicTerm<Name, Klass, Role>>) term;
			for (ISWRLAtomicTerm<Name, Klass, Role> subTerm : list) {
				getVariables(subTerm, targetColl);
			}
		} else {
			throw new IllegalArgumentException("Unknown SWRL Term type: " + term.getClass());
		}
		return targetColl;
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Collection<ISWRLVariable<Name, Klass, Role>> getVariables(
		final Collection<? extends ISWRLTerm<Name, Klass, Role>> terms,
		Collection<ISWRLVariable<Name, Klass, Role>> targetColl)
	{
		for (ISWRLTerm<Name, Klass, Role> term : terms) {
			getVariables(term, targetColl);
		}
		return targetColl;
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Collection<ISWRLNominalReference<Name, Klass, Role>> getNominals(
		final ISWRLTerm<Name, Klass, Role> term, Collection<ISWRLNominalReference<Name, Klass, Role>> targetColl)
	{
		if (term instanceof ISWRLClassAtom) {
			final ISWRLClassAtom<Name, Klass, Role> classAtom = (ISWRLClassAtom<Name, Klass, Role>) term;
			if (classAtom.getIndividual() instanceof ISWRLNominalReference) {
				targetColl.add((ISWRLNominalReference<Name, Klass, Role>) classAtom.getIndividual());
			}
		} else if (term instanceof ISWRLRoleAtom) {
			final ISWRLRoleAtom<Name, Klass, Role> roleAtom = (ISWRLRoleAtom<Name, Klass, Role>) term;
			if (roleAtom.getFirstIndividual() instanceof ISWRLNominalReference) {
				targetColl.add((ISWRLNominalReference<Name, Klass, Role>) roleAtom.getFirstIndividual());
			}
			if (roleAtom.getSecondIndividual() instanceof ISWRLNominalReference) {
				targetColl.add((ISWRLNominalReference<Name, Klass, Role>) roleAtom.getSecondIndividual());
			}
		} else if (term instanceof ITermList) {
			@SuppressWarnings("unchecked")
			final ITermList<ISWRLAtomicTerm<Name, Klass, Role>> list = (ITermList<ISWRLAtomicTerm<Name, Klass, Role>>) term;
			for (ISWRLAtomicTerm<Name, Klass, Role> subTerm : list) {
				getNominals(subTerm, targetColl);
			}
		} else {
			throw new IllegalArgumentException("Unknown SWRL Term type: " + term.getClass());
		}
		return targetColl;
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Collection<ISWRLNominalReference<Name, Klass, Role>> getNominals(
		final Collection<? extends ISWRLTerm<Name, Klass, Role>> terms,
		Collection<ISWRLNominalReference<Name, Klass, Role>> targetColl)
	{
		for (ISWRLTerm<Name, Klass, Role> term : terms) {
			getNominals(term, targetColl);
		}
		return targetColl;
	}


	private static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Map<ISWRLVariable<Name, Klass, Role>, ISWRLIndividual<Name, Klass, Role>> compareIndividuals(
		final ISWRLIndividual<Name, Klass, Role> ind1, final ISWRLIndividual<Name, Klass, Role> ind2,
		final Map<ISWRLVariable<Name, Klass, Role>, ISWRLIndividual<Name, Klass, Role>> varMap)
	{
		final ISWRLIndividual<Name, Klass, Role> mappedInd1 = getMappedIndividual(ind1, varMap);
		final ISWRLIndividual<Name, Klass, Role> mappedInd2 = getMappedIndividual(ind2, varMap);

		if (mappedInd1.equals(mappedInd2)) /* already equal? */ {
			return varMap;
		} else if (mappedInd1 instanceof ISWRLVariable) {
			final ISWRLVariable<Name, Klass, Role> var1 = (ISWRLVariable<Name, Klass, Role>) mappedInd1;
			varMap.put(var1, mappedInd2);
			return varMap;
		} else if (mappedInd2 instanceof ISWRLVariable) {
			final ISWRLVariable<Name, Klass, Role> var2 = (ISWRLVariable<Name, Klass, Role>) mappedInd2;
			varMap.put(var2, mappedInd1);
			return varMap;
		} else {
			throw new IllegalArgumentException(String.format("Incompatible individuals %s<->%s", ind1, ind2));
		}
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Map<ISWRLVariable<Name, Klass, Role>, ISWRLIndividual<Name, Klass, Role>> compareTerms(
		final ISWRLTerm<Name, Klass, Role> term1, final ISWRLTerm<Name, Klass, Role> term2)
	{
		final Map<ISWRLVariable<Name, Klass, Role>, ISWRLIndividual<Name, Klass, Role>> varMap = new TreeMap<>();
		return compareTerms(term1, term2, varMap);
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Map<ISWRLVariable<Name, Klass, Role>, ISWRLIndividual<Name, Klass, Role>> compareTerms(
		final ISWRLTerm<Name, Klass, Role> term1, final ISWRLTerm<Name, Klass, Role> term2,
		final Map<ISWRLVariable<Name, Klass, Role>, ISWRLIndividual<Name, Klass, Role>> varMap)
	{
		if (term1 instanceof ISWRLClassAtom) {
			if (term2 instanceof ISWRLClassAtom) {
				final ISWRLClassAtom<Name, Klass, Role> clsAtom1 = (ISWRLClassAtom<Name, Klass, Role>) term1;
				final ISWRLClassAtom<Name, Klass, Role> clsAtom2 = (ISWRLClassAtom<Name, Klass, Role>) term2;
				if (clsAtom1.getKlass().equals(clsAtom2.getKlass())) {
					return compareIndividuals(clsAtom1.getIndividual(), clsAtom2.getIndividual(), varMap);
				}
			}
		} else if (term1 instanceof ISWRLRoleAtom) {
			if (term2 instanceof ISWRLRoleAtom) {
				final ISWRLRoleAtom<Name, Klass, Role> roleAtom1 = (ISWRLRoleAtom<Name, Klass, Role>) term1;
				final ISWRLRoleAtom<Name, Klass, Role> roleAtom2 = (ISWRLRoleAtom<Name, Klass, Role>) term2;
				if (roleAtom1.getRole().equals(roleAtom2.getRole())) {
					if (compareIndividuals(roleAtom1.getFirstIndividual(), roleAtom2.getFirstIndividual(), varMap) != null) {
						return compareIndividuals(roleAtom1.getSecondIndividual(), roleAtom2.getSecondIndividual(),
												  varMap);
					}
				}
			}
		} else if (term1 instanceof ISWRLIntersection) {
			if (term2 instanceof ISWRLIntersection) {
				final ISWRLIntersection<Name, Klass, Role> intersection1 = (ISWRLIntersection<Name, Klass, Role>) term1;
				final ISWRLIntersection<Name, Klass, Role> intersection2 = (ISWRLIntersection<Name, Klass, Role>) term2;
				for (ISWRLAtomicTerm<Name, Klass, Role> subTerm1 : intersection1) {
					final SortedSet<ISWRLAtomicTerm<Name, Klass, Role>> termSet1 = new TreeSet<>(
						intersection1);
					termSet1.remove(subTerm1);
					for (ISWRLAtomicTerm<Name, Klass, Role> subTerm2 : intersection2) {
						final SortedSet<ISWRLAtomicTerm<Name, Klass, Role>> termSet2 = new TreeSet<>(
							intersection2);
						termSet2.remove(subTerm2);
						final Map<ISWRLVariable<Name, Klass, Role>, ISWRLIndividual<Name, Klass, Role>> subVarMap = new TreeMap<>(
							varMap);
						boolean doCompare = false;
						try {
							compareTerms(subTerm1, subTerm2, subVarMap);
							doCompare = true;
						} catch (IllegalArgumentException ex) {
							/* ignore */
						}
						if (doCompare) {
							final ISWRLTerm<Name, Klass, Role> prunedIs1 = replaceVariables(joinIntoIntersection(
								termSet1), subVarMap);
							final ISWRLTerm<Name, Klass, Role> prunedIs2 = replaceVariables(joinIntoIntersection(
								termSet2), subVarMap);
							return compareTerms(prunedIs1, prunedIs2, subVarMap);
						}
					}
					throw new IllegalArgumentException(String.format("No matching term for %s (in %s)", subTerm1,
																	 intersection2));

				}
			}
		}
		throw new IllegalArgumentException(String.format("Incompatible terms: %s<->%s", term1, term2));
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Set<ISWRLAtomicTerm<Name, Klass, Role>> splitIntersection(
		final ISWRLTerm<Name, Klass, Role> sourceTerm)
	{
		if (sourceTerm instanceof ISWRLAtomicTerm) {
			return Collections.singleton((ISWRLAtomicTerm<Name, Klass, Role>) sourceTerm);
		} else if (sourceTerm instanceof ISWRLIntersection) {
			return new TreeSet<>((ISWRLIntersection<Name, Klass, Role>) sourceTerm);
		} else {
			throw new IllegalArgumentException(String.format("Unsupported SWRL term type: %s", sourceTerm.getClass()));
		}
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> ISWRLTerm<Name, Klass, Role> simplify(
		final ISWRLTerm<Name, Klass, Role> inTerm,
		final IABox<Name, Klass, Role> abox,
		final IReasoner<Name, Klass, Role> reasoner,
		final ISWRLTermFactory<Name, Klass, Role> swrlFactory)
		throws EReasonerException, EInconsistencyException
	{
		if (inTerm instanceof ISWRLAtomicTerm) {
			return inTerm;
		} else if (inTerm instanceof ISWRLIntersection) {
			final ISWRLIntersection<Name, Klass, Role> intersection = (ISWRLIntersection<Name, Klass, Role>) inTerm;
			final SortedSet<ISWRLAtomicTerm<Name, Klass, Role>> subTerms = new TreeSet<>(intersection);
			Iterator<ISWRLAtomicTerm<Name, Klass, Role>> subTermIter = subTerms.iterator();
			while (subTermIter.hasNext()) {
				final ISWRLAtomicTerm<Name, Klass, Role> subTerm = subTermIter.next();
				for (ISWRLAtomicTerm<Name, Klass, Role> otherTerm : subTerms) {
					if (!subTerm.equals(otherTerm)) {
						if ((subTerm instanceof ISWRLClassAtom) && (otherTerm instanceof ISWRLClassAtom)) {
							final ISWRLClassAtom<Name, Klass, Role> subAtom = (ISWRLClassAtom<Name, Klass, Role>) subTerm;
							final ISWRLClassAtom<Name, Klass, Role> otherAtom = (ISWRLClassAtom<Name, Klass, Role>) otherTerm;

							if ((subAtom.getIndividual().equals(otherAtom.getIndividual())
								&& reasoner.isSubClassOf(abox, subAtom.getKlass(), otherAtom.getKlass()))) {
								subTermIter.remove();
								/* stop iterating over other subterms */
								break;
							}
						} else if ((subTerm instanceof ISWRLRoleAtom) && (otherTerm instanceof ISWRLRoleAtom)) {
							final ISWRLRoleAtom<Name, Klass, Role> subAtom = (ISWRLRoleAtom<Name, Klass, Role>) subTerm;
							final ISWRLRoleAtom<Name, Klass, Role> otherAtom = (ISWRLRoleAtom<Name, Klass, Role>) otherTerm;

							if (subAtom.getFirstIndividual().equals(otherAtom.getFirstIndividual())
								&& subAtom.getSecondIndividual().equals(otherAtom.getSecondIndividual())
								&& abox.getRBox().isSubRole(subAtom.getRole(), otherAtom.getRole())) {
								subTermIter.remove();
								/* stop iterating over other subterms */
								break;
							}
						}
					}
				}
			}

			return SWRLTermUtil.joinIntoIntersection(subTerms, swrlFactory);
		} else {
			throw new IllegalArgumentException(String.format("Unsupported SWRL term type `%s'", inTerm.getClass()));
		}
	}
}
