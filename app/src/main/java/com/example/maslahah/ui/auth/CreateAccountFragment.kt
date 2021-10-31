package com.example.maslahah.ui.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.maslahah.BaseFragment
import com.example.maslahah.R
import com.example.maslahah.data.UserData
import com.example.maslahah.databinding.FragmentCreateAccountBinding
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.concurrent.TimeUnit


class CreateAccountFragment : BaseFragment() {


    private val TAG = "CreateAccountFragment"

    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!


    private var imageUri: Uri? = null
    private var imageUrl: String = ""


    private lateinit var auth: FirebaseAuth


    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference


    private fun uploadImageToServer() {
        val storage = storageReference.child("images")
            .child("${System.currentTimeMillis()}image")
        storage.putFile(imageUri!!)
            .addOnSuccessListener { task ->
                storage.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri.toString()
                    createUserWithEmailAndPassword(
                        binding.emailCreateAcc.text.toString().trim(),
                        binding.passwordCreateAcc.text.toString().trim()
                    )

                }
            }.addOnFailureListener { e ->
                ProgressLoading.dismiss()
                Toast.makeText(requireContext(), "${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                imageUrl = ""
            }
    }

    private fun initFirebaseTools() {
        storageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference
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
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                imageUri = data.data
                binding.cameraImage.visibility = View.GONE
                binding.userImage.visibility = View.VISIBLE
                binding.userImage.setImageURI(data.data)
                binding.removeBtn.visibility = View.VISIBLE

                binding.createAccBtn.isEnabled =
                    binding.nameCreateAcc.text.toString().isNotEmpty() &&
                            binding.passwordCreateAcc.text.toString().isNotEmpty() &&
                            binding.emailCreateAcc.text.toString().isNotEmpty() &&
                            binding.phoneCreateAcc.text.toString().length == 11
                            && imageUri != null


            }

        }
    }

    lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateAccountBinding.bind(view)

        navController = Navigation.findNavController(requireView())
        enableConfirmButton()


        //open gallery
        binding.selectImageLayout.setOnClickListener {
            if (checkStoragePermission(MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_GALLERY))
                galleryIntent()
        }

        binding.goLogin.setOnClickListener {
            navController.navigateUp()
            navController.navigate(
                CreateAccountFragmentDirections.actionCreateAccountFragmentToEmailLoginScreen()
            )
        }

        binding.removeBtn.setOnClickListener {
            imageUri = null
            imageUrl = ""
            binding.userImage.setImageResource(0)
            binding.userImage.visibility=View.GONE
            binding.cameraImage.visibility=View.VISIBLE
            binding.removeBtn.visibility = View.GONE
            binding.createAccBtn.isEnabled = false

        }


        auth = FirebaseAuth.getInstance()
        binding.createAccBtn.setOnClickListener {
            if (!binding.phoneCreateAcc.text.toString().startsWith("01")) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.worng_phone_number),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }else {
                checkPhoneNotHaveAccount(binding.phoneCreateAcc.text.toString())
            }




        }


    }


    private fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    uploadUserData(
                        binding.nameCreateAcc.text.toString().trim(),
                        email, binding.phoneCreateAcc.text.toString().trim(), imageUrl,password
                    )
                else {
                    ProgressLoading.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

    }


    private fun checkPhoneNotHaveAccount(phone: String) {
        ProgressLoading.show(requireActivity())
        databaseReference.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(phone)) {
                        ProgressLoading.dismiss()
                        Toast.makeText(requireContext(), resources.getString(R.string.have_acc), Toast.LENGTH_SHORT).show()
                    }
                    else
                        uploadImageToServer()
                }
                override fun onCancelled(error: DatabaseError) {
                    ProgressLoading.dismiss()

                }
            })



    }

    private fun uploadUserData(name: String, email: String, phone: String, image: String ,password: String) {
        databaseReference.child("users").child(phone)
            .setValue(UserData(name, phone, image, email ,password)).addOnSuccessListener { task ->

                //cash user data
                MyPreference.saveUserData(UserData(name, phone, image, email))


                ProgressLoading.dismiss()


                //navigate to verify phone
                navController.navigateUp()
                navController.navigate(
                    CreateAccountFragmentDirections.actionCreateAccountFragmentToVerifyFragment(
                        phone
                    )
                )

            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                ProgressLoading.dismiss()
            }


    }


    private fun enableConfirmButton() {
        binding.passwordCreateAcc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.createAccBtn.isEnabled =
                    binding.nameCreateAcc.text.toString().isNotEmpty() &&
                            binding.passwordCreateAcc.text.toString().isNotEmpty() &&
                            binding.emailCreateAcc.text.toString().isNotEmpty() &&
                            binding.phoneCreateAcc.text.toString().length == 11
                            && imageUri != null
            }

        })

        binding.nameCreateAcc.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.createAccBtn.isEnabled =
                        binding.nameCreateAcc.text.toString().isNotEmpty() &&
                                binding.passwordCreateAcc.text.toString().isNotEmpty() &&
                                binding.emailCreateAcc.text.toString().isNotEmpty() &&
                                binding.phoneCreateAcc.text.toString().length == 11
                                && imageUri != null
                }

            })


        binding.emailCreateAcc.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.createAccBtn.isEnabled =
                        binding.nameCreateAcc.text.toString().isNotEmpty() &&
                                binding.passwordCreateAcc.text.toString().isNotEmpty() &&
                                binding.emailCreateAcc.text.toString().isNotEmpty() &&
                                binding.phoneCreateAcc.text.toString().length == 11
                                && imageUri != null
                }

            })

        binding.phoneCreateAcc.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.createAccBtn.isEnabled =
                        binding.nameCreateAcc.text.toString().isNotEmpty() &&
                                binding.passwordCreateAcc.text.toString().isNotEmpty() &&
                                binding.emailCreateAcc.text.toString().isNotEmpty() &&
                                binding.phoneCreateAcc.text.toString().length == 11
                                && imageUri != null
                }

            })
    }


}