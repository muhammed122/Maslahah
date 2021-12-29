package com.example.maslahah.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.maslahah.R
import com.example.maslahah.data.ServiceData
import com.example.maslahah.databinding.FragmentEditServiceScreenBinding
import com.example.maslahah.databinding.FragmentServiceDetailsScreenBinding
import com.example.maslahah.utils.Const
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import java.text.DateFormatSymbols
import java.util.*


class EditServiceScreenFragment : Fragment() {


    private var _binding: FragmentEditServiceScreenBinding? = null
    private val binding get() = _binding!!

    var service: ServiceData? = null


    var lastDate = ""
    var serviceDateSetListener: DatePickerDialog.OnDateSetListener? = null
    var serviceTimeSetListener: TimePickerDialog.OnTimeSetListener? = null
    private fun initDateDialog() {
        serviceDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                var mMonth = month
                mMonth += 1
                val date = "$dayOfMonth/$month/$year"
                binding.serviceDateEt.setText(date)
                lastDate = "$dayOfMonth ${DateFormatSymbols().months[month]}"

                Log.d("dd", "initDateDialog: $lastDate ")
            }


        serviceTimeSetListener =
            TimePickerDialog.OnTimeSetListener { view, hours, minutes ->
                var mHours = hours
                var du = "AM"
                if (hours > 12) {
                    mHours -= 12
                    du = "PM"
                }

                val time = "$mHours : $minutes $du"
                binding.serviceTimeEt.setText(time)
            }
    }


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
        return inflater.inflate(R.layout.fragment_edit_service_screen, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditServiceScreenBinding.bind(view)

        getServiceDetails()


        initDateDialog()
        binding.serviceDateEt.setOnClickListener {

            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            val month = cal[Calendar.MONTH]
            val day = cal[Calendar.DAY_OF_MONTH]


            val dialog = DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                serviceDateSetListener,
                year, month, day
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }


        binding.serviceTimeEt.setOnClickListener {

            val cal = Calendar.getInstance()
            val hours = cal[Calendar.HOUR_OF_DAY]
            val minutes = cal[Calendar.MINUTE]


            val dialog = TimePickerDialog(
                requireContext(),
                serviceTimeSetListener,
                hours, minutes,
                false
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }



        binding.editBtn.setOnClickListener {


            if (service?.selected!!)
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.service_selected),
                    Toast.LENGTH_SHORT
                ).show()
            else
                updateService(
                    binding.serviceTitleEt.text.toString().trim(),
                    binding.serviceDetailsEt.text.toString().trim(),
                    binding.servicePriceEt.text.toString().trim().toDouble(),
                    binding.servicePapersEt.text.toString().trim(),
                    lastDate,
                    binding.serviceTimeEt.text.toString().trim(),
                )

        }

        binding.cancelBtn.setOnClickListener {

            if (service?.selected!!) {

                showConfirmDialog(resources.getString(R.string.cancel_service_owner))

            } else {
                databaseReference.child("services").child(service?.id!!)
                    .setValue(null).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                requireContext(), resources.getString(R.string.service_deleted),
                                Toast.LENGTH_SHORT
                            ).show()
                            MyServiceScreen.detailsServiceSheet.state =
                                BottomSheetBehavior.STATE_COLLAPSED
                        }
                        else {
                            Toast.makeText(
                                requireContext(),
                                it.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                databaseReference.child("myService").child(service?.id!!)
                    .setValue(null)
            }
        }

    }

    private fun getServiceDetails() {
        ProgressLoading.show()
        databaseReference.child("services").child(Const.serviceId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    service = snapshot.getValue(ServiceData::class.java)
                    if (service != null) {
                        showServiceDetails(service!!)
                        lastDate = service?.date!!
                        ProgressLoading.dismiss()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    ProgressLoading.dismiss()
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showServiceDetails(service: ServiceData) {
        binding.serviceTitleEt.setText("${service.title}")
        binding.servicePriceEt.setText("${service.price}")
        binding.servicePapersEt.setText("${service.papers}")
        binding.serviceDetailsEt.setText("${service.details}")
        binding.serviceTimeEt.setText("${service.time}")
        binding.serviceDateEt.setText("${service.date}")

    }


    private fun updateService(
        title: String,
        details: String,
        price: Double,
        papers: String,
        date: String,
        time: String
    ) {

        ProgressLoading.show()
        val userPhone = MyPreference.getPrefString("userPhone")
        val id = service?.id!!
        databaseReference.child("services").child(id).setValue(
            ServiceData(
                id, userPhone, title, details, price, papers, date, time
            )
        ).addOnCompleteListener { task ->
            ProgressLoading.dismiss()
            if (task.isSuccessful) {
                databaseReference.child("myServices").child(userPhone)
                    .child(id).setValue(
                        ServiceData(
                            id, userPhone, title, details, price, papers, date, time
                        )
                    )

                MyServiceScreen.detailsServiceSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.successfully_updated),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(requireContext(), "something wrong", Toast.LENGTH_SHORT).show()

            }
        }


    }

    private fun showConfirmDialog(message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setTitle("Current Service")
        builder.setMessage(message)
        builder.setPositiveButton(resources.getString(R.string.confirm),
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                databaseReference.child("myService").child(service?.id!!)
                    .setValue(null)
                databaseReference.child("services").child(service?.id!!)
                    .child("status").setValue(6)


                val tax = MyPreference.getUserData().tax?.plus(10)
                databaseReference.child("users").child(MyPreference.getUserData().phone!!)
                    .child("tax").setValue(tax)

                MyServiceScreen.detailsServiceSheet.state = BottomSheetBehavior.STATE_COLLAPSED

            })
        builder.setNegativeButton(android.R.string.cancel,
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })

        val dialog: AlertDialog = builder.create()
        dialog.show()

    }


}