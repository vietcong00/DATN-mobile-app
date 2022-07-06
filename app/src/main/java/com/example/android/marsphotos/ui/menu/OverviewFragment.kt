package com.example.android.marsphotos.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsphotos.R
import com.example.android.marsphotos.databinding.FragmentOverviewBinding
import com.example.android.marsphotos.pojo.Food

/**
 * This fragment shows the the status of the Mars photos web services transaction.
 */
class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by viewModels()

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentOverviewBinding.inflate(inflater)
//        val binding = GridViewItemBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.photosGrid.adapter = ProductGridAdapter(this)

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        return binding.root
    }

    fun selectProduct(food: Food) {
        var detailsFragment =
            fragmentManager?.findFragmentById(R.id.detailFragment) as DetailFragment
        detailsFragment.setProduct(food)
    }
}

