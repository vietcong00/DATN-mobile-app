package com.example.android.marsphotos.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.example.android.marsphotos.R
import com.example.android.marsphotos.databinding.FragmentDetailBinding
import com.example.android.marsphotos.network.CreateProductRequest
import com.example.android.marsphotos.pojo.Food
import com.example.android.marsphotos.pojo.FoodBilling
import com.example.android.marsphotos.network.UpdateProductRequest
import java.text.NumberFormat
import java.util.*

/**
 * This fragment shows the the status of the Mars photos web services transaction.
 */
class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: OverviewViewModel by viewModels()
    private var idProduct = 0
    private val VALID = "valid"

    private lateinit var foodBillingSelected: FoodBilling
    private var isCreate: Boolean = false

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        binding.apply {
            updateBtn.setOnClickListener {
                var selected = inputSelected.text.toString()
                var valid = checkNull(selected)
                if (valid === VALID) {
                    outlinedSelected.error = null
                } else {
                    outlinedSelected.error = valid
                }
                var note = inputNote.text.toString()
                valid = checkNull(note)
                if (valid === VALID) {
                    outlinedNote.error = null
                } else {
                    outlinedNote.error = valid
                }

                if (isCreate) {
                    var request = CreateProductRequest(
                        foodId = foodBillingSelected.foodId!!,
                        billingId = foodBillingSelected.billingId!!,
                        selectedCount = selected.toInt(),
                        note = note
                    )
                    viewModel?.createProduct(request)
                }else{
                    var request = UpdateProductRequest(
                        foodId = foodBillingSelected.foodId!!,
                        selectedCount = selected.toInt(),
                        canceledCount = 0,
                        note = note
                    )
                    viewModel?.updateProduct(foodBillingSelected.id!!, request)
                }
            }

            deleteBtn.setOnClickListener {
                viewModel?.deleteProduct(idProduct)
            }
        }
        return binding.root
    }

    fun setProduct(food: Food, foodBilling: FoodBilling, isCreate:Boolean) {
        this.isCreate = isCreate
        foodBillingSelected = foodBilling
        binding.apply {
            food.foodImg.url?.let {
                val imgUri = food.foodImg.url.toUri().buildUpon().scheme("https").build()
                foodImage.load(imgUri) {
                    placeholder(R.drawable.loading_animation)
                    error(R.drawable.ic_broken_image)
                }
            }
            foodName.setText(food.foodName, TextView.BufferType.EDITABLE)
            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            format.setMaximumFractionDigits(0)
            format.setCurrency(Currency.getInstance("VND"))
            foodPrice.setText(format.format(food.price));
            idProduct = food.id
            inputSelected.setText(
                foodBilling.selectedCount.toString(),
                TextView.BufferType.EDITABLE
            )
            inputNote.setText(foodBilling.note, TextView.BufferType.EDITABLE)
        }

    }

    private fun checkNull(text: String): String {
        if (text.isEmpty()) {
            return getString(R.string.null_error)
        }
        return VALID
    }
}
