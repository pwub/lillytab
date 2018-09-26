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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.IReasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITermList;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLAtomicTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLClassAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLDArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLDataRangeAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLDataRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLObjectRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLVariable;
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
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class SWRLTermUtil
{
	SWRLTermUtil()
	{
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ISWRLTerm<I, L, K, R> joinIntoIntersection(
		Collection<? extends ISWRLTerm<I, L, K, R>> sourceTerms, ISWRLTermFactory<I, L, K, R> termFactory)
	{
		/**
		 * XXX - this should be done more elegantly
		 */
		/* create union of terms, join up */
		Set<ISWRLAtomicTerm<I, L, K, R>> terms = new TreeSet<>();
		if (sourceTerms.isEmpty()) {
			return null;
		} else if (sourceTerms.size() > 1) {
			for (ISWRLTerm<I, L, K, R> sourceTerm : sourceTerms) {
				if (sourceTerm == null) {
					/* skip */
				} else if (sourceTerm instanceof ISWRLIntersection) {
					terms.addAll((ISWRLIntersection<I, L, K, R>) sourceTerm);
				} else if (sourceTerm instanceof ISWRLAtomicTerm) {
					terms.add((ISWRLAtomicTerm<I, L, K, R>) sourceTerm);
				} else {
					throw new IllegalArgumentException("Unsupported term type: " + sourceTerm.getClass());
				}
			}
			/* handle empty terms, too */
			if (terms.size() == 1) {
				return terms.iterator().next();
			} else if (!terms.isEmpty()) {
				final ISWRLIntersection<I, L, K, R> intersection = termFactory.getSWRLIntersection(terms);
				return intersection;
			} else {
				return null;
			}
		} else {
			return sourceTerms.iterator().next();
		}
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ISWRLTerm<I, L, K, R> joinIntoIntersection(
		final ISWRLTerm<I, L, K, R> first, final ISWRLTerm<I, L, K, R> second,
		final ISWRLTermFactory<I, L, K, R> termFactory)
	{
		if (first == null) {
			return second;
		} else if (second == null) {
			return first;
		} else {
			final ArrayList<ISWRLTerm<I, L, K, R>> list = new ArrayList<>(2);
			list.add(first);
			list.add(second);
			list.trimToSize();
			return joinIntoIntersection(list, termFactory);
		}
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ISWRLTerm<I, L, K, R> replaceIndividuals(
		final ISWRLTerm<I, L, K, R> term,
		final Map<? extends ISWRLArgument<I, L, K, R>, ? extends ISWRLArgument<I, L, K, R>> varMap,
		final ISWRLTermFactory<I, L, K, R> swrlTermFactory)
	{
		if (term instanceof ISWRLClassAtom) {
			final ISWRLClassAtom<I, L, K, R> clsAtom = (ISWRLClassAtom<I, L, K, R>) term;
			return swrlTermFactory.getSWRLClassAtom(clsAtom.getKlass(),
													getMappedIndividual(clsAtom.getIndividual(),
																		varMap));
		} else if (term instanceof ISWRLDataRoleAtom) {
			final ISWRLDataRoleAtom<I, L, K, R> dataRole = (ISWRLDataRoleAtom<I, L, K, R>) term;

			return swrlTermFactory.getSWRLDataRoleAtom(dataRole.getRole(),
													   getMappedIndividual(dataRole.getFirstIndividual(), varMap),
													   getMappedIndividual(dataRole.getSecondIndividual(), varMap));
		} else if (term instanceof ISWRLObjectRoleAtom) {
			final ISWRLObjectRoleAtom<I, L, K, R> objRole = (ISWRLObjectRoleAtom<I, L, K, R>) term;

			return swrlTermFactory.getSWRLObjectRoleAtom(objRole.getRole(),
														 getMappedIndividual(objRole.getFirstIndividual(), varMap),
														 getMappedIndividual(objRole.getSecondIndividual(), varMap));
		} else if (term instanceof ISWRLDataRangeAtom) {
			final ISWRLDataRangeAtom<I, L, K, R> rangeAtom = (ISWRLDataRangeAtom<I, L, K, R>) term;
			return swrlTermFactory.getSWRLDataRange(rangeAtom.getDataRange(), getMappedIndividual(rangeAtom.
													getIndividual(), varMap));
		} else if (term instanceof ITermList) {
			@SuppressWarnings("unchecked")
			final ITermList<ISWRLAtomicTerm<I, L, K, R>> list = (ITermList<ISWRLAtomicTerm<I, L, K, R>>) term;
			final Set<ISWRLTerm<I, L, K, R>> terms = new TreeSet<>();
			for (ISWRLTerm<I, L, K, R> subTerm : list) {
				terms.add(replaceIndividuals(subTerm, varMap, swrlTermFactory));
			}
			return joinIntoIntersection(terms, swrlTermFactory);
		} else {
			throw new IllegalArgumentException("Unknown SWRL Term type: " + term.getClass());
		}
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ISWRLArgument<I, L, K, R> getMappedIndividual(
		final ISWRLArgument<I, L, K, R> ind,
		final Map<? extends ISWRLArgument<I, L, K, R>, ? extends ISWRLArgument<I, L, K, R>> varMap)
	{
		if (varMap.containsKey(ind)) {
			return varMap.get(ind);
		} else {
			return ind;
		}
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ISWRLDArgument<I, L, K, R> getMappedIndividual(
		final ISWRLDArgument<I, L, K, R> ind,
		final Map<? extends ISWRLArgument<I, L, K, R>, ? extends ISWRLArgument<I, L, K, R>> varMap)
	{
		return (ISWRLDArgument<I, L, K, R>) getMappedIndividual((ISWRLArgument<I, L, K, R>) ind, varMap);
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ISWRLIArgument<I, L, K, R> getMappedIndividual(
		final ISWRLIArgument<I, L, K, R> ind,
		final Map<? extends ISWRLArgument<I, L, K, R>, ? extends ISWRLArgument<I, L, K, R>> varMap)
	{
		return (ISWRLIArgument<I, L, K, R>) getMappedIndividual((ISWRLArgument<I, L, K, R>) ind, varMap);
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>, C extends Collection<? super ISWRLArgument<I, L, K, R>>> C getArguments(
		final Collection<? extends ISWRLTerm<I, L, K, R>> terms, final C targetColl)
	{
		for (ISWRLTerm<I, L, K, R> term : terms) {
			getArguments(term, targetColl);
		}
		return targetColl;
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>, C extends Collection<? super ISWRLArgument<I, L, K, R>>> C getArguments(
		final ISWRLTerm<I, L, K, R> term, final C targetColl)
	{
		if (term instanceof ISWRLClassAtom) {
			final ISWRLClassAtom<I, L, K, R> classAtom = (ISWRLClassAtom<I, L, K, R>) term;
			targetColl.add(classAtom.getIndividual());
		} else if (term instanceof ISWRLRoleAtom) {
			final ISWRLRoleAtom<I, L, K, R> roleAtom = (ISWRLRoleAtom<I, L, K, R>) term;
			targetColl.add(roleAtom.getFirstIndividual());
			targetColl.add(roleAtom.getSecondIndividual());
		} else if (term instanceof ISWRLDataRangeAtom) {
			final ISWRLDataRangeAtom<I, L, K, R> rangeAtom = (ISWRLDataRangeAtom<I, L, K, R>) term;
			targetColl.add(rangeAtom.getIndividual());

		} else if (term instanceof ITermList) {
			@SuppressWarnings("unchecked")
			final ITermList<ISWRLAtomicTerm<I, L, K, R>> list = (ITermList<ISWRLAtomicTerm<I, L, K, R>>) term;
			for (ISWRLAtomicTerm<I, L, K, R> subTerm : list) {
				getArguments(subTerm, targetColl);
			}
		} else {
			throw new IllegalArgumentException("Unknown SWRL Term type: " + term.getClass());
		}
		return targetColl;
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>, C extends Collection<? super ISWRLVariable<I, L, K, R>>> C getVariables(
		final ISWRLTerm<I, L, K, R> term, final C targetColl)
	{
		if (term instanceof ISWRLClassAtom) {
			final ISWRLClassAtom<I, L, K, R> classAtom = (ISWRLClassAtom<I, L, K, R>) term;
			if (classAtom.getIndividual() instanceof ISWRLVariable) {
				targetColl.add((ISWRLVariable<I, L, K, R>) classAtom.getIndividual());
			}
		} else if (term instanceof ISWRLRoleAtom) {
			final ISWRLRoleAtom<I, L, K, R> roleAtom = (ISWRLRoleAtom<I, L, K, R>) term;
			if (roleAtom.getFirstIndividual() instanceof ISWRLVariable) {
				targetColl.add((ISWRLVariable<I, L, K, R>) roleAtom.getFirstIndividual());
			}
			if (roleAtom.getSecondIndividual() instanceof ISWRLVariable) {
				targetColl.add((ISWRLVariable<I, L, K, R>) roleAtom.getSecondIndividual());
			}
		} else if (term instanceof ISWRLDataRangeAtom) {
			final ISWRLDataRangeAtom<I, L, K, R> dataRange = (ISWRLDataRangeAtom<I, L, K, R>) term;
			if (dataRange.getIndividual() instanceof ISWRLVariable) {
				targetColl.add((ISWRLVariable<I, L, K, R>) dataRange.getIndividual());
			}
		} else if (term instanceof ITermList) {
			@SuppressWarnings("unchecked")
			final ITermList<ISWRLAtomicTerm<I, L, K, R>> list = (ITermList<ISWRLAtomicTerm<I, L, K, R>>) term;
			for (ISWRLAtomicTerm<I, L, K, R> subTerm : list) {
				getVariables(subTerm, targetColl);
			}
		} else {
			throw new IllegalArgumentException("Unknown SWRL Term type: " + term.getClass());
		}
		return targetColl;
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>, C extends Collection<? super ISWRLVariable<I, L, K, R>>> C getVariables(
		final Collection<? extends ISWRLTerm<I, L, K, R>> terms, final C targetColl)
	{
		for (ISWRLTerm<I, L, K, R> term : terms) {
			getVariables(term, targetColl);
		}
		return targetColl;
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> Map<ISWRLVariable<I, L, K, R>, ISWRLArgument<I, L, K, R>> compareTerms(
		final ISWRLTerm<I, L, K, R> term1, final ISWRLTerm<I, L, K, R> term2,
		final Map<ISWRLVariable<I, L, K, R>, ISWRLArgument<I, L, K, R>> varMap,
		final IDLTermFactory<I, L, K, R> termFactory, final ISWRLTermFactory<I, L, K, R> swrlTermFactory)
	{
		if (term1 instanceof ISWRLClassAtom) {
			if (term2 instanceof ISWRLClassAtom) {
				final ISWRLClassAtom<I, L, K, R> clsAtom1 = (ISWRLClassAtom<I, L, K, R>) term1;
				final ISWRLClassAtom<I, L, K, R> clsAtom2 = (ISWRLClassAtom<I, L, K, R>) term2;
				if (clsAtom1.getKlass().equals(clsAtom2.getKlass())) {
					return compareIndividuals(clsAtom1.getIndividual(), clsAtom2.getIndividual(), varMap);
				}
			}
		} else if (term1 instanceof ISWRLRoleAtom) {
			if (term2 instanceof ISWRLRoleAtom) {
				final ISWRLRoleAtom<I, L, K, R> roleAtom1 = (ISWRLRoleAtom<I, L, K, R>) term1;
				final ISWRLRoleAtom<I, L, K, R> roleAtom2 = (ISWRLRoleAtom<I, L, K, R>) term2;
				if (roleAtom1.getRole().equals(roleAtom2.getRole())) {
					if (compareIndividuals(roleAtom1.getFirstIndividual(), roleAtom2.getFirstIndividual(), varMap) != null) {
						return compareIndividuals(roleAtom1.getSecondIndividual(), roleAtom2.getSecondIndividual(),
												  varMap);
					}
				}
			}
		} else if (term1 instanceof ISWRLIntersection) {
			if (term2 instanceof ISWRLIntersection) {
				final ISWRLIntersection<I, L, K, R> intersection1 = (ISWRLIntersection<I, L, K, R>) term1;
				final ISWRLIntersection<I, L, K, R> intersection2 = (ISWRLIntersection<I, L, K, R>) term2;
				for (ISWRLAtomicTerm<I, L, K, R> subTerm1 : intersection1) {
					final SortedSet<ISWRLAtomicTerm<I, L, K, R>> termSet1 = new TreeSet<>(
						intersection1);
					termSet1.remove(subTerm1);
					for (ISWRLAtomicTerm<I, L, K, R> subTerm2 : intersection2) {
						final SortedSet<ISWRLAtomicTerm<I, L, K, R>> termSet2 = new TreeSet<>(
							intersection2);
						termSet2.remove(subTerm2);
						final Map<ISWRLVariable<I, L, K, R>, ISWRLArgument<I, L, K, R>> subVarMap = new TreeMap<>(
							varMap);
						boolean doCompare = false;
						try {
							compareTerms(subTerm1, subTerm2, subVarMap, termFactory, swrlTermFactory);
							doCompare = true;
						} catch (IllegalArgumentException ex) {
							/* ignore */
						}
						if (doCompare) {
							final ISWRLTerm<I, L, K, R> prunedIs1 = replaceIndividuals(joinIntoIntersection(
								termSet1, swrlTermFactory), subVarMap, swrlTermFactory);
							final ISWRLTerm<I, L, K, R> prunedIs2 = replaceIndividuals(joinIntoIntersection(
								termSet2, swrlTermFactory), subVarMap, swrlTermFactory);
							return compareTerms(prunedIs1, prunedIs2, subVarMap, termFactory, swrlTermFactory);
						}
					}
					throw new IllegalArgumentException(String.format("No matching term for %s (in %s)", subTerm1,
																	 intersection2));

				}
			}
		}
		throw new IllegalArgumentException(String.format("Incompatible terms: %s<->%s", term1, term2));
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> Set<ISWRLAtomicTerm<I, L, K, R>> splitIntersection(
		final ISWRLTerm<I, L, K, R> sourceTerm)
	{
		if (sourceTerm instanceof ISWRLAtomicTerm) {
			return Collections.singleton((ISWRLAtomicTerm<I, L, K, R>) sourceTerm);
		} else if (sourceTerm instanceof ISWRLIntersection) {
			return new TreeSet<>((ISWRLIntersection<I, L, K, R>) sourceTerm);
		} else {
			throw new IllegalArgumentException(String.format("Unsupported SWRL term type: %s", sourceTerm.getClass()));
		}
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ISWRLTerm<I, L, K, R> simplify(
		final ISWRLTerm<I, L, K, R> inTerm, final IABox<I, L, K, R> abox, final IReasoner<I, L, K, R> reasoner,
		final ISWRLTermFactory<I, L, K, R> swrlFactory) throws EReasonerException, EInconsistencyException
	{
		return simplify(inTerm, abox, reasoner, swrlFactory, true);
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ISWRLTerm<I, L, K, R> simplify(
		final ISWRLTerm<I, L, K, R> inTerm, final IABox<I, L, K, R> abox, final IReasoner<I, L, K, R> reasoner,
		final ISWRLTermFactory<I, L, K, R> swrlFactory, final boolean pruneTopElements) throws EReasonerException, EInconsistencyException
	{
		final IDLClassExpression<I, L, K, R> thing = abox.getDLTermFactory().getDLThing();

		if (inTerm == null)
			return null;
		else if (inTerm instanceof ISWRLAtomicTerm) {
			return inTerm;
		} else if (inTerm instanceof ISWRLIntersection) {
			final ISWRLIntersection<I, L, K, R> intersection = (ISWRLIntersection<I, L, K, R>) inTerm;
			final SortedSet<ISWRLAtomicTerm<I, L, K, R>> subTerms = new TreeSet<>(intersection);
			Iterator<ISWRLAtomicTerm<I, L, K, R>> subTermIter = subTerms.iterator();
			while (subTermIter.hasNext()) {
				final ISWRLAtomicTerm<I, L, K, R> subTerm = subTermIter.next();
				if (pruneTopElements) {
					if (subTerm instanceof ISWRLClassAtom) {
						final ISWRLClassAtom<I, L, K, R> clsAtom = (ISWRLClassAtom<I, L, K, R>) subTerm;
						if (clsAtom.getKlass().equals(thing)) {
							subTermIter.remove();
							continue;
						}
					} else if (subTerm instanceof ISWRLDataRangeAtom) {
						final ISWRLDataRangeAtom<I, L, K, R> dataRange = (ISWRLDataRangeAtom<I, L, K, R>) subTerm;
						if (dataRange.getDataRange().isTopDatatype()) {
							subTermIter.remove();
							continue;
						}
					}
				}
				for (ISWRLAtomicTerm<I, L, K, R> otherTerm : subTerms) {
					if (!subTerm.equals(otherTerm)) {
						if ((subTerm instanceof ISWRLClassAtom) && (otherTerm instanceof ISWRLClassAtom)) {
							final ISWRLClassAtom<I, L, K, R> subAtom = (ISWRLClassAtom<I, L, K, R>) subTerm;
							final ISWRLClassAtom<I, L, K, R> otherAtom = (ISWRLClassAtom<I, L, K, R>) otherTerm;

							if ((subAtom.getIndividual().equals(otherAtom.getIndividual())
								&& reasoner.isSubClassOf(abox, otherAtom.getKlass(), subAtom.getKlass()))) {
								subTermIter.remove();
								/* stop iterating over other subterms */
								break;
							}
						} else if ((subTerm instanceof ISWRLRoleAtom) && (otherTerm instanceof ISWRLRoleAtom)) {
							final ISWRLRoleAtom<I, L, K, R> subAtom = (ISWRLRoleAtom<I, L, K, R>) subTerm;
							final ISWRLRoleAtom<I, L, K, R> otherAtom = (ISWRLRoleAtom<I, L, K, R>) otherTerm;

							if (subAtom.getFirstIndividual().equals(otherAtom.getFirstIndividual())
								&& subAtom.getSecondIndividual().equals(otherAtom.getSecondIndividual())
								&& abox.getRBox().isSubRole(otherAtom.getRole(), subAtom.getRole())) {
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

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> boolean isSyntacticSubTerm(
		final ISWRLTerm<I, L, K, R> presumedSub, final ISWRLTerm<I, L, K, R> presumedSuper)
	{
		if (presumedSub.equals(presumedSuper)) {
			return true;
		} else if (presumedSuper instanceof ISWRLIntersection) {
			final ISWRLIntersection<I, L, K, R> superIntersection = (ISWRLIntersection<I, L, K, R>) presumedSuper;
			if (presumedSub instanceof ISWRLIntersection) {
				final ISWRLIntersection<I, L, K, R> subIntersection = (ISWRLIntersection<I, L, K, R>) presumedSub;
				return superIntersection.containsAll(subIntersection);

			} else if (presumedSub instanceof ISWRLAtomicTerm) {
				return superIntersection.contains((ISWRLAtomicTerm<I, L, K, R>) presumedSub);
			} else
				throw new IllegalArgumentException("Unsupported SWRL argument type: " + presumedSub.getClass());
		} else
			return false;
	}

	private static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> Map<ISWRLVariable<I, L, K, R>, ISWRLArgument<I, L, K, R>> compareIndividuals(
		final ISWRLArgument<I, L, K, R> ind1, final ISWRLArgument<I, L, K, R> ind2,
		final Map<ISWRLVariable<I, L, K, R>, ISWRLArgument<I, L, K, R>> varMap)
	{
		final ISWRLArgument<I, L, K, R> mappedInd1 = getMappedIndividual(ind1, varMap);
		final ISWRLArgument<I, L, K, R> mappedInd2 = getMappedIndividual(ind2, varMap);

		if (mappedInd1.equals(mappedInd2)) /* already equal? */ {
			return varMap;
		} else if (mappedInd1 instanceof ISWRLVariable) {
			final ISWRLVariable<I, L, K, R> var1 = (ISWRLVariable<I, L, K, R>) mappedInd1;
			varMap.put(var1, mappedInd2);
			return varMap;
		} else if (mappedInd2 instanceof ISWRLVariable) {
			final ISWRLVariable<I, L, K, R> var2 = (ISWRLVariable<I, L, K, R>) mappedInd2;
			varMap.put(var2, mappedInd1);
			return varMap;
		} else {
			throw new IllegalArgumentException(String.format("Incompatible individuals %s<->%s", ind1, ind2));
		}
	}
}
