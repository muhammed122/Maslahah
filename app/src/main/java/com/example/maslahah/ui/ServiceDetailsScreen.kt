package com.example.maslahah.ui

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.maslahah.R
import com.example.maslahah.data.ServiceData
import com.example.maslahah.data.UserData
import com.example.maslahah.databinding.FragmentServiceDetailsScreenBinding
import com.example.maslahah.utils.Const
import com.example.maslahah.utils.ProgressLoading
import com.google.firebase.database.*

import android.content.Intent

import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log

import androidx.core.content.ContextCompat
import com.example.maslahah.data.CurrentService
import com.example.maslahah.utils.MyPreference


class ServiceDetailsScreen : Fragment() {

    private lateinit var userServicePhone: String
    private var _binding: FragmentServiceDetailsScreenBinding? = null
    private val binding get() = _binding!!


    var service: ServiceData? = null

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseReference = FirebaseDatabase.getInstance().reference

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_details_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentServiceDetailsScreenBinding.bind(view)


        Log.d("ddddd", "onViewCreated: id ${Const.serviceId} ")

        binding.acceptServiceBtn.setOnClickListener {
            addServiceToUser()
        }

    }


    override fun onResume() {
        super.onResume()
        if (Const.serviceId != "")
            getServiceDetails()
    }

    private fun getServiceDetails() {
        ProgressLoading.show(requireActivity())
        databaseReference.child("services").child(Const.serviceId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    service = snapshot.getValue(ServiceData::class.java)
                    if (service!=null)
                    showServiceDetails(service!!)

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })


    }

    private fun showServiceDetails(service: ServiceData) {

        getServiceOwnerData(service.serviceOwnerPhone)
        binding.serviceDateTimeTxt.text = "${service.date}, ${service.time}"
        binding.serviceDurationValueTxt.text =
            "${service.duration} : ${service.duration?.toInt()?.plus(1)} Hours"
        binding.serviceTitleTxtValue.text = service.title
        binding.serviceDetailsTxtValue.text = service.details
        binding.servicePaperTxtValue.text = service.papers

    }

    private fun getServiceOwnerData(serviceOwnerPhone: String?) {
        databaseReference.child("users").child(serviceOwnerPhone!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserData::class.java)
                    showServiceUserData(user!!)

                }

                override fun onCancelled(error: DatabaseError) {
                    ProgressLoading.dismiss()
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showServiceUserData(user: UserData) {
        userServicePhone = user.phone!!
        binding.userServiceNameTxt.text = user.name
        Glide.with(requireContext()).load(user.image).into(binding.userImage)
        ProgressLoading.dismiss()

        binding.callBtn.setOnClickListener {
            makePhoneCall(userServicePhone)
        }
    }


    val REQUEST_CALL = 300
    private fun makePhoneCall(phone: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL
            )
        } else {
            val dial = "tel:$phone"
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(userServicePhone)
            } else {
                Toast.makeText(requireContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }


    }


    private fun addServiceToUser() {

        val userAvailable = MyPreference.getPrefBoolean("available")
        val phone = MyPreference.getPrefString("userPhone")
        if (!userAvailable) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.user_have_other_service),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (service?.selected!!) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.service_accepted_from_other_user),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (service?.serviceOwnerPhone!! == phone) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.can_not_accept_your_service),
                Toast.LENGTH_SHORT
            ).show()
            return
        }


        //make service selected
        databaseReference.child("services").child(service?.id!!).child("selected").setValue(true)

        MyPreference.saveStringPreference("currentService", service?.id!!)

        //add service to current service to user
        databaseReference.child("current_service").child(phone)
            .setValue(CurrentService(phone, service?.id, 1))

        //make user unavailable
        databaseReference.child("users").child(phone).child("available").setValue(false)


    }


}