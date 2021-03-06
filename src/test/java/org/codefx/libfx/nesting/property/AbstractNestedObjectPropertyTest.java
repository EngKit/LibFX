package org.codefx.libfx.nesting.property;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.testhelper.SomeValue;

/**
 * Abstract superclass to tests for {@link NestedObjectProperty NestedObjectProperties} which only leaves the creation
 * of the tested properties (by {@link #createNestedPropertyFromNesting(Nesting, InnerObservableMissingBehavior)}) to
 * the subclasses.
 */
public abstract class AbstractNestedObjectPropertyTest
		extends AbstractNestedPropertyTest<SomeValue, SomeValue, Property<SomeValue>> {

	@Override
	protected final boolean wrapsPrimitive() {
		return false;
	}

	@Override
	protected final SomeValue createNewValue() {
		return new SomeValue();
	}

	@Override
	protected final Property<SomeValue> createNewObservableWithValue(SomeValue value) {
		return new SimpleObjectProperty<>(value);
	}

	@Override
	protected final Property<SomeValue> createNewObservableWithSomeValue() {
		SomeValue someValue = new SomeValue();
		return createNewObservableWithValue(someValue);
	}

}
