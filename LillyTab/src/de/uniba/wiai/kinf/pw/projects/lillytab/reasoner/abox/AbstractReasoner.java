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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.dhke.projects.lutil.LoggingClass;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import java.util.Collection;

/**
 *
 * @param <Name>
 * @param <Klass> 
 * @param <Role>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public abstract class AbstractReasoner<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends LoggingClass
	implements IReasoner<Name, Klass, Role>
{
	public boolean isConsistent(final IDLRestriction<Name, Klass, Role> concept,
								final IABoxFactory<Name, Klass, Role> aboxFactory)
		throws EReasonerException
	{
		IABox<Name, Klass, Role> abox = aboxFactory.createABox();
		return isConsistent(abox, concept);
	}

	public Collection<? extends IReasonerResult<Name, Klass, Role>> checkConsistency(final IABox<Name, Klass, Role> abox)
		throws EReasonerException, EInconsistentABoxException
	{
		return checkConsistency(abox, false);
	}

	public boolean isConsistent(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> concept)
		throws EReasonerException
	{
		assert isConsistent(abox);
		try {
			checkConsistency(abox, concept, true);
		} catch (EInconsistentABoxException ex) {
			return true;
		}
		return false;
	}

	public boolean isConsistent(final IABox<Name, Klass, Role> abox)
		throws EReasonerException
	{
		try {
			checkConsistency(abox, true);
		} catch (EInconsistentABoxException ex) {
			return false;
		}
		return true;
	}
}
