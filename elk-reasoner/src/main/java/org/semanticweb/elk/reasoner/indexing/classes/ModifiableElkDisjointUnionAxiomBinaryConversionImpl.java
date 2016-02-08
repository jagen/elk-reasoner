package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointUnionAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectIntersectionOf;

/**
 * Implements {@link ModifiableElkDisjointUnionAxiomBinaryConversion}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableElkDisjointUnionAxiomBinaryConversionImpl
		extends
			ModifiableIndexedSubClassOfAxiomInferenceImpl<ElkDisjointUnionAxiom>
		implements
			ModifiableElkDisjointUnionAxiomBinaryConversion {

	private final int firstDisjunctPosition_, secondDisjunctPosition;

	ModifiableElkDisjointUnionAxiomBinaryConversionImpl(
			ElkDisjointUnionAxiom originalAxiom, int firstDisjunctPosition,
			int secondDisjunctPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		super(originalAxiom, conjunction, bottom);
		this.firstDisjunctPosition_ = firstDisjunctPosition;
		this.secondDisjunctPosition = secondDisjunctPosition;
	}

	@Override
	public int getFirstDisjunctPosition() {
		return firstDisjunctPosition_;
	}

	@Override
	public int getSecondDisjunctPosition() {
		return secondDisjunctPosition;
	}

	@Override
	public IndexedSubClassOfAxiom getConclusion() {
		return this;
	}

	@Override
	public final <O> O accept(
			IndexedSubClassOfAxiomInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
