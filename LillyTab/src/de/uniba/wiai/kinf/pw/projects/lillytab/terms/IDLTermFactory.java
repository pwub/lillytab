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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms;

import java.util.Collection;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 */
public interface IDLTermFactory<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> {
	IDLClassReference<Name, Klass, Role> getDLClassReference(Klass klass);
	IDLClassReference<Name, Klass, Role>getDLThing();
	IDLClassReference<Name, Klass, Role> getDLNothing();

	IDLNegation<Name, Klass, Role> getDLNegation(IDLRestriction<Name, Klass, Role> d);
	IDLIntersection<Name, Klass, Role> getDLIntersection(IDLRestriction<Name, Klass, Role> d0, IDLRestriction<Name, Klass, Role> d1);
	IDLIntersection<Name, Klass, Role> getDLIntersection(Collection<? extends IDLRestriction<Name, Klass, Role>> ds);
	IDLUnion<Name, Klass, Role> getDLUnion(IDLRestriction<Name, Klass, Role> d0, IDLRestriction<Name, Klass, Role> d1);
	IDLUnion<Name, Klass, Role> getDLUnion(Collection<? extends IDLRestriction<Name, Klass, Role>> ds);

	IDLSomeRestriction<Name, Klass, Role> getDLSomeRestriction(Role role, IDLRestriction<Name, Klass, Role> d);
	IDLAllRestriction<Name, Klass, Role> getDLAllRestriction(Role role, IDLRestriction<Name, Klass, Role> d);

	IDLImplies<Name, Klass, Role> getDLImplies(IDLRestriction<Name, Klass, Role> sub, IDLRestriction<Name, Klass, Role> sup);

	IDLNominalReference<Name, Klass, Role> getDLNominalReference(Name individual);
}

