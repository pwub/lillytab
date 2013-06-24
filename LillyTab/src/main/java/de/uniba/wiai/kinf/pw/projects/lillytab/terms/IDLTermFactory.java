/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDatatype;
import java.util.Collection;


/**
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 *
 */
public interface IDLTermFactory<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
{
	/**
	 * @param klass The klass to reference
	 * @return A named class reference to the specified class.
	 */
	IDLClassReference<I, L, K, R> getDLClassReference(K klass);

	/**
	 * @return This {@link IDLTermFactory}'s implementation of _Thing_, the set of all individuals.
	 */
	IDLClassReference<I, L, K, R> getDLThing();

	/**
	 * @return This {@link IDLTermFactory}'s implementation of _Nothing_, the set of no individuals.
	 */
	IDLClassReference<I, L, K, R> getDLNothing();

	/**
	 * @param d The expression to negate.
	 * @return The negation of {@literal d}
	 */
	IDLObjectNegation<I, L, K, R> getDLObjectNegation(IDLClassExpression<I, L, K, R> d);

	IDLObjectIntersection<I, L, K, R> getDLObjectIntersection(IDLClassExpression<I, L, K, R> d0,
															  IDLClassExpression<I, L, K, R> d1);

	IDLObjectIntersection<I, L, K, R> getDLObjectIntersection(Collection<? extends IDLClassExpression<I, L, K, R>> ds);

	IDLObjectUnion<I, L, K, R> getDLObjectUnion(IDLClassExpression<I, L, K, R> d0, IDLClassExpression<I, L, K, R> d1);

	IDLObjectUnion<I, L, K, R> getDLObjectUnion(Collection<? extends IDLClassExpression<I, L, K, R>> ds);

	IDLObjectSomeRestriction<I, L, K, R> getDLObjectSomeRestriction(R role, IDLClassExpression<I, L, K, R> d);

	IDLObjectAllRestriction<I, L, K, R> getDLObjectAllRestriction(R role, IDLClassExpression<I, L, K, R> d);

	IDLImplies<I, L, K, R> getDLImplies(IDLClassExpression<I, L, K, R> sub,
										IDLClassExpression<I, L, K, R> sup);

	IDLIndividualReference<I, L, K, R> getDLIndividualReference(I individual);

	IDLLiteralReference<I, L, K, R> getDLLiteralReference(L Literal);

	/**
	 * Datatype handling
	 *
	 */
	/**
	 * @return The datatype implementation of the most generic datatype.
	 */
	IDLDatatype<I, L, K, R> getDLTopDatatype();
	
	/**
	 * Get the negation of the supplied {@link IDLDataRange}.
	 *
	 * @param d The data expression to negate
	 * @return The negation of {@literal d}.
	 */
	IDLDataNegation<I, L, K, R> getDLDataNegation(final IDLDataRange<I, L, K, R> d);

	IDLDataUnion<I, L, K, R> getDLDataUnion(IDLDataRange<I, L, K, R> d0, IDLDataRange<I, L, K, R> d1);

	IDLDataUnion<I, L, K, R> getDLDataUnion(Collection<? extends IDLDataRange<I, L, K, R>> ds);

	IDLDataIntersection<I, L, K, R> getDLDataIntersection(IDLDataRange<I, L, K, R> d0,
														  IDLDataRange<I, L, K, R> d1);

	IDLDataIntersection<I, L, K, R> getDLDataIntersection(Collection<? extends IDLDataRange<I, L, K, R>> ds);

	IDLDataSomeRestriction<I, L, K, R> getDLDataSomeRestriction(R role, IDLDataRange<I, L, K, R> d);

	IDLDataAllRestriction<I, L, K, R> getDLDataAllRestriction(R role, IDLDataRange<I, L, K, R> d);
}
