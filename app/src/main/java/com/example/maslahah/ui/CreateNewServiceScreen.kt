package com.example.maslahah.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.maslahah.R
import com.example.maslahah.data.ServiceData
import com.example.maslahah.databinding.FragmentCreateNewServiceScreenBinding
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.drawable.ColorDrawable
import android.util.Log
import java.text.DateFormatSymbols


class CreateNewServiceScreen : Fragment() {

    private var _binding: FragmentCreateNewServiceScreenBinding? = null
    val binding get() = _binding!!

    private lateinit var databaseReference: DatabaseReference


    var serviceDateSetListener: DatePickerDialog.OnDateSetListener? = null
    var serviceTimeSetListener: TimePickerDialog.OnTimeSetListener? = null


    var lastDate = ""
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseReference = FirebaseDatabase.getInstance().reference

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_service_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateNewServiceScreenBinding.bind(view)
        enableConfirmButton()


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


        binding.createServiceBtn.setOnClickListener {
            ProgressLoading.show(requireActivity())
            createNewService(
                binding.serviceTitleEt.text.toString().trim(),
                binding.serviceDetailsEt.text.toString().trim(),
                binding.serviceDurationEt.text.toString().trim(),
                binding.servicePapersEt.text.toString().trim(),
                lastDate,
                binding.serviceTimeEt.text.toString().trim(),

                )
        }
    }


    private fun createNewService(
        title: String,
        details: String,
        duration: String,
        papers: String,
        date: String,
        time: String,
    ) {
        val userPhone = MyPreference.getPrefString("userPhone")
        val id = databaseReference.child("services").push().key
        databaseReference.child("services").child(id!!).setValue(
            ServiceData(
                id, userPhone, title, details, duration, papers, date, time
            )
        ).addOnCompleteListener { task ->
            ProgressLoading.dismiss()
            if (task.isSuccessful) {
                databaseReference.child("myServices").child(userPhone)
                    .child(id).setValue(ServiceData(
                        id, userPhone, title, details, duration, papers, date, time
                    ))
                clearData()
                Toast.makeText(requireContext(), "successfully created", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "something wrong", Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun clearData() {

        binding.serviceTimeEt.text?.clear()
        binding.serviceDateEt.text?.clear()
        binding.serviceDurationEt.text?.clear()
        binding.serviceTitleEt.text?.clear()
        binding.serviceDetailsEt.text?.clear()
        binding.servicePapersEt.text?.clear()


    }

    private fun enableConfirmButton() {
        binding.serviceTitleEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.createServiceBtn.isEnabled =
                    binding.serviceTitleEt.text.toString().isNotEmpty() &&
                            binding.serviceDetailsEt.text.toString().isNotEmpty() &&
                            binding.serviceDateEt.text.toString().isNotEmpty() &&
                            binding.serviceTimeEt.text.toString().isNotEmpty() &&
                            binding.serviceDurationEt.text.toString().isNotEmpty() &&
                            binding.servicePapersEt.text.toString().isNotEmpty()

            }

        })

        binding.serviceDetailsEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.createServiceBtn.isEnabled =
                    binding.serviceTitleEt.text.toString().isNotEmpty() &&
                            binding.serviceDetailsEt.text.toString().isNotEmpty() &&
                            binding.serviceDateEt.text.toString().isNotEmpty() &&
                            binding.serviceTimeEt.text.toString().isNotEmpty() &&
                            binding.serviceDurationEt.text.toString().isNotEmpty() &&
                            binding.servicePapersEt.text.toString().isNotEmpty()

            }

        })

        binding.serviceDateEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.createServiceBtn.isEnabled =
                    binding.serviceTitleEt.text.toString().isNotEmpty() &&
                            binding.serviceDetailsEt.text.toString().isNotEmpty() &&
                            binding.serviceDateEt.text.toString().isNotEmpty() &&
                            binding.serviceTimeEt.text.toString().isNotEmpty() &&
                            binding.serviceDurationEt.text.toString().isNotEmpty() &&
                            binding.servicePapersEt.text.toString().isNotEmpty()

            }

        })

        binding.serviceTimeEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.createServiceBtn.isEnabled =
                    binding.serviceTitleEt.text.toString().isNotEmpty() &&
                            binding.serviceDetailsEt.text.toString().isNotEmpty() &&
                            binding.serviceDateEt.text.toString().isNotEmpty() &&
                            binding.serviceTimeEt.text.toString().isNotEmpty() &&
                            binding.serviceDurationEt.text.toString().isNotEmpty() &&
                            binding.servicePapersEt.text.toString().isNotEmpty()

            }

        })

        binding.serviceDurationEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.createServiceBtn.isEnabled =
                    binding.serviceTitleEt.text.toString().isNotEmpty() &&
                            binding.serviceDetailsEt.text.toString().isNotEmpty() &&
                            binding.serviceDateEt.text.toString().isNotEmpty() &&
                            binding.serviceTimeEt.text.toString().isNotEmpty() &&
                            binding.serviceDurationEt.text.toString().isNotEmpty() &&
                            binding.servicePapersEt.text.toString().isNotEmpty()

            }

        })

        binding.servicePapersEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.createServiceBtn.isEnabled =
                    binding.serviceTitleEt.text.toString().isNotEmpty() &&
                            binding.serviceDetailsEt.text.toString().isNotEmpty() &&
                            binding.serviceDateEt.text.toString().isNotEmpty() &&
                            binding.serviceTimeEt.text.toString().isNotEmpty() &&
                            binding.serviceDurationEt.text.toString().isNotEmpty() &&
                            binding.servicePapersEt.text.toString().isNotEmpty()

            }

        })


    }

}