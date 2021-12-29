package com.example.maslahah.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.maslahah.BaseFragment
import com.example.maslahah.MainActivity
import com.example.maslahah.R
import com.example.maslahah.data.UserData
import com.example.maslahah.databinding.FragmentMainHomeScreenBinding
import com.example.maslahah.databinding.FragmentProfileScreenBinding
import com.example.maslahah.ui.auth.CreateAccountFragmentDirections
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ProfileFragmentScreen : BaseFragment() {

    private var _binding: FragmentProfileScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseReference: DatabaseReference


    var userdata: UserData? = null


    lateinit var auth: FirebaseAuth

    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseReference = FirebaseDatabase.getInstance().reference

        auth = FirebaseAuth.getInstance()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileScreenBinding.bind(view)
        navController = Navigation.findNavController(requireView())


        getUserData()
        if (MyPreference.getLanguage() == "ar") {
            binding.profileLanguageValue.text = "English"
        } else {
            binding.profileLanguageValue.text = "العربيه"

        }


        //open gallery
        binding.editBtn.setOnClickListener {
            if (checkStoragePermission(MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_GALLERY))
                galleryIntent()
        }


        binding.saveUpdatedImage.setOnClickListener {
            uploadImageToServer()
        }


        binding.profileLanguageValue.setOnClickListener {

            if (MyPreference.getLanguage() == "ar") {

                changeLanguage("en")
                binding.profileLanguageValue.text = "العربيه"
            } else {

                changeLanguage("ar")
                binding.profileLanguageValue.text = "English"
            }
        }

        binding.logout.setOnClickListener {
            if (!userdata?.available!!)
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.you_have_service),
                    Toast.LENGTH_SHORT
                ).show()
            else {
                MyPreference.clearData()
                MyPreference.setPrefBoolean("login", false)
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
                auth.signOut()


            }

        }


        binding.yourTeleChat.setOnClickListener {
            val url =
                "https://t.me/hesham_rafat"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }


        binding.conditionsLayout.setOnClickListener {

            navController.navigate(
                ProfileFragmentScreenDirections.actionProfileFragmentScreenToTermsAndPolicyFragment2()
            )


        }


        binding.yourTasks.setOnClickListener {
            navController.navigate(
                ProfileFragmentScreenDirections.actionProfileFragmentScreenToTasksScreenFragment(
                    userdata?.tasks!!
                )
            )
        }



        binding.yourTax.setOnClickListener {
            navController.navigate(
                ProfileFragmentScreenDirections.actionProfileFragmentScreenToTaxScreenFragment(
                    userdata?.tax!!.toFloat(), userdata?.balance!!.toFloat()
                )
            )
        }

        binding.yourBalance.setOnClickListener {
            navController.navigate(
                ProfileFragmentScreenDirections.actionProfileFragmentScreenToBalanceFragmentScreen(
                    userdata?.balance!!.toFloat()
                )
            )
        }

//        binding.yourOrders.setOnClickListener {
//            navController.navigate(
//                ProfileFragmentScreenDirections.actionProfileFragmentScreenToTaxScreenFragment(
//                    userdata?.!!.toFloat()
//                )
//            )
//        }

    }

    private fun changeLanguage(language: String) {
        MyPreference.setLanguage(language.toLowerCase())
        val activity = requireActivity() as HomeActivityScreen
        activity.setLanguage(language)
    }

    override fun onResume() {
        super.onResume()


        Log.d("dddd", "onResume: imaaaaaaage")
        val image = MyPreference.getPrefString("userImage")
        if (image == "")
            binding.userProfileImage.setImageResource(R.drawable.ic_avatar)
        else
            Glide.with(requireContext()).load(image)
                .into(binding.userProfileImage)
    }


    private fun getUserData() {
        ProgressLoading.show()
        val phone = MyPreference.getPrefString("userPhone")
        databaseReference.child("users").child(phone)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userdata = snapshot.getValue(UserData::class.java)
                    Log.d("ddddd", "onDataChange:  $userdata ")
                    MyPreference.saveUserData(userdata!!)
                    showData()
                    ProgressLoading.dismiss()

                }

                override fun onCancelled(error: DatabaseError) {
                    ProgressLoading.dismiss()
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showData() {
        if (userdata != null) {

            binding.profileTasksValue.text = userdata?.tasks.toString()
            binding.profileBalanceValue.text = userdata?.balance.toString() + " LE"
            binding.profileTaxValue.text = userdata?.tax.toString() + " LE"
            binding.profileName.text = userdata?.name.toString()
            binding.profilePhoneDetails.text = userdata?.phone
            binding.profileEmailValue.text = userdata?.email


        }
    }

    lateinit var imageUri: Uri
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("dddddd", "onActivityResult:  ${data?.data}")
        if (resultCode == Activity.RESULT_OK) {
            Log.d("dddddd", "onActivityResult:222  ${data?.data}")

            if (data?.data != null) {
                imageUri = data.data!!
                binding.editBtn.visibility = View.GONE
                binding.saveUpdatedImage.visibility = View.VISIBLE
                binding.userProfileImage.visibility = View.GONE
                binding.userNewImage.visibility = View.VISIBLE
                binding.userNewImage.setImageURI(data.data)
            }

        }


    }

    var imageUrl = ""
    private lateinit var storageReference: StorageReference
    private fun uploadImageToServer() {
        if (imageUri != null) {
            ProgressLoading.show()
            storageReference = FirebaseStorage.getInstance().reference
            val storage = storageReference.child("images")
                .child("${System.currentTimeMillis()}image")
            storage.putFile(imageUri)
                .addOnSuccessListener { task ->
                    storage.downloadUrl.addOnSuccessListener { uri ->
                        imageUrl = uri.toString()

                        updateUserImage(imageUrl)
                    }
                }.addOnFailureListener { e ->
                    ProgressLoading.dismiss()
                    Toast.makeText(requireContext(), "${e.localizedMessage}", Toast.LENGTH_SHORT)
                        .show()
                    imageUrl = ""
                }
        }
    }

    private fun updateUserImage(image: String) {

        databaseReference.child("users").child(userdata?.phone!!)
            .child("image")
            .setValue(image)
            .addOnCompleteListener { task ->
                binding.editBtn.visibility = View.VISIBLE
                binding.saveUpdatedImage.visibility = View.GONE
                ProgressLoading.dismiss()
                if (task.isSuccessful) {
                    //cash user data
                    MyPreference.saveStringPreference("userImage", image)
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.successfully_updated),
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(
                        requireContext(),
                        "${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()

                }


            }


    }


}