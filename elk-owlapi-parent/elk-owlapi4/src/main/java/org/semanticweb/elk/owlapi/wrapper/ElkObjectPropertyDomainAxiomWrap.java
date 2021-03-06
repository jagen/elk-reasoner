/*
 * #%L
 * ELK OWL API Binding
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyDomainAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkPropertyDomainAxiomVisitor;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;

/**
 * Implements the {@link ElkObjectPropertyDomainAxiom} interface by wrapping
 * instances of {@link OWLObjectPropertyDomainAxiom}.
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkObjectPropertyDomainAxiomWrap<T extends OWLObjectPropertyDomainAxiom>
		extends ElkObjectPropertyAxiomWrap<T> implements
		ElkObjectPropertyDomainAxiom {

	public ElkObjectPropertyDomainAxiomWrap(T owlObjectPropertyDomainAxiom) {
		super(owlObjectPropertyDomainAxiom);
	}

	@Override
	public ElkObjectPropertyExpression getProperty() {
		return converter.convert(getProperty(owlObject));
	}

	@Override
	public ElkClassExpression getDomain() {
		return converter.convert(getDomain(owlObject));
	}

	@Override
	public <O> O accept(ElkObjectPropertyAxiomVisitor<O> visitor) {
		return accept((ElkObjectPropertyDomainAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkPropertyDomainAxiomVisitor<O> visitor) {
		return accept((ElkObjectPropertyDomainAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkPropertyAxiomVisitor<O> visitor) {
		return accept((ElkObjectPropertyDomainAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectPropertyDomainAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
