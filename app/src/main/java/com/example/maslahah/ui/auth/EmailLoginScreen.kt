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
import androidx.navigation.fragment.findNavController
import com.example.maslahah.R
import com.example.maslahah.data.UserData
import com.example.maslahah.databinding.FragmentCreateAccountBinding
import com.example.maslahah.databinding.FragmentEmailLoginScreenBinding
import com.example.maslahah.ui.home.HomeActivityScreen
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.example.maslahah.utils.Utile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class EmailLoginScreen : Fragment() {

    private var _binding: FragmentEmailLoginScreenBinding? = null
    private val binding get() = _binding!!


    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference




    private fun initFirebaseTools() {
        storageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirebaseTools()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email_login_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEmailLoginScreenBinding.bind(view)

        enableConfirmButton()

        binding.loginBtn.setOnClickListener {
            ProgressLoading.show()
            signInWithEmailAndPassword(
                binding.emailLogin.text.toString().trim(),
                binding.passwordLogin.text.toString().trim()
            )
        }

        binding.loginPhoneBtn.setOnClickListener {
            //navController.navigateUp()
            findNavController().navigate(
                EmailLoginScreenDirections.actionEmailLoginScreenToLoginFragment()
            )
        }

        binding.goSignup.setOnClickListener {
           // navController.navigateUp()
            findNavController().popBackStack()
        }

        binding.forgetPassword.setOnClickListener {
            findNavController().navigate(
                EmailLoginScreenDirections.actionEmailLoginScreenToGetPasswordByEmailScreen()
            )
        }


    }


    private fun getUserDataWithEmail(email: String) {
        databaseReference.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    if (data.child("email").getValue(String::class.java) == email) {
                        val user = data.getValue(UserData::class.java)
                        MyPreference.saveUserData(user!!)

                        ProgressLoading.dismiss()
                        Toast.makeText(requireContext(), "Successfully Login", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(requireActivity(), HomeActivityScreen::class.java))
                        activity?.finish()
                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                   getUserDataWithEmail(email)
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
        binding.passwordLogin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.loginBtn.isEnabled =
                    binding.passwordLogin.text!!.length > 5 && binding.emailLogin.text.toString()
                        .isNotEmpty()
            }
        })
        binding.emailLogin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.loginBtn.isEnabled =
                    binding.passwordLogin.text!!.length > 5 && binding.emailLogin.text.toString()
                        .isNotEmpty()
            }
        })
    }

}