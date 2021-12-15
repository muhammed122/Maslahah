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
import com.example.maslahah.R
import com.example.maslahah.databinding.FragmentProfileScreenBinding
import com.example.maslahah.databinding.FragmentResetPasswordScreenBinding
import com.example.maslahah.utils.ProgressLoading
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ResetPasswordFragmentScreen : Fragment() {


    private var _binding: FragmentResetPasswordScreenBinding? = null
    private val binding get() = _binding!!

    lateinit var navController: NavController
    lateinit var auth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference

    var email = ""
    var phone = ""
    var passsword = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        email = ResetPasswordFragmentScreenArgs.fromBundle(
            requireArguments()
        ).email

        phone = ResetPasswordFragmentScreenArgs.fromBundle(
            requireArguments()
        ).phone

        passsword = ResetPasswordFragmentScreenArgs.fromBundle(
            requireArguments()
        ).password

        auth = FirebaseAuth.getInstance()
        databaseReference=FirebaseDatabase.getInstance().reference

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentResetPasswordScreenBinding.bind(view)
        navController = Navigation.findNavController(requireView())
        enableConfirmButton()

        binding.confirmBtn.setOnClickListener {
            addNewPassword(binding.newPassword1.text.toString().trim(),binding.newPassword2.text.toString().trim())
        }
    }

    private fun addNewPassword(pass1: String, pass2: String) {
        if (pass1 != pass2) {
            Toast.makeText(requireContext(), "password not match", Toast.LENGTH_SHORT).show()
            return
        }

        ProgressLoading.show()
        val credential = EmailAuthProvider.getCredential(email, passsword)
        val user = auth.currentUser
        user?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.updatePassword(pass1).addOnCompleteListener { task2 ->
                    ProgressLoading.dismiss()
                    if (task2.isSuccessful) {
                        databaseReference.child("users").child(phone)
                            .child("password").setValue(pass1)

                        Toast.makeText(requireContext(), "password updated", Toast.LENGTH_SHORT)
                            .show()

                        navController.
                        navigate(ResetPasswordFragmentScreenDirections.actionResetPasswordFragmentScreenToEmailLoginScreen())
                    }
                    else
                        Toast.makeText(
                            requireContext(),
                            task2.exception?.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()

                }
            } else {
                ProgressLoading.dismiss()
                Toast.makeText(
                    requireContext(),
                    task.exception?.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

    }


    private fun enableConfirmButton() {
        binding.newPassword1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.confirmBtn.isEnabled = binding.newPassword1.text.toString().trim().length>5 &&
                        binding.newPassword2.text.toString().trim().length>5


            }
        })

        binding.newPassword2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.confirmBtn.isEnabled = binding.newPassword1.text.toString().trim().length>5 &&
                        binding.newPassword2.text.toString().trim().length>5


            }
        })
    }


}