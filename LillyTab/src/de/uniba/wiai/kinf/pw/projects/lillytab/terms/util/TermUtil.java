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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.util;

import de.dhke.projects.cutil.collections.CollectionUtil;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLNothing;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLThing;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITermList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections15.SetUtils;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TermUtil {

	private TermUtil()
	{
	}

	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Set<IDLTerm<Name, Klass, Role>> unfoldIntersections(
		final IDLTerm<Name, Klass, Role> sourceDesc, final IDLTermFactory<Name, Klass, Role> termFactory)
	{
		Set<IDLTerm<Name, Klass, Role>> targetSet = new HashSet<IDLTerm<Name, Klass, Role>>();
		if (sourceDesc instanceof IDLIntersection) {
			for (IDLRestriction<Name, Klass, Role> subDesc : (IDLIntersection<Name, Klass, Role>) sourceDesc)
				targetSet.addAll(unfoldIntersections(simplify(subDesc, termFactory), termFactory));
		} else
			targetSet.add(sourceDesc);
		return targetSet;
	}

	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> Set<IDLTerm<Name, Klass, Role>> unfoldIntersections(
		final Set<IDLTerm<Name, Klass, Role>> sourceSet, final IDLTermFactory<Name, Klass, Role> termFactory)
	{
		Set<IDLTerm<Name, Klass, Role>> targetSet = new HashSet<IDLTerm<Name, Klass, Role>>(sourceSet.size());
		for (IDLTerm<Name, Klass, Role> sourceDesc : sourceSet)
			targetSet.addAll(unfoldIntersections(sourceDesc, termFactory));
		return targetSet;
	}

	/**
	 * <p>
	 * Try to simplify the specified term. The exact results of the simplification are undefined,
	 * but the returned term is required to be a possibly transformed, but semantically equivalent
	 * representation of the input term.
	 * </p><p>
	 * The current implementation tries to reduce the complexity of the incoming
	 * terms to make more efficient tableaux reasoning possible. The result is <emph>NOT</emph>
	 * necessarily a term in negation normal form. If NNF is required, 
	 * use {@link #toNNF(de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction, de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory) ).
	 * </p><p>
	 * 
	 * @param <Name> The name type of the description
	 * @param <Klass> The concept type of the description
	 * @param <Role> The role type of the description
	 * @param desc The {@link IDLClassExpression} to simplify
	 * @param termFactory The {@link IDLTermFactory} to use for creating new terms.
	 * @return A term representing a simplified version of the input term.
	 */
	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> IDLRestriction<Name, Klass, Role> simplify(
		final IDLRestriction<Name, Klass, Role> desc, final IDLTermFactory<Name, Klass, Role> termFactory)
	{
		if (desc instanceof IDLImplies) {
			IDLImplies<Name, Klass, Role> implies = (IDLImplies<Name, Klass, Role>) desc;
			if (implies.getSubDescription() instanceof DLThing)
				/* (simp (implies _Thing_ A)) => (simp A) */
				return simplify(implies.getSuperDescription(), termFactory);
			else if (implies.getSubDescription() instanceof DLNothing)
				/* (simp (implies _Nothing_ A)) => _Thing_ */
				return termFactory.getDLThing();
			else if (implies.getSubDescription().equals(implies.getSuperDescription()))
				/* (simp (implies A A) => _Thing_ */
				return termFactory.getDLThing();
			else if (implies.getSubDescription() instanceof IDLUnion) {
				/* (simp (implies (or A B) C) => (and (implies (simp A) (simp C)) (implies (simp B) (simp C))) */
				final IDLUnion<Name, Klass, Role> subUnion = (IDLUnion<Name, Klass, Role>)implies.getSubDescription();
				final IDLRestriction<Name, Klass, Role> supTerm = simplify(implies.getSuperDescription(), termFactory);
				final Set<IDLRestriction<Name, Klass, Role>> newTerms = new HashSet<IDLRestriction<Name, Klass, Role>>();
				for (IDLRestriction<Name, Klass, Role> subTerm: subUnion) {					
					final IDLRestriction<Name, Klass, Role> newTerm =
						simplify(termFactory.getDLImplies(
						simplify(subTerm, termFactory), supTerm), termFactory);
					newTerms.add(newTerm);
				}
				return simplify(termFactory.getDLIntersection(newTerms), termFactory);			
			} else
				/* (simp (implies A B)) => (implies (simp A) (simp B))) */
				return termFactory.getDLImplies(
					simplify(implies.getSubDescription(), termFactory),
					simplify(implies.getSuperDescription(), termFactory));
		} else if (desc instanceof IDLUnion) {
			IDLUnion<Name, Klass, Role> union = (IDLUnion<Name, Klass, Role>) desc;
			Set<IDLRestriction<Name, Klass, Role>> subTerms = new HashSet<IDLRestriction<Name, Klass, Role>>();
			for (IDLRestriction<Name, Klass, Role> subTerm : union) {
				if (subTerms.contains(termFactory.getDLThing()))
					return termFactory.getDLThing();
				else {
					IDLRestriction<Name, Klass, Role> simplifiedSubTerm = simplify(subTerm, termFactory);
					if (simplifiedSubTerm instanceof IDLUnion)
						/* (or (and (A (and B C))) => (or (simp A) (simp B) (simp C)) */
						subTerms.addAll(((IDLUnion<Name, Klass, Role>) simplifiedSubTerm));
					else if (!(simplifiedSubTerm instanceof DLNothing))
						subTerms.add(simplifiedSubTerm);
				}
			}
			return joinToUnion(subTerms, termFactory);
		} else if (desc instanceof IDLIntersection) {
			IDLIntersection<Name, Klass, Role> intersection = (IDLIntersection<Name, Klass, Role>) desc;
			Set<IDLRestriction<Name, Klass, Role>> subTerms = new HashSet<IDLRestriction<Name, Klass, Role>>();
			for (IDLRestriction<Name, Klass, Role> subTerm : intersection) {
				if (subTerms.contains(termFactory.getDLNothing()))
					return termFactory.getDLNothing();
				else {
					IDLRestriction<Name, Klass, Role> simplifiedSubTerm = simplify(subTerm, termFactory);
					if (simplifiedSubTerm instanceof IDLIntersection)
						/* (simp (and (A (and B C))) => (and (simp A) (simp B) (simp C)) */
						subTerms.addAll(((IDLIntersection<Name, Klass, Role>) simplifiedSubTerm));
					else if (!(simplifiedSubTerm instanceof DLThing))
						subTerms.add(simplifiedSubTerm);
				}
			}
			return joinToIntersection(subTerms, termFactory);
		} else if (desc instanceof IDLSomeRestriction) {
			/* (simp (some r A)) => (some r (simp A)) */
			IDLSomeRestriction<Name, Klass, Role> some = (IDLSomeRestriction<Name, Klass, Role>) desc;
			return termFactory.getDLSomeRestriction(some.getRole(), simplify(some.getTerm(), termFactory));
		} else if (desc instanceof IDLAllRestriction) {
			/* (simp (only r A)) => (only r (simp A)) */
			IDLAllRestriction<Name, Klass, Role> some = (IDLAllRestriction<Name, Klass, Role>) desc;
			return termFactory.getDLAllRestriction(some.getRole(), simplify(some.getTerm(), termFactory));
		} else if (desc instanceof IDLNegation) {
			IDLNegation<Name, Klass, Role> neg = (IDLNegation<Name, Klass, Role>) desc;
			if (neg.getTerm() instanceof IDLNegation)
				/* (simp (not (not A))) => (simp A) */
				return simplify(((IDLNegation<Name, Klass, Role>) neg.getTerm()).getTerm(), termFactory);
			else if (neg.getTerm() instanceof IDLImplies) {
				/* (simp (not (implies A B))) => (simp (and A (not B))) */
				final IDLImplies<Name, Klass, Role> implies = (IDLImplies<Name, Klass, Role>) neg.getTerm();
				final IDLIntersection<Name, Klass, Role> intersection = termFactory.getDLIntersection(implies.getSubDescription(), termFactory.getDLNegation(implies.getSuperDescription()));
				return simplify(intersection, termFactory);
			} else
				return termFactory.getDLNegation(simplify(neg.getTerm(), termFactory));
		} else
			return desc;
	}

	/**
	 *
	 * @param <Name> The name type of the description
	 * @param <Klass> The concept type of the description
	 * @param <Role> The role type of the description
	 * @param ds A list of terms
	 * @param termFactory The {@link IDLTermFactory} to use for creating new terms.
	 * @return A list of terms with negations of all input terms.
	 */
	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> List<IDLRestriction<Name, Klass, Role>> negateAll(
		final Collection<IDLRestriction<Name, Klass, Role>> ds, final IDLTermFactory<Name, Klass, Role> termFactory)
	{
		List<IDLRestriction<Name, Klass, Role>> subTerms = new ArrayList<IDLRestriction<Name, Klass, Role>>();
		for (IDLRestriction<Name, Klass, Role> subTerm : ds) {
			subTerms.add(toNNF(termFactory.getDLNegation(subTerm), termFactory));
		}
		return subTerms;
	}

	/**
	 * @param <Name> The name type of the description
	 * @param <Klass> The concept type of the description
	 * @param <Role> The role type of the description
	 * @param ds A list of terms.
	 * @param termFactory The {@link IDLTermFactory} to use for creating new terms.
	 * @return A list of terms where in Negation Normal Form.
	 **/
	static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> List<IDLRestriction<Name, Klass, Role>> allToNNF(
		final Collection<IDLRestriction<Name, Klass, Role>> ds, final IDLTermFactory<Name, Klass, Role> termFactory)
	{
		List<IDLRestriction<Name, Klass, Role>> subTerms = new ArrayList<IDLRestriction<Name, Klass, Role>>();
		for (IDLRestriction<Name, Klass, Role> subTerm : ds) {
			subTerms.add(toNNF(subTerm, termFactory));
		}
		return subTerms;
	}

	/**
	 * Transform an input term into negation normal form (NNF).
	 *
	 * @param <Name> The name type of the description
	 * @param <Klass> The concept type of the description
	 * @param <Role> The role type of the description
	 * @param desc A term
	 * @param termFactory The {@link IDLTermFactory} to use for creating new terms.
	 * @return A transformation of the input term into Negation Normal Form.
	 */
	// @SuppressWarnings("unchecked")
	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> IDLRestriction<Name, Klass, Role> toNNF(
		IDLRestriction<Name, Klass, Role> desc, final IDLTermFactory<Name, Klass, Role> termFactory)
	{
		desc = simplify(desc, termFactory);
		
		IDLRestriction<Name, Klass, Role> returnValue = null;
		if (desc instanceof IAtom)
			/* do we need to return a copy? */
			returnValue = desc;
		else if (desc instanceof IDLImplies) {
			/* transform GCIs into unions */
			IDLImplies<Name, Klass, Role> scRes = (IDLImplies<Name, Klass, Role>) desc;
			IDLUnion<Name, Klass, Role> union = termFactory.getDLUnion(
				termFactory.getDLNegation(scRes.getSubDescription()),
				scRes.getSuperDescription());
			return toNNF(union, termFactory);
		} else if (desc instanceof IDLNegation) {
			IDLNegation<Name, Klass, Role> neg = (IDLNegation<Name, Klass, Role>) desc;
			if (neg.getTerm() instanceof IAtom)
				/* for negations of atoms, simply return the original term */
				return neg;
			else if (neg.getTerm() instanceof DLThing)
				/* special case: not Thing => Nothing */
				return termFactory.getDLNothing();
			else if (neg.getTerm() instanceof DLNothing)
				/* special case: not Nothing => Thing */
				return termFactory.getDLThing();
			else if (neg.getTerm() instanceof IDLUnion) {
				/* union handling: (not (or A B)) => (and (not A) (not B)) */
				IDLUnion<Name, Klass, Role> union = (IDLUnion<Name, Klass, Role>) neg.getTerm();
				List<IDLRestriction<Name, Klass, Role>> newSubTerms = negateAll(union, termFactory);
				IDLIntersection<Name, Klass, Role> intersection = termFactory.getDLIntersection(newSubTerms);
				return intersection;
			} else if (neg.getTerm() instanceof IDLIntersection) {
				/* intersection handling: (not (and A B)) => (or (not A) (not B)) */
				IDLIntersection<Name, Klass, Role> intersection = (IDLIntersection<Name, Klass, Role>) neg.getTerm();
				List<IDLRestriction<Name, Klass, Role>> newSubTerms = negateAll(intersection, termFactory);
				IDLUnion<Name, Klass, Role> union = termFactory.getDLUnion(newSubTerms);
				return union;
			} else if (neg.getTerm() instanceof IDLSomeRestriction) {
				/* some restrictions: (not (some r A)) => (only r (not A)) */
				IDLSomeRestriction some = (IDLSomeRestriction<Name, Klass, Role>) neg.getTerm();
				/* we could cast above, but this is less restrictive, as it actually allows for different role types */
				@SuppressWarnings("unchecked")
				IDLAllRestriction<Name, Klass, Role> all = termFactory.getDLAllRestriction((Role) some.getRole(), toNNF(
					termFactory.getDLNegation((IDLRestriction<Name, Klass, Role>) some.getTerm()), termFactory));
				returnValue = all;
			} else if (neg.getTerm() instanceof IDLAllRestriction) {
				/* all restrictions: (not (only r A)) => (some r (not * A)) */
				IDLAllRestriction all = (IDLAllRestriction<Name, Klass, Role>) neg.getTerm();
				/**
				 * we could include <Role> in the cast above, but this is less restrictive,
				 * as it actually allows for different role types
				 **/
				@SuppressWarnings("unchecked")
				IDLSomeRestriction<Name, Klass, Role> some = termFactory.getDLSomeRestriction((Role) all.getRole(), toNNF(
					termFactory.getDLNegation((IDLRestriction<Name, Klass, Role>) all.getTerm()), termFactory));
				returnValue = some;
			} else if (neg.getTerm() instanceof IDLImplies) {
				/* (not (implies A B)) = (not (or (not A) B)) = (and A (not B)) */
				IDLImplies<Name, Klass, Role> scRes = (IDLImplies<Name, Klass, Role>) neg.getTerm();
				IDLIntersection<Name, Klass, Role> intersection = termFactory.getDLIntersection(
					toNNF(scRes.getSubDescription(), termFactory),
					toNNF(termFactory.getDLNegation(scRes.getSuperDescription()), termFactory));
				return intersection;
			} else if (neg.getTerm() instanceof IDLNegation) {
				return toNNF(((IDLNegation<Name, Klass, Role>) neg.getTerm()).getTerm(), termFactory);
			} else
				throw new IllegalArgumentException("Unsupported term type: " + neg.getTerm().getClass());

			/**
			 * end negation handling
			 *
			 * For all other terms, we recursively simplify nested
			 * terms, only.
			 **/
		} else if (desc instanceof IDLUnion) {
			final IDLUnion<Name, Klass, Role> union = (IDLUnion<Name, Klass, Role>) desc;
			List<IDLRestriction<Name, Klass, Role>> newTerms = allToNNF(union, termFactory);
			return termFactory.getDLUnion(newTerms);
		} else if (desc instanceof IDLIntersection) {
			IDLIntersection<Name, Klass, Role> intersection = (IDLIntersection<Name, Klass, Role>) desc;
			List<IDLRestriction<Name, Klass, Role>> newTerms = allToNNF(intersection, termFactory);
			return termFactory.getDLIntersection(newTerms);
		} else if (desc instanceof IDLSomeRestriction) {
			IDLSomeRestriction<Name, Klass, Role> some = (IDLSomeRestriction<Name, Klass, Role>) desc;
			IDLRestriction<Name, Klass, Role> t = toNNF(some.getTerm(), termFactory);
			return termFactory.getDLSomeRestriction(some.getRole(), t);
		} else if (desc instanceof IDLAllRestriction) {
			IDLAllRestriction<Name, Klass, Role> all = (IDLAllRestriction<Name, Klass, Role>) desc;
			IDLRestriction<Name, Klass, Role> t = toNNF(all.getTerm(), termFactory);
			return termFactory.getDLAllRestriction(all.getRole(), t);
		} else if (desc instanceof IDLNominalReference) {
			return desc;
		} else
			throw new IllegalArgumentException("Unsupported term type: " + desc.getClass());

		return returnValue;
	}

	/**
	 * <p>
	 * Do a syntactic check if {@literal desc1} and {@literal desc2}
	 * are negations of each other.
	 * </p><p>
	 * {@literal true} is returned if {@literal desc1} is a negation of {@literal desc2}.
	 * A return value of {@literal false} does not mean, that the terms are not contradictory,
	 * just that it could not be verified syntactically.
	 * </p>
	 * @param <Name>
	 * @param <Klass>
	 * @param <Role>
	 * @param desc1
	 * @param desc2
	 * @param termFactory
	 * @return {@literal true} is returned if {@literal desc1} is a negation of {@literal desc2}. {@literal false} if not clash could be found (syntactically).
	 */
	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> boolean isSyntacticNegation(
		final IDLRestriction<Name, Klass, Role> desc1,
		final IDLRestriction<Name, Klass, Role> desc2,
		final IDLTermFactory<Name, Klass, Role> termFactory)
	{
		if (desc1.equals(desc2)) {
			return false;
		} else if (desc1 instanceof IDLNegation) {
			/* is desc1 the negation of desc2? */
			if (((IDLNegation<Name, Klass, Role>) desc1).getTerm().equals(desc2))
				return true;
		} else if (desc2 instanceof IDLNegation) {
			/* is desc2 the negation of desc1? */
			if (((IDLNegation<Name, Klass, Role>) desc2).getTerm().equals(desc1))
				return true;
		}

		/* no direct negations: convert to negation normal form and try again */
		final IDLRestriction<Name, Klass, Role> nnf1 = toNNF(desc1, termFactory);
		final IDLRestriction<Name, Klass, Role> nnf2 = toNNF(desc2, termFactory);
		if (nnf1 instanceof IDLNegation) {
			if (((IDLNegation<Name, Klass, Role>) nnf1).getTerm().equals(nnf2))
				return true;
		} else if (nnf2 instanceof IDLNegation) {
			if (((IDLNegation<Name, Klass, Role>) nnf2).getTerm().equals(nnf1))
				return true;
		}
		/* no direct negation found, don't know => return false */
		return false;
	}

	/**
	 *
	 **/
	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> boolean isSyntacticSubClass(
		final IDLRestriction<Name, Klass, Role> presumedSub,
		final IDLRestriction<Name, Klass, Role> presumedSuper,
		final IDLTermFactory<Name, Klass, Role> termFactory
	)
	{
		if (presumedSub.equals(presumedSuper))
			return true;

		final IDLRestriction<Name, Klass, Role> subNNF = simplify(presumedSub, termFactory);
		final IDLRestriction<Name, Klass, Role> superNNF = simplify(presumedSuper, termFactory);

		if (subNNF instanceof IDLUnion) {
				final IDLUnion<Name, Klass, Role> subUnion = (IDLUnion<Name, Klass, Role>)presumedSub;
				if (subUnion.contains(superNNF))
					return true;
		}
		
		if (superNNF instanceof IDLIntersection) {
			final IDLIntersection<Name, Klass, Role> superIntersection = (IDLIntersection<Name, Klass, Role>)superNNF;
			if (superIntersection.contains(presumedSub))
				return true;
			else if (subNNF instanceof IDLIntersection) {
				final IDLIntersection<Name, Klass, Role> subIntersection = (IDLIntersection<Name, Klass, Role>)subNNF;
				return superIntersection.containsAll(subIntersection);
			} if (subNNF instanceof IDLUnion) {
				final IDLUnion<Name, Klass, Role> subUnion = (IDLUnion<Name, Klass, Role>)presumedSub;
				return CollectionUtil.containsOne(superIntersection, subUnion);
			}
				return false;
		} else
			return false;
	}

	/**
	 * <p>
	 * Compare two term lists by comparing their subterms in order.
	 * </p><p>
	 * The comparison of the first non-equal subterm decides. If the first
	 * {@literal min(tl0.size(), tl1.size())} of both term lists are equal,
	 * the term length is used to decide.
	 * </p>
	 *
	 * @param <Term> The subterm type, must derive from {@link IDLTerm}.
	 * @param tl0 The first term list
	 * @param tl1 The second term list
	 * @return {@literal -1}, if {@literal tl0 &lt; tl1}, {@literal 0}, if {@literal tl0.equals(tl1)}
	 *	and {@literal 1}, if {@literal tl0 &gt; tl1}.
	 */
	public static <Term extends Comparable<? super Term>> int compareTermList(final List<Term> tl0, final List<? extends Term> tl1)
	{
		int minSize = Math.min(tl0.size(), tl1.size());
		int compare = 0;
		/* compareTermList for common initial subterms */
		for (int i = 0; (compare == 0) && (i < minSize); ++i)
			compare = tl0.get(i).compareTo(tl1.get(i));
		/* common subterms match, term length decides */
		if (compare == 0)
			compare = tl1.size() - tl0.size();

		if (compare < 0)
			return -1;
		else if (compare > 0)
			return 1;
		else
			return 0;
	}

	/**
	 * Create a union of the specified subterms.
	 * If there is only one subterm, return it directly.
	 *
	 * @param <Name>
	 * @param <Klass>
	 * @param <Role>
	 * @param subTerms List of subterms for the new term.
	 * @param termFactory
	 * @return
	 */
	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> IDLRestriction<Name, Klass, Role> joinToUnion(final Collection<IDLRestriction<Name, Klass, Role>> subTerms, final IDLTermFactory<Name, Klass, Role> termFactory)
	{
		if (subTerms.size() > 1)
			return termFactory.getDLUnion(subTerms);
		else
			return subTerms.iterator().next();
	}

	/**
	 * Create an intersection of the specified subterms.
	 * If there is only one subterm, return it directly.
	 *
	 * @param <Name>
	 * @param <Klass>
	 * @param <Role>
	 * @param subTerms List of subterms for the new term.
	 * @param termFactory
	 * @return
	 */
	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> IDLRestriction<Name, Klass, Role> joinToIntersection(final Collection<IDLRestriction<Name, Klass, Role>> subTerms, final IDLTermFactory<Name, Klass, Role> termFactory)
	{
		if (subTerms.size() > 1)
			return termFactory.getDLIntersection(subTerms);
		else
			return subTerms.iterator().next();
	}

	@SuppressWarnings("unchecked")
	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>, Term extends IDLTerm<Name, Klass, Role>> void collectSubTerms(final IDLTerm<Name, Klass, Role> targetTerm, final Class<Term> termType, final Set targetSet)
	{
		if (termType.isInstance(targetTerm))
			targetSet.add(targetTerm);
		if (targetTerm instanceof ITermList) {
			for (Object subTerm : (ITermList) targetTerm) {
				if (subTerm instanceof IDLTerm)
					collectSubTerms((IDLTerm<Name, Klass, Role>) subTerm, termType, targetSet);
			}
		}
	}

	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>, Term extends IDLTerm<Name, Klass, Role>> Set<Term> collectSubTerms(final IDLTerm<Name, Klass, Role> targetTerm, final Class<Term> termType)
	{
		final Set<Term> targetTerms = new HashSet<Term>();
		collectSubTerms(targetTerm, termType, targetTerms);
		return targetTerms;
	}
}
