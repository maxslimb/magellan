package com.wealthfront.magellan.sample.migration.tide

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.wealthfront.magellan.core.Step
import com.wealthfront.magellan.sample.migration.R
import com.wealthfront.magellan.sample.migration.api.DogApi
import com.wealthfront.magellan.sample.migration.databinding.DashboardBinding
import com.wealthfront.magellan.sample.migration.toolbar.ToolbarHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

@AssistedFactory
fun interface DogListStepFactory {
  fun create(goToDogDetails: (name: String) -> Unit): DogListStep
}

class DogListStep @AssistedInject constructor(
  private val toolbarHelper: ToolbarHelper,
  private val api: DogApi,
  @Assisted private val goToDogDetails: (name: String) -> Unit
) : Step<DashboardBinding>(DashboardBinding::inflate) {

  override fun onShow(context: Context, binding: DashboardBinding) {
    toolbarHelper.setTitle(context.getText(R.string.app_name))
    binding.dogItems.layoutManager = LinearLayoutManager(context, VERTICAL, false)
    binding.dogItems.adapter = DogListAdapter(emptyList(), goToDogDetails)
    val decoration = DividerItemDecoration(context, VERTICAL)
    binding.dogItems.addItemDecoration(decoration)

    binding.dogItemsLoading.visibility = View.VISIBLE
    shownScope.launch {
      val dogBreedsResponse = runCatching { api.getAllBreeds() }
      dogBreedsResponse.onSuccess { dogBreeds ->
        val mutableList = dogBreeds.message.keys.toMutableList()
        mutableList.add(0, "View Random dog breed?")
        (binding.dogItems.adapter as DogListAdapter).dataSet = mutableList.toList()
        (binding.dogItems.adapter as DogListAdapter).notifyDataSetChanged()
      }
      dogBreedsResponse.onFailure { throwable ->
        Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
      }
      binding.dogItemsLoading.visibility = View.GONE
    }
  }
}
