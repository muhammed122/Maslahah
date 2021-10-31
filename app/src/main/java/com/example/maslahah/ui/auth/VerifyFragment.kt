package com.example.maslahah.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.maslahah.R
import com.example.maslahah.databinding.FragmentCreateAccountBinding
import com.example.maslahah.databinding.FragmentVerifyBinding
import com.example.maslahah.ui.home.HomeActivityScreen
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class VerifyFragment : Fragment() {

    private val TAG = "VerifyFragment"

    private var _binding: FragmentVerifyBinding? = null
    private val binding get() = _binding!!


    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null

    private lateinit var auth: FirebaseAuth


    var phone = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phone = VerifyFragmentArgs.fromBundle(
            requireArguments()
        ).phone


        auth = FirebaseAuth.getInstance()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVerifyBinding.bind(view)




        binding.otp.setOtpCompletionListener { code ->
            binding.verifyOtp.isEnabled = true
            verifyPhoneNumberWithCode(mVerificationId, code)
        }



        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                //Log.d(TAG, "ddddddddonVerificationCompleted:$credential")
                //signInWithPhoneAuthCredential(credential)

                ProgressLoading.dismiss()

            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "dddddddonVerificationFailed", e)
                Toast.makeText(context, "${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                ProgressLoading.dismiss()


                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "dddddddonCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                forceResendingToken = token
            }


        }

        startPhoneNumberVerify("+20$phone")
    }

    private fun startPhoneNumberVerify(phone: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks!!)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        ProgressLoading.show(requireActivity())
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)

    }

    private fun resendVerificationCode(
        phone: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks!!) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(token!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)


    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "ddddddddd signInWithCredential:success")
                    val user = task.result?.user
                    ProgressLoading.dismiss()

                    Toast.makeText(requireContext(), "Successfully login", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(requireActivity(), HomeActivityScreen::class.java))
                    activity?.finish()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "ddddd signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }

                    ProgressLoading.dismiss()
                    Toast.makeText(context, "${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

