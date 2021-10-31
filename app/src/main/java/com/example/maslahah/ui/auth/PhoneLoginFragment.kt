package com.example.maslahah.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.maslahah.R
import com.example.maslahah.data.UserData
import com.example.maslahah.databinding.FragmentLoginBinding
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.example.maslahah.utils.Utile
import com.google.firebase.database.*


class PhoneLoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseReference: DatabaseReference


    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseReference = FirebaseDatabase.getInstance().reference

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        navController = Navigation.findNavController(requireView())

        binding.goSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
        }

        enableConfirmButton()

        binding.loginBtn.setOnClickListener {
            checkPhoneNotHaveAccount(binding.phoneLogin.text.toString())
        }


    }

    private fun checkPhoneNotHaveAccount(phone: String) {
        ProgressLoading.show(requireActivity())
        databaseReference.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ProgressLoading.dismiss()
                    if (!snapshot.hasChild(phone)) {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.not_have_acc),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //navigate to verify phone
                        //cash user data
                        MyPreference.saveUserData(snapshot.child(phone).getValue(UserData::class.java)!!)
                        navController.navigate(
                            PhoneLoginFragmentDirections.actionLoginFragmentToVerifyFragment(phone)
                        )
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    ProgressLoading.dismiss()
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                }
            })


    }

    private fun enableConfirmButton() {
        binding.phoneLogin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (binding.phoneLogin.text!!.length == 11) {
                    binding.loginBtn.isEnabled = true
                    Utile.hideKeyboard(requireActivity())
                }
                else{
                    binding.loginBtn.isEnabled = false
                }

            }
        })


    }

}