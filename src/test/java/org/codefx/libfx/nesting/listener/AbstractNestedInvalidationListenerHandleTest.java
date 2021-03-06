package org.codefx.libfx.nesting.listener;

import static org.codefx.libfx.nesting.testhelper.NestingAccess.getNestingObservable;
import static org.codefx.libfx.nesting.testhelper.NestingAccess.setNestingObservable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import org.codefx.libfx.listener.handle.CreateListenerHandle;
import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.testhelper.NestingAccess;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to tests of {@link NestedInvalidationListenerHandle}.
 */
public abstract class AbstractNestedInvalidationListenerHandleTest {

	// #begin INSTANCES USED FOR TESTING

	/**
	 * The nesting's inner observable.
	 */
	private StringProperty innerObservable;

	/**
	 * The nesting to which the listener is added.
	 */
	private NestingAccess.EditableNesting<StringProperty> nesting;

	/**
	 * The default listener. This {@link InvalidationListener} will be mocked to verify possible invocations.
	 */
	private InvalidationListener listener;

	/**
	 * A listener which fails the test when being called.
	 */
	private InvalidationListener listenerWhichFailsWhenCalled;

	/**
	 * The tested nested listener, which adds the {@link #listener} to the {@link #nesting}.
	 */
	private NestedInvalidationListenerHandle nestedListenerHandle;

	//#end INSTANCES USED FOR TESTING

	// #begin SETUP

	/**
	 * Creates a new instance of {@link #nesting} and {@link #listener}.
	 * <p>
	 * Note: A {@link #nestedListenerHandle} has to be created in the test according to the desired initial state
	 * (attached or detached).
	 */
	@Before
	public void setUp() {
		innerObservable = new SimpleStringProperty();
		nesting = NestingAccess.EditableNesting.createWithInnerObservable(innerObservable);
		listener = mock(InvalidationListener.class);
		listenerWhichFailsWhenCalled = any -> fail();
	}

	/**
	 * Creates a new, initially attached nested listener from the specified nesting and listener.
	 *
	 * @param nesting
	 *            the {@link Nesting} to which the listener will be added
	 * @param listener
	 *            the {@link ChangeListener} which will be added to the nesting
	 * @return a new {@link NestedChangeListenerHandle}
	 */
	private NestedInvalidationListenerHandle createAttachedNestedListenerHandle(
			Nesting<? extends Observable> nesting, InvalidationListener listener) {

		return createNestedListenerHandle(nesting, listener, CreateListenerHandle.ATTACHED);
	}

	/**
	 * Creates a new, initially detached nested listener from the specified nesting and listener.
	 *
	 * @param nesting
	 *            the {@link Nesting} to which the listener will be added
	 * @param listener
	 *            the {@link ChangeListener} which will be added to the nesting
	 * @return a new {@link NestedChangeListenerHandle}
	 */
	private NestedInvalidationListenerHandle createDetachedNestedListenerHandle(
			Nesting<? extends Observable> nesting, InvalidationListener listener) {

		return createNestedListenerHandle(nesting, listener, CreateListenerHandle.DETACHED);
	}

	/**
	 * Creates a new nested listener from the specified nesting and listener.
	 *
	 * @param nesting
	 *            the {@link Nesting} to which the listener will be added
	 * @param listener
	 *            the {@link ChangeListener} which will be added to the nesting
	 * @param attachedOrDetached
	 *            indicates whether the listener will be initially attached or detached
	 * @return a new {@link NestedChangeListenerHandle}
	 */
	protected abstract NestedInvalidationListenerHandle createNestedListenerHandle(
			Nesting<? extends Observable> nesting,
			InvalidationListener listener,
			CreateListenerHandle attachedOrDetached);

	//end SETUP

	// #begin TESTS

	// construction

	/**
	 * Tests whether the properties the tested nested listener owns have the correct bean.
	 */
	@Test
	public void testPropertyBean() {
		nestedListenerHandle = createDetachedNestedListenerHandle(nesting, listener);
		assertSame(nestedListenerHandle, nestedListenerHandle.innerObservablePresentProperty().getBean());
	}

	/**
	 * Tests whether the {@link #nestedListenerHandle} correctly reports whether the inner observable is present.
	 */
	@Test
	public void testObservablePresentAfterConstruction() {
		nestedListenerHandle = createDetachedNestedListenerHandle(nesting, listener);
		assertTrue(nestedListenerHandle.isInnerObservablePresent());
	}

	/**
	 * Tests whether the construction does not call the {@link #listener}.
	 */
	@Test
	public void testNoInteractionWithListenerDuringConstruction() {
		nestedListenerHandle = createDetachedNestedListenerHandle(nesting, listenerWhichFailsWhenCalled);
		nestedListenerHandle = createAttachedNestedListenerHandle(nesting, listenerWhichFailsWhenCalled);
	}

	// changing value

