package org.codefx.libfx.nesting.property;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Abstract superclass to tests for {@link NestedFloatProperty NestedFloatProperty} which only leaves the creation of
 * the tested properties (by {@link #createNestedPropertyFromNesting(Nesting, InnerObservableMissingBehavior)}) to the
 * subclasses.
 */
public abstract class AbstractNestedFloatPropertyTest extends
		AbstractNestedPropertyTest<Float, Number, FloatProperty> {

	/**
	 * The last value returned by {@link #createNewValue()}.
	 */
	private float lastValue = 1.5f;

	@Override
	protected boolean wrapsPrimitive() {
		return true;
	}

	@Override
	protected Float createNewValue() {
		lastValue += 1;
		return lastValue;
	}

	@Override
	protected FloatProperty createNewObservableWithValue(Number value) {
		return new SimpleFloatProperty(value.floatValue());
	}

	@Override
	protected FloatProperty createNewObservableWithSomeValue() {
		return createNewObservableWithValue(0.0);
	}

}
