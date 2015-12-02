/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.DummySaturationInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;

/**
 * A {@link SaturationInferenceVisitor} that passes the side conditions of
 * inferences to the given {@link ElkAxiomVisitor}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 */
public class SideConditionVisitor<O>
		extends
			DummySaturationInferenceVisitor<O> {

	private final SideConditionLookup lookup_ = new SideConditionLookup();

	private final ElkAxiomVisitor<O> visitor_;

	public SideConditionVisitor(ElkAxiomVisitor<O> visitor) {
		this.visitor_ = visitor;
	}

	@Override
	protected O defaultVisit(ClassInference conclusion) {
		ElkAxiom sideCondition = lookup_.lookup(conclusion);

		if (sideCondition != null) {
			return sideCondition.accept(visitor_);
		}

		return null;
	}

	@Override
	protected O defaultVisit(ObjectPropertyInference conclusion) {
		ElkAxiom sideCondition = lookup_.lookup(conclusion);

		if (sideCondition != null) {
			return sideCondition.accept(visitor_);
		}

		return null;
	}

}