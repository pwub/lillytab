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
package de.uniba.wiai.kinf.pw.projects.lillytab.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IToStringFormatter;
import java.util.Arrays;
import java.util.HashSet;
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
public class LabelToStringConverter
	implements IToStringFormatter
{
	private final Set<OWLOntology> _ontologies = new HashSet<OWLOntology>();

	public LabelToStringConverter(final OWLOntology... ontologies)
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

		if (obj instanceof OWLEntity) {
			final OWLEntity entity = (OWLEntity) obj;
			final Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
			for (OWLOntology ontology : _ontologies)
				annotations.addAll(entity.getAnnotations(ontology));
			for (OWLAnnotation annotation : annotations) {
				if (annotation.getProperty().getIRI().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI())) {
					String label = annotation.getValue().toString();
					// strip data trailing datatype marker "^^xsd:", that gets appended by the OWLAPI.
					label = dataTypeEndPattern.matcher(label).replaceAll("");
					if (label.startsWith("\"") && label.endsWith("\""))
						label = label.substring(1, label.length() - 1);
					sb.append(label);
				}
			}
			if (sb.length() == 0) {
				if (entity.getIRI() != null) {
					final IRI iri = entity.getIRI();
					if (iri.getFragment() != null) {
						/* pick the fragment, if there is one */
						sb.append(entity.getIRI().getFragment());
					} else {
						/* pick the last component of the IRI path, if available */
						final Matcher m = iriPathSepEndPattern.matcher(iri.toString());
						if (m.find())
							sb.append(m.group(1));
					}
				} else
					sb.append(entity);
			}
		} else if (obj instanceof IDLClassReference) {
			IDLClassReference<?, ?, ?> classRef = (IDLClassReference<?, ?, ?>) obj;
			append(sb, classRef.getElement());
		} else
			sb.append(obj);

	}
}
