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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.SoftItemCache;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.WeakHashMap;

/**
 * <p>
 * Implementation of an {@link IDLTermFactory} with unique term caching.
 * </p><p>
 * The implementation stores information about existing term instances. If an instance for a term is requested, the
 * stored term is returned instead of a new object instance.
 * </p><p> {@link WeakHashMap} and {@link WeakReference} are used to make sure, stored terms can be cleared up by the
 * garbage collector if they are not referenced elsewhere.
 * </p>
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLTermFactory<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends SoftItemCache<IDLTerm<Name, Klass, Role>>
	implements IDLTermFactory<Name, Klass, Role> {

	private final IDLClassReference<Name, Klass, Role> _thing;
	private final IDLClassReference<Name, Klass, Role> _nothing;


	public DLTermFactory(final Klass thing, final Klass nothing)
	{
		_thing = updateCache(new DLClassReference<Name, Klass, Role>(thing));
		_nothing = updateCache(new DLClassReference<Name, Klass, Role>(nothing));
	}


	@Override
	public IDLClassReference<Name, Klass, Role> getDLClassReference(final Klass klass)
	{
		IDLClassReference<Name, Klass, Role> klassRef = new DLClassReference<>(klass);

		return updateCache(klassRef);
	}


	@Override
	public IDLNegation<Name, Klass, Role> getDLNegation(final IDLRestriction<Name, Klass, Role> d)
	{
		final IDLNegation<Name, Klass, Role> neg = new DLNegation<>(d);
		return updateCache(neg);
	}


	@Override
	public IDLIntersection<Name, Klass, Role> getDLIntersection(final IDLRestriction<Name, Klass, Role> d0,
																final IDLRestriction<Name, Klass, Role> d1)
	{
		final IDLIntersection<Name, Klass, Role> and = new DLIntersection<>(d0, d1);
		return updateCache(and);
	}


	@Override
	public IDLIntersection<Name, Klass, Role> getDLIntersection(
		final Collection<? extends IDLRestriction<Name, Klass, Role>> ds)
	{
		final IDLIntersection<Name, Klass, Role> and = new DLIntersection<>(ds);
		return updateCache(and);
	}


	@Override
	public IDLUnion<Name, Klass, Role> getDLUnion(final IDLRestriction<Name, Klass, Role> d0,
												  final IDLRestriction<Name, Klass, Role> d1)
	{
		final IDLUnion<Name, Klass, Role> or = new DLUnion<>(d0, d1);
		return updateCache(or);
	}


	@Override
	public IDLUnion<Name, Klass, Role> getDLUnion(final Collection<? extends IDLRestriction<Name, Klass, Role>> ds)
	{
		final IDLUnion<Name, Klass, Role> or = new DLUnion<>(ds);
		return updateCache(or);
	}


	@Override
	public IDLSomeRestriction<Name, Klass, Role> getDLSomeRestriction(final Role role,
																	  final IDLRestriction<Name, Klass, Role> d)
	{
		final IDLSomeRestriction<Name, Klass, Role> some = new DLSomeRestriction<>(role, d);
		return updateCache(some);
	}


	@Override
	public IDLAllRestriction<Name, Klass, Role> getDLAllRestriction(final Role role,
																	final IDLRestriction<Name, Klass, Role> d)
	{
		final IDLAllRestriction<Name, Klass, Role> all = new DLAllRestriction<>(role, d);
		return updateCache(all);
	}


	@Override
	public IDLImplies<Name, Klass, Role> getDLImplies(final IDLRestriction<Name, Klass, Role> sub,
													  final IDLRestriction<Name, Klass, Role> sup)
	{
		final IDLImplies<Name, Klass, Role> imp = new DLImplies<>(sub, sup);
		return updateCache(imp);
	}


	@Override
	public IDLClassReference<Name, Klass, Role> getDLThing()
	{
		return _thing;
	}


	@Override
	public IDLClassReference<Name, Klass, Role> getDLNothing()
	{
		return _nothing;
	}


	@Override
	public IDLNominalReference<Name, Klass, Role> getDLNominalReference(final Name individual)
	{
		final IDLNominalReference<Name, Klass, Role> nominalRef = new DLNominalReference<>(individual);
		return updateCache(nominalRef);
	}
}
