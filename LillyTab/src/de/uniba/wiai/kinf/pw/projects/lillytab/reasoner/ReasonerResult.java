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

import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Map;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ReasonerResult<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>	
	implements IReasonerResult<Name, Klass, Role>
{
	private final IABox<Name, Klass, Role> _abox;
	private final Map<NodeID, NodeID> _mergeMap;
	
	protected ReasonerResult(final IABox<Name, Klass, Role> abox, final Map<NodeID, NodeID> mergeMap)
	{
		_abox = abox;
		_mergeMap = mergeMap;
	}	
	
	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> 
		ReasonerResult<Name, Klass, Role> create(final IABox<Name, Klass, Role> abox, final Map<NodeID, NodeID> mergeMap)
	{
		return new ReasonerResult<Name, Klass, Role>(abox, mergeMap);
	}
	
	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> 
		ReasonerResult<Name, Klass, Role> create(final IABox<Name, Klass, Role> abox)
	{
		return new ReasonerResult<Name, Klass, Role>(abox, null);
	}

	
	public IABox<Name, Klass, Role> getABox()
	{
		return _abox;
	}

	public Map<NodeID, NodeID> getMergeMap()
	{
		return _mergeMap;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		else if (obj instanceof IReasonerResult) {
			final IReasonerResult<?, ?, ?> other = (IReasonerResult<?, ?, ?>)obj;
			return _abox.equals(other.getABox());
		} else
			return false;
	}

	@Override
	public int hashCode()
	{
		return _abox.hashCode();
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(_abox);
		return sb.toString();
	}	
	
	public boolean deepEquals(Object obj)
	{
		if (this == obj)
			return true;
		else if (obj instanceof IReasonerResult) {
			final IReasonerResult<?, ?, ?> other = (IReasonerResult<?, ?, ?>)obj;
			return _abox.deepEquals(other.getABox());
		} else
			return false;		
	}
	
	public int deepHashCode()
	{
		return _abox.deepHashCode();
	}
}
