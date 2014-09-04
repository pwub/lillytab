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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange;

import java.util.Collection;
import java.util.Set;


/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface IDLDatatypeExpression<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends IDLDataRange<I, L, K, R>
{
	/**
	 * Determine, if the specified literal is a valid value for the specified datatype.
	 *
	 * @param individual the literal to validate.
	 * @return
	 */
	public boolean isValidValue(final L literal);

	/**
	 * Determine, if the specified values are compatible with each other under the constraints of the current data type.
	 *
	 * @param lit1 First value
	 * @param lit2 Second value
	 * @return {@literal true} if the values are compatible {@literal false}, if both values cannot refer to the same
	 * individual.
	 *
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public boolean isCompatibleValue(final L lit1, final L lit2, final L... otherLits);

	/**
	 * Determine, if the specified values are compatible with each other under the constraints of the current data type.
	 *
	 * @param lits literal values.
	 * @return A set of sets of mutually incompatible values. An empty set is returned if all values are compatible.
	 */
	public Set<Set<L>> getIncompatibleValues(final Collection<? extends L> lits);
}
