/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collection;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class NoAddTermSet<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends TermSet<Name, Klass, Role>
	implements ITermSet<Name, Klass, Role> {

	public NoAddTermSet(final ITermSet<Name, Klass, Role> baseSet, final Object sender)
	{
		super(baseSet, baseSet);
	}

	@Override
	protected void notifyBeforeElementAdded(
		CollectionItemEvent<IDLTerm<Name, Klass, Role>, Collection<IDLTerm<Name, Klass, Role>>> ev)
	{
		if (!contains(ev.getItem())) {
			throw new UnsupportedOperationException("Cannot add to ABox term set directly, use addUnfoldedDescription()");
		} else
			super.notifyBeforeElementAdded(ev);
	}
}
