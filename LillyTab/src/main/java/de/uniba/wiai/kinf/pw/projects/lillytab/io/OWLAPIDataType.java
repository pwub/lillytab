/**
 * (c) 2009-2014 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.io;

import com.sun.msv.datatype.xsd.*;
import de.dhke.projects.cutil.collections.set.Flat3Set;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDatatype;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.AnyDataType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.visitor.IDLTermVisitor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.relaxng.datatype.ValidationContext;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.vocab.XSDVocabulary;


/**
 * A wrapper that implments datatypes for the {@link OWLAPILoader} and forwards validation to a back to
 * {@link XSDatatypeImpl}.
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class OWLAPIDataType
	implements IDLDatatype<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>
{
	private static final BidiMap<IRI, XSDatatypeImpl> _dataTypeMap = new DualHashBidiMap<>();

	static {
		_dataTypeMap.put(XSDVocabulary.ANY_URI.getIRI(), AnyURIType.theInstance);
		_dataTypeMap.put(XSDVocabulary.BASE_64_BINARY.getIRI(), Base64BinaryType.theInstance);
		_dataTypeMap.put(XSDVocabulary.BOOLEAN.getIRI(), BooleanType.theInstance);
		_dataTypeMap.put(XSDVocabulary.BYTE.getIRI(), ByteType.theInstance);
		_dataTypeMap.put(XSDVocabulary.DATE.getIRI(), DateType.theInstance);
		_dataTypeMap.put(XSDVocabulary.DATE_TIME.getIRI(), DateTimeType.theInstance);
		_dataTypeMap.put(XSDVocabulary.DOUBLE.getIRI(), DoubleType.theInstance);
		_dataTypeMap.put(XSDVocabulary.DURATION.getIRI(), DurationType.theInstance);
		_dataTypeMap.put(XSDVocabulary.ENTITY.getIRI(), EntityType.theInstance);
		_dataTypeMap.put(XSDVocabulary.FLOAT.getIRI(), FloatType.theInstance);
		_dataTypeMap.put(XSDVocabulary.HEX_BINARY.getIRI(), HexBinaryType.theInstance);
		_dataTypeMap.put(XSDVocabulary.ID.getIRI(), IDType.theInstance);
		_dataTypeMap.put(XSDVocabulary.IDREF.getIRI(), IDREFType.theInstance);
		_dataTypeMap.put(XSDVocabulary.INT.getIRI(), IntType.theInstance);
		_dataTypeMap.put(XSDVocabulary.INTEGER.getIRI(), IntegerType.theInstance);
		_dataTypeMap.put(XSDVocabulary.LANGUAGE.getIRI(), LanguageType.theInstance);
		_dataTypeMap.put(XSDVocabulary.LONG.getIRI(), LongType.theInstance);
		_dataTypeMap.put(XSDVocabulary.NAME.getIRI(), NameType.theInstance);
		_dataTypeMap.put(XSDVocabulary.NCNAME.getIRI(), NcnameType.theInstance);
		_dataTypeMap.put(XSDVocabulary.NEGATIVE_INTEGER.getIRI(), NegativeIntegerType.theInstance);
		_dataTypeMap.put(XSDVocabulary.NMTOKEN.getIRI(), NmtokenType.theInstance);
		_dataTypeMap.put(XSDVocabulary.NON_NEGATIVE_INTEGER.getIRI(), NonNegativeIntegerType.theInstance);
		_dataTypeMap.put(XSDVocabulary.NON_POSITIVE_INTEGER.getIRI(), NonPositiveIntegerType.theInstance);
		_dataTypeMap.put(XSDVocabulary.NORMALIZED_STRING.getIRI(), NormalizedStringType.theInstance);
		_dataTypeMap.put(XSDVocabulary.POSITIVE_INTEGER.getIRI(), PositiveIntegerType.theInstance);
		_dataTypeMap.put(XSDVocabulary.G_DAY.getIRI(), GDayType.theInstance);
		_dataTypeMap.put(XSDVocabulary.G_MONTH.getIRI(), GMonthType.theInstance);
		_dataTypeMap.put(XSDVocabulary.G_MONTH_DAY.getIRI(), GMonthDayType.theInstance);
		_dataTypeMap.put(XSDVocabulary.G_YEAR.getIRI(), GYearType.theInstance);
		_dataTypeMap.put(XSDVocabulary.G_YEAR_MONTH.getIRI(), GYearMonthType.theInstance);
		_dataTypeMap.put(XSDVocabulary.Q_NAME.getIRI(), QnameType.theInstance);
		_dataTypeMap.put(XSDVocabulary.SHORT.getIRI(), ShortType.theInstance);
		_dataTypeMap.put(XSDVocabulary.STRING.getIRI(), StringType.theInstance);
		_dataTypeMap.put(XSDVocabulary.TIME.getIRI(), TimeType.theInstance);
		_dataTypeMap.put(XSDVocabulary.TOKEN.getIRI(), TokenType.theInstance);
		_dataTypeMap.put(XSDVocabulary.UNSIGNED_BYTE.getIRI(), UnsignedByteType.theInstance);
		_dataTypeMap.put(XSDVocabulary.UNSIGNED_INT.getIRI(), UnsignedIntType.theInstance);
		_dataTypeMap.put(XSDVocabulary.UNSIGNED_LONG.getIRI(), UnsignedLongType.theInstance);
		_dataTypeMap.put(XSDVocabulary.UNSIGNED_SHORT.getIRI(), UnsignedShortType.theInstance);
	}
	private final IRI _datatypeIRI;
	private final XSDatatypeImpl _dataTypeImpl;
	private final ValidationContext _validationCtx = new SimpleValidationContext();

	public OWLAPIDataType(final IRI datatypeIRI)
		throws EUnsupportedDatatypeException
	{
		_datatypeIRI = datatypeIRI;
		_dataTypeImpl = _dataTypeMap.get(datatypeIRI);
		if (_dataTypeImpl == null) {
			throw new EUnsupportedDatatypeException("Unsupported datatype IRI: " + datatypeIRI);
		}
	}

	public IRI getDatatypeIRI()
	{
		return _datatypeIRI;
	}

	@Override
	public boolean isValidValue(OWLLiteral literal)
	{
		final String stringLit = getStringLiteral(literal);
		return (stringLit != null) && _dataTypeImpl.isValid(stringLit, _validationCtx);
	}

	public String getStringLiteral(OWLLiteral literal)
	{
		return literal.getLiteral();
	}

	@Override
	public boolean isCompatibleValue(final OWLLiteral lit1, final OWLLiteral lit2, final OWLLiteral... otherLits)
	{
		final String stringLit1 = getStringLiteral(lit1);
		final String stringLit2 = getStringLiteral(lit2);
		final Object firstValue = _dataTypeImpl.createValue(stringLit1, _validationCtx);
		final Object secondValue = _dataTypeImpl.createValue(stringLit2, _validationCtx);
		if (_dataTypeImpl.sameValue(firstValue, secondValue)) {
			for (int i = 0; i < otherLits.length; ++i) {
				final String literal = getStringLiteral(otherLits[i]);
				if (literal == null) {
					return false;
				}
				final Object otherValue = _dataTypeImpl.createValue(literal, _validationCtx);
				if (!_dataTypeImpl.sameValue(firstValue, otherValue)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean isCompatibleValue(final Collection<? extends OWLLiteral> literals)
	{
		if (literals.isEmpty()) {
			return true;
		} else {
			final Iterator<? extends OWLLiteral> iter = literals.iterator();
			final String firstLiteral = getStringLiteral(iter.next());
			final Object firstValue = _dataTypeImpl.createValue(firstLiteral, _validationCtx);
			while (iter.hasNext()) {
				final String thisLiteral = getStringLiteral(iter.next());
				final Object thisValue = _dataTypeImpl.createValue(thisLiteral, _validationCtx);
				if (!_dataTypeImpl.sameValue(firstValue, thisValue)) {
					return false;
				}
			}
			return true;
		}
	}

	@Override
	public Set<Set<OWLLiteral>> getIncompatibleValues(Collection<? extends OWLLiteral> literals)
	{
		final Set<Set<OWLLiteral>> incompatibles = new HashSet<>();
		for (OWLLiteral thisObject : literals) {
			final String thisLiteral = getStringLiteral(thisObject);
			final Object thisValue = _dataTypeImpl.createValue(thisLiteral, _validationCtx);
			for (OWLLiteral otherObject : literals) {
				if ((thisObject != otherObject) && (!thisObject.equals(otherObject))) {
					final String otherLiteral = getStringLiteral(otherObject);
					final Object otherValue = _dataTypeImpl.createValue(otherLiteral, _validationCtx);
					if (!_dataTypeImpl.sameValue(thisValue, otherValue)) {
						incompatibles.add(new Flat3Set<>(thisObject, otherObject));
					}
				}
			}
		}
		return incompatibles;
	}

	@Override
	public IDLTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> getBefore()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_BEFORE_DATATYPE_EXPRESSION);
	}

	@Override
	public IDLTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> getAfter()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_AFTER_DATATYPE_EXPRESSION);
	}

	@Override
	public DLTermOrder getDLTermOrder()
	{
		return DLTermOrder.DL_DATATYPE_EXPRESSION;
	}

	@Override
	public OWLAPIDataType clone()
	{
		return this;
	}

	@Override
	public int compareTo(IDLTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			if (o instanceof AnyDataType)
				/* any sorts before all other datatypes */
				return 1;
			else if (!(o instanceof OWLAPIDataType)) {
				throw new EInvalidTermException(
					"Mixing OWLAPIDatatype with other datatypes other than any is not supported");
			} else {
				OWLAPIDataType other = (OWLAPIDataType) o;
				compare = getDatatypeIRI().compareTo(other.getDatatypeIRI());
			}
		}
		return compare;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder(10);
		if (_dataTypeImpl.getName() != null) {
			sb.append("xsd:");
			sb.append(_dataTypeImpl.getName());
		} else
			sb.append("<anonymous>");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		else if (obj instanceof OWLAPIDataType) {
			OWLAPIDataType other = (OWLAPIDataType) obj;
			return getDatatypeIRI().equals(other.getDatatypeIRI());
		} else
			return false;
	}

	@Override
	public int hashCode()
	{
		return _datatypeIRI.hashCode();
	}

	@Override
	public void accept(
		IDLTermVisitor<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> visitor)
	{
		visitor.visit(this);
	}

	@Override
	public boolean isTopDatatype()
	{
		/* literal/top is represented by its own separate class */
		return false;
	}

	@Override
	public boolean isBottomDatatype()
	{
		/* this does not exist, here */
		return false;
	}

}
