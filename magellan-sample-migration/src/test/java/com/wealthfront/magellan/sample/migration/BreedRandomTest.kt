package com.wealthfront.magellan.sample.migration

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.test.core.app.ApplicationProvider
import com.wealthfront.magellan.core.Step
import com.wealthfront.magellan.sample.migration.api.DogApi
import com.wealthfront.magellan.sample.migration.api.DogBreedsResponse
import com.wealthfront.magellan.sample.migration.databinding.DashboardBinding
import com.wealthfront.magellan.sample.migration.tide.DogListAdapter
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class BreedRandomTest : Step<DashboardBinding>(DashboardBinding::inflate){

  private val dogApi = mockk<DogApi>(relaxed = true)
  private val goToDogDetails: (String) -> Unit = { /* Mock implementation */ }
  private val context = ApplicationProvider.getApplicationContext<Context>()
  private val layoutInflater = LayoutInflater.from(context)
  private val binding : DashboardBinding = DashboardBinding.inflate(layoutInflater, null, false)
  @Before
  fun setup(){
    binding.dogItems.layoutManager = LinearLayoutManager(context,
      LinearLayoutManager.VERTICAL, false)
    binding.dogItems.adapter = DogListAdapter(emptyList(), goToDogDetails)
    // creating a fake API response
    val fakeDogBreedsResponse = DogBreedsResponse(
      message =
      mapOf(
        "Siberian Husky" to listOf(),
        "african" to listOf(),
        "wolfhound" to listOf("irish")
      ),
      status = "success"
    )
    coEvery { dogApi.getAllBreeds() } returns fakeDogBreedsResponse
  }

  @Test
  fun `Check MutableList and Adapter contains View Random dog breed element, correct Adapter DataList`(): Unit = runBlocking{
    val dogBreeds = dogApi.getAllBreeds()
    val mutableList = dogBreeds.message.keys.toMutableList()
    mutableList.add(0, "View Random dog breed?")
    assertEquals(mutableList.first(), "View Random dog breed?" )    //Test the element in the list
    (binding.dogItems.adapter as DogListAdapter).dataSet = mutableList.toList()
    val dataset = (binding.dogItems.adapter as DogListAdapter).dataSet
    assertEquals(4, dataset.size)                                 // 3 breeds + "View Random dog breed?"
    assertEquals("View Random dog breed?", dataset.first())
  }
}