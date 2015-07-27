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
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.util.stringer;

import de.dhke.projects.cutil.stringer.AbstractAnnotationStringer;
import de.dhke.projects.cutil.stringer.IToStringConverter;
import de.dhke.projects.cutil.stringer.SupportsType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
@SupportsType({OWLEntity.class})
public class OWLObjectLabelStringer
	extends AbstractAnnotationStringer {

	static final Pattern DATATYPE_END_PATTERN = Pattern.compile("\\^\\^xsd:\\w+$");
	static final Pattern IRI_PATHSEP_END_PATTERN = Pattern.compile("/([^/]+)$");
	private static final boolean USE_ALL_ANNOTATIONS = false;
	private final Set<OWLOntology> _ontologies = new HashSet<>();


	public OWLObjectLabelStringer(final OWLOntology... ontologies)
	{
		_ontologies.addAll(Arrays.asList(ontologies));
	}


	public void append(final StringBuilder sb, final OWLEntity entity)
	{
		boolean haveLabel = false;
		final Set<OWLAnnotation> annotations = new HashSet<>();
		for (OWLOntology ontology : _ontologies) {
			annotations.addAll(EntitySearcher.getAnnotations(entity, ontology));
		}
		for (OWLAnnotation annotation : annotations) {
			if (annotation.getProperty().getIRI().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI())) {
				String label = annotation.getValue().toString();
				// strip data trailing datatype marker "^^xsd:", that gets appended by the OWLAPI.
				label = DATATYPE_END_PATTERN.matcher(label).replaceAll("");

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
		if (!haveLabel) {
			final IRI iri = entity.getIRI();
			if (! iri.getRemainder().or("").isEmpty()) {
				/* pick the fragment, if there is one */
				haveLabel = true;
				sb.append(iri.getRemainder().get());
			} else {
				/* pick the last component of the IRI path, if available */
				final Matcher m = IRI_PATHSEP_END_PATTERN.matcher(iri.toString());
				if (m.find() && (m.groupCount() > 0) && (!m.group(1).isEmpty())) {
					haveLabel = true;
					sb.append(m.group(1));
				}
			}
		}

		/**
		 * fallback: still no label: use object's toString()
		 *
		 */
		if (!haveLabel) {
			sb.append(entity);
		}
	}


	@Override
	public void append(StringBuilder sb, Object obj, final IToStringConverter backStringer)
	{
		if (obj instanceof OWLEntity) {
			final OWLEntity entity = (OWLEntity) obj;
			append(sb, entity);
		} else
			append(sb, obj);
	}


	@Override
	public String toString(Object obj)
	{
		final StringBuilder sb = new StringBuilder();
		append(sb, obj);
		return sb.toString();
	}
}
