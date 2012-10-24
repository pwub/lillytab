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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl;

import java.util.Collection;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface ISWRLTermFactory<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> {
	ISWRLNominalReference<Name, Klass, Role> getSWRLNominalReference(Name nomimal);

	ISWRLClassAtom<Name, Klass, Role> getSWRLClassAtom(Klass klass, ISWRLIndividual<Name, Klass, Role> individual);

	ISWRLRoleAtom<Name, Klass, Role> getSWRLRoleAtom(Role role, ISWRLIndividual<Name, Klass, Role> source, ISWRLIndividual<Name, Klass, Role> target);

	ISWRLIntersection<Name, Klass, Role> getSWRLIntersection(Collection<ISWRLAtomicTerm<Name, Klass, Role>> atoms);
	ISWRLIntersection<Name, Klass, Role> getSWRLIntersection(ISWRLAtomicTerm<Name, Klass, Role> atom1, ISWRLAtomicTerm<Name, Klass, Role> atom2);

	ISWRLRule<Name, Klass, Role> getSWRLRule(ISWRLTerm<Name, Klass, Role> head, ISWRLTerm<Name, Klass, Role> body);

	ISWRLVariable<Name, Klass, Role> getSWRLVariable(String name);
}
