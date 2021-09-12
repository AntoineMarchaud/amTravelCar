package com.amarchaud.travelcar.ui.account.modify.photo_dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.amarchaud.travelcar.R

class ChoicePhotoDialog : DialogFragment() {

    companion object {
        const val ARG_OUTPUT = "arg_output"
        const val ARG_OUTPUT_CHOICE = "arg_output_choice"

        enum class Choice {
            CANCEL,
            TAKE_PHOTO,
            SELECT_PHOTO
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setTitle(R.string.account_modify_change_photo_title)
                .setItems(
                    arrayOf(getString(R.string.account_modify_change_photo_take_photo), getString(R.string.account_modify_change_photo_select_photo))
                ) { _, which ->
                    when (which) {
                        0 -> setFragmentResult(ARG_OUTPUT, Bundle().apply {
                            putSerializable(ARG_OUTPUT_CHOICE, Choice.TAKE_PHOTO)
                        })
                        1 -> setFragmentResult(ARG_OUTPUT, Bundle().apply {
                            putSerializable(ARG_OUTPUT_CHOICE, Choice.SELECT_PHOTO)
                        })
                    }
                }
                .setNegativeButton(getString(android.R.string.cancel)) { dialog, id ->
                    setFragmentResult(ARG_OUTPUT, Bundle().apply {
                        putSerializable(ARG_OUTPUT_CHOICE, Choice.CANCEL)
                    })
                    dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}