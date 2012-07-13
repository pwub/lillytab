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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datatype.IDLDatatypeExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLNominalReference;


/**
 * <p>
 *  An enumeration yielding a preferred order for all {@link IDLDescription}-derived
 *  term orders.
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public enum DLTermOrder
/**
 *  This enum needs to be extended to support additional {@link IDLTerm} types.
 **/
{
	/**
	 * This order also represents the operation of the reasoner:
	 * It starts with concepts that are processed during non-generating
	 * rules. Concepts (Negation, Class references, nominmal references)
	 * that are never checked during tableaux expansion, but only
	 * used during lookups are at the back, so that we do not have to
	 * skip over them during iteration.
	 *
	 * The  "_BEFORE_" and "_AFTER_" terms are filler objects
	 * that are helpful in implementing sorted sets. Objects with
	 * these types should never be part of a real collection.
	 **/
	DL_BEFORE(null),

	DL_BEFORE_INTERSECTION(null),
	DL_INTERSECTION(IDLIntersection.class),
	DL_AFTER_INTERSECTION(null),

	DL_BEFORE_UNION(null),
	DL_UNION(IDLUnion.class),
	DL_AFTER_UNION(null),

	DL_BEFORE_IMPLIES(null),
	DL_IMPLIES(IDLImplies.class),
	DL_AFTER_IMPLIES(null),

	DL_BEFORE_SOME_RESTRICTION(null),
	DL_SOME_RESTRICTION(IDLSomeRestriction.class),
	DL_AFTER_SOME_RESTRICTION(null),

	DL_BEFORE_ALL_RESTRICTION(null),
	DL_ALL_RESTRICTION(IDLAllRestriction.class),
	DL_AFTER_ALL_RESTRICTION(null),

	DL_BEFORE_NEGATION(null),
	DL_NEGATION(IDLNegation.class),
	DL_AFTER_NEGATION(null),

	DL_BEFORE_NOTHING(null),
	DL_NOTHING(DLNothing.class),
	DL_AFTER_NOTHING(null),

	DL_BEFORE_NOMINAL_REFERENCE(null),
	DL_NOMIMAL_REFERENCE(DLNominalReference.class),
	DL_AFTER_NOMINAL_REFERENCE(null),

	DL_BEFORE_CLASS_REFERENCE(null),
	DL_CLASS_REFERENCE(IDLClassReference.class),
	DL_AFTER_CLASS_REFERENCE(null),

	DL_BEFORE_THING(null),
	DL_THING(DLThing.class),
	DL_AFTER_THING(null),
	
	DL_BEFORE_DATATYPE_EXPRESSION(null),
	DL_DATATYPE_EXPRESSION(IDLDatatypeExpression.class),
	DL_AFTER_DATATYPE_EXPRESSION(null),

	DL_AFTER(null);
	/**
	 * The interface class associated with the enumeration item
	 **/
	private final Class<? extends IDLTerm> _dlTermClass;

	/**
	 * Create a new enumeration item wrapping the specified class.
	 * @param klass The wrapped class.
	 */
	DLTermOrder(final Class<? extends IDLTerm> klass)
	{
		_dlTermClass = klass;
	}

	/**
	 * Get the  {@link IDLTerm} interface class
	 * wrapped by this enumeration item.
	 * @return The {@link IDLTerm} interface class
	 */
	public Class<? extends IDLTerm> getDLTermClass()
	{
		return _dlTermClass;
	}

	/**
	 * Compare the term orders of two {@link IDLTerm}s.
	 * @param t0 The first term
	 * @param t1 The second term
	 * @return {@literal -1}, if {@literal t0} has a lower term order than {@literal t1},
	 *	{@literal 0} if the term orders match, and {@literal 1}, if {@literal t0} has a higher
	 *  term order than {@literal t1}.
	 */
	public static int compareTermOrder(final IDLTerm t0, final IDLTerm t1)
	{
		return t0.getDLTermOrder().compareTo(t1);
	}

	/**
	 * Compare this term order to the term order of a {@link IDLTerm}.
	 *
	 * @param term The term whose term order to compare.
	 * @return {@literal -1}, if {@literal t0} has a lower term order than {@literal this},
	 *	{@literal 0} if the term orders match, and {@literal 1}, if {@literal t0} has a higher
	 *  term order than {@literal this}.
	 */
	public int compareTo(final IDLTerm term)
	{
		return compareTo(term.getDLTermOrder());
	}
}
