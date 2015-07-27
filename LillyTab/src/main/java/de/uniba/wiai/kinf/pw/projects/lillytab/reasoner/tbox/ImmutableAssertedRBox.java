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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox;

import de.dhke.projects.cutil.IDecorator;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import org.apache.commons.collections15.MultiMap;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class ImmutableAssertedRBox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends ImmutableRBox<I, L, K, R>
	implements IAssertedRBox<I, L, K, R>, IDecorator<IAssertedRBox<I, L, K, R>>
{
	final IRBox<I, L, K, R> _rbox;
	final IAssertedRBox<I, L, K, R> _assertedRBox;

	public ImmutableAssertedRBox(final ITBox<I, L, K, R> tbox,
								 final IRBox<I, L, K, R> rbox, final IAssertedRBox<I, L, K, R> assertedRBox)
	{
		super(tbox, rbox);
		_rbox = rbox;
		_assertedRBox = assertedRBox;
	}

	@Override
	public boolean addRole(R role, RoleType roleType) throws EInconsistentRBoxException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public boolean removeRole(R role)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public void setRoleType(R role, RoleType roleType) throws EInconsistentRBoxException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public boolean removeEquivalentRole(R first, R second)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public boolean addEquivalentRole(R first, R second) throws EInconsistentRBoxException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public boolean removeInverseRole(R first, R second)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public boolean addInverseRole(R first, R second) throws EInconsistentRBoxException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public boolean removeSubRole(R sup, R sub)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public boolean addSubRole(R sup, R sub) throws EInconsistentRBoxException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public boolean setRoleProperty(R role, RoleProperty property) throws EInconsistentRBoxException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public boolean clearRoleProperty(R role, RoleProperty property)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public MultiMap<R, IDLClassExpression<I, L, K, R>> getRoleDomains()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public MultiMap<R, IDLNodeTerm<I, L, K, R>> getRoleRanges()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableAssertedRBox");
	}

	@Override
	public IAssertedRBox<I, L, K, R> getImmutable()
	{
		return this;
	}

	@Override
	public IAssertedRBox<I, L, K, R> getDecoratee()
	{
		return _assertedRBox;
	}
}
