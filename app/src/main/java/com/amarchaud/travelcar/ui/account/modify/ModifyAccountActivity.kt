package com.amarchaud.travelcar.ui.account.modify

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amarchaud.travelcar.databinding.FragmentModifyAccountBinding
import com.amarchaud.travelcar.domain.local.user.AppUser
import com.amarchaud.travelcar.ui.account.modify.photo_dialog.ChoicePhotoDialog
import com.amarchaud.travelcar.ui.account.modify.photo_dialog.DatePickerFragment
import com.amarchaud.travelcar.utils.data_adapter.LocalDateAdapter
import com.amarchaud.travelcar.utils.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.File
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.time.LocalDate
import java.util.*


@AndroidEntryPoint
class ModifyAccountActivity : AppCompatActivity() {

    private lateinit var binding: FragmentModifyAccountBinding
    private val viewModel: ModifyAccountFragmentViewModel by viewModels()

    companion object {
        fun newInstance(): ModifyAccountActivity {
            return ModifyAccountActivity()
        }
    }

    private val tempPhoto: File by lazy {
        File.createTempFile(
            "IMG_",
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }

    private val photoUri by lazy {
        FileProvider.getUriForFile(
            this, "${packageName}.provider", tempPhoto
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentModifyAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeTextChanges()
        viewModel.imageUri = null

        with(binding) {
            chooseImage.setOnClickListener {
                manageChoicePhotoDialog()
            }

            birthdayEditText.keyListener = null
            birthdayEditText.setOnClickListener {
                manageBirthdayDialog()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collect {
                    handleUser(it)
                }
            }
        }
    }

    private fun manageChoicePhotoDialog() {
        val dialog = ChoicePhotoDialog()
        dialog.show(supportFragmentManager, "ChoicePhotoDialog")
        dialog.setFragmentResultListener(ChoicePhotoDialog.ARG_OUTPUT) { _, bundle ->
            when (bundle.getSerializable(ChoicePhotoDialog.ARG_OUTPUT_CHOICE) as ChoicePhotoDialog.Companion.Choice) {
                ChoicePhotoDialog.Companion.Choice.TAKE_PHOTO -> {
                    launchTakePhoto()
                }
                ChoicePhotoDialog.Companion.Choice.SELECT_PHOTO -> {
                    launchSelectPhoto()
                }
                else -> {
                    // dismiss, nothing to do
                }
            }
        }
    }

    private fun manageBirthdayDialog() {

        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()
        picker.show(supportFragmentManager, "datePicker")
        picker.addOnPositiveButtonClickListener {

            it?.let {
                val localDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                binding.birthdayEditText.setText(localDate.toString())
            }

        }

        //val newFragment = DatePickerFragment()
        //newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun handleUser(user: AppUser?) {
        user?.let {
            with(binding) {
                this.firstNameEditText.setText(user.firstName)
                this.lastNameEditText.setText(user.lastName)
                this.chooseImage.setImageURI(user.photoUri)
                this.addressEditText.setText(user.address)
                this.birthdayEditText.setText(user.birthday.toString())
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeTextChanges() {
        lifecycleScope.launchWhenStarted {
            with(binding) {
                saveButton.isEnabled = false
                combine(
                    firstNameEditText.textChanges(),
                    lastNameEditText.textChanges(),
                    addressEditText.textChanges()
                ) { firstName, lastName, address ->
                    !firstName.isNullOrEmpty() && !lastName.isNullOrEmpty() && !address.isNullOrEmpty()
                }.collectLatest { isEnable ->
                    saveButton.isEnabled = isEnable
                }
            }
        }
    }


    private fun launchSelectPhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        imageDiskResult.launch(intent)
    }

    private fun launchTakePhoto() {
        permissionCameraResult.launch(Manifest.permission.CAMERA)
    }


    /**
     * Contracts
     */

    private val imageDiskResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    val uri = result.data?.data
                    binding.chooseImage.setImageURI(uri)
                    viewModel.imageUri = uri // save
                }
            }
        }

    private val permissionCameraResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraResult.launch(photoUri)
            }
        }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result: Boolean ->
            if (result) {
                binding.chooseImage.setImageURI(photoUri)
                viewModel.imageUri = photoUri // save
            }
        }
}