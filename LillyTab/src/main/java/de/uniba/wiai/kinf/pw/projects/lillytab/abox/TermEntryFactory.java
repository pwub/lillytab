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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.SoftItemCache;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TermEntryFactory<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends SoftItemCache<TermEntry<Name, Klass, Role>> {

	private final WeakHashMap<TermEntry<Name, Klass, Role>, WeakReference<TermEntry<Name, Klass, Role>>> _termCache = new WeakHashMap<>();


	public TermEntryFactory()
	{
	}


	public TermEntry<Name, Klass, Role> getEntry(final IABoxNode<Name, Klass, Role> node,
												 final IDLTerm<Name, Klass, Role> term)
	{
		return getEntry(node.getNodeID(), term);
	}


	public TermEntry<Name, Klass, Role> getEntry(final NodeID nodeID, final IDLTerm<Name, Klass, Role> term)
	{
		TermEntry<Name, Klass, Role> entry = TermEntry.wrap(nodeID, term);
		return updateCache(entry);
	}
}
