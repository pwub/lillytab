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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms;

import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;

/**
 * 
 * Base interface for all DL related {@link ITerm}s.
 * 
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface IDLTerm<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends ITerm, Comparable<IDLTerm<I, L, K, R>> {

	/**
	 * @return A dummy term that sorts BEFORE all terms of the current type.
	 *
	 */
	IDLTerm<I, L, K, R> getBefore();


	/**
	 * @return A dummy term that sorts AFTER all terms of the current type.
	 *
	 */
	IDLTerm<I, L, K, R> getAfter();


	DLTermOrder getDLTermOrder();


	/**
	 * Convert the current term into a String using the provided {
	 *
	 * @see IToStringFormatter} to format external entities (OWL objects).
	 *
	 * @param entityFormatter
	 * @return
	 */
	String toString(final IToStringFormatter entityFormatter);
}
