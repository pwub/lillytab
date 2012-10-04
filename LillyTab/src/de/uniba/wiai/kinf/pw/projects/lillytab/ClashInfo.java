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
package de.uniba.wiai.kinf.pw.projects.lillytab;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.dhke.projects.cutil.collections.frozen.FrozenSortedList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public final class ClashInfo<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
{
	public static enum ResultType {		
		CONSISTENT,
		FINAL_CLASH,
		INTERMEDIATE_CLASH,
	}
	
	private final ResultType _resultType;
	private final Set<Set<TermEntry<Name, Klass, Role>>> _clashingTerms;
	
	public ClashInfo(final ResultType resultType)
	{
		_resultType = resultType;
		if (_resultType != ResultType.CONSISTENT)
			_clashingTerms = new HashSet<Set<TermEntry<Name, Klass, Role>>>();
		else
			_clashingTerms = null;
	}

	public ResultType getResultType()
	{
		return _resultType;
	}	
	
	public Set<Set<TermEntry<Name, Klass, Role>>> getClashingTerms()
	{
		return _clashingTerms;
	}	

	public void addClashingSet(final TermEntry<Name, Klass, Role> term1, final TermEntry<Name, Klass, Role> term2)	
	{
		final ArrayList<TermEntry<Name, Klass, Role>> clashingTerms = new ArrayList<TermEntry<Name, Klass, Role>>(2);
		clashingTerms.add(term1);
		clashingTerms.add(term2);
		addClashingSet(clashingTerms);
	}
	
	
	public void addClashingSet(final Collection<? extends TermEntry<Name, Klass, Role>> clashingTerms)
	{
		if (_clashingTerms != null) {
			assert _resultType != ResultType.CONSISTENT;			
			final Set<TermEntry<Name, Klass, Role>> clashSet = new FrozenSortedList<TermEntry<Name, Klass, Role>>(clashingTerms);
			_clashingTerms.add(clashSet);
		} else {
			assert _resultType == ResultType.CONSISTENT;
			throw new UnsupportedOperationException("Cannot add clashing set to consistent check result");
		}
	}
}
