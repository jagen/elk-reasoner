package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

/**
 * An {@link IndexedAxiom} constructed from an {@link IndexedClass} and an
 * {@link IndexedClassExpression}.<br>
 * 
 * Notation:
 * 
 * <pre>
 * [A = D]
 * </pre>
 * 
 * It is logically equivalent to the OWL axiom {@code EquivalentClasses(A D)}
 * <br>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * A = {@link #getDefinedClass()}<br>
 * D = {@link #getDefinition()}<br>
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedDefinitionAxiom extends IndexedAxiom {

	IndexedClass getDefinedClass();

	IndexedClassExpression getDefinition();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedDefinitionAxiom axiom);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		IndexedDefinitionAxiom getIndexedDefinitionAxiom(ElkAxiom originalAxiom,
				IndexedClass definedClass, IndexedClassExpression definition);

	}

}
