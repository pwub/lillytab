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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyDescription;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLNominalReference<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IDLNominalReference<Name, Klass, Role> {

	private final Name _nominal;


	protected DLNominalReference(final Name nominal)
	{
		_nominal = nominal;
	}


	public Name getIndividual()
	{
		return _nominal;
	}


	public DLTermOrder getDLTermOrder()
	{
		return DLTermOrder.DL_NOMIMAL_REFERENCE;
	}


	@Override
	public ITerm clone()
	{
		return this;
	}


	public int compareTo(final IDLTerm<Name, Klass, Role> o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLNominalReference;
			IDLNominalReference<Name, Klass, Role> otherNRef = (IDLNominalReference<Name, Klass, Role>) o;
			compare = getIndividual().compareTo(otherNRef.getIndividual());
		}
		return compare;
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(_nominal.toString());
		sb.append("}");
		return sb.toString();
	}


	public String toString(IToStringFormatter entityFormatter)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(entityFormatter.toString(_nominal));
		sb.append("}");
		return sb.toString();
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		} else {
			return ((obj instanceof IDLNominalReference)
				&& getIndividual().equals(((IDLNominalReference) obj).getIndividual()));
		}
	}


	@Override
	public int hashCode()
	{
		return getIndividual().hashCode();
	}


	@Override
	public IDLRestriction<Name, Klass, Role> getBefore()
	{
		return new DLDummyDescription<>(DLTermOrder.DL_BEFORE_NOMINAL_REFERENCE);
	}


	@Override
	public IDLRestriction<Name, Klass, Role> getAfter()
	{
		return new DLDummyDescription<>(DLTermOrder.DL_AFTER_NOMINAL_REFERENCE);
	}
}
