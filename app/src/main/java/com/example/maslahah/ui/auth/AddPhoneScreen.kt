package com.example.maslahah.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import com.example.maslahah.R
import com.example.maslahah.data.UserData
import com.example.maslahah.databinding.FragmentAddPhoneScreenBinding
import com.example.maslahah.databinding.FragmentResetPasswordScreenBinding
import com.example.maslahah.ui.home.HomeActivityScreen
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.example.maslahah.utils.Utile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddPhoneScreen : Fragment() {


    private var _binding: FragmentAddPhoneScreenBinding? = null
    private val binding get() = _binding!!

    var email = ""
    var name = ""
    var image = ""


    lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        email = AddPhoneScreenArgs.fromBundle(
            requireArguments()
        ).email

        name = AddPhoneScreenArgs.fromBundle(
            requireArguments()
        ).name

        image = AddPhoneScreenArgs.fromBundle(
            requireArguments()
        ).image

        databaseReference = FirebaseDatabase.getInstance().reference

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_phone_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddPhoneScreenBinding.bind(view)
        enableConfirmButton()
        binding.createBtn.setOnClickListener {
            uploadUserData(name, email, image, binding.phoneCreate.text.toString().trim())
        }


    }


    private fun uploadUserData(name: String, email: String, image: String, phone: String) {
        ProgressLoading.show()
        databaseReference.child("users").child(phone)
            .setValue(UserData(name, phone, image, email)).addOnCompleteListener {
                ProgressLoading.dismiss()
                if (it.isSuccessful){
                    //cash user data
                    MyPreference.saveUserData(UserData(name, phone, image, email))
                    Toast.makeText(requireContext(), "Successfully Login", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(requireActivity(), HomeActivityScreen::class.java))
                    activity?.finish()
                }
                else{
                    Toast.makeText(requireContext(), "${it.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()

                }


            }

    }

    private fun enableConfirmButton() {
        binding.phoneCreate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (binding.phoneCreate.text!!.length == 11) {
                    binding.createBtn.isEnabled = true
                    Utile.hideKeyboard(requireActivity())
                } else {
                    binding.createBtn.isEnabled = false
                }

            }
        })


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}