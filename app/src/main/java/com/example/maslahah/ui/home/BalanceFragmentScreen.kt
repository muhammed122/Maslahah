package com.example.maslahah.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.maslahah.R
import com.example.maslahah.databinding.FragmentBalanceScreenBinding
import com.example.maslahah.databinding.FragmentTaxScreenBinding


class BalanceFragmentScreen : Fragment() {


    private var _binding: FragmentBalanceScreenBinding? = null
    private val binding get() = _binding!!

    var value = 0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        value = BalanceFragmentScreenArgs.fromBundle(requireArguments()).value

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_balance_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBalanceScreenBinding.bind(view)

        binding.balanceValue.text = value.toString()
    }

}