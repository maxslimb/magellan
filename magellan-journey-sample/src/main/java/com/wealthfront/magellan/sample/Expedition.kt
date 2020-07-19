package com.wealthfront.magellan.sample

import android.content.Context
import android.util.Log
import com.wealthfront.magellan.core.Journey
import com.wealthfront.magellan.core.Navigable
import com.wealthfront.magellan.navigation.NavigationPropagator.addStepNavigationListener
import com.wealthfront.magellan.navigation.NavigationPropagator.removeStepNavigationListener
import com.wealthfront.magellan.navigation.NavigationTraverser
import com.wealthfront.magellan.navigation.StepNavigationListener
import com.wealthfront.magellan.sample.App.Provider.appComponent
import com.wealthfront.magellan.sample.databinding.ExpeditionBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Expedition : Journey<ExpeditionBinding>(
  ExpeditionBinding::inflate,
  ExpeditionBinding::container
), StepNavigationListener {

  @Inject lateinit var navigationTraverser: NavigationTraverser

  override fun onCreate(context: Context) {
    appComponent.inject(this)
    addStepNavigationListener(this)
    navigator.goTo(FirstJourney(::goToSecondJourney))
  }

  override fun onDestroy(context: Context) {
    removeStepNavigationListener(this)
  }

  private fun goToSecondJourney() {
    navigator.show(SecondJourney())
  }

  override fun onNavigableShown(navigable: Navigable) {
    Log.i(this::class.java.simpleName, "Shown: ${navigable.javaClass.simpleName}")
  }

  override fun onNavigableHidden(navigable: Navigable) {
    Log.i(this::class.java.simpleName, "Hidden: ${navigable.javaClass.simpleName}")
  }

  override fun onNavigate() {
    navigationTraverser.logGlobalBackStack()
  }
}
