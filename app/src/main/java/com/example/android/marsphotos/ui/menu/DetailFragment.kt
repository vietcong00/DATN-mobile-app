package com.example.android.marsphotos.ui.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.example.android.marsphotos.MainActivity
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.constant.FOOD_SELECT_MAX
import com.example.android.marsphotos.data.constant.RESPONSE_TYPE
import com.example.android.marsphotos.data.db.entity.FoodInfo
import com.example.android.marsphotos.data.db.entity.Food
import com.example.android.marsphotos.util.SharedPreferencesUtil
import com.example.android.marsphotos.util.convertMoney
import com.example.android.marsphotos.databinding.FragmentDetailBinding
import java.util.*

/**
 * This fragment shows the the status of the Mars photos web services transaction.
 */
class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: OverviewViewModel by viewModels()
    private val VALID = "valid"

    private lateinit var selectedFood: Food

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModelObservers()
        binding.apply {
            updateBtn.setOnClickListener {
                var selected = inputSelected.text.toString()
                var valid = checkNull(selected)
                if (valid === VALID) {
                    valid = checkSelectedMax(selected)
                }
                if (valid === VALID) {
                    outlinedSelected.error = null
                    var note = inputNote.text.toString()
                    viewModel?.createFoodRequestsOfBilling(
                        FoodInfo(
                            foodId = selectedFood.id,
                            billingId = SharedPreferencesUtil.getBilling(requireContext())?.id ?: 0,
                            tableName = SharedPreferencesUtil.getTable(requireContext())?.name
                                ?: "",
                            quantity = selected.toInt(),
                            singlePrice = selectedFood.price,
                            note = note,
                            updatedAt = Date().time
                        )
                    )
                } else {
                    outlinedSelected.error = valid
                }
            }
            closeDetailBtn.setOnClickListener{
                closingDetailFragment()
            }
        }
    }

    fun setProduct(food: Food) {
        selectedFood = food
        binding.apply {
            inputSelected.setText("", TextView.BufferType.EDITABLE)
            inputNote.setText("", TextView.BufferType.EDITABLE)
            if(!food?.foodImg?.url.isNullOrEmpty()) {
                food?.foodImg?.url.let {
                    val imgUri = food.foodImg.url.toUri().buildUpon().scheme("https").build()
                    foodImage.load(imgUri) {
                        placeholder(R.drawable.loading_animation)
                        error(R.drawable.ic_broken_image)
                    }
                }
            }else{
                foodImage.setImageResource(R.drawable.ic_broken_image)
            }
            foodName.setText(food.foodName, TextView.BufferType.EDITABLE)
            foodPrice.text = convertMoney(food.price)
        }
        openDetailFragment()
    }

    private fun setupViewModelObservers() {
        viewModel.response.observe(requireActivity()) {
            if (viewModel.response.value === RESPONSE_TYPE.success) {
                (activity as MainActivity).showSuccessNotify(
                    viewModel.message.value.toString()
                )
                viewModel.resetResponseType()
            } else if (viewModel.response.value === RESPONSE_TYPE.fail) {
                (activity as MainActivity).showErrorNotify(
                    viewModel.message.value.toString()
                )
                viewModel.resetResponseType()
            }
        }
    }

    private fun checkNull(text: String): String {
        if (text.isEmpty()) {
            return getString(R.string.null_error)
        }
        return VALID
    }

    private fun closingDetailFragment(){
        var overviewFragment =
            fragmentManager?.findFragmentById(R.id.overviewFragment) as OverviewFragment
        var view= overviewFragment.view
        val layoutParams = view?.layoutParams
        if (layoutParams != null) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        view?.layoutParams = layoutParams
    }

    private fun openDetailFragment(){
        var overviewFragment =
            fragmentManager?.findFragmentById(R.id.overviewFragment) as OverviewFragment
        var view= overviewFragment.view
        val layoutParams = view?.layoutParams
        if (layoutParams != null) {
            layoutParams.width = 600
        }
        view?.layoutParams = layoutParams
    }

    @SuppressLint("StringFormatInvalid", "StringFormatMatches")
    private fun checkSelectedMax(text: String): String {
        if (text.toInt() > FOOD_SELECT_MAX) {
            return getString(R.string.select_max_error, FOOD_SELECT_MAX)
        }
        return VALID
    }
}
