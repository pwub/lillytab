/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLAtomicTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLClassAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLDArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLDataRangeAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLDataRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLObjectRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRule;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLVariable;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.SoftItemCache;
import java.util.Collection;

/**
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * <p/>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLTermFactory<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends SoftItemCache<Object>
	implements ISWRLTermFactory<I, L, K, R> {

	public SWRLTermFactory()
	{
	}

	@Override
	public ISWRLIndividualReference<I, L, K, R> getSWRLIndividualReference(final I individual)
	{
		final ISWRLIndividualReference<I, L, K, R> individualRef = new SWRLIndividualReference<>(individual);

		return updateCache(individualRef);
	}


	@Override
	public ISWRLLiteralReference<I, L, K, R> getSWRLLiteralReference(L literal)
	{
		final ISWRLLiteralReference<I, L, K, R> literalRef = new SWRLLiteralReference<>(literal);

		return updateCache(literalRef);
	}


	@Override
	public ISWRLClassAtom<I, L, K, R> getSWRLClassAtom(final K klass,
													   final ISWRLIArgument<I, L, K, R> individual)
	{
		final ISWRLClassAtom<I, L, K, R> classAtom = new SWRLClassAtom<>(klass, individual);

		return updateCache(classAtom);
	}


	@Override
	public ISWRLIntersection<I, L, K, R> getSWRLIntersection(
		final Collection<ISWRLAtomicTerm<I, L, K, R>> atoms)
	{
		final ISWRLIntersection<I, L, K, R> intersection = new SWRLIntersection<>(atoms);

		return updateCache(intersection);
	}


	@Override
	public ISWRLIntersection<I, L, K, R> getSWRLIntersection(final ISWRLAtomicTerm<I, L, K, R> atom1,
															 final ISWRLAtomicTerm<I, L, K, R> atom2)
	{
		final ISWRLIntersection<I, L, K, R> intersection = new SWRLIntersection<>(atom1, atom2);

		return updateCache(intersection);
	}


	@Override
	public ISWRLRule<I, L, K, R> getSWRLRule(final ISWRLTerm<I, L, K, R> head,
											 final ISWRLTerm<I, L, K, R> body)
	{
		final ISWRLRule<I, L, K, R> rule = new SWRLRule<>(head, body);

		return updateCache(rule);
	}


	@Override
	public ISWRLVariable<I, L, K, R> getSWRLVariable(final String name)
	{
		final ISWRLVariable<I, L, K, R> variable = new SWRLVariable<>(name);

		return updateCache(variable);
	}


	@Override
	public ISWRLObjectRoleAtom<I, L, K, R> getSWRLObjectRoleAtom(final R role,
																 final ISWRLIArgument<I, L, K, R> source,
																 final ISWRLIArgument<I, L, K, R> target)
	{
		final ISWRLObjectRoleAtom<I, L, K, R> roleAtom = new SWRLObjectRoleAtom<>(role, source, target);

		return updateCache(roleAtom);
	}


	@Override
	public ISWRLDataRoleAtom<I, L, K, R> getSWRLDataRoleAtom(R role,
															 ISWRLIArgument<I, L, K, R> source,
															 ISWRLDArgument<I, L, K, R> target)
	{
		final SWRLDataRoleAtom<I, L, K, R> roleAtom = new SWRLDataRoleAtom<>(role, source, target);

		return updateCache(roleAtom);
	}


	@Override
	public ISWRLDataRangeAtom<I, L, K, R> getSWRLDataRange(
		IDLDataRange<I, L, K, R> range,
		ISWRLDArgument<I, L, K, R> individual)
	{
		final SWRLDataRangeAtom<I, L, K, R> rangeAtom = new SWRLDataRangeAtom<>(range, individual);
		return updateCache(rangeAtom);
	}
}
