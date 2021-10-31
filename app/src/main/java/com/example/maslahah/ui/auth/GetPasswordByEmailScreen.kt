package com.example.maslahah.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.maslahah.R
import com.example.maslahah.data.UserData
import com.example.maslahah.databinding.FragmentGetPasswordByEmailScreenBinding
import com.example.maslahah.databinding.FragmentProfileScreenBinding
import com.example.maslahah.ui.home.HomeActivityScreen
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.example.maslahah.utils.Utile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GetPasswordByEmailScreen : Fragment() {

    private var _binding: FragmentGetPasswordByEmailScreenBinding? = null
    private val binding get() = _binding!!


    lateinit var firebaseAuth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference

    lateinit var navController: NavController


    var phone = ""
    var password = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_password_by_email_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGetPasswordByEmailScreenBinding.bind(view)
        navController = Navigation.findNavController(requireView())
        enableConfirmButton()

        binding.resetBtn.setOnClickListener {
            ProgressLoading.show(requireActivity())
            resetPasswordByEmail(binding.emailReset.text.toString().trim())
        }

    }

    private fun resetPasswordByEmail(email: String) {
        databaseReference.child("users")
            .orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("ddddddd", "onDataChange: $snapshot ")

                    if (snapshot.exists()) {
                        for (data in snapshot.children) {
                            val user = data.getValue(UserData::class.java)
                            Log.d(
                                "ddddddd",
                                "onDataChange: $user "
                            )
                            if (user != null) {
                                phone = user.phone!!
                                password=user.password!!
                                signInWithEmailAndPassword(user.email!!, user.password)
                            } else {
                                ProgressLoading.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "user Not Found",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            }

                        }
                    } else {
                        ProgressLoading.dismiss()
                        Toast.makeText(requireContext(), "Email Not Found", Toast.LENGTH_SHORT)
                            .show()
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    ProgressLoading.dismiss()
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })


//        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//
//
//            } else {
//                Toast.makeText(
//                    requireContext(),
//                    task.exception?.localizedMessage,
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ProgressLoading.dismiss()
                    navController.navigate(
                        GetPasswordByEmailScreenDirections.actionGetPasswordByEmailScreenToResetPasswordFragmentScreen(
                            email, phone,password
                        )
                    )
                } else {
                    ProgressLoading.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }


    }

    private fun enableConfirmButton() {
        binding.emailReset.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                binding.resetBtn.isEnabled = binding.emailReset.text!!.length > 6

            }
        })


    }

}