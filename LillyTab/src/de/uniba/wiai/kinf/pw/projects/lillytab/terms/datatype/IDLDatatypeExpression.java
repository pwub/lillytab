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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.datatype;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface IDLDatatypeExpression<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends IDLRestriction<Name, Klass, Role>
{
	/**
	 * Determine, if the specified individual is
	 * a valid value for the specified datatype.
	 *
	 * @param individual the individual to validate.
	 * @return
	 */
	public boolean isValidValue(final Name individual);

	/**
	 * Determine, if the specified values are compatible with
	 * each other under the constraints of the current data type.
	 *
	 * @param ind1 First value
	 * @param ind2 Second value
	 * @return {@literal true} if the values are compatible {@literal false}, if
	 *   both values cannot refer to the same individual.
	 */
	public boolean isCompatibleValue(final Name ind1, final Name ind2, final Name... otherInds);

	/**
	 * Determine, if the specified values are compatible with
	 * each other under the constraints of the current data type.
	 *
	 * @param ind Individuals
	 * @return {@literal true} if the values are compatible {@literal false}, if
	 *   both values cannot refer to the same individual.
	 */
	public boolean isCompatibleValue(final Collection<? extends Name> inds);
}
