package com.example.maslahah.ui.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.maslahah.BaseFragment
import com.example.maslahah.R
import com.example.maslahah.data.UserData
import com.example.maslahah.databinding.FragmentCreateAccountBinding
import com.example.maslahah.ui.home.HomeActivityScreen
import com.example.maslahah.utils.Const
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.json.JSONException


class CreateAccountFragment : BaseFragment() {


    private val TAG = "CreateAccountFragment"

    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!


    private var imageUri: Uri? = null
    private var imageUrl: String = ""


    private lateinit var auth: FirebaseAuth


    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference


    lateinit var googleSignInClient: GoogleSignInClient


    private fun initGoogleClientSignIn() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }


    private val RC_SIGN_IN = 111
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun uploadImageToServer() {

        if (imageUri != null) {
            val storage = storageReference.child("images")
                .child("${System.currentTimeMillis()}image")
            storage.putFile(imageUri!!)
                .addOnSuccessListener { task ->
                    storage.downloadUrl.addOnSuccessListener { uri ->
                        imageUrl = uri.toString()
                        createUserWithEmailAndPassword(
                            binding.emailCreateAcc.text.toString().trim(),
                            binding.passwordCreateAcc.text.toString().trim()
                        )

                    }
                }.addOnFailureListener { e ->
                    ProgressLoading.dismiss()
                    Toast.makeText(requireContext(), "${e.localizedMessage}", Toast.LENGTH_SHORT)
                        .show()
                    imageUrl = ""
                }
        } else {

            createUserWithEmailAndPassword(
                binding.emailCreateAcc.text.toString().trim(),
                binding.passwordCreateAcc.text.toString().trim()
            )
        }
    }

    private fun initFirebaseTools() {
        storageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirebaseTools()
        initGoogleClientSignIn()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                     auth.currentUser?.let {
                        getUserDataWithEmail(it)
                    }

                } else {

                    Log.w("ddddddd", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed. ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK) {
                if (data?.data != null) {
                    imageUri = data.data
                    binding.cameraImage.visibility = View.GONE
                    binding.userImage.visibility = View.VISIBLE
                    binding.userImage.setImageURI(data.data)
                    binding.removeBtn.visibility = View.VISIBLE

                    binding.createAccBtn.isEnabled =
                        binding.nameCreateAcc.text.toString().isNotEmpty() &&
                                binding.passwordCreateAcc.text.toString().isNotEmpty() &&
                                binding.emailCreateAcc.text.toString().isNotEmpty() &&
                                binding.phoneCreateAcc.text.toString().length == 11
                                && imageUri != null
                }

            }
        }

    }

    lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateAccountBinding.bind(view)

        navController = Navigation.findNavController(requireView())
        enableConfirmButton()


        //open gallery
        binding.selectImageLayout.setOnClickListener {
            if (checkStoragePermission(MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_GALLERY))
                galleryIntent()
        }

        binding.goLogin.setOnClickListener {
            navController.navigateUp()
            navController.navigate(
                CreateAccountFragmentDirections.actionCreateAccountFragmentToEmailLoginScreen()
            )
        }



        binding.googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }

