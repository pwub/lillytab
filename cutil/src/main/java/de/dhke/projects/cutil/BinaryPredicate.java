/**
 * (c) 2009-2012 Peter Wullinger
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
package de.dhke.projects.cutil;

import org.apache.commons.collections15.Predicate;

/**
 * 
 * Binary version of {@link Predicate}
 * 
 * 
 * @param <First> Type of the first parameter
 * @param <Second> Type of the second parameter
 * @author Peter Wullinger <java@dhke.de>
 */
public interface BinaryPredicate<First, Second> {
	/**
	 * 
	 * Perform an evaluation using two parameters.
	 * 
	 * @param first The first parameter
	 * @param second The second parameter.
	 * @return {@literal true} if the evaluation was successful, {@literal false} otherwise.
	 */
	boolean evalute(final First first, final Second second);
}
