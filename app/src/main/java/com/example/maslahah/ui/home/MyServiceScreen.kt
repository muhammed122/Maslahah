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
import com.example.maslahah.databinding.FragmentMyServiceScreenBinding
import com.example.maslahah.ui.ServiceDetailsScreen
import com.example.maslahah.ui.adapter.ServiceAdapter
import com.example.maslahah.ui.adapter.ServiceItemClickListener
import com.example.maslahah.utils.Const
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*


class MyServiceScreen : Fragment(), ServiceItemClickListener {


    private var _binding: FragmentMyServiceScreenBinding? = null
    private val binding get() = _binding!!

    val servicesList = ArrayList<ServiceData>()
    lateinit var serviceAdapter: ServiceAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager


    private lateinit var databaseReference: DatabaseReference


    lateinit var listener: ValueEventListener

    private var detailsServiceView: ConstraintLayout? = null


    companion object {
        var detailsServiceSheet = BottomSheetBehavior<ConstraintLayout>()
    }


    private fun initBottomSheet() {
        detailsServiceView = binding.detailsServiceLayout3.parentMyServiceDetailsLayout
        detailsServiceSheet = BottomSheetBehavior.from(detailsServiceView!!)
        detailsServiceSheet.state = BottomSheetBehavior.STATE_COLLAPSED




    }

    private fun initRecyclerView() {
        serviceAdapter = ServiceAdapter(this)
        layoutManager = LinearLayoutManager(requireContext())
        binding.myServiceRecycle.adapter = serviceAdapter
        binding.myServiceRecycle.layoutManager = layoutManager


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
        return inflater.inflate(R.layout.fragment_my_service_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMyServiceScreenBinding.bind(view)


        if (MyPreference.getLanguage() == "ar") {
            binding.myServicesScreen.rotationY = 180f
        }

        initBottomSheet()
        initRecyclerView()
        getServicesData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val phone = MyPreference.getPrefString("userPhone")
        databaseReference.child("services")
            .orderByChild("serviceOwnerPhone")
            .equalTo(phone)
            .removeEventListener(listener)

        _binding=null

    }



    private fun getServicesData() {
        ProgressLoading.show()
        val phone = MyPreference.getPrefString("userPhone")
        listener =
            databaseReference.child("services").orderByChild("serviceOwnerPhone").equalTo(phone)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        servicesList.clear()

                        if (snapshot.hasChildren()) {
                            binding.noLoadedTxt.visibility = View.GONE
                            for (data in snapshot.children) {
                                val service = data.getValue(ServiceData::class.java)!!
                                servicesList.add(service)
                            }

                        } else {
                                binding.noLoadedTxt.visibility = View.VISIBLE

                        }
                        serviceAdapter.setServices(servicesList)
                        ProgressLoading.dismiss()

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
            ?.replace(R.id.details_my_service_fragment, EditServiceScreenFragment())?.commit()

        detailsServiceSheet.state = BottomSheetBehavior.STATE_EXPANDED

    }


}