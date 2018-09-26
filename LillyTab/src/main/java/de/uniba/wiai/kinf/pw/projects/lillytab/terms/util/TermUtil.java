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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.util;

import de.dhke.projects.cutil.collections.CollectionUtil;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLDataAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLDataSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITermList;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataUnion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class TermUtil {

	private TermUtil()
	{
	}


	/**
	 *
	 * @param <I>         The type for individuals/nominals
	 * @param <L>         The type for literals
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 *
	 * @param sourceDesc
	 * @param termFactory
	 *                       <p/>
	 * @return
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> Set<IDLTerm<I, L, K, R>> unfoldIntersections(
		final IDLTerm<I, L, K, R> sourceDesc, final IDLTermFactory<I, L, K, R> termFactory)
	{
		final Set<IDLTerm<I, L, K, R>> targetSet = new HashSet<>();
		if (sourceDesc instanceof IDLObjectIntersection) {
			for (IDLClassExpression<I, L, K, R> subDesc : (IDLObjectIntersection<I, L, K, R>) sourceDesc) {
				targetSet.addAll(unfoldIntersections(simplify(subDesc, termFactory), termFactory));
			}
		} else {
			targetSet.add(sourceDesc);
		}
		return targetSet;
	}


	/**
	 *
	 * @param <I> The type for individuals/nominals
	 * @param <L> The type for literals
	 * @param <K> The type for DL classes
	 * @param <R> The type for properties (roles)
	 *
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> Set<IDLTerm<I, L, K, R>> unfoldIntersections(
		final Set<IDLTerm<I, L, K, R>> sourceSet, final IDLTermFactory<I, L, K, R> termFactory)
	{
		final Set<IDLTerm<I, L, K, R>> targetSet = new HashSet<>(sourceSet.size());
		for (IDLTerm<I, L, K, R> sourceDesc : sourceSet) {
			targetSet.addAll(unfoldIntersections(sourceDesc, termFactory));
		}
		return targetSet;
	}


	/**
	 *
	 * @param <I> The type for individuals/nominals
	 * @param <L> The type for literals
	 * @param <K> The type for DL classes
	 * @param <R> The type for properties (roles)
	 *
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLNodeTerm<I, L, K, R> simplify(
		final IDLNodeTerm<I, L, K, R> desc, final IDLTermFactory<I, L, K, R> termFactory)
	{
		if (desc instanceof IDLDataRange) {
			return simplify((IDLDataRange<I, L, K, R>) desc, termFactory);
		} else if (desc instanceof IDLClassExpression) {
			return simplify((IDLClassExpression<I, L, K, R>) desc, termFactory);
		} else
			throw new IllegalArgumentException("Unsupported term type: " + desc.getClass());
	}


	/**
	 * Try to simplify the specified term. The exact results of the simplification are undefined, but the returned term
	 * is required to be a possibly transformed, but semantically equivalent representation of the input term.
	 * <p /> The current implementation tries to reduce the complexity of the incoming terms to make more efficient
	 * tableaux reasoning possible. The result is <emph>NOT</emph> necessarily a term in negation normal form. If NNF is
	 * required, use {@link #toNNF(de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression, de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory) ).
	 * <p />
	 *
	 * @param <I>         The type for nominals and values
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 * @param desc        The {@link IDLClassExpression} to simplify
	 * @param termFactory The {@link IDLTermFactory} to use for creating new terms.
	 * <p/>
	 * @return A term representing a simplified version of the input term.
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLClassExpression<I, L, K, R> simplify(
		final IDLClassExpression<I, L, K, R> desc, final IDLTermFactory<I, L, K, R> termFactory)
	{
		if (desc instanceof IDLImplies) {
			final IDLImplies<I, L, K, R> implies = (IDLImplies<I, L, K, R>) desc;
			if (implies.getSubDescription().equals(termFactory.getDLThing())) /*
			 * (simp (implies _Thing_ A)) => (simp A)
			 */ {
				return simplify(implies.getSuperDescription(), termFactory);
			} else if (implies.getSubDescription().equals(termFactory.getDLNothing())) /*
			 * (simp (implies _Nothing_ A)) => _Thing_
			 */ {
				return termFactory.getDLThing();
			} else if (implies.getSubDescription().equals(implies.getSuperDescription())) /*
			 * (simp (implies A A) => _Thing_
			 */ {
				return termFactory.getDLThing();
			} else if (implies.getSubDescription() instanceof IDLObjectUnion) {
				/*
				 * (simp (implies (or A B) C) => (and (implies (simp A) (simp C)) (implies (simp B) (simp C)))
				 */
				final IDLObjectUnion<I, L, K, R> subUnion = (IDLObjectUnion<I, L, K, R>) implies.getSubDescription();
				final IDLClassExpression<I, L, K, R> supTerm = simplify(implies.getSuperDescription(), termFactory);
				final Set<IDLClassExpression<I, L, K, R>> newTerms = new HashSet<>();
				for (IDLClassExpression<I, L, K, R> subTerm : subUnion) {
					final IDLClassExpression<I, L, K, R> newTerm =
						simplify(termFactory.getDLImplies(
						simplify(subTerm, termFactory), supTerm), termFactory);
					newTerms.add(newTerm);
				}
				return simplify(termFactory.getDLObjectIntersection(newTerms), termFactory);
			} else /*
			 * (simp (implies A B)) => (implies (simp A) (simp B)))
			 */ {
				return termFactory.getDLImplies(
					simplify(implies.getSubDescription(), termFactory),
					simplify(implies.getSuperDescription(), termFactory));
			}
		} else if (desc instanceof IDLObjectUnion) {
			final IDLObjectUnion<I, L, K, R> union = (IDLObjectUnion<I, L, K, R>) desc;
			final Set<IDLClassExpression<I, L, K, R>> subTerms = new HashSet<>();
			for (IDLClassExpression<I, L, K, R> subTerm : union) {
				if (subTerms.contains(termFactory.getDLThing())) {
					return termFactory.getDLThing();
				} else {
					IDLClassExpression<I, L, K, R> simplifiedSubTerm = simplify(subTerm, termFactory);
					if (simplifiedSubTerm instanceof IDLObjectUnion) /*
					 * (or (and (A (and B C))) => (or (simp A) (simp B) (simp C))
					 */ {
						subTerms.addAll(((IDLObjectUnion<I, L, K, R>) simplifiedSubTerm));
					} else if (!(simplifiedSubTerm.equals(termFactory.getDLNothing()))) {
						subTerms.add(simplifiedSubTerm);
					}
				}
			}
			return joinToUnion(subTerms, termFactory);
		} else if (desc instanceof IDLObjectIntersection) {
			final IDLObjectIntersection<I, L, K, R> intersection = (IDLObjectIntersection<I, L, K, R>) desc;
			final Set<IDLClassExpression<I, L, K, R>> subTerms = new HashSet<>();
			for (IDLClassExpression<I, L, K, R> subTerm : intersection) {
				if (subTerms.contains(termFactory.getDLNothing())) {
					return termFactory.getDLNothing();
				} else {
					IDLClassExpression<I, L, K, R> simplifiedSubTerm = simplify(subTerm, termFactory);
					if (simplifiedSubTerm instanceof IDLObjectIntersection) /*
					 * (simp (and (A (and B C))) => (and (simp A) (simp B) (simp C))
					 */ {
						subTerms.addAll(((IDLObjectIntersection<I, L, K, R>) simplifiedSubTerm));
					} else if (!(simplifiedSubTerm.equals(termFactory.getDLThing()))) {
						subTerms.add(simplifiedSubTerm);
					}
				}
			}
			return joinToIntersection(subTerms, termFactory);
		} else if (desc instanceof IDLObjectSomeRestriction) {
			/*
			 * (simp (some r A)) => (some r (simp A))
			 */
			final IDLObjectSomeRestriction<I, L, K, R> some = (IDLObjectSomeRestriction<I, L, K, R>) desc;
			return termFactory.getDLObjectSomeRestriction(some.getRole(), simplify(some.getTerm(), termFactory));
		} else if (desc instanceof IDLObjectAllRestriction) {
			/*
			 * (simp (only r A)) => (only r (simp A))
			 */
			final IDLObjectAllRestriction<I, L, K, R> some = (IDLObjectAllRestriction<I, L, K, R>) desc;
			return termFactory.getDLObjectAllRestriction(some.getRole(), simplify(some.getTerm(), termFactory));
		} else if (desc instanceof IDLObjectNegation) {
			IDLObjectNegation<I, L, K, R> neg = (IDLObjectNegation<I, L, K, R>) desc;
			if (neg.getTerm() instanceof IDLObjectNegation) /*
			 * (simp (not (not A))) => (simp A)
			 */ {
				return simplify(((IDLObjectNegation<I, L, K, R>) neg.getTerm()).getTerm(), termFactory);
			} else if (neg.getTerm() instanceof IDLImplies) {
				/*
				 * (simp (not (implies A B))) => (simp (and A (not B)))
				 */
				final IDLImplies<I, L, K, R> implies = (IDLImplies<I, L, K, R>) neg.getTerm();
				final IDLObjectIntersection<I, L, K, R> intersection = termFactory.getDLObjectIntersection(implies.
					getSubDescription(), termFactory.getDLObjectNegation(implies.getSuperDescription()));
				return simplify(intersection, termFactory);
			} else {
				return termFactory.getDLObjectNegation(simplify(neg.getTerm(), termFactory));
			}
		} else {
			return desc;
		}
	}


	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLClassExpression<I, L, K, R> negate(
		final IDLClassExpression<I, L, K, R> exp, final IDLTermFactory<I, L, K, R> termFactory)
	{
		return simplify(termFactory.getDLObjectNegation(exp), termFactory);
	}


	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLDataRange<I, L, K, R> negate(
		final IDLDataRange<I, L, K, R> exp, final IDLTermFactory<I, L, K, R> termFactory)
	{
		return simplify(termFactory.getDLDataNegation(exp), termFactory);
	}


	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLNodeTerm<I, L, K, R> negate(
		final IDLNodeTerm<I, L, K, R> exp, final IDLTermFactory<I, L, K, R> termFactory)
	{
		if (exp instanceof IDLDataRange) {
			return negate((IDLDataRange<I, L, K, R>) exp, termFactory);
		} else if (exp instanceof IDLClassExpression) {
			return negate((IDLClassExpression<I, L, K, R>) exp, termFactory);
		} else
			throw new IllegalArgumentException("Unsupported term type: " + exp.getClass());
	}


	/**
	 *
	 * @param <I>         The type for nominals and values
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 * @param ds          A list of terms
	 * @param termFactory The {@link IDLTermFactory} to use for creating new terms.
	 * <p/>
	 * @return A list of terms with negations of all input terms.
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> List<IDLClassExpression<I, L, K, R>> negateAll(
		final Collection<IDLClassExpression<I, L, K, R>> ds, final IDLTermFactory<I, L, K, R> termFactory)
	{
		final List<IDLClassExpression<I, L, K, R>> subTerms = new ArrayList<>();
		for (IDLClassExpression<I, L, K, R> subTerm : ds) {
			subTerms.add(toNNF(termFactory.getDLObjectNegation(subTerm), termFactory));
		}
		return subTerms;
	}


	/**
	 *
	 * @param <I>         The type for nominals and values
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 * @param ds          A list of terms
	 * @param termFactory The {@link IDLTermFactory} to use for creating new terms.
	 * <p/>
	 * @return A list of terms with negations of all input terms.
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> List<IDLDataRange<I, L, K, R>> negateAllData(
		final Collection<IDLDataRange<I, L, K, R>> ds, final IDLTermFactory<I, L, K, R> termFactory)
	{
		final List<IDLDataRange<I, L, K, R>> subTerms = new ArrayList<>();
		for (IDLDataRange<I, L, K, R> subTerm : ds) {
			subTerms.add(toNNF(termFactory.getDLDataNegation(subTerm), termFactory));
		}
		return subTerms;
	}


	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLDataRange<I, L, K, R> toNNF(
		IDLDataRange<I, L, K, R> desc, final IDLTermFactory<I, L, K, R> termFactory)
	{
		final IDLDataRange<I, L, K, R> simpDesc = simplify(desc, termFactory);

		if (simpDesc instanceof IAtom) {
			return simpDesc;
		} else if (simpDesc instanceof IDLDataNegation) {
			final IDLDataNegation<I, L, K, R> neg = (IDLDataNegation<I, L, K, R>) simpDesc;
			if (neg.getTerm() instanceof IAtom) {
				return neg;
			} else if (neg.getTerm() instanceof IDLDataNegation) {
				final IDLDataNegation<I, L, K, R> subNeg = (IDLDataNegation<I, L, K, R>) neg.getTerm();
				return toNNF(subNeg.getTerm(), termFactory);
			} else if (neg.getTerm() instanceof IDLDataUnion) {
				/*
				 * union handling: (not (or A B)) => (and (not A) (not B))
				 */
				final IDLDataUnion<I, L, K, R> union = (IDLDataUnion<I, L, K, R>) neg.getTerm();
				final List<IDLDataRange<I, L, K, R>> newSubTerms = negateAllData(union, termFactory);
				return joinToDataIntersection(newSubTerms, termFactory);
			} else if (neg.getTerm() instanceof IDLDataIntersection) {
				/*
				 * intersection handling: (not (and A B)) => (or (not A) (not B))
				 */
				final IDLDataIntersection<I, L, K, R> intersection = (IDLDataIntersection<I, L, K, R>) neg.getTerm();
				final List<IDLDataRange<I, L, K, R>> newSubTerms = negateAllData(intersection, termFactory);
				return joinToDataUnion(newSubTerms, termFactory);
			} else
				throw new IllegalArgumentException("Unsupported term type: " + neg.getTerm().getClass());
		} else if (simpDesc instanceof IDLDataUnion) {
			final IDLDataUnion<I, L, K, R> union = (IDLDataUnion<I, L, K, R>) desc;
			final Set<IDLDataRange<I, L, K, R>> subTerms = new HashSet<>();
			for (IDLDataRange<I, L, K, R> subTerm : union) {
				final IDLDataRange<I, L, K, R> simplifiedSubTerm = toNNF(subTerm, termFactory);
				/* (or (and (A (and B C))) => (or (simp A) (simp B) (simp C)) */

				if (simplifiedSubTerm instanceof IDLDataUnion) /*
				 * (or (and (A (and B C))) => (or (simp A) (simp B) (simp C))
				 */ {
					subTerms.addAll(((IDLDataUnion<I, L, K, R>) simplifiedSubTerm));
				} else
					subTerms.add(simplifiedSubTerm);
			}
			return joinToDataUnion(subTerms, termFactory);
		} else if (simpDesc instanceof IDLDataIntersection) {
			final IDLDataIntersection<I, L, K, R> intersection = (IDLDataIntersection<I, L, K, R>) desc;
			final Set<IDLDataRange<I, L, K, R>> subTerms = new HashSet<>();
			for (IDLDataRange<I, L, K, R> subTerm : intersection) {
				final IDLDataRange<I, L, K, R> simplifiedSubTerm = toNNF(subTerm, termFactory);
				/* (or (and (A (and B C))) => (or (simp A) (simp B) (simp C)) */
				if (simplifiedSubTerm instanceof IDLDataIntersection) /*
				 * (or (and (A (and B C))) => (or (simp A) (simp B) (simp C))
				 */ {
					subTerms.addAll(((IDLDataIntersection<I, L, K, R>) simplifiedSubTerm));
				} else
					subTerms.add(simplifiedSubTerm);
			}
			return joinToDataIntersection(subTerms, termFactory);
		} else {
			throw new IllegalArgumentException("Unsupported term type: " + desc.getClass());
		}
	}


	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLDataRange<I, L, K, R> simplify(
		final IDLDataRange<I, L, K, R> desc, final IDLTermFactory<I, L, K, R> termFactory)
	{
		if (desc instanceof IAtom)
			return desc;
		else if (desc instanceof IDLDataNegation) {
			final IDLDataNegation<I, L, K, R> neg = (IDLDataNegation<I, L, K, R>) desc;
			if (neg.getTerm() instanceof IDLObjectNegation) {
				IDLDataNegation<I, L, K, R> subNeg = (IDLDataNegation<I, L, K, R>) neg.getTerm();
				return simplify(subNeg.getTerm(), termFactory);
			} else
				return termFactory.getDLDataNegation(simplify(neg.getTerm(), termFactory));
		} else if (desc instanceof IDLDataUnion) {
			final IDLDataUnion<I, L, K, R> union = (IDLDataUnion<I, L, K, R>) desc;
			final Set<IDLDataRange<I, L, K, R>> subTerms = new HashSet<>();
			for (IDLDataRange<I, L, K, R> subTerm : union) {
				final IDLDataRange<I, L, K, R> simplifiedSubTerm = simplify(subTerm, termFactory);
				if (simplifiedSubTerm instanceof IDLDataUnion) {
					/* (or (and (A (and B C))) => (or (simp A) (simp B) (simp C)) */
					subTerms.addAll(((IDLDataUnion<I, L, K, R>) simplifiedSubTerm));
				} else
					subTerms.add(simplifiedSubTerm);
			}

			return joinToDataUnion(subTerms, termFactory);
		} else if (desc instanceof IDLDataIntersection) {
			final IDLDataIntersection<I, L, K, R> intersection = (IDLDataIntersection<I, L, K, R>) desc;
			final Set<IDLDataRange<I, L, K, R>> subTerms = new HashSet<>();
			for (IDLDataRange<I, L, K, R> subTerm : intersection) {
				final IDLDataRange<I, L, K, R> simplifiedSubTerm = simplify(subTerm, termFactory);
				if (simplifiedSubTerm instanceof IDLDataUnion) {
					/* (or (and (A (and B C))) => (or (simp A) (simp B) (simp C)) */
					subTerms.addAll(((IDLDataUnion<I, L, K, R>) simplifiedSubTerm));
				} else
					subTerms.add(simplifiedSubTerm);
			}

			return joinToDataIntersection(subTerms, termFactory);
		} else {
			throw new IllegalArgumentException("Unsupported term type: " + desc.getClass());
		}
	}


	/**
	 * Transform an input term into negation normal form (NNF).
	 *
	 * @param <I>         The type for nominals and values
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 * @param simpDesc    A term
	 * @param termFactory The {@link IDLTermFactory} to use for creating new terms.
	 * <p/>
	 * @return A transformation of the input term into Negation Normal Form.
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLClassExpression<I, L, K, R> toNNF(
		IDLClassExpression<I, L, K, R> desc, final IDLTermFactory<I, L, K, R> termFactory)
	{
		final IDLClassExpression<I, L, K, R> simpDesc = simplify(desc, termFactory);

		if (simpDesc instanceof IAtom) /*
		 * do we need to return a copy?
		 */ {
			return simpDesc;
		} else if (simpDesc instanceof IDLImplies) {
			/*
			 * transform GCIs into unions
			 */
			final IDLImplies<I, L, K, R> scRes = (IDLImplies<I, L, K, R>) simpDesc;
			final IDLObjectUnion<I, L, K, R> union = termFactory.getDLObjectUnion(
				termFactory.getDLObjectNegation(scRes.getSubDescription()),
				scRes.getSuperDescription());
			return toNNF(union, termFactory);
		} else if (simpDesc instanceof IDLObjectNegation) {
			IDLObjectNegation<I, L, K, R> neg = (IDLObjectNegation<I, L, K, R>) simpDesc;
			if (neg.getTerm() instanceof IAtom) /*
			 * for negations of atoms, simply return the original term
			 */ {
				return neg;
			} else if (neg.getTerm().equals(termFactory.getDLThing())) /*
			 * special case: not Thing => Nothing
			 */ {
				return termFactory.getDLNothing();
			} else if (neg.getTerm().equals(termFactory.getDLNothing())) /*
			 * special case: not Nothing => Thing
			 */ {
				return termFactory.getDLThing();
			} else if (neg.getTerm() instanceof IDLObjectUnion) {
				/*
				 * union handling: (not (or A B)) => (and (not A) (not B))
				 */
				final IDLObjectUnion<I, L, K, R> union = (IDLObjectUnion<I, L, K, R>) neg.getTerm();
				final List<IDLClassExpression<I, L, K, R>> newSubTerms = negateAll(union, termFactory);
				return joinToIntersection(newSubTerms, termFactory);
			} else if (neg.getTerm() instanceof IDLObjectIntersection) {
				/*
				 * intersection handling: (not (and A B)) => (or (not A) (not B))
				 */
				final IDLObjectIntersection<I, L, K, R> intersection = (IDLObjectIntersection<I, L, K, R>) neg.getTerm();
				final List<IDLClassExpression<I, L, K, R>> newSubTerms = negateAll(intersection, termFactory);
				return joinToUnion(newSubTerms, termFactory);
			} else if (neg.getTerm() instanceof IDLObjectSomeRestriction) {
				/*
				 * some restrictions: (not (some r A)) => (only r (not A))
				 */
				final IDLObjectSomeRestriction<I, L, K, R> some = (IDLObjectSomeRestriction<I, L, K, R>) neg.getTerm();
				/*
				 * we could cast above, but this is less restrictive, as it actually allows for different role types
				 */
				final IDLObjectAllRestriction<I, L, K, R> all = termFactory.getDLObjectAllRestriction(some.getRole(),
																									  toNNF(
					termFactory.getDLObjectNegation(some.getTerm()), termFactory));
				return all;
			} else if (neg.getTerm() instanceof IDLObjectAllRestriction) {
				/*
				 * all restrictions: (not (only r A)) => (some r (not * A))
				 */
				final IDLObjectAllRestriction<I, L, K, R> all = (IDLObjectAllRestriction<I, L, K, R>) neg.getTerm();
				/**
				 * we could include <R> in the cast above, but this is less restrictive, as it actually allows for
				 * different role types
				 *
				 */
				@SuppressWarnings("unchecked")
				final IDLObjectSomeRestriction<I, L, K, R> some = termFactory.getDLObjectSomeRestriction(all.getRole(),
																										 toNNF(
					termFactory.getDLObjectNegation(all.getTerm()), termFactory));
				return some;
			} else if (neg.getTerm() instanceof IDLDataSomeRestriction) {
				final IDLDataSomeRestriction<I, L, K, R> some = (IDLDataSomeRestriction<I, L, K, R>) neg.getTerm();
				final IDLDataAllRestriction<I, L, K, R> all = termFactory.getDLDataAllRestriction(some.getRole(),
																								  toNNF(
					termFactory.getDLDataNegation(some.getTerm()), termFactory));
				return all;

			} else if (neg.getTerm() instanceof IDLDataAllRestriction) {
				final IDLDataAllRestriction<I, L, K, R> all = (IDLDataAllRestriction<I, L, K, R>) neg.getTerm();
				final IDLDataSomeRestriction<I, L, K, R> some = termFactory.getDLDataSomeRestriction(all.getRole(),
																									 toNNF(
					termFactory.getDLDataNegation(all.getTerm()), termFactory));
				return some;
			} else if (neg.getTerm() instanceof IDLImplies) {
				/*
				 * (not (implies A B)) = (not (or (not A) B)) = (and A (not B))
				 */
				final IDLImplies<I, L, K, R> scRes = (IDLImplies<I, L, K, R>) neg.getTerm();
				final IDLObjectIntersection<I, L, K, R> intersection = termFactory.getDLObjectIntersection(
					toNNF(scRes.getSubDescription(), termFactory),
					toNNF(termFactory.getDLObjectNegation(scRes.getSuperDescription()), termFactory));
				return intersection;
			} else if (neg.getTerm() instanceof IDLObjectNegation) {
				return toNNF(((IDLObjectNegation<I, L, K, R>) neg.getTerm()).getTerm(), termFactory);
			} else {
				throw new IllegalArgumentException("Unsupported term type: " + neg.getTerm().getClass());
			}

			/**
			 * end negation handling
			 *
			 * For all other terms, we recursively simplify nested terms, only.
			 *
			 */
		} else if (simpDesc instanceof IDLObjectUnion) {
			final IDLObjectUnion<I, L, K, R> union = (IDLObjectUnion<I, L, K, R>) simpDesc;
			final List<IDLClassExpression<I, L, K, R>> newTerms = allToNNF(union, termFactory);
			return termFactory.getDLObjectUnion(newTerms);
		} else if (simpDesc instanceof IDLObjectIntersection) {
			final IDLObjectIntersection<I, L, K, R> intersection = (IDLObjectIntersection<I, L, K, R>) simpDesc;
			final List<IDLClassExpression<I, L, K, R>> newTerms = allToNNF(intersection, termFactory);
			return termFactory.getDLObjectIntersection(newTerms);
		} else if (simpDesc instanceof IDLObjectSomeRestriction) {
			final IDLObjectSomeRestriction<I, L, K, R> some = (IDLObjectSomeRestriction<I, L, K, R>) simpDesc;
			final IDLClassExpression<I, L, K, R> t = toNNF(some.getTerm(), termFactory);
			return termFactory.getDLObjectSomeRestriction(some.getRole(), t);
		} else if (simpDesc instanceof IDLObjectAllRestriction) {
			final IDLObjectAllRestriction<I, L, K, R> all = (IDLObjectAllRestriction<I, L, K, R>) simpDesc;
			final IDLClassExpression<I, L, K, R> t = toNNF(all.getTerm(), termFactory);
			return termFactory.getDLObjectAllRestriction(all.getRole(), t);
		} else if (simpDesc instanceof IDLDataSomeRestriction) {
			final IDLDataSomeRestriction<I, L, K, R> some = (IDLDataSomeRestriction<I, L, K, R>) simpDesc;
			final IDLDataRange<I, L, K, R> t = toNNF(some.getTerm(), termFactory);
			return termFactory.getDLDataSomeRestriction(some.getRole(), t);
		} else if (simpDesc instanceof IDLDataAllRestriction) {
			final IDLDataAllRestriction<I, L, K, R> all = (IDLDataAllRestriction<I, L, K, R>) simpDesc;
			final IDLDataRange<I, L, K, R> t = toNNF(all.getTerm(), termFactory);
			return termFactory.getDLDataAllRestriction(all.getRole(), t);
		} else if (simpDesc instanceof IDLIndividualReference) {
			return simpDesc;
		} else {
			throw new IllegalArgumentException("Unsupported term type: " + simpDesc.getClass());
		}
	}


	/**
	 * Do a syntactic check if {@literal desc1} and {@literal desc2} are negations of each other. <p /> {@literal true}
	 * is returned if {@literal desc1} is a negation of {@literal desc2}. A return value of {@literal false} does not
	 * mean, that the terms are not contradictory, just that it could not be verified syntactically.
	 *
	 * @param <I>         The type for nominals and values
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 * @param desc1
	 * @param desc2
	 * @param termFactory
	 *                       <p/>
	 * @return {@literal true} is returned if {@literal desc1} is a negation of {@literal desc2}. {@literal false} if
	 *            not clash could be found (syntactically).
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> boolean isSyntacticNegation(
		final IDLClassExpression<I, L, K, R> desc1, final IDLClassExpression<I, L, K, R> desc2,
		final IDLTermFactory<I, L, K, R> termFactory)
	{
		if (desc1.equals(desc2)) {
			return false;
		} else if (desc1 instanceof IDLObjectNegation) {
			/*
			 * is desc1 the negation of desc2?
			 */
			if (((IDLObjectNegation<I, L, K, R>) desc1).getTerm().equals(desc2)) {
				return true;
			}
		} else if (desc2 instanceof IDLObjectNegation) {
			/*
			 * is desc2 the negation of desc1?
			 */
			if (((IDLObjectNegation<I, L, K, R>) desc2).getTerm().equals(desc1)) {
				return true;
			}
		}

		/*
		 * no direct negations: convert to negation normal form and try again
		 */
		final IDLClassExpression<I, L, K, R> nnf1 = toNNF(desc1, termFactory);
		final IDLClassExpression<I, L, K, R> nnf2 = toNNF(desc2, termFactory);
		if (nnf1 instanceof IDLObjectNegation) {
			if (((IDLObjectNegation<I, L, K, R>) nnf1).getTerm().equals(nnf2)) {
				return true;
			}
		} else if (nnf2 instanceof IDLObjectNegation) {
			if (((IDLObjectNegation<I, L, K, R>) nnf2).getTerm().equals(nnf1)) {
				return true;
			}
		}
		/*
		 * no direct negation found, don't know => return false
		 */
		return false;
	}


	/**
	 *
	 * Test, if {@literal presumedSub} is subclass of {@literal presumedSuper} using only syntactic tests.
	 * <p />
	 * If the returned value is {@literal true}, {@literal presumedSub} is a subclass of {@literal presumedSub}.
	 * {@literal false} means that the relationship between {@literal presumedSub} and {@literal presumedSuper} could
	 * not be properly determined (and full reasoning is required to do this).
	 *
	 *
	 * @param <I>           The type for nominals and values
	 * @param <K>           The type for DL classes
	 * @param <R>           The type for properties (roles)
	 * @param presumedSub   The presumed subclass
	 * @param presumedSuper The presumed superclass
	 * @param termFactory   The {@link IDLTermFactory} to use
	 * <p/>
	 * @return {@literal true} if {@literal presumedSub} is a subclass of {@literal presumedSub}. {@literal false}, if
	 *            not or if the relationship between both could not be determined.
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> boolean isSyntacticSubClass(
		final IDLClassExpression<I, L, K, R> presumedSub, final IDLClassExpression<I, L, K, R> presumedSuper,
		final IDLTermFactory<I, L, K, R> termFactory)
	{
		if (presumedSub.equals(presumedSuper)) {
			return true;
		}

		final IDLClassExpression<I, L, K, R> subNNF = simplify(presumedSub, termFactory);
		final IDLClassExpression<I, L, K, R> superNNF = simplify(presumedSuper, termFactory);

		if (subNNF instanceof IDLObjectUnion) {
			final IDLObjectUnion<I, L, K, R> subUnion = (IDLObjectUnion<I, L, K, R>) presumedSub;
			if (subUnion.contains(superNNF)) {
				return true;
			}
		}

		if (superNNF instanceof IDLObjectIntersection) {
			final IDLObjectIntersection<I, L, K, R> superIntersection = (IDLObjectIntersection<I, L, K, R>) superNNF;
			if (superIntersection.contains(presumedSub)) {
				return true;
			} else if (subNNF instanceof IDLObjectIntersection) {
				final IDLObjectIntersection<I, L, K, R> subIntersection = (IDLObjectIntersection<I, L, K, R>) subNNF;
				return superIntersection.containsAll(subIntersection);
			}
			if (subNNF instanceof IDLObjectUnion) {
				final IDLObjectUnion<I, L, K, R> subUnion = (IDLObjectUnion<I, L, K, R>) presumedSub;
				return CollectionUtil.containsOne(superIntersection, subUnion);
			}
			return false;
		} else {
			return false;
		}
	}


	/**
	 * Compare two term lists by comparing their subterms in order. <p />
	 * The comparison of the first non-equal subterm decides. If the first {@literal min(tl0.size(), tl1.size())} of
	 * both term lists are equal, the term length is used to decide.
	 *
	 * @param <Term> The subterm type, must derive from {@link IDLTerm}.
	 * @param tl0    The first term list
	 * @param tl1    The second term list
	 * <p/>
	 * @return {@literal -1}, if {@literal tl0 &lt; tl1}, {@literal 0}, if {@literal tl0.equals(tl1)} and {@literal 1},
	 *            if {@literal tl0 &gt; tl1}.
	 */
	public static <Term extends Comparable<? super Term>> int compareTermList(
		final List<Term> tl0, final List<? extends Term> tl1)
	{
		int minSize = Math.min(tl0.size(), tl1.size());
		int compare = 0;
		/*
		 * compareTermList for common initial subterms
		 */
		for (int i = 0; (compare == 0) && (i < minSize); ++i) {
			compare = tl0.get(i).compareTo(tl1.get(i));
		}
		/*
		 * common subterms match, term length decides
		 */
		if (compare == 0) {
			compare = tl1.size() - tl0.size();
		}

		if (compare < 0) {
			return -1;
		} else if (compare > 0) {
			return 1;
		} else {
			return 0;
		}
	}


	/**
	 * Create a union of the specified subterms. If there is only one subterm, return it directly.
	 *
	 * @param <I>         The type for nominals and values
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 * @param subTerms    List of subterms for the new term.
	 * @param termFactory
	 *                       <p/>
	 * @return
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLClassExpression<I, L, K, R> joinToUnion(
		final Collection<IDLClassExpression<I, L, K, R>> subTerms,
		final IDLTermFactory<I, L, K, R> termFactory)
	{
		if (subTerms.size() > 1) {
			return termFactory.getDLObjectUnion(subTerms);
		} else {
			return subTerms.iterator().next();
		}
	}


	/**
	 * Create a union of the specified subterms. If there is only one subterm, return it directly.
	 *
	 * @param <I>         The type for nominals and values
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 * @param subTerms    List of subterms for the new term.
	 * @param termFactory
	 *                       <p/>
	 * @return
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLDataRange<I, L, K, R> joinToDataUnion(
		final Collection<IDLDataRange<I, L, K, R>> subTerms, final IDLTermFactory<I, L, K, R> termFactory)
	{
		if (subTerms.size() > 1) {
			return termFactory.getDLDataUnion(subTerms);
		} else {
			return subTerms.iterator().next();
		}
	}


	/**
	 * Create a union of the specified subterms. If there is only one subterm, return it directly.
	 *
	 * @param <I>         The type for nominals and values
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 * @param subTerms    List of subterms for the new term.
	 * @param termFactory
	 *                       <p/>
	 * @return
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLDataRange<I, L, K, R> joinToDataIntersection(
		final Collection<IDLDataRange<I, L, K, R>> subTerms, final IDLTermFactory<I, L, K, R> termFactory)
	{
		if (subTerms.size() > 1) {
			return termFactory.getDLDataIntersection(subTerms);
		} else {
			return subTerms.iterator().next();
		}
	}


	/**
	 * Create an intersection of the specified subterms. If there is only one subterm, return it directly.
	 *
	 * @param <I>         The type for nominals and values
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 * @param subTerms    List of subterms for the new term.
	 * @param termFactory
	 *                       <p/>
	 * @return
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLClassExpression<I, L, K, R> joinToIntersection(
		final Collection<IDLClassExpression<I, L, K, R>> subTerms, final IDLTermFactory<I, L, K, R> termFactory)
	{
		if (subTerms.size() == 1) {
			return subTerms.iterator().next();
		} else {
			List<IDLClassExpression<I, L, K, R>> terms = new ArrayList<>(subTerms.
				size());
			for (IDLClassExpression<I, L, K, R> subTerm : subTerms) {
				if (subTerm instanceof IDLObjectIntersection) {
					terms.addAll((IDLObjectIntersection<I, L, K, R>) subTerm);
				} else {
					terms.add(subTerm);
				}
			}
			if (terms.size() > 1) {
				return termFactory.getDLObjectIntersection(terms);
			} else {
				return terms.get(0);
			}
		}
	}


	/**
	 * Create an intersection of two terms, possible folding multiple layers of intersections.
	 *
	 * @param <I>         The type for nominals and values
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 * @param first       First term
	 * @param second      Second term
	 * @param termFactory
	 *                       <p/>
	 * @return
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> IDLClassExpression<I, L, K, R> joinToIntersection(
		final IDLClassExpression<I, L, K, R> first, final IDLClassExpression<I, L, K, R> second,
		final IDLTermFactory<I, L, K, R> termFactory)
	{
		final ArrayList<IDLClassExpression<I, L, K, R>> list = new ArrayList<>(2);
		list.add(first);
		list.add(second);
		list.trimToSize();
		return joinToIntersection(list, termFactory);
	}


	/**
	 * Collect all subterms of a certain type
	 *
	 * @param <I>        The type for nominals and values
	 * @param <K>        The type for DL classes
	 * @param <R>        The type for properties (roles)
	 * @param <Term>     The type of the term to extract
	 * @param targetTerm The target term to extract from
	 * @param termType   The class/type of the terms to extract
	 * @param targetSet  The set to add results to.
	 */
	@SuppressWarnings("unchecked")
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>, Term extends IDLTerm<I, L, K, R>> void collectSubTerms(
		final IDLTerm<I, L, K, R> targetTerm, final Class<Term> termType, final Set<? super Term> targetSet)
	{
		if (termType.isInstance(targetTerm)) {
			targetSet.add((Term) targetTerm);
		}
		if (targetTerm instanceof ITermList) {
			for (Object subTerm : (ITermList) targetTerm) {
				if (subTerm instanceof IDLTerm) {
					collectSubTerms((IDLTerm<I, L, K, R>) subTerm, termType, targetSet);
				}
			}
		}
	}


	/**
	 * Collect all subterms of a certain type
	 *
	 * @param <I>        The type for nominals and values
	 * @param <K>        The type for DL classes
	 * @param <R>        The type for properties (roles)
	 * @param <Term>     The type of the term to extract
	 * @param targetTerm The target term to extract from
	 * @param termType   The class/type of the terms to extract
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>, Term extends IDLTerm<I, L, K, R>> Set<Term> collectSubTerms(
		final IDLTerm<I, L, K, R> targetTerm, final Class<Term> termType)
	{
		final Set<Term> targetTerms = new HashSet<>();
		collectSubTerms(targetTerm, termType, targetTerms);
		return targetTerms;
	}


	/**
	 * @param <I>         The type for nominals and values
	 * @param <K>         The type for DL classes
	 * @param <R>         The type for properties (roles)
	 * @param ds          A list of terms.
	 * @param termFactory The {@link IDLTermFactory} to use for creating new terms.
	 * <p/>
	 * @return A list of terms where in Negation Normal Form.
	 *
	 */
	static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> List<IDLClassExpression<I, L, K, R>> allToNNF(
		final Collection<IDLClassExpression<I, L, K, R>> ds, final IDLTermFactory<I, L, K, R> termFactory)
	{
		final List<IDLClassExpression<I, L, K, R>> subTerms = new ArrayList<>();
		for (IDLClassExpression<I, L, K, R> subTerm : ds) {
			subTerms.add(toNNF(subTerm, termFactory));
		}
		return subTerms;
	}
}
