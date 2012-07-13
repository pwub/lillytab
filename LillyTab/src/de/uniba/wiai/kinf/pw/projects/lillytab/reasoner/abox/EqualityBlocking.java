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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import org.apache.commons.collections15.SetUtils;

/**
 * Equality blocking as it is required for some logics with inverse roles but no
 * transitive and reflexive roles.
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class EqualityBlocking<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends SubsetBlockingStrategy<Name, Klass, Role>
{

	@Override
	protected boolean isPotentialBlocker(IABoxNode<Name, Klass, Role> blocker,
										 IABoxNode<Name, Klass, Role> target)
	{
		/**
		 * This node is potentially blocking {@literal target}, if
		 * target's set of concept terms is a EQUAL to ours
		 *
		 * The size check is a very simple performance trick, that
		 * helps for some ontologies.
		 **/
		return ((blocker.compareTo(target) < 0) && (blocker.getTerms().size() == target.getTerms().size())
			&& SetUtils.isEqualSet(blocker.getTerms(), target.getTerms()));
	}

}
