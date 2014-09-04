/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox;

import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import java.util.Collection;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class ImmutableRBox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IRBox<I, L, K, R>
{
	private final ITBox<I, L, K, R> _tbox;
	private final IRBox<I, L, K, R> _rbox;
	private final IAssertedRBox<I, L, K, R> _assertedRBox;

	public ImmutableRBox(
		final ITBox<I, L, K, R> tbox,
		final IRBox<I, L, K, R> rbox)
	{
		_tbox = tbox;
		_rbox = rbox;
		_assertedRBox = new ImmutableAssertedRBox<>(tbox, this, rbox.getAssertedRBox());
	}

	@Override
	public Collection<R> getEquivalentRoles(R role)
	{
		return _rbox.getEquivalentRoles(role);
	}

	@Override
	public Collection<R> getInverseRoles(R role)
	{
		return _rbox.getInverseRoles(role);
	}

	@Override
	public Collection<RoleProperty> getRoleProperties(R role)
	{
		return _rbox.getRoleProperties(role);
	}

	@Override
	public Collection<IDLClassExpression<I, L, K, R>> getRoleDomains(R role)
	{
		return _rbox.getRoleDomains(role);
	}

	@Override
	public Collection<IDLNodeTerm<I, L, K, R>> getRoleRanges(R role)
	{
		return _rbox.getRoleRanges(role);
	}

	@Override
	public RoleType getRoleType(R role)
	{
		return _rbox.getRoleType(role);
	}

	@Override
	public Collection<R> getRoles()
	{
		return _rbox.getRoles();
	}

	@Override
	public Collection<R> getRoles(RoleProperty property)
	{
		return _rbox.getRoles(property);
	}

	@Override
	public Collection<R> getRoles(RoleType type)
	{
		return _rbox.getRoles(type);
	}

	@Override
	public Collection<R> getSubRoles(R role)
	{
		return _rbox.getSubRoles(role);
	}

	@Override
	public Collection<R> getSuperRoles(R role)
	{
		return _rbox.getSuperRoles(role);
	}

	@Override
	public ITBox<I, L, K, R> getTBox()
	{
		return _rbox.getTBox();
	}

	@Override
	public boolean hasRoleProperty(R role, RoleProperty property)
	{
		return _rbox.hasRoleProperty(role, property);
	}

	@Override
	public boolean hasRoleType(R role, RoleType roleType)
	{
		return _rbox.hasRoleType(role, roleType);
	}

	@Override
	public boolean hasRole(R role)
	{
		return _rbox.hasRole(role);
	}

	@Override
	public boolean isEquivalentRole(R first, R second)
	{
		return _rbox.isEquivalentRole(first, second);
	}

	@Override
	public boolean isInverseRole(R first, R second)
	{
		return _rbox.isInverseRole(first, second);
	}

	@Override
	public boolean hasInverseRoles()
	{
		return _rbox.hasInverseRoles();
	}

	@Override
	public boolean isSubRole(R sup, R sub)
	{
		return _rbox.isSubRole(sup, sub);
	}

	@Override
	public boolean isSuperRole(R sub, R sup)
	{
		return _rbox.isSuperRole(sub, sup);
	}

	@Override
	public IAssertedRBox<I, L, K, R> getAssertedRBox()
	{
		return _assertedRBox;
	}

	@Override
	public String toString(String prefix)
	{
		return _rbox.toString(prefix);
	}

	@Override
	public String toString()
	{
		return _tbox.toString();
	}

	@Override
	public IRBox<I, L, K, R> getImmutable()
	{
		return this;
	}
}
