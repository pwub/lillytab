/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.io;

import de.dhke.projects.cutil.collections.set.SortedListSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDatatype;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.AnyDataType;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.OWLXMLVocabulary;
import uk.ac.manchester.cs.owl.owlapi.OWL2DatatypeImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryInternals;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryInternalsImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNaryDataRangeImpl;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class OWLAPIStorer {

	private OWLAPIStorer()
	{
	}


	public static OWLDataRange convertDataRange(
		final OWLDataFactory dataFactory,
		final IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> range)
	{
		if (range instanceof IDLDataIntersection) {
			final IDLDataIntersection<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> intersection =
				(IDLDataIntersection<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) range;
			final Set<OWLDataRange> convertedSubRanges = new SortedListSet<>(intersection.size());
			for (IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> subRange : intersection) {
				OWLDataRange convertedSubRange = convertDataRange(dataFactory, subRange);
				convertedSubRanges.add(convertedSubRange);
			}
			return dataFactory.getOWLDataIntersectionOf(convertedSubRanges);
		} else if (range instanceof IDLDataUnion) {
			final IDLDataUnion<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> union =
				(IDLDataUnion<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) range;
			final Set<OWLDataRange> convertedSubRanges = new SortedListSet<>(union.size());
			for (IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> subRange : union) {
				OWLDataRange convertedSubRange = convertDataRange(dataFactory, subRange);
				convertedSubRanges.add(convertedSubRange);
			}
			return dataFactory.getOWLDataUnionOf(convertedSubRanges);
		} else if (range instanceof IDLDataNegation) {
			final IDLDataNegation<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> neg = (IDLDataNegation<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) range;
			final OWLDataRange convertedSubTerm = convertDataRange(dataFactory, neg.getTerm());
			return dataFactory.getOWLDataComplementOf(convertedSubTerm);
		} else if (range instanceof IDLDatatype) {
			if (range instanceof OWLAPIDataType) {
				final OWLAPIDataType dataType = (OWLAPIDataType) range;
				return dataFactory.getOWLDatatype(dataType.getDatatypeIRI());
			} else if (range instanceof AnyDataType) {
				return dataFactory.getOWLDatatype(OWL2Datatype.RDFS_LITERAL.getIRI());
			} else
				throw new IllegalArgumentException("Only OWLAPI datatypes can be stored, got " + range.getClass());
		} else if (range instanceof IDLLiteralReference) {
			final IDLLiteralReference<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> litRef =
				(IDLLiteralReference<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) range;
			final OWLDataOneOf oneOf = dataFactory.getOWLDataOneOf(litRef.getLiteral());
			return oneOf;
		} else {

			throw new IllegalArgumentException("Unsupported data range type" + range.getClass());
		}
	}
}
