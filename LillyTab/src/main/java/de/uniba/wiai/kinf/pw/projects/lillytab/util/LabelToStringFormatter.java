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
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class LabelToStringFormatter
	implements IToStringFormatter {

	private static final boolean USE_ALL_ANNOTATIONS = false;
	private final Set<OWLOntology> _ontologies = new HashSet<>();


	public LabelToStringFormatter(final OWLOntology... ontologies)
	{
		_ontologies.addAll(Arrays.asList(ontologies));
	}


	@Override
	public String toString(final Object obj)
	{
		final StringBuilder sb = new StringBuilder();
		append(sb, obj);
		return sb.toString();
	}


	@Override
	public void append(final StringBuilder sb, final Object obj)
	{
		final Pattern dataTypeEndPattern = Pattern.compile("\\^\\^xsd:\\w+$");
		final Pattern iriPathSepEndPattern = Pattern.compile("/([^/]+)$");

		boolean haveLabel = false;
		if (obj instanceof OWLEntity) {
			final OWLEntity entity = (OWLEntity) obj;
			final Set<OWLAnnotation> annotations = new HashSet<>();
			for (OWLOntology ontology : _ontologies) {
				annotations.addAll(entity.getAnnotations(ontology));
			}
			for (OWLAnnotation annotation : annotations) {
				if (annotation.getProperty().getIRI().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI())) {
					String label = annotation.getValue().toString();
					// strip data trailing datatype marker "^^xsd:", that gets appended by the OWLAPI.
					label = dataTypeEndPattern.matcher(label).replaceAll("");

					/* strip trailing and leading quotation marks if both are present */
					if (label.startsWith("\"") && label.endsWith("\"")) {
						label = label.substring(1, label.length() - 1);
					}

					/* is there still is a non-empty label, append it */
					if (!label.isEmpty()) {
						haveLabel = true;
						sb.append(label);
						/* use only the first, non-empty annotation, if requested to do so */
						if (!USE_ALL_ANNOTATIONS) {
							break;
						}

					}
				}
			}

			/**
			 * Try to build the label from the IRI if we do not yet have one.
			 *
			 */
			if ((!haveLabel) && (entity.getIRI() != null)) {
				final IRI iri = entity.getIRI();
				if ((iri.getFragment() != null) && (!iri.getFragment().isEmpty())) {
					/* pick the fragment, if there is one */
					haveLabel = true;
					sb.append(entity.getIRI().getFragment());
				} else {
					/* pick the last component of the IRI path, if available */
					final Matcher m = iriPathSepEndPattern.matcher(iri.toString());
					if (m.find() && (m.groupCount() > 0) && (!m.group(1).isEmpty())) {
						haveLabel = true;
						sb.append(m.group(1));
					}
				}
			}
		}

		/**
		 * descend into class reference
		 *
		 */
		if ((!haveLabel) && (obj instanceof IDLClassReference)) {
			IDLClassReference<?, ?, ?, ?> classRef = (IDLClassReference<?, ?, ?, ?>) obj;
			haveLabel = true;
			append(sb, classRef.getElement());
		}

		/**
		 * descend into nominal reference *
		 *
		 */
		if ((!haveLabel) && (obj instanceof IDLIndividualReference)) {
			IDLIndividualReference<?, ?, ?, ?> nomRef = (IDLIndividualReference<?, ?, ?, ?>) obj;
			haveLabel = true;
			sb.append("{");
			append(sb, nomRef.getIndividual());
			sb.append("}");
		}

		if ((!haveLabel) && (obj instanceof Iterable)) {
			final Iterator<?> iter = ((Iterable<?>) obj).iterator();
			sb.append("[");
			boolean first = false;
			while (iter.hasNext()) {
				if (!first)
					sb.append(", ");
				sb.append(toString(iter.next()));
			}
			sb.append("]");
		}

		/**
		 * fallback: still no label: use object's toString()
		 *
		 */
		if (!haveLabel) {
			sb.append(obj);
		}
	}
}
