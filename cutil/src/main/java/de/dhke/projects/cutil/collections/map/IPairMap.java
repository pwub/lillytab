/**
 * (c) 2009-2014 Peter Wullinger
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
package de.dhke.projects.cutil.collections.map;

import de.dhke.projects.cutil.Pair;
import java.util.Map;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public interface IPairMap<First, Second, Tag>
	extends Map<Pair<First, Second>, Tag>
{
	boolean containsKey(final First first, final Second second);

	/**
	 * Retrieve the element from the map represented by the compound key (first, second).
	 * 
	 * @param first The first component of the key
	 * @param second The second component of the key
	 * @return The value 
	 */
	Tag get(final First first, final Second second);
	/**
	 * Remove the map element specified by the key (first, second).
	 * <p />
	 * This used to be called only remove(), but Java 8 introduced
	 * {@link #remove(java.lang.Object, java.lang.Object) to remove a specific key-value pair.
	 * <p />
	 * @param first The first component of the key
	 * @param second The second component of the key
	 * @return 
	 */
	Tag removePair(final First first, final Second second);
	
	/**
	 * Put the item with (first, second) as a key and tag 
	 * 
	 * @param first
	 * @param second
	 * @param tag
	 * @return 
	 */
	Tag put(final First first, final Second second, final Tag tag);
}
