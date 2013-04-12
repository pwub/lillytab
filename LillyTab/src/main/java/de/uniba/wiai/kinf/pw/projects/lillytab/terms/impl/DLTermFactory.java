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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLDataAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLDataSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDatatype;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.SoftItemCache;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.WeakHashMap;

/**
 *
 * Implementation of an {@link IDLTermFactory} with unique term caching.
 * <p />
 * The implementation stores information about existing term instances. If an instance for a term is requested, the
 * stored term is returned instead of a new object instance.
 * <p /> {@link WeakHashMap} and {@link WeakReference} are used to make sure, stored terms can be cleared up by the
 * garbage collector if they are not referenced elsewhere.
 *
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLTermFactory<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends SoftItemCache<IDLTerm<I, L, K, R>>
	implements IDLTermFactory<I, L, K, R> {

	private final IDLClassReference<I, L, K, R> _thing;
	private final IDLClassReference<I, L, K, R> _nothing;
	private final IDLDatatype<I, L, K, R> _topDatatype;


	public DLTermFactory(final K thing, final K nothing)
	{
		_thing = updateCache(new DLClassReference<I, L, K, R>(thing));
		_nothing = updateCache(new DLClassReference<I, L, K, R>(nothing));
		_topDatatype = (IDLDatatype<I, L, K, R>)updateCache(new AnyDataType<I, L, K, R>());
	}


	@Override
	public IDLClassReference<I, L, K, R> getDLClassReference(final K klass)
	{
		IDLClassReference<I, L, K, R> klassRef = new DLClassReference<>(klass);

		return updateCache(klassRef);
	}


	@Override
	public IDLObjectNegation<I, L, K, R> getDLObjectNegation(final IDLClassExpression<I, L, K, R> d)
	{
		final IDLObjectNegation<I, L, K, R> neg = new DLObjectNegation<>(d);
		return updateCache(neg);
	}


	@Override
	public IDLObjectIntersection<I, L, K, R> getDLObjectIntersection(final IDLClassExpression<I, L, K, R> d0,
																	 final IDLClassExpression<I, L, K, R> d1)
	{
		final IDLObjectIntersection<I, L, K, R> and = new DLObjectIntersection<>(d0, d1);
		return updateCache(and);
	}


	@Override
	public IDLObjectIntersection<I, L, K, R> getDLObjectIntersection(
		final Collection<? extends IDLClassExpression<I, L, K, R>> ds)
	{
		final IDLObjectIntersection<I, L, K, R> and = new DLObjectIntersection<>(ds);
		return updateCache(and);
	}


	@Override
	public IDLObjectUnion<I, L, K, R> getDLObjectUnion(final IDLClassExpression<I, L, K, R> d0,
													   final IDLClassExpression<I, L, K, R> d1)
	{
		final IDLObjectUnion<I, L, K, R> or = new DLObjectUnion<>(d0, d1);
		return updateCache(or);
	}


	@Override
	public IDLObjectUnion<I, L, K, R> getDLObjectUnion(final Collection<? extends IDLClassExpression<I, L, K, R>> ds)
	{
		final IDLObjectUnion<I, L, K, R> or = new DLObjectUnion<>(ds);
		return updateCache(or);
	}


	@Override
	public IDLDataAllRestriction<I, L, K, R> getDLDataAllRestriction(R role,
																	 IDLDataRange<I, L, K, R> d)
	{
		final IDLDataAllRestriction<I, L, K, R> allRes = new DLDataAllRestriction<>(role, d);
		return updateCache(allRes);
	}


	@Override
	public IDLDataSomeRestriction<I, L, K, R> getDLDataSomeRestriction(R role,
																	   IDLDataRange<I, L, K, R> d)
	{
		final IDLDataSomeRestriction<I, L, K, R> someRes = new DLDataSomeRestriction<>(role, d);
		return updateCache(someRes);
	}


	@Override
	public IDLObjectAllRestriction<I, L, K, R> getDLObjectAllRestriction(R role,
																		 IDLClassExpression<I, L, K, R> d)
	{
		final IDLObjectAllRestriction<I, L, K, R> allRes = new DLObjectAllRestriction<>(role, d);
		return updateCache(allRes);
	}


	@Override
	public IDLObjectSomeRestriction<I, L, K, R> getDLObjectSomeRestriction(R role,
																		   IDLClassExpression<I, L, K, R> d)
	{
		final IDLObjectSomeRestriction<I, L, K, R> someRes = new DLObjectSomeRestriction<>(role, d);
		return updateCache(someRes);
	}


	@Override
	public IDLImplies<I, L, K, R> getDLImplies(final IDLClassExpression<I, L, K, R> sub,
											   final IDLClassExpression<I, L, K, R> sup)
	{
		final IDLImplies<I, L, K, R> imp = new DLImplies<>(sub, sup);
		return updateCache(imp);
	}


	@Override
	public IDLClassReference<I, L, K, R> getDLThing()
	{
		return _thing;
	}


	@Override
	public IDLClassReference<I, L, K, R> getDLNothing()
	{
		return _nothing;
	}


	@Override
	public IDLIndividualReference<I, L, K, R> getDLIndividualReference(final I individual)
	{
		final IDLIndividualReference<I, L, K, R> nominalRef = new DLIndividualReference<>(individual);
		return updateCache(nominalRef);
	}


	@Override
	public IDLLiteralReference<I, L, K, R> getDLLiteralReference(L literal)
	{
		final IDLLiteralReference<I, L, K, R> litRef = new DLLiteralReference<>(literal);
		return updateCache(litRef);
	}


	@Override
	public IDLDataNegation<I, L, K, R> getDLDataNegation(
		IDLDataRange<I, L, K, R> d)
	{
		final IDLDataNegation<I, L, K, R> litRef = new DLDataNegation<>(d);
		return updateCache(litRef);
	}


	@Override
	public IDLDataUnion<I, L, K, R> getDLDataUnion(
		Collection<? extends IDLDataRange<I, L, K, R>> ds)
	{
		final IDLDataUnion<I, L, K, R> union = new DLDataUnion<>(ds);
		return updateCache(union);
	}


	@Override
	public IDLDataUnion<I, L, K, R> getDLDataUnion(
		IDLDataRange<I, L, K, R> d0,
		IDLDataRange<I, L, K, R> d1)
	{
		final IDLDataUnion<I, L, K, R> union = new DLDataUnion<>(d0, d1);
		return updateCache(union);
	}


	@Override
	public IDLDataIntersection<I, L, K, R> getDLDataIntersection(
		Collection<? extends IDLDataRange<I, L, K, R>> ds)
	{
		final IDLDataIntersection<I, L, K, R> intersection = new DLDataIntersection<>(ds);
		return updateCache(intersection);
	}


	@Override
	public IDLDataIntersection<I, L, K, R> getDLDataIntersection(
		IDLDataRange<I, L, K, R> d0,
		IDLDataRange<I, L, K, R> d1)
	{
		final IDLDataIntersection<I, L, K, R> intersection = new DLDataIntersection<>(d0, d1);
		return updateCache(intersection);
	}


	@Override
	public IDLDatatype<I, L, K, R> getDLTopDatatype()
	{
		return _topDatatype;
	}
}
