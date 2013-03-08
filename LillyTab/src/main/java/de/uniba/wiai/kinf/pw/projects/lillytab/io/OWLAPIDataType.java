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
package de.uniba.wiai.kinf.pw.projects.lillytab.io;

import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;
import com.sun.msv.datatype.xsd.*;
import de.dhke.projects.cutil.collections.set.Flat3Set;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datatype.IDLDatatype;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.relaxng.datatype.ValidationContext;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

/**
 * A wrapper that implments datatypes for the {@link OWLAPILoader} and forwards validation to a
 * {@link StringLiteralValidator}.
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class OWLAPIDataType
	implements IDLDatatype<OWLObject, OWLClass, OWLProperty<?, ?>> {

	private final IRI _datatypeIRI;
	private final XSDatatypeImpl _dataTypeImpl;
	private final ValidationContext _validationCtx = new SimpleValidationContext();
	private static final BidiMap<IRI, XSDatatypeImpl> _dataTypeMap = new DualHashBidiMap<>();


	static {
		/* XXX: fill */
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


	public IRI getDatatypeIRI()
	{
		return _datatypeIRI;
	}


	public OWLAPIDataType(final IRI datatypeIRI)
		throws EUnsupportedDatatypeException
	{
		_datatypeIRI = datatypeIRI;
		_dataTypeImpl = _dataTypeMap.get(datatypeIRI);
		if (_dataTypeImpl == null) {
			throw new EUnsupportedDatatypeException("Unsupported datatype IRI: " + datatypeIRI);
		}
	}


	public boolean isValidValue(OWLObject individual)
	{
		String literal = getStringLiteral(individual);
		return (literal != null) && _dataTypeImpl.isValid(literal, _validationCtx);
	}


	public String getStringLiteral(OWLObject individual)
	{
		if (individual instanceof OWLLiteral) {
			final OWLLiteral literal = (OWLLiteral) individual;
			return literal.getLiteral();
		} else {
			return null;
		}
	}


	public boolean isCompatibleValue(final OWLObject ind1, final OWLObject ind2, final OWLObject... otherInds)
	{
		final String lit1 = getStringLiteral(ind1);
		final String lit2 = getStringLiteral(ind2);
		final Object firstValue = _dataTypeImpl.createValue(lit1, _validationCtx);
		final Object secondValue = _dataTypeImpl.createValue(lit2, _validationCtx);
		if (_dataTypeImpl.sameValue(firstValue, secondValue)) {
			for (int i = 0; i < otherInds.length; ++i) {
				final String literal = getStringLiteral(otherInds[i]);
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


	public boolean isCompatibleValue(final Collection<? extends OWLObject> individuals)
	{
		if (individuals.isEmpty()) {
			return true;
		} else {
			final Iterator<? extends OWLObject> iter = individuals.iterator();
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
	public Set<Set<OWLObject>> getIncompatibleValues(Collection<? extends OWLObject> individuals)
	{
		final Set<Set<OWLObject>> incompatibles = new HashSet<>();
		for (OWLObject thisObject : individuals) {
			final String thisLiteral = getStringLiteral(thisObject);
			final Object thisValue = _dataTypeImpl.createValue(thisLiteral, _validationCtx);
			for (OWLObject otherObject : individuals) {
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
	public IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> getBefore()
	{
		return new DLDummyDescription<>(DLTermOrder.DL_BEFORE_DATATYPE_EXPRESSION);
	}


	@Override
	public IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> getAfter()
	{
		return new DLDummyDescription<>(DLTermOrder.DL_AFTER_DATATYPE_EXPRESSION);
	}


	@Override
	public DLTermOrder getDLTermOrder()
	{
		return DLTermOrder.DL_DATATYPE_EXPRESSION;
	}


	@Override
	public ITerm clone()
	{
		return this;
	}


	@Override
	public int compareTo(IDLTerm<OWLObject, OWLClass, OWLProperty<?, ?>> o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			if (!(o instanceof OWLAPIDataType)) {
				throw new EInvalidTermException("Mixing OWLAPIDatatype with other datatypes is not supported");
			}
			@SuppressWarnings("unchecked")
			OWLAPIDataType other = (OWLAPIDataType) o;
			compare = getDatatypeIRI().compareTo(other.getDatatypeIRI());
		}
		return compare;
	}


	@Override
	public String toString()
	{
		/// TODO: this needs to be changed
		return "Datatype: " + _datatypeIRI;
	}


	@Override
	public String toString(IToStringFormatter entityFormatter)
	{
		return toString();
	}
}
