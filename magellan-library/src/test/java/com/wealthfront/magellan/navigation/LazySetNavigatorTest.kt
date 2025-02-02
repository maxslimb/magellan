package com.wealthfront.magellan.navigation

import android.app.Activity
import android.os.Looper
import com.google.common.truth.Truth.assertThat
import com.wealthfront.magellan.ScreenContainer
import com.wealthfront.magellan.internal.test.DummyStep
import com.wealthfront.magellan.lifecycle.LifecycleState
import com.wealthfront.magellan.lifecycle.transitionToState
import com.wealthfront.magellan.transitions.CrossfadeTransition
import io.mockk.MockKAnnotations.init
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class LazySetNavigatorTest {

  private val activityController = buildActivity(Activity::class.java)
  private lateinit var navigator: LazySetNavigator
  private lateinit var step1: DummyStep
  private lateinit var step2: DummyStep
  @MockK private lateinit var navigableListener: NavigationListener

  @Before
  fun setUp() {
    init(this)
    initMocks()

    navigator = LazySetNavigator { ScreenContainer(activityController.get()) }
    step1 = DummyStep()
    step2 = DummyStep()

    NavigationPropagator.addNavigableListener(navigableListener)
  }

  @After
  fun tearDown() {
    NavigationPropagator.removeNavigableListener(navigableListener)
  }

  private fun initMocks() {
    every { navigableListener.onNavigatedTo(any()) }.answers { }
    every { navigableListener.onNavigatedFrom(any()) }.answers { }
    every { navigableListener.beforeNavigation() }.answers { }
    every { navigableListener.afterNavigation() }.answers { }
  }

  @Test
  fun lifecycleTeardown() {
    navigator.addNavigables(setOf(step1, step2))
    navigator.transitionToState(LifecycleState.Created(activityController.get()))

    navigator.replace(step1, CrossfadeTransition())
    navigator.transitionToState(LifecycleState.Shown(activityController.get()))
    assertThat(navigator.containerView!!.childCount).isEqualTo(1)
    assertThat(step1.currentState).isInstanceOf(LifecycleState.Shown::class.java)

    navigator.transitionToState(LifecycleState.Created(activityController.get()))
    assertThat(navigator.containerView).isNull()
    assertThat(step1.currentState).isInstanceOf(LifecycleState.Created::class.java)

    navigator.transitionToState(LifecycleState.Destroyed)
    assertThat(navigator.containerView).isNull()
    assertThat(step1.currentState).isInstanceOf(LifecycleState.Destroyed::class.java)
  }

  @Test
  fun replace() {
    navigator.addNavigables(setOf(step1, step2))
    navigator.transitionToState(LifecycleState.Resumed(activityController.get()))

    navigator.replace(step1, CrossfadeTransition())
    step1.view!!.viewTreeObserver.dispatchOnPreDraw()
    shadowOf(Looper.getMainLooper()).idle()
    assertThat(navigator.containerView!!.childCount).isEqualTo(1)
    assertThat(navigator.containerView!!.getChildAt(0)).isEqualTo(step1.view)
    assertThat(step1.currentState).isInstanceOf(LifecycleState.Resumed::class.java)
    assertThat(step2.currentState).isInstanceOf(LifecycleState.Created::class.java)

    verify { navigableListener.beforeNavigation() }
    verify(exactly = 0) { navigableListener.onNavigatedFrom(any()) }
    verify { navigableListener.onNavigatedTo(step1) }
    verify { navigableListener.afterNavigation() }
    clearMocks(navigableListener)
    initMocks()

    navigator.replace(step2, CrossfadeTransition())
    step2.view!!.viewTreeObserver.dispatchOnPreDraw()
    shadowOf(Looper.getMainLooper()).idle()
    assertThat(navigator.containerView!!.childCount).isEqualTo(1)
    assertThat(navigator.containerView!!.getChildAt(0)).isEqualTo(step2.view)
    assertThat(step1.currentState).isInstanceOf(LifecycleState.Shown::class.java)
    assertThat(step2.currentState).isInstanceOf(LifecycleState.Resumed::class.java)

    verify { navigableListener.beforeNavigation() }
    verify { navigableListener.onNavigatedFrom(step1) }
    verify { navigableListener.onNavigatedTo(step2) }
    verify { navigableListener.afterNavigation() }
  }
}
