package com.example.maslahah

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException


open class BaseFragment : Fragment() {


    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_CAMERA: Int = 122
    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_GALLERY = 123
    val SELECT_FILE = 101
    val REQUEST_CAMERA = 100
    val SELECT_FILE_TITLE = "SELECT_FILE"



    var currentImagePath = ""
    fun cameraIntent() {
        if(!checkCameraPermission(9)){
            return
        }
        val fileName = "image"
        val storageFile = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        try {
            val imageFile = File.createTempFile(fileName, ".jpg", storageFile)
            currentImagePath=imageFile.absolutePath
            val imageUri = FileProvider.getUriForFile(
                requireContext(),
                "com.drovox.captinapp.fileprovider",
                imageFile
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)

            startActivityForResult(intent, REQUEST_CAMERA)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    fun galleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, SELECT_FILE_TITLE), SELECT_FILE)
    }


    private fun permissionCameraAllowed(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
            return true
        return false
    }

    private fun permissionAllowed(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
            return true
        return false
    }
    fun checkCameraPermission(permissionRequestCode: Int): Boolean {
        if (permissionCameraAllowed())
            return true
        //check permission Allowed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    permissionRequestCode
                )

                return false
            } else {
                return true
            }

        }
        return true
    }


    //permissions fun
    @SuppressLint("ObsoleteSdkInt")
    fun checkStoragePermission(permissionRequestCode: Int): Boolean {
        if (permissionAllowed())
            return true
        //check permission Allowed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    // explain why we want
                    explainMessage(
                        getString(R.string.external_storage_message),
                        permissionRequestCode
                    )
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        permissionRequestCode
                    )
                }
                return false
            } else {
                return false
            }

        }
        return true
    }


    private fun explainMessage(message: String, permissionRequestCode: Int) {
        val alert: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alert.setMessage(message)
        alert.setPositiveButton("Ok",
            DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()

                //ask permission again
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    permissionRequestCode
                )

            })
        alert.setCancelable(false)
        alert.show()
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


}