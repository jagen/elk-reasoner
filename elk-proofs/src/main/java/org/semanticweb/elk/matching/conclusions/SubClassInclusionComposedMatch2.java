package org.semanticweb.elk.matching.conclusions;

/*-
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.matching.root.IndexedContextRootMatch;

public class SubClassInclusionComposedMatch2
		extends AbstractClassConclusionMatch<SubClassInclusionComposedMatch1> {

	private final IndexedContextRootMatch extendedDestinationMatch_;

	SubClassInclusionComposedMatch2(SubClassInclusionComposedMatch1 parent,
			IndexedContextRootMatch extendedDestinationMatch) {
		super(parent);
		this.extendedDestinationMatch_ = extendedDestinationMatch;
	}

	public IndexedContextRootMatch getExtendedDestinationMatch() {
		return extendedDestinationMatch_;
	}

	@Override
	public <O> O accept(ClassConclusionMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubClassInclusionComposedMatch2 getSubClassInclusionComposedMatch2(
				SubClassInclusionComposedMatch1 parent,
				IndexedContextRootMatch extendedDestinationMatch);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(SubClassInclusionComposedMatch2 conclusionMatch);

	}

}
