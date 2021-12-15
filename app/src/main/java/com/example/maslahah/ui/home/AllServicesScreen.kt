package com.example.maslahah.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maslahah.R
import com.example.maslahah.data.ServiceData
import com.example.maslahah.databinding.FragmentAllServicesScreenBinding
import com.example.maslahah.ui.ServiceDetailsScreen
import com.example.maslahah.ui.adapter.ServiceAdapter
import com.example.maslahah.ui.adapter.ServiceItemClickListener
import com.example.maslahah.utils.Const
import com.example.maslahah.utils.ProgressLoading
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*


class AllServicesScreen : Fragment(), ServiceItemClickListener {


    private var _binding: FragmentAllServicesScreenBinding? = null
    private val binding get() = _binding!!


    private lateinit var databaseReference: DatabaseReference


    lateinit var serviceAdapter: ServiceAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager


    private var detailsServiceView: ConstraintLayout? = null
    companion object{
      var detailsServiceSheet = BottomSheetBehavior<ConstraintLayout>()
    }



    private var addServiceView: ConstraintLayout? = null
    private var addServiceSheet = BottomSheetBehavior<ConstraintLayout>()


    lateinit var listener: ChildEventListener

    private fun initBottomSheet() {
        detailsServiceView = binding.detailsServiceLayout2.parentServiceDetailsLayout
        detailsServiceSheet = BottomSheetBehavior.from(detailsServiceView!!)
        detailsServiceSheet.state = BottomSheetBehavior.STATE_COLLAPSED

        addServiceView = binding.addServiceLayout2.parentServiceLayout
        addServiceSheet = BottomSheetBehavior.from(addServiceView!!)
        addServiceSheet.state = BottomSheetBehavior.STATE_COLLAPSED
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
        return inflater.inflate(R.layout.fragment_all_services_screen, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllServicesScreenBinding.bind(view)

        initBottomSheet()
        initRecyclerView()
        getServicesData()

        binding.addNewServiceBtn.setOnClickListener {
            addServiceSheet.state = BottomSheetBehavior.STATE_EXPANDED

        }
    }


    private fun initRecyclerView() {
        serviceAdapter = ServiceAdapter(this)
        layoutManager = LinearLayoutManager(requireContext())
        binding.allServicesRecycler.adapter = serviceAdapter
        binding.allServicesRecycler.layoutManager = layoutManager


    }

    override fun onDestroyView() {
        super.onDestroyView()
        databaseReference.child("services").orderByChild("selected").equalTo(false)
            .removeEventListener(listener)
    }

    private fun getServicesData() {
        ProgressLoading.show()
        listener = databaseReference.child("services").orderByChild("selected").equalTo(false)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.hasChildren()) {
                        serviceAdapter.setOneService(snapshot.getValue(ServiceData::class.java)!!)
                        binding.noLoadedTxt.visibility = View.GONE
                    } else
                        binding.noLoadedTxt.visibility = View.VISIBLE

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                   serviceAdapter.updateService(snapshot.getValue(ServiceData::class.java)!!)

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    serviceAdapter.removeService(snapshot.getValue(ServiceData::class.java)!!)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {
                    ProgressLoading.dismiss()
                    binding.noLoadedTxt.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun serviceClickListener(serviceData: ServiceData) {
        Const.serviceId = serviceData.id!!
        val v = activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.details_service_fragment, ServiceDetailsScreen())?.commitNowAllowingStateLoss()

        detailsServiceSheet.state = BottomSheetBehavior.STATE_EXPANDED


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var time = false

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            when {
                detailsServiceSheet.state != BottomSheetBehavior.STATE_COLLAPSED -> {
                    detailsServiceSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@addCallback
                }
                addServiceSheet.state != BottomSheetBehavior.STATE_COLLAPSED -> {
                    addServiceSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@addCallback
                }

                MyServiceScreen.detailsServiceSheet.state != BottomSheetBehavior.STATE_COLLAPSED -> {
                    MyServiceScreen.detailsServiceSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@addCallback
                }
                else -> {
                    if (time) {
                        activity?.finish()
                        return@addCallback
                    }
                    time = true
                    Toast.makeText(requireContext(), "click again to exit", Toast.LENGTH_SHORT)
                        .show()
                    Handler(Looper.myLooper()!!).postDelayed({ time = false }, 2000)
                }
            }
        }
    }

}