package com.wealthfront.magellan.sample.migration.uitest

import android.app.Application
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.wealthfront.magellan.sample.migration.AppComponentContainer
import com.wealthfront.magellan.sample.migration.CoroutineIdlingRule
import com.wealthfront.magellan.sample.migration.MainActivity
import com.wealthfront.magellan.sample.migration.R
import com.wealthfront.magellan.sample.migration.TestAppComponent
import com.wealthfront.magellan.sample.migration.api.DogApi
import com.wealthfront.magellan.sample.migration.api.DogBreedsResponse
import com.wealthfront.magellan.sample.migration.api.DogImageResponse
import com.wealthfront.magellan.sample.migration.coWhen
import com.wealthfront.magellan.sample.migration.tide.DogListAdapter
import kotlinx.coroutines.runBlocking
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matcher
import javax.inject.Inject

class DogListAdapterTest {

  @Rule @JvmField
  val coroutineIdlingRule = CoroutineIdlingRule()

  @Inject lateinit var api: DogApi

  private lateinit var activityScenario: ActivityScenario<MainActivity>

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Application>()
    ((context as AppComponentContainer).injector() as TestAppComponent).inject(this)

    //Creating dummy data
    coWhen { api.getAllBreeds() }
      .thenReturn(DogBreedsResponse(
        message =
        mapOf(
          "Siberian Husky" to listOf(),
          "african" to listOf(),
          "wolfhound" to listOf("irish")
        ),
        status = "success"
      ))

    coWhen { api.getRandomImageForBreed("african") }.thenReturn(
      DogImageResponse(message = "image-url", status = "success")
    )
    coWhen { api.getRandomImageForBreed("Siberian Husky") }.thenReturn(
      DogImageResponse(message = "https:\\/\\/images.dog.ceo\\/breeds\\/akita\\/Akita_inu_blanc.jpg", status = "success")
    )
    coWhen { api.getRandomImageForBreed("wolfhound") }.thenReturn(
      DogImageResponse(message = "https:\\/\\/images.dog.ceo\\/breeds\\/akita\\/Akita_inu_blanc.jpg", status = "success")
    )
  }
  @Test
  fun checkRandomDogBreed(): Unit = runBlocking{
    activityScenario = launchActivity()
    onView(withText("View Random dog breed?")).perform(click())
    onView(withId(R.id.dogDetailsView)).check(matches(isDisplayed()))

  }

  @Test
  fun clickOnSpecificDogBreedItem() {
    activityScenario = launchActivity()
    onView(withText("african")).perform(click())  //Clicking the Specific Breed here `african`
    onView(withText("african")).check(matches(isDisplayed()))   //Verifying the Breed Name matches
    onView(withId(R.id.dogDetailsView)).check(matches(isDisplayed()))
  }
}