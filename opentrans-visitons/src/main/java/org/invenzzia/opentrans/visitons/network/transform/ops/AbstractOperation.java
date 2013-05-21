/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.network.transform.ops;

import com.google.common.base.Preconditions;
import java.util.LinkedList;
import java.util.List;
import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;
import org.invenzzia.opentrans.visitons.network.transform.TransformInput;
import org.invenzzia.opentrans.visitons.network.transform.conditions.ICondition;
import org.invenzzia.opentrans.visitons.network.transform.modifiers.IModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common code for a single transformation operation (such as vertex movement). The
 * operation usually consists of several use cases for different scenarios. Each scenario
 * is represented as a condition object graph.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractOperation implements IOperation {
	private static final Logger logger = LoggerFactory.getLogger(AbstractOperation.class);
	
	private final List<CaseDefinition> cases;
	private ICondition<TransformInput> initialCondition;
	private IModifier initialModifier;
	private ITransformAPI api;
	
	public AbstractOperation() {
		this.cases = new LinkedList<CaseDefinition>();
		this.configure();
	}
	
	@Override
	public void setTransformAPI(ITransformAPI api) {
		this.api = Preconditions.checkNotNull(api);
	}
	
	protected final ITransformAPI getAPI() {
		return this.api;
	}

	/**
	 * Sets the initial conditions verified before everything else.
	 * 
	 * @param condition 
	 */
	protected final void initialCondition(ICondition<TransformInput> condition) {
		this.initialCondition = condition;
	}
	
	/**
	 * Sets the initial modifier applied after validating the initial conditions.
	 * 
	 * @param modifier 
	 */
	protected final void initialModifier(IModifier modifier) {
		this.initialModifier = modifier;
	}
	
	/**
	 * Registers a new case that is fired, if the given condition matches.
	 * 
	 * @param condition
	 * @param caseImpl 
	 */
	protected final void register(ICondition<TransformInput> condition, IOperationCase caseImpl) {
		this.cases.add(new CaseDefinition(condition, null, caseImpl));
	}
	
	/**
	 * Registers a new case that is fired, if the given condition matches. Before entering
	 * the case, the specified modifier is applied to the input.
	 * 
	 * @param condition
	 * @param modifier
	 * @param caseImpl 
	 */
	protected final void register(ICondition<TransformInput> condition, IModifier modifier, IOperationCase caseImpl) {
		this.cases.add(new CaseDefinition(condition, modifier, caseImpl));
	}
	
	/**
	 * The method shall be called in the custom-specified <tt>do()</tt> method to evaluate
	 * the case conditions on the constructed input.
	 * 
	 * @param input
	 * @return True, if the operation has been performed.
	 */
	protected final boolean evaluateCases(TransformInput input) {
		this.importData(input, this.api);
		if(null != this.initialCondition) {
			if(!this.initialCondition.matches(input)) {
				return false;
			}
		}
		if(null != this.initialModifier) {
			this.initialModifier.modify(input);
		}
		int idx = 1;
		for(CaseDefinition caseDef: this.cases) {
			try {
				if(caseDef.evaluate(input)) {
					return true;
				}
				idx++;
			} catch(RuntimeException exception) {
				logger.error("Failure occurred while evaluating conditions for case #"+idx);
				throw exception;
			}
		}
		return false;
	}
	
	/**
	 * The programmer may overwrite this method to provide custom data importer.
	 * The method is called before performing any checks.
	 * 
	 * @param input The input to the operation.
	 * @param api The transformation API
	 */
	protected void importData(TransformInput input, ITransformAPI api) {
	}
	
	/**
	 * Configures the operation: registers the cases and specifies their conditions.
	 */
	protected abstract void configure();
	
	/**
	 * Structure of a single use case.
	 */
	private class CaseDefinition {
		public final ICondition<TransformInput> condition;
		public final IModifier modifier;
		public final IOperationCase caseImpl;
		
		public CaseDefinition(ICondition<TransformInput> condition, IModifier modifier, IOperationCase caseImpl) {
			this.condition = Preconditions.checkNotNull(condition);
			this.modifier = modifier;
			this.caseImpl = Preconditions.checkNotNull(caseImpl);
		}
		
		public boolean evaluate(TransformInput input) {
			if(this.condition.matches(input)) {
				if(null != this.modifier) {
					this.modifier.modify(input);
				}
				this.caseImpl.execute(input, api);
				return true;
			}
			return false;
		}
	}		
}
