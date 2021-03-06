package com.example.maslahah.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.maslahah.R
import com.example.maslahah.data.ServiceData
import com.example.maslahah.data.UserData
import com.example.maslahah.data.notification.NotificationData
import com.example.maslahah.data.notification.PushNotification
import com.example.maslahah.databinding.FragmentCurrentServiceScreenBinding
import com.example.maslahah.databinding.FragmentServiceDetailsScreenBinding
import com.example.maslahah.service.RetrofitInstance
import com.example.maslahah.utils.Const
import com.example.maslahah.utils.MyBottomSheetListener
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


const val TOPIC = "/topics/myTopic2"


class CurrentServiceScreen : Fragment() {


    private var phone: String = ""
    private var userServicePhone: String = ""
    private var _binding: FragmentCurrentServiceScreenBinding? = null
    private val binding get() = _binding!!


    lateinit var userService: UserData
    lateinit var currentServiceData: ServiceData

    lateinit var databaseReference: DatabaseReference

    lateinit var user: UserData

    var userAvailable = false
    var currentServiceId = ""

    var serviceStatus = 1
    var currentServiceStatus = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userAvailable = MyPreference.getPrefBoolean("available")
        currentServiceId = MyPreference.getPrefString("currentService")
        databaseReference = FirebaseDatabase.getInstance().reference
        phone = MyPreference.getPrefString("userPhone")
        user = MyPreference.getUserData()



        currentServiceStatus = MyPreference.getPrefInt("serviceStatus")
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_service_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCurrentServiceScreenBinding.bind(view)

        MainHomeScreenFragment.myBottomSheetListener = object : MyBottomSheetListener {
            override fun onStateChange(state: Int) {
                if (state == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.currentServiceTitle.visibility = View.VISIBLE
                    binding.v.setImageResource(R.drawable.ic_down_arrow)
                } else {
                    binding.currentServiceTitle.visibility = View.GONE
                    binding.v.setImageResource(R.drawable.ic_up_arrow)
                }
            }


        }




        binding.cancelBtn.setOnClickListener {
            showConfirmDialog(2, resources.getString(R.string.cancel_message))
        }

