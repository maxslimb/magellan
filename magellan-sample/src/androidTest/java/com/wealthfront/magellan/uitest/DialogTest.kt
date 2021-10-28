package com.wealthfront.magellan.uitest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.wealthfront.magellan.action.orientationLandscape
import com.wealthfront.magellan.sample.MainActivity
import com.wealthfront.magellan.sample.R
import org.junit.Rule
import org.junit.Test

class DialogTest {

  @get:Rule
  var activityRule = ActivityTestRule(MainActivity::class.java)

  @Test
  fun showDialog() {
    onView(withId(R.id.nextJourney)).perform(click())

    onView(withId(R.id.dialog)).perform(click())

    assertDialogContentsShown()

    onView(isRoot()).perform(orientationLandscape())

    assertDialogContentsShown()
  }
}

private fun assertDialogContentsShown() {
  onView(withText("Hello"))
    .check(matches(isDisplayed()))

  onView(withText("Are you sure about this?"))
    .check(matches(isDisplayed()))
}
