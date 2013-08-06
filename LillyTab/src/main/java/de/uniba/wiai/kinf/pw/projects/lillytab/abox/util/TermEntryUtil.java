/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
