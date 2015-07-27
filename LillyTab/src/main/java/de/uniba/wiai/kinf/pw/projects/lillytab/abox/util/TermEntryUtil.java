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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TermEntryUtil {

	private TermEntryUtil()
	{
	}


	/**
	 * @param <I> The type for individuals/nominals
	 * @param <L> The type for literals
	 * @param <K> The type for DL classes
	 * @param <R> The type for properties (roles)
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> TermEntry<I, L, K, R> getMappedTermEntry(
		final TermEntry<I, L, K, R> termEntry, final Map<NodeID, NodeID> nodeMap,
		final TermEntryFactory<I, L, K, R> termEntryFactory)
	{
		final NodeID mappedID = nodeMap.get(termEntry.getNodeID());
		final TermEntry<I, L, K, R> mappedEntry;
		if (mappedID == null) {
			mappedEntry = termEntry;
		} else {
			mappedEntry = termEntryFactory.getEntry(mappedID, termEntry.getTerm());
		}
		return mappedEntry;
	}


	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> Collection<TermEntry<I, L, K, R>> getMappedTermEntries(
		final Collection<TermEntry<I, L, K, R>> termEntries, final Map<NodeID, NodeID> nodeMap,
		final TermEntryFactory<I, L, K, R> termEntryFactory)
	{
		final Set<TermEntry<I, L, K, R>> mappedEntries = new TreeSet<>();
		for (TermEntry<I, L, K, R> entry : termEntries) {
			final TermEntry<I, L, K, R> mappedEntry = getMappedTermEntry(entry, nodeMap, termEntryFactory);
			mappedEntries.add(mappedEntry);
		}
		return mappedEntries;
	}
}
