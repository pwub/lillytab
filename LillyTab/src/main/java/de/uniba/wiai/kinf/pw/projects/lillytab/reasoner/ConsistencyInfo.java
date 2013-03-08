/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.dhke.projects.cutil.collections.frozen.FrozenFlat3Set;
import de.dhke.projects.cutil.collections.set.Flat3Set;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public final class ConsistencyInfo<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> {

	public enum ClashType {

		CONSISTENT,
		TRANSIENT,
		FINAL
	}
	private ClashType _clashType;
	/**
	 * The culprit set contains information about the ... XXX
	 */
	private final Set<Set<Set<TermEntry<Name, Klass, Role>>>> _culprits;


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
	public final void addCulprits(final IABoxNode<Name, Klass, Role> node, IDLTerm<Name, Klass, Role>... culprits)
	{
		addCulprits(node, Arrays.asList(culprits));
	}


	public void addCulprits(final IABoxNode<Name, Klass, Role> node,
							final Collection<? extends IDLTerm<Name, Klass, Role>> culprits)
	{
		final IABox<Name, Klass, Role> abox = node.getABox();
		final TermEntryFactory<Name, Klass, Role> termEntryFactory = abox.getTermEntryFactory();
		final IDependencyMap<Name, Klass, Role> depMap = abox.getDependencyMap();

		final Set<Set<TermEntry<Name, Klass, Role>>> culpritsSet = new Flat3Set<>();
		for (IDLTerm<Name, Klass, Role> culprit : culprits) {
			TermEntry<Name, Klass, Role> culpritEntry = termEntryFactory.getEntry(node, culprit);
			final Set<TermEntry<Name, Klass, Role>> altSet = new Flat3Set<>();
			/* if we still have another culprit and if it is not part of the set of alternative culprits, yet */
			while ((depMap != null) && (culpritEntry != null) && altSet.add(culpritEntry)) {
				final Collection<TermEntry<Name, Klass, Role>> parentSet = depMap.getParents(culpritEntry);
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


	public void addCulpritEntries(final IABox<Name, Klass, Role> abox,
								  final Collection<? extends Collection<TermEntry<Name, Klass, Role>>> culpritEntrySets)
	{
		final IDependencyMap<Name, Klass, Role> depMap;
		if (abox == null) {
			depMap = null;
		} else {
			depMap = abox.getDependencyMap();
		}

		final Set<Set<TermEntry<Name, Klass, Role>>> culpritsSet = new Flat3Set<>();
		for (Collection<TermEntry<Name, Klass, Role>> culpritEntries : culpritEntrySets) {
			for (TermEntry<Name, Klass, Role> culpritEntry : culpritEntries) {
				final Set<TermEntry<Name, Klass, Role>> altSet = new Flat3Set<>();
				TermEntry<Name, Klass, Role> currentEntry = culpritEntry;
				while ((depMap != null) && (currentEntry != null) && altSet.add(currentEntry)) {
					final Collection<TermEntry<Name, Klass, Role>> parentSet = depMap.getParents(currentEntry);
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


	public void addCulpritEntries(final IABoxNode<Name, Klass, Role> node,
								  final Collection<? extends Collection<TermEntry<Name, Klass, Role>>> culpritEntrySets)
	{
		addCulpritEntries(node.getABox(), culpritEntrySets);
	}


	/**
	 * <p> Determine if the {@literal abox} still has all terms that lead to the clash described by the current
	 * {@link ConsistencyInfo}. </p>
	 *
	 * @param abox The {@link IABox} to check against.
	 * @return {@literal false} if {@literal abox} is missing at least one term out of all the clashing term sets.
	 */
	public boolean hasClashingTerms(final IABox<Name, Klass, Role> abox)
	{
		for (Set<Set<TermEntry<Name, Klass, Role>>> culpritSet : _culprits) {
			boolean hasAll = true;
			for (Set<TermEntry<Name, Klass, Role>> culpritAlts : culpritSet) {
				boolean containsOne = false;
				for (TermEntry<Name, Klass, Role> culpritAlt : culpritAlts) {
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


	public ConsistencyInfo<Name, Klass, Role> updateFrom(final ConsistencyInfo<Name, Klass, Role> other)
	{
		upgradeClashType(other.getClashType());
		_culprits.addAll(other._culprits);
		return this;
	}
}
