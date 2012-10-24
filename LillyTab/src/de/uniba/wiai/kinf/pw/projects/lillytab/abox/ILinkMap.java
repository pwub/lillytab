/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import java.util.Collection;
import org.apache.commons.collections15.MultiMap;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface ILinkMap<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends MultiMap<Role, NodeID>
{
	IABoxNode<Name, Klass, Role> getNode();

	NodeID put(final Role role, final IABoxNode<Name, Klass, Role> node);
	boolean putAll(Role key,
				   Collection<? extends IABoxNode<Name, Klass, Role>> values);
}