//        binding.fbLoginButton.setOnClickListener {
//
//            val intent = Intent(requireActivity(), FacebookAuthActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
//            startActivity(intent)
//            activity?.finish()
//
//
//        }

        binding.removeBtn.setOnClickListener {
            imageUri = null
            imageUrl = ""
            binding.userImage.setImageResource(0)
            binding.userImage.visibility = View.GONE
            binding.cameraImage.visibility = View.VISIBLE
            binding.removeBtn.visibility = View.GONE
            binding.createAccBtn.isEnabled = false

        }


        auth = FirebaseAuth.getInstance()
        binding.createAccBtn.setOnClickListener {
            if (!binding.phoneCreateAcc.text.toString().startsWith("01")) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.worng_phone_number),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                if (!binding.termsCheck.isChecked) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.terms_agree),
                        Toast.LENGTH_SHORT
                    ).show()
                } else
                    checkPhoneNotHaveAccount(binding.phoneCreateAcc.text.toString())
            }
        }


        binding.termsText.setOnClickListener {

            navController.navigateUp()
            navController.navigate(
                CreateAccountFragmentDirections.actionCreateAccountFragmentToTermsAndPolicyFragment()
            )
        }


        faceBookInit()

        //binding.fbLoginButton.performClick()


    }


    private fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    uploadUserData(
                        binding.nameCreateAcc.text.toString().trim(),
                        email, binding.phoneCreateAcc.text.toString().trim(), imageUrl, password
                    )
                else {
                    ProgressLoading.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

    }


    private fun checkPhoneNotHaveAccount(phone: String) {
        ProgressLoading.show()
        databaseReference.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(phone)) {
                        ProgressLoading.dismiss()
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.have_acc),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else
                        uploadImageToServer()
                }

                override fun onCancelled(error: DatabaseError) {
                    ProgressLoading.dismiss()

                }
            })


    }

    private fun uploadUserData(
        name: String,
        email: String,
        phone: String,
        image: String,
        password: String
    ) {
        databaseReference.child("users").child(phone)
            .setValue(UserData(name, phone, image, email, password))
            .addOnCompleteListener { task ->
                ProgressLoading.dismiss()
                if (task.isSuccessful) {
                    //cash user data
                    MyPreference.saveUserData(UserData(name, phone, image, email))


                    //navigate to verify phone
                    navController.navigateUp()
                    navController.navigate(
                        CreateAccountFragmentDirections.actionCreateAccountFragmentToVerifyFragment(
                            phone
                        )
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        "${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()

                }


            }

    }


    private fun enableConfirmButton() {
        binding.passwordCreateAcc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.createAccBtn.isEnabled =
                    binding.nameCreateAcc.text.toString().isNotEmpty() &&
                            binding.passwordCreateAcc.text.toString().isNotEmpty() &&
                            binding.emailCreateAcc.text.toString().isNotEmpty() &&
                            binding.phoneCreateAcc.text.toString().length == 11

            }

        })

        binding.nameCreateAcc.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.createAccBtn.isEnabled =
                        binding.nameCreateAcc.text.toString().isNotEmpty() &&
                                binding.passwordCreateAcc.text.toString().isNotEmpty() &&
                                binding.emailCreateAcc.text.toString().isNotEmpty() &&
                                binding.phoneCreateAcc.text.toString().length == 11

                }

            })


        binding.emailCreateAcc.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.createAccBtn.isEnabled =
                        binding.nameCreateAcc.text.toString().isNotEmpty() &&
                                binding.passwordCreateAcc.text.toString().isNotEmpty() &&
                                binding.emailCreateAcc.text.toString().isNotEmpty() &&
                                binding.phoneCreateAcc.text.toString().length == 11

                }

            })

        binding.phoneCreateAcc.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.createAccBtn.isEnabled =
                        binding.nameCreateAcc.text.toString().isNotEmpty() &&
                                binding.passwordCreateAcc.text.toString().isNotEmpty() &&
                                binding.emailCreateAcc.text.toString().isNotEmpty() &&
                                binding.phoneCreateAcc.text.toString().length == 11
                }

            })
    }


    lateinit var callbackManager: CallbackManager
    lateinit var accessToken: AccessToken
    private fun faceBookInit() {
        callbackManager = CallbackManager.Factory.create()
        binding.fbLoginButton.apply {
            setPermissions(listOf("public_profile", "email"))
            fragment = this@CreateAccountFragment

            registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    accessToken = loginResult!!.accessToken
                    // App code
                    val request =
                        GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response -> // Application code
                            try {
//                                val name = `object`.getString("name")
//                                val email = `object`.getString("email")
//                                val id = `object`.getString("id")
//                                val image = `object`.getString("picture")
//
//                                Log.d(TAG, "onSuccess: $image")

                                handleFacebookAccessToken(loginResult.accessToken)


                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }

                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,birthday,picture.type(large)")
                    request.parameters = parameters
                    request.executeAsync()
                    LoginManager.getInstance().logOut()
                }

                override fun onCancel() {
                    // App code
                    Log.e("ddddddd", "onSuccess cancel: ")
                }

                override fun onError(exception: FacebookException) {
                    // App code
                    Log.e("dddddddddd", "onSuccess: ${exception.localizedMessage} ")
                }
            })
        }
    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    //  updateUI(user)
                    if (user != null)
                        getUserDataWithEmail(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("ddddddd", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed. ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                }
            }
    }

    private fun getUserDataWithEmail(firebaseUser: FirebaseUser) {
        databaseReference.child("users").orderByChild("email").equalTo(firebaseUser.email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        for (data in snapshot.children) {
                            if (data.child("email")
                                    .getValue(String::class.java) == firebaseUser.email
                            ) {
                                val user = data.getValue(UserData::class.java)
                                MyPreference.saveUserData(user!!)

                                ProgressLoading.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "Successfully Login",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        requireActivity(),
                                        HomeActivityScreen::class.java
                                    )
                                )
                                activity?.finish()
                            }
                        }
                    } else {
                        navController.navigateUp()
                        navController.navigate(
                            CreateAccountFragmentDirections.actionCreateAccountFragmentToAddPhoneScreen(
                                firebaseUser.email.toString(),
                                firebaseUser.displayName.toString(),
                                firebaseUser.photoUrl.toString()
                            )
                        )

                    }


                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })


    }


}