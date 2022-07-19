package com.example.android.marsphotos.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.db.entity.Food
import com.example.android.marsphotos.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentOverviewBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.photosGrid.adapter = ProductGridAdapter(this)
        binding.viewModel = viewModel
        return binding.root
    }

    fun selectProduct(food: Food) {
        var detailsFragment =
            fragmentManager?.findFragmentById(R.id.detailFragment) as DetailFragment
        detailsFragment.setProduct(food)
    }
}

