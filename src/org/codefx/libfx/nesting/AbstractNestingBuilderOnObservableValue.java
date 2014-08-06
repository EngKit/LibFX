package org.codefx.libfx.nesting;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.listener.NestedChangeListener;
import org.codefx.libfx.nesting.listener.NestedChangeListenerBuilder;

/**
 * A nesting builder which allows adding change listeners.
 *
 * @param <T>
 *            the type of the wrapped value
 * @param <O>
 *            the type of observable this builder can build
 */
abstract class AbstractNestingBuilderOnObservableValue<T, O extends ObservableValue<T>>
		extends AbstractNestingBuilderOnObservable<T, O> {

	// #region CONSTRUCTION

	/**
	 * Creates a new nesting builder which acts as the outer builder.
	 *
	 * @param outerObservable
	 *            the outer observable upon which the constructed nesting depends
	 */
	protected AbstractNestingBuilderOnObservableValue(O outerObservable) {
		super(outerObservable);
	}

	/**
	 * Creates a new nesting builder which acts as a nested builder.
	 *
	 * @param <P>
	 *            the type the previous builder wraps
	 * @param previousNestedBuilder
	 *            the previous builder
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 */
	protected <P> AbstractNestingBuilderOnObservableValue(
			AbstractNestingBuilderOnObservable<P, ?> previousNestedBuilder, NestingStep<P, O> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

	// #region LISTENERS

	/**
	 * Adds the specified change listener to the nesting hierarchy's inner {@link ObservableValue}.
	 *
	 * @param listener
	 *            the added {@link ChangeListener}
	 * @return the {@link NestedChangeListener} which can be used to check the nesting's state
	 */
	public NestedChangeListener<T> addListener(ChangeListener<? super T> listener) {
		Nesting<O> nesting = buildNesting();
		return NestedChangeListenerBuilder
				.forNesting(nesting)
				.withListener(listener)
				.build();
	}

	//#end LISTENERS

}
