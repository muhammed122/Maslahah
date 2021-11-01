package com.example.maslahah.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.maslahah.R
import com.example.maslahah.databinding.FragmentMainHomeScreenBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Adapter
import java.text.SimpleDateFormat
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.maslahah.data.ServiceData
import com.example.maslahah.ui.NotificationActivityScreen
import com.example.maslahah.ui.ServiceDetailsScreen
import com.example.maslahah.ui.adapter.ServiceAdapter
import com.example.maslahah.ui.adapter.ServiceItemClickListener
import com.example.maslahah.ui.adapter.TabLayoutAdapter
import com.example.maslahah.ui.auth.EmailLoginScreenDirections
import com.example.maslahah.utils.MyBottomSheetListener
import com.example.maslahah.utils.MyPreference
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*
import kotlin.collections.ArrayList


class MainHomeScreenFragment : Fragment() {


    private var _binding: FragmentMainHomeScreenBinding? = null
    private val binding get() = _binding!!


    private var currentServiceView: ConstraintLayout? = null
    private var currentServiceSheet = BottomSheetBehavior<ConstraintLayout>()



    lateinit var navController: NavController

    companion object {
        var myBottomSheetListener: MyBottomSheetListener? = null
    }

    private fun initBottomSheet() {
        currentServiceView = binding.currentServiceLayout2.parentServiceCurrentLayout
        currentServiceSheet = BottomSheetBehavior.from(currentServiceView!!)
        currentServiceSheet.state = BottomSheetBehavior.STATE_COLLAPSED

        currentServiceSheet.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                myBottomSheetListener?.onStateChange(newState)
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }


    lateinit var tabLayoutAdapter: TabLayoutAdapter

    private fun setUpTabLayoutWithViewPager() {
        tabLayoutAdapter = TabLayoutAdapter(
            childFragmentManager, 1,
            binding.servicesTabLayout2.tabCount, 1
        )
        binding.servicesPager.adapter = tabLayoutAdapter



        binding.servicesPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                binding.servicesTabLayout2
            )
        )
        binding.servicesTabLayout2.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {


            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.servicesPager.currentItem = tab!!.position
            }
        })


    }


    lateinit var databaseReference: DatabaseReference

    var currentDate = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val date = Calendar.getInstance().time
        val d = date.toString().split(' ')
        currentDate = "${d[2]} ${d[1]}"
        databaseReference = FirebaseDatabase.getInstance().reference


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_home_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainHomeScreenBinding.bind(view)
        Glide.with(requireContext()).load(MyPreference.getPrefString("userImage")).into(
            binding.userImageToolbar
        )
        navController = Navigation.findNavController(requireView())
        setUpTabLayoutWithViewPager()
        initBottomSheet()

        binding.todayDateTxt.text = currentDate
        observeToCurrentService()

        binding.notificationToolbar.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivityScreen::class.java))
        }

        binding.userImageToolbar.setOnClickListener {
            navController.navigate(
                MainHomeScreenFragmentDirections.actionHomeScreenFragmentToProfileFragmentScreen()
            )
        }

    }

    private fun observeToCurrentService() {
        val phone = MyPreference.getPrefString("userPhone")
        databaseReference.child("users").child(phone).child("available")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.getValue(Boolean::class.java) == false) {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.current_service_fragment, CurrentServiceScreen())
                            ?.commit()

                        currentServiceSheet.peekHeight = 450
                        MyPreference.setPrefBoolean("available", false)
                    } else {
                        currentServiceSheet.peekHeight = 0
                        currentServiceSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                        MyPreference.setPrefBoolean("available", true)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })

    }




}