	/**
	 * Tests whether no listener invocation occurs when the nesting's observable changes its value and the listener is
	 * initially detached.
	 */
	@Test
	public void testChangingValueWhenInitiallyDetached() {
		nestedListenerHandle = createDetachedNestedListenerHandle(nesting, listenerWhichFailsWhenCalled);
		innerObservable.set("new value");
	}

	/**
	 * Tests whether the listener is correctly invoked when the nesting's observable changes its value.
	 */
	@Test
	public void testChangingValue() {
		nestedListenerHandle = createAttachedNestedListenerHandle(nesting, listener);
		innerObservable.set("new value");

		// assert that 'invalidated' was called once and with the right observable
		verify(listener, times(1)).invalidated(innerObservable);
		verifyNoMoreInteractions(listener);
	}

	// changing observable

	/**
	 * Tests whether no listener invocation occurs when the nesting's inner observable is changed.
	 */
	@Test
	public void testChangingObservable() {
		nestedListenerHandle = createAttachedNestedListenerHandle(nesting, listenerWhichFailsWhenCalled);
		StringProperty newObservable = new SimpleStringProperty();
		setNestingObservable(nesting, newObservable);

		assertTrue(nestedListenerHandle.isInnerObservablePresent());
	}

	/**
	 * Tests whether no listener invocation occurs when the nesting's inner observable is changed to null.
	 */
	@Test
	public void testChangingObservableToNull() {
		nestedListenerHandle = createAttachedNestedListenerHandle(nesting, listenerWhichFailsWhenCalled);
		setNestingObservable(nesting, null);

		assertFalse(nestedListenerHandle.isInnerObservablePresent());
	}

	// changing observable and value

	/**
	 * Tests whether the listener is correctly invoked when the nesting's new observable gets a new value.
	 */
	@Test
	public void testChangingNewObservablesValue() {
		nestedListenerHandle = createAttachedNestedListenerHandle(nesting, listener);
		// set a new observable ...
		StringProperty newObservable = new SimpleStringProperty();
		setNestingObservable(nesting, newObservable);
		// (assert that setting the observable worked)
		assertEquals(newObservable, getNestingObservable(nesting));

		// ... and change its value
		newObservable.setValue("new observable's new value");

		// assert that the listener was invoked once and with the right observable
		verify(listener, times(1)).invalidated(newObservable);
		verifyNoMoreInteractions(listener);
	}

	/**
	 * Tests whether the listener is not invoked when the nesting's old observable gets a new value.
	 */
	@Test
	public void testChangingOldObservablesValue() {
		nestedListenerHandle = createAttachedNestedListenerHandle(nesting, listenerWhichFailsWhenCalled);
		// set a new observable ...
		StringProperty newObservable = new SimpleStringProperty();
		setNestingObservable(nesting, newObservable);
		// (assert that setting the observable worked)
		assertEquals(newObservable, getNestingObservable(nesting));

		// ... and change the old observable's value
		innerObservable.setValue("intial observable's new value");
	}

	// attach & detach

	/**
	 * Tests whether no listener invocation occurs when the nesting's inner observable's value is changed after the
	 * listener was detached.
	 */
	@Test
	public void testDetach() {
		nestedListenerHandle = createAttachedNestedListenerHandle(nesting, listenerWhichFailsWhenCalled);
		nestedListenerHandle.detach();

		innerObservable.set("new value while detached");
	}

	/**
	 * Tests whether the listener ignores values after it was detached repeatedly.
	 */
	@Test
	public void testMultipleDetach() {
		nestedListenerHandle = createAttachedNestedListenerHandle(nesting, listenerWhichFailsWhenCalled);
		nestedListenerHandle.detach();
		nestedListenerHandle.detach();
		nestedListenerHandle.detach();

		innerObservable.set("new value while detached");
	}

	/**
	 * Tests whether the listener is correctly invoked when the nesting's observable changes its value after the
	 * listener was detached and reattached.
	 */
	@Test
	public void testReattach() {
		nestedListenerHandle = createAttachedNestedListenerHandle(nesting, listener);
		nestedListenerHandle.detach();
		nestedListenerHandle.attach();
		innerObservable.set("new value");

		// assert that 'invalidated' was called once and with the right arguments
		verify(listener, times(1)).invalidated(innerObservable);
		verifyNoMoreInteractions(listener);
	}

	/**
	 * Tests whether the listener is only called once even when attached is called repeatedly.
	 */
	@Test
	public void testMultipleAttach() {
		nestedListenerHandle = createAttachedNestedListenerHandle(nesting, listener);
		nestedListenerHandle.attach();
		nestedListenerHandle.attach();
		nestedListenerHandle.attach();
		innerObservable.set("new value");

		// assert that 'invalidated' was called only once
		verify(listener, times(1)).invalidated(innerObservable);
		verifyNoMoreInteractions(listener);
	}

	//#end TESTS

}
