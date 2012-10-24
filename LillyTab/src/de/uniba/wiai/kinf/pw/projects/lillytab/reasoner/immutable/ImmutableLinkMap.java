/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable;

import de.dhke.projects.cutil.collections.immutable.ImmutableMultiMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ILinkMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Collection;
import org.omg.CosNaming._BindingIteratorImplBase;


/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 *
 */
public class ImmutableLinkMap<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends ImmutableMultiMap<Role, NodeID>
	implements ILinkMap<Name, Klass, Role>
{
	protected ImmutableLinkMap(final ILinkMap<Name, Klass, Role> baseMap)
	{
		super(baseMap, null, null);
	}

	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> ImmutableLinkMap<Name, Klass, Role> decorate(
		final ILinkMap<Name, Klass, Role> baseMap)
	{
		return new ImmutableLinkMap<Name, Klass, Role>(baseMap);
	}

	@Override
	@SuppressWarnings("unchecked")
	public IABoxNode<Name, Klass, Role> getNode()
	{
		/* XXX - this maybe a loophole */
		return ((ILinkMap<Name, Klass, Role>)getDecoratee()).getNode().getImmutable();
	}

	@Override
	public NodeID put(Role role,
					  IABoxNode<Name, Klass, Role> node)
	{
		throw new UnsupportedOperationException("Cannot modify Immutable link map");
	}

	@Override
	public boolean putAll(Role key,
						  Collection<? extends IABoxNode<Name, Klass, Role>> values)
	{
		throw new UnsupportedOperationException("Cannot modify Immutable link map");
	}		
}
