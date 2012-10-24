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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLAtomicTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLClassAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIndividual;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRule;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLVariable;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.SoftItemCache;
import java.util.Collection;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLTermFactory<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends SoftItemCache<ITerm>
	implements ISWRLTermFactory<Name, Klass, Role>
{
	public ISWRLNominalReference<Name, Klass, Role> getSWRLNominalReference(final Name nomimal)
	{
		ISWRLNominalReference<Name, Klass, Role> nominalRef = new SWRLNominalReference<Name, Klass, Role>(nomimal);

		return updateCache(nominalRef);
	}

	public ISWRLClassAtom<Name, Klass, Role> getSWRLClassAtom(final Klass klass,
															  final ISWRLIndividual<Name, Klass, Role> individual)
	{
		final ISWRLClassAtom<Name, Klass, Role> classAtom = new SWRLClassAtom<Name, Klass, Role>(klass, individual);
		
		return updateCache(classAtom);
	}

	public ISWRLIntersection<Name, Klass, Role> getSWRLIntersection(final Collection<ISWRLAtomicTerm<Name, Klass, Role>> atoms)
	{
		final ISWRLIntersection<Name, Klass, Role> intersection = new SWRLIntersection<Name, Klass, Role>(atoms);
		
		return updateCache(intersection);
	}

	public ISWRLIntersection<Name, Klass, Role> getSWRLIntersection(final ISWRLAtomicTerm<Name, Klass, Role> atom1,
																	final ISWRLAtomicTerm<Name, Klass, Role> atom2)
	{
		final ISWRLIntersection<Name, Klass, Role> intersection = new SWRLIntersection<Name, Klass, Role>(atom1, atom2);
		
		return updateCache(intersection);
	}

	public ISWRLRule<Name, Klass, Role> getSWRLRule(final ISWRLTerm<Name, Klass, Role> head,
													final ISWRLTerm<Name, Klass, Role> body)
	{
		final ISWRLRule<Name, Klass, Role> rule = new SWRLRule<Name, Klass, Role>(head, body);
		
		return updateCache(rule);
	}

	public ISWRLVariable<Name, Klass, Role> getSWRLVariable(final String name)
	{
		final ISWRLVariable<Name, Klass, Role> variable = new SWRLVariable<Name, Klass, Role>(name);
		
		return updateCache(variable);
	}

	public ISWRLRoleAtom<Name, Klass, Role> getSWRLRoleAtom(final Role role,
															final ISWRLIndividual<Name, Klass, Role> source,
															final ISWRLIndividual<Name, Klass, Role> target)
	{
		final ISWRLRoleAtom<Name, Klass, Role> roleAtom = new SWRLRoleAtom<Name, Klass, Role>(role, source, target);
		
		return updateCache(roleAtom);
	}
}
