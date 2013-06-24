/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLObjectRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.SWRLTermOrder;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLObjectRoleAtom<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends SWRLRoleAtom<I, L, K, R>
	implements ISWRLObjectRoleAtom<I, L, K, R> {

	public SWRLObjectRoleAtom(R role, ISWRLIArgument<I, L, K, R> first, ISWRLIArgument<I, L, K, R> second)
	{
		super(role, first, second);
	}


	@Override
	public ISWRLIArgument<I, L, K, R> getSecondIndividual()
	{
		return (ISWRLIArgument<I, L, K, R>) super.getSecondIndividual();
	}


	@Override
	public SWRLTermOrder getSWRLTermOrder()
	{
		return SWRLTermOrder.SWRL_OBJECT_ROLE_ATOM;
	}
}
