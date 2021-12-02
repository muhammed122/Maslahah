package com.example.maslahah.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.maslahah.MainActivity
import com.example.maslahah.R
import com.example.maslahah.data.UserData
import com.example.maslahah.databinding.FragmentMainHomeScreenBinding
import com.example.maslahah.databinding.FragmentProfileScreenBinding
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.google.firebase.database.*


class ProfileFragmentScreen : Fragment() {

    private var _binding: FragmentProfileScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseReference: DatabaseReference


    var userdata: UserData? = null

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
        return inflater.inflate(R.layout.fragment_profile_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileScreenBinding.bind(view)
        navController = Navigation.findNavController(requireView())

        getUserData()

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
            }

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
                    userdata?.tax!!.toFloat() , userdata?.balance!!.toFloat()
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


    private fun getUserData() {
        ProgressLoading.show(requireActivity())
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
            Glide.with(requireContext()).load(userdata?.image).into(binding.userProfileImage)
            binding.profileTasksValue.text = userdata?.tasks.toString()
            binding.profileBalanceValue.text = userdata?.balance.toString() + " LE"
            binding.profileTaxValue.text = userdata?.tax.toString() + " LE"
            binding.profileName.text = userdata?.name.toString()
            binding.profilePhoneDetails.text = userdata?.phone
            binding.profileEmailValue.text = userdata?.email
        }
    }

}