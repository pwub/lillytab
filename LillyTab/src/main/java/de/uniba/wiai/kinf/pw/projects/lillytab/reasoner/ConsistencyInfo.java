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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.dhke.projects.cutil.collections.frozen.FrozenFlat3Set;
import de.dhke.projects.cutil.collections.set.Flat3Set;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public final class ConsistencyInfo<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> {

	public enum ClashType {

		CONSISTENT,
		TRANSIENT,
		FINAL
	}
	private ClashType _clashType;
	/**
	 * The culprit set contains information about the ... XXX
	 */
	private final Set<Set<Set<TermEntry<I, L, K, R>>>> _culprits;


	public ConsistencyInfo(final ClashType clashType)
	{
		_clashType = clashType;
		_culprits = new HashSet<>();
	}


	public ConsistencyInfo()
	{
		this(ClashType.CONSISTENT);
	}


	public ClashType getClashType()
	{
		return _clashType;
	}


	public void setClashType(final ClashType clashType)
	{
		_clashType = clashType;
	}


	public void upgradeClashType(final ClashType minimumClashType)
	{
		if (_clashType.compareTo(minimumClashType) < 0) {
			_clashType = minimumClashType;
		}
	}


	public boolean isInconsistent()
	{
		return _clashType != ClashType.CONSISTENT;
	}


	public boolean isFinallyInconsistent()
	{
		return _clashType == ClashType.FINAL;
	}


	@SafeVarargs
	public final void addCulprits(final IABoxNode<I, L, K, R> node, IDLTerm<I, L, K, R>... culprits)
	{
		addCulprits(node, Arrays.asList(culprits));
	}


	public void addCulprits(final IABoxNode<I, L, K, R> node,
							final Collection<? extends IDLTerm<I, L, K, R>> culprits)
	{
		final IABox<I, L, K, R> abox = node.getABox();
		final TermEntryFactory<I, L, K, R> termEntryFactory = abox.getTermEntryFactory();
		final IDependencyMap<I, L, K, R> depMap = abox.getDependencyMap();

		final Set<Set<TermEntry<I, L, K, R>>> culpritsSet = new Flat3Set<>();
		for (IDLTerm<I, L, K, R> culprit : culprits) {
			TermEntry<I, L, K, R> culpritEntry = termEntryFactory.getEntry(node, culprit);
			final Set<TermEntry<I, L, K, R>> altSet = new Flat3Set<>();
			/* if we still have another culprit and if it is not part of the set of alternative culprits, yet */
			while ((depMap != null) && (culpritEntry != null) && altSet.add(culpritEntry)) {
				final Collection<TermEntry<I, L, K, R>> parentSet = depMap.getParents(culpritEntry);
				/* if the culprit has only a single parent, it is an alternative */
				if ((parentSet != null) && parentSet.isEmpty() || (parentSet.size() > 1)) {
					culpritEntry = null;
				} else {
					culpritEntry = parentSet.iterator().next();
				}
			}
			culpritsSet.add(new FrozenFlat3Set<>(altSet));
		}
		_culprits.add(new FrozenFlat3Set<>(culpritsSet));
	}


	public void addCulpritEntries(final IABox<I, L, K, R> abox,
								  final Collection<? extends Collection<TermEntry<I, L, K, R>>> culpritEntrySets)
	{
		final IDependencyMap<I, L, K, R> depMap;
		if (abox == null) {
			depMap = null;
		} else {
			depMap = abox.getDependencyMap();
		}

		final Set<Set<TermEntry<I, L, K, R>>> culpritsSet = new Flat3Set<>();
		for (Collection<TermEntry<I, L, K, R>> culpritEntries : culpritEntrySets) {
			for (TermEntry<I, L, K, R> culpritEntry : culpritEntries) {
				final Set<TermEntry<I, L, K, R>> altSet = new Flat3Set<>();
				TermEntry<I, L, K, R> currentEntry = culpritEntry;
				while ((depMap != null) && (currentEntry != null) && altSet.add(currentEntry)) {
					final Collection<TermEntry<I, L, K, R>> parentSet = depMap.getParents(currentEntry);
					/* if the culprit has only a single parent, it is an alternative */
					if ((parentSet == null) || parentSet.isEmpty() || (parentSet.size() > 1)) {
						currentEntry = null;
					} else {
						currentEntry = parentSet.iterator().next();
					}
				}
				culpritsSet.add(new FrozenFlat3Set<>(altSet));
			}
			_culprits.add(new FrozenFlat3Set<>(culpritsSet));
		}
	}


	public void addCulpritEntries(final IABoxNode<I, L, K, R> node,
								  final Collection<? extends Collection<TermEntry<I, L, K, R>>> culpritEntrySets)
	{
		addCulpritEntries(node.getABox(), culpritEntrySets);
	}


	/**
	 * Determine if the {@literal abox} still has all terms that lead to the clash described by the current
	 * {@link ConsistencyInfo}.
	 *
	 * @param abox The {@link IABox} to check against.
	 * @return {@literal false} if {@literal abox} is missing at least one term out of all the clashing term sets.
	 */
	public boolean hasClashingTerms(final IABox<I, L, K, R> abox)
	{
		for (Set<Set<TermEntry<I, L, K, R>>> culpritSet : _culprits) {
			boolean hasAll = true;
			for (Set<TermEntry<I, L, K, R>> culpritAlts : culpritSet) {
				boolean containsOne = false;
				for (TermEntry<I, L, K, R> culpritAlt : culpritAlts) {
					if (abox.containsTermEntry(culpritAlt)) {
						containsOne = true;
						break;
					}
				}
				if (!containsOne) {
					hasAll = false;
					break;
				}
			}
			if (hasAll) {
				return true;
			}
		}
		return false;
	}


	public ConsistencyInfo<I, L, K, R> updateFrom(final ConsistencyInfo<I, L, K, R> other)
	{
		upgradeClashType(other.getClashType());
		_culprits.addAll(other._culprits);
		return this;
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(_clashType);
		sb.append(": ");
		for (Set<Set<TermEntry<I, L, K, R>>> clashItem : _culprits) {
			sb.append("\t");
			sb.append(clashItem);
			sb.append("\n");
		}
		return sb.toString();
	}
}