        binding.doneBtn.setOnClickListener {
            showConfirmDialog(3, resources.getString(R.string.done_message))
        }


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (currentServiceId != "") {
            getServiceDetails()
            observeOnServiceDeleted()
        }
    }


    private fun getServiceDetails() {
        ProgressLoading.show()
        databaseReference.child("services").child(currentServiceId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val service = snapshot.getValue(ServiceData::class.java)
                    if (service != null) {
                        currentServiceData = service
                        binding.whatsapp.setOnClickListener {
                            val url =
                                "https://api.whatsapp.com/send?phone=20${service.serviceOwnerPhone}"
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            startActivity(i)
                        }
                        showServiceDetails(service)
                    }
                    ProgressLoading.dismiss()

                }

                override fun onCancelled(error: DatabaseError) {
                    ProgressLoading.dismiss()
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })


    }

    private fun showServiceDetails(service: ServiceData) {
        getServiceOwnerData(service.serviceOwnerPhone)
        binding.serviceTitleTxtValue.text = service.title
        binding.serviceDetailsTxtValue.text = service.details
        binding.serviceDateTimeTxt.text = "${service.date}, ${service.time}"
        binding.servicePriceValueTxt.text = "${service.price} LE"
        binding.servicePaperTxtValue.text = service.papers
    }

    private fun getServiceOwnerData(serviceOwnerPhone: String?) {
        databaseReference.child("users").child(serviceOwnerPhone!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserData::class.java)
                    if (user != null) {
                        userService = user
                        showServiceUserData(userService)
                        observeOnServiceStatus(phone, currentServiceData)
                    }
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
        if (user.image == "")
            binding.userImage.setImageResource(R.drawable.ic_avatar)
        else {
            val activity = activity
            if (activity != null)
                Glide.with(activity).load(user.image).into(binding.userImage)

        }
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

    private fun observeOnServiceStatus(phone: String, service: ServiceData) {
        databaseReference.child("current_service")
            .child(phone).child("serviceStatus")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("dddd", "onDataChange: status  $currentServiceStatus")
                    when (snapshot.getValue(Int::class.java)) {
                        1 -> {
                            if (currentServiceStatus == 0) {
                                sendNotification(
                                    PushNotification(
                                        NotificationData(
                                            "service state",
                                            "Your ${currentServiceData.title} accepted from ${user.name}"
                                        ),
                                        userService.userToken!!
                                    ),userServicePhone
                                )

                                MyPreference.setPrefInt("serviceStatus", 1)
                                currentServiceStatus = 1

                            }
                            return
                        }
                        2 -> {
                            if (currentServiceStatus == 1) {
                                sendNotification(
                                    PushNotification(
                                        NotificationData(
                                            "service state",
                                            "Your ${currentServiceData.title} canceled from ${user.name}"
                                        ),
                                        userService.userToken!!
                                    ),userServicePhone
                                )

                                MyPreference.setPrefInt("serviceStatus", 1)
                                currentServiceStatus = 1

                                //remove current service
                                databaseReference.child("current_service").child(phone)
                                    .removeValue()

                                //make user free
                                databaseReference.child("users").child(phone)
                                    .child("available").setValue(true)

                                service.selected = false
                                databaseReference.child("services").child(service.id!!)
                                    .setValue(service)

                                val tax = 10.plus(user.tax!!)
                                MyPreference.setPrefInt("userTax", tax.toInt())
                                databaseReference.child("users").child(phone)
                                    .child("tax").setValue(tax)

                                databaseReference.child("current_service")
                                    .child(phone).child("serviceStatus").setValue(0)
                            }
                            return
                        }
                        3 -> {
                            if (currentServiceStatus != 5) {
                                sendNotification(
                                    PushNotification(
                                        NotificationData(
                                            "service state",
                                            "${user.name}  finished your service ${currentServiceData.title}"
                                        ),
                                        userService.userToken!!
                                    ),userServicePhone
                                )
                                MyPreference.setPrefInt("serviceStatus", 0)
//                                Toast.makeText(
//                                    requireContext(),
//                                    resources.getString(R.string.service_done),
//                                    Toast.LENGTH_SHORT
//                                ).show()

                                serviceStatus = 1
                                //remove current service
                                databaseReference.child("current_service").child(phone)
                                    .removeValue()

                                //make user free
                                databaseReference.child("users").child(phone)
                                    .child("available").setValue(true)


                                //add to done services
                                databaseReference.child("done_services").child(phone)
                                    .child(service.id!!).setValue(service)

                                //add balance and tasks to user
                                val balance = service.price?.plus(user.balance!!)
                                val tax = service.price?.times(0.10)?.plus(user.tax!!)

                                databaseReference.child("users").child(phone)
                                    .child("tax").setValue(tax)
                                databaseReference.child("users").child(phone)
                                    .child("balance").setValue(balance)

                                databaseReference.child("users").child(phone).child("tasks")
                                    .setValue(
                                        user.tasks?.plus(1)
                                    )

                                MyPreference.savePrefFloat("userBalance", balance?.toFloat()!!)
                                MyPreference.setPrefInt("userTasks", user.tasks?.plus(1)!!)
                                tax?.toInt()?.let { MyPreference.setPrefInt("userTax", it) }


                                // remove from all services
                                databaseReference.child("services").child(service.id).removeValue()
                            }

                            return
                        }

                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun sendNotification(notification: PushNotification , phone :String) {
        ProgressLoading.show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {

                    databaseReference.child("notification").child(phone).push()
                        .setValue(notification.data.message)

                    ProgressLoading.dismiss()

                } else {
                    ProgressLoading.dismiss()

                }
            } catch (e: Exception) {
                ProgressLoading.dismiss()

            }
        }

    }


    private fun showConfirmDialog(value: Int, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setTitle("Current Service")
        builder.setMessage(message)
        builder.setPositiveButton(resources.getString(R.string.confirm),
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                databaseReference.child("current_service")
                    .child(phone).child("serviceStatus").setValue(value)

            })
        builder.setNegativeButton(android.R.string.cancel,
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })

        val dialog: AlertDialog = builder.create()
        dialog.show()

    }


    private fun observeOnServiceDeleted() {
        databaseReference.child("services").child(currentServiceId)
            .child("status").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(Int::class.java)
                    if (status == 6) {
                        databaseReference.child("services").child(currentServiceId)
                            .removeValue()
                        databaseReference.child("users").child(user.phone!!)
                            .child("userToken")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val token = snapshot.getValue(String::class.java)
                                    if (token!=null)
                                    sendNotification(
                                        PushNotification(
                                            NotificationData(
                                                "service state",
                                                "sorry service  ${currentServiceData.title} canceled from owner"
                                            ),
                                            token
                                        ),user.phone!!
                                    )

                                    MyPreference.setPrefInt("serviceStatus", 1)
                                    currentServiceStatus = 1

                                    //remove current service
                                    databaseReference.child("current_service").child(phone)
                                        .removeValue()

                                    //make user free
                                    databaseReference.child("users").child(phone)
                                        .child("available").setValue(true)
                                    databaseReference.child("current_service")
                                        .child(phone).child("serviceStatus").setValue(0)
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })
                    }
                }


                override fun onCancelled(error: DatabaseError) {

                }
            })


    }

}