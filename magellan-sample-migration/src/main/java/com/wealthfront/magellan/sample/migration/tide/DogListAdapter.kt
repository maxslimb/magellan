package com.wealthfront.magellan.sample.migration.tide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wealthfront.magellan.sample.migration.R

class DogListAdapter(
  var dataSet: List<String> = emptyList(),
  private val onDogSelected: (String) -> Unit
) : RecyclerView.Adapter<DogListAdapter.ViewHolder>() {

  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val textView: TextView = view.findViewById(R.id.dogName)
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(viewGroup.context)
      .inflate(R.layout.dog_item, viewGroup, false)

    return ViewHolder(view)
  }

  override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
    viewHolder.textView.text = dataSet[position]
    viewHolder.itemView.setOnClickListener {
      if(dataSet[position] == "View Random dog breed?")                           //Checking if the element is "View Random dog breed?"
      {
        val filteredDataSet = dataSet.filter { it != "View Random dog breed?" }   //Filtering out the element "View Random dog breed?"
        val randomBreed = filteredDataSet.random()                                //Choosing a Random Breed
        onDogSelected(randomBreed)
      }
      else{
        onDogSelected(dataSet[position])
      }

    }
  }

  override fun getItemCount() = dataSet.size
}
