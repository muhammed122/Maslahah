package com.example.maslahah.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.maslahah.R
import com.example.maslahah.databinding.FragmentTasksScreenBinding
import com.example.maslahah.databinding.FragmentTaxScreenBinding


class TaxScreenFragment : Fragment() {


    private var _binding: FragmentTaxScreenBinding? = null
    private val binding get() = _binding!!

    var value = 0.0f
    var balance = 0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        value = TaxScreenFragmentArgs.fromBundle(
            requireArguments()
        ).value

        balance = TaxScreenFragmentArgs.fromBundle(
            requireArguments()
        ).balance

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tax_screen, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding= FragmentTaxScreenBinding.bind(view)

        binding.taxValue.text = value.toString()



        val spanString = SpannableString(" ${balance.toString()} ")
        spanString.setSpan(StyleSpan(Typeface.BOLD), 0, spanString.length, 0)
        binding.guidOfBalance.text = resources.getString(R.string.balance_guide_first)
        binding.guidOfBalance.append(spanString)
        binding.guidOfBalance.append(resources.getString(R.string.balance_guide_second))

    }
}