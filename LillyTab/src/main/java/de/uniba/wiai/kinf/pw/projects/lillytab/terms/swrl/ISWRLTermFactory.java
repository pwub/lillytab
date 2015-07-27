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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import java.util.Collection;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface ISWRLTermFactory<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> {

	ISWRLIndividualReference<I, L, K, R> getSWRLIndividualReference(I individual);


	ISWRLLiteralReference<I, L, K, R> getSWRLLiteralReference(L literal);


	ISWRLClassAtom<I, L, K, R> getSWRLClassAtom(K klass, ISWRLIArgument<I, L, K, R> individual);


	ISWRLObjectRoleAtom<I, L, K, R> getSWRLObjectRoleAtom(R role, ISWRLIArgument<I, L, K, R> source,
														  ISWRLIArgument<I, L, K, R> target);


	ISWRLDataRoleAtom<I, L, K, R> getSWRLDataRoleAtom(R role, ISWRLIArgument<I, L, K, R> source,
													  ISWRLDArgument<I, L, K, R> target);


	ISWRLIntersection<I, L, K, R> getSWRLIntersection(Collection<ISWRLAtomicTerm<I, L, K, R>> atoms);


	ISWRLIntersection<I, L, K, R> getSWRLIntersection(ISWRLAtomicTerm<I, L, K, R> atom1,
													  ISWRLAtomicTerm<I, L, K, R> atom2);


	ISWRLRule<I, L, K, R> getSWRLRule(ISWRLTerm<I, L, K, R> head, ISWRLTerm<I, L, K, R> body);


	ISWRLVariable<I, L, K, R> getSWRLVariable(String name);


	ISWRLDataRangeAtom<I, L, K, R> getSWRLDataRange(final IDLDataRange<I, L, K, R> range,
												final ISWRLDArgument<I, L, K, R> individual);
}
