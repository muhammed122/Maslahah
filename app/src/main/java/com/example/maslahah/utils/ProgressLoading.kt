package com.example.maslahah.utils


import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import com.example.maslahah.R

object ProgressLoading {
    private var progress: Dialog? = null

    private var hasActivity :Boolean = false



    fun init(activity: Activity) {
        progress = Dialog(activity)
        progress?.setContentView(R.layout.progress_dialog)
        progress?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progress?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    } // init

    fun show() {

        if (!hasActivity) {
            hasActivity = true
        }
            try {
                progress?.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }

    } // show

    fun isVisible(): Boolean {
        try {
            return progress!!.isShowing
        } catch (e: Exception) {
            return false
        }
    } // fun of isVisible

    fun dismiss() {
        progress?.dismiss()
        hasActivity = false
    } // dismiss
} // class of ProgressLoading