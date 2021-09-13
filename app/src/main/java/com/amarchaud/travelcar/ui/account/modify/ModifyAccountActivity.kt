package com.amarchaud.travelcar.ui.account.modify

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.amarchaud.travelcar.R
import com.amarchaud.travelcar.databinding.FragmentModifyAccountBinding
import com.amarchaud.travelcar.domain.local.user.AppUser
import com.amarchaud.travelcar.ui.account.modify.photo_dialog.ChoicePhotoDialog
import com.amarchaud.travelcar.utils.extensions.nowToMilliseconds
import com.amarchaud.travelcar.utils.extensions.toMilliseconds
import com.amarchaud.travelcar.utils.extensions.toShortDate
import com.amarchaud.travelcar.utils.textChanges
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.io.File
import java.util.*


@AndroidEntryPoint
class ModifyAccountActivity : AppCompatActivity() {

    private lateinit var binding: FragmentModifyAccountBinding
    private val viewModel: ModifyAccountFragmentViewModel by viewModels()

    companion object {
        const val TAG = "ModifyAccountActivity"
        fun newInstance(): ModifyAccountActivity {
            return ModifyAccountActivity()
        }

        const val ARG_USER_IN = "arg_user"
        const val ARG_USER_SAVED = "arg_user_saved"
    }

    // immutable, do not touch to this pointer
    private val userToModify by lazy { intent.getParcelableExtra<AppUser?>(ARG_USER_IN) }

    // manage autocomplete
    private val autoCompleteToken by lazy { AutocompleteSessionToken.newInstance() }
    private val placesClient by lazy { Places.createClient(this@ModifyAccountActivity) }
    private val predictionsRequest by lazy {
        FindAutocompletePredictionsRequest
            .builder()
            .setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(autoCompleteToken)
    }

    private val tempFile: File by lazy {
        File.createTempFile("IMG_", ".png", getExternalFilesDir(Environment.DIRECTORY_PICTURES))
    }

    private val photoUri by lazy {
        FileProvider.getUriForFile(this, "${packageName}.provider", tempFile)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(ARG_USER_SAVED, viewModel.appUser)
        super.onSaveInstanceState(outState)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentModifyAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeTextChanges()

        with(binding) {
            chooseImage.setOnClickListener {
                manageChoicePhotoDialog()
            }

            birthdayEditText.keyListener = null
            birthdayEditText.setOnClickListener {
                manageBirthdayDialog()
            }

            saveButton.setOnClickListener {

                viewModel.manageUser()
                setResult(RESULT_OK)
                /*
                setResult(RESULT_OK, Intent().apply {
                    putExtra(ARG_USER_SAVED, viewModel.appUser)
                })*/
                finish()
                overridePendingTransition(R.anim.exit_nothing, R.anim.exit_to_right)
            }

            deleteButton.isEnabled = userToModify != null
            deleteButton.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.eraseAccount()
                    setResult(RESULT_CANCELED)
                    finish()
                    overridePendingTransition(R.anim.exit_nothing, R.anim.exit_to_right)
                }
            }
        }

        if (savedInstanceState == null) {
            if (userToModify == null) {
                viewModel.appUser = AppUser() // create empty user
            } else {
                viewModel.appUser = userToModify?.copy()
            }
        } else {
            viewModel.appUser = savedInstanceState.getParcelable(ARG_USER_SAVED)
        }

        handleUser(viewModel.appUser)
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
        builder.setTheme(R.style.MaterialDatePicker_Theme)
        val calendarConstraints = CalendarConstraints.Builder()

        viewModel.appUser?.birthday?.let {
            calendarConstraints.apply {
                this.setOpenAt(it.toMilliseconds())
            }
        } ?: calendarConstraints.apply {
            this.setOpenAt(nowToMilliseconds())
        }

        calendarConstraints.apply {
            this.setEnd(nowToMilliseconds())
        }
        builder.setCalendarConstraints(calendarConstraints.build())
        val picker = builder.build()
        picker.show(supportFragmentManager, "datePicker")
        picker.addOnPositiveButtonClickListener {
            it?.let {
                val birthdayDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                viewModel.appUser?.birthday = birthdayDate // save
                binding.birthdayEditText.setText(birthdayDate.toShortDate())
            }
        }
    }

    private fun handleUser(user: AppUser?) {
        user?.let {
            with(binding) {
                this.firstNameEditText.setText(user.firstName)
                this.lastNameEditText.setText(user.lastName)
                user.photoUri?.let {
                    Glide.with(binding.chooseImage)
                        .load(it)
                        .into(binding.chooseImage)
                }
                this.addressEditText.setText(user.address)
                this.birthdayEditText.setText(user.birthday?.toShortDate())
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeTextChanges() {

        /**
         * Management saveButton isEnable status
         */
        lifecycleScope.launchWhenCreated {

            with(binding) {
                saveButton.isEnabled = false
                combine(
                    firstNameEditText.textChanges(),
                    lastNameEditText.textChanges(),
                    addressEditText.textChanges(),
                    birthdayEditText.textChanges()
                ) { firstName, lastName, address, birthday ->

                    // save datas
                    if (!firstName.isNullOrEmpty()) {
                        viewModel.appUser?.firstName = firstName.toString()
                    } else {
                        viewModel.appUser?.firstName = null
                    }

                    if (!lastName.isNullOrEmpty()) {
                        viewModel.appUser?.lastName = lastName.toString()
                    } else {
                        viewModel.appUser?.lastName = null
                    }

                    if (!address.isNullOrEmpty()) {
                        viewModel.appUser?.address = address.toString()
                    } else {
                        viewModel.appUser?.address = null
                    }

                    !firstName.isNullOrEmpty()

                }.collectLatest { isEnable ->
                    saveButton.isEnabled = isEnable
                }
            }
        }

        /**
         * Manage address search
         */
        lifecycleScope.launchWhenCreated {
            binding.addressEditText.textChanges().collectLatest {

                val request = predictionsRequest
                    .setQuery(it.toString())
                    .build()

                placesClient
                    .findAutocompletePredictions(request)
                    .addOnSuccessListener { response: FindAutocompletePredictionsResponse -> // if place is found

                        val mutableSearchList = mutableListOf<String>()
                        response.autocompletePredictions.forEach {
                            mutableSearchList.add(
                                getString(R.string.account_modify_displayed_address_search, it.getPrimaryText(null), it.getSecondaryText(null))
                            )
                        }

                        val adapter = ArrayAdapter(
                            this@ModifyAccountActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            mutableSearchList
                        )
                        binding.addressEditText.setAdapter(adapter)
                    }
                    .addOnFailureListener { exception: Exception? -> // no connection
                        if (exception is ApiException) {
                            Log.e(TAG, "Place not found: " + exception.statusCode)
                        }
                    }
            }
        }
    }


    private fun launchSelectPhoto() {
        selectImageFromGalleryResult.launch("image/*")
    }

    private fun launchTakePhoto() {
        permissionCameraResult.launch(Manifest.permission.CAMERA)
    }

    override fun onBackPressed() {
        when {
            userToModify == null && viewModel.somethingChanged() -> { // no account yet
                displayQuitAlertDialog()
            }
            userToModify != null && (userToModify != viewModel.appUser) -> {
                displayQuitAlertDialog()
            }
            else -> {
                super.onBackPressed()
                setResult(RESULT_CANCELED)
                finish()
                overridePendingTransition(R.anim.exit_nothing, R.anim.exit_to_right)
            }
        }
    }

    private fun displayQuitAlertDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.account_modify_user_not_saved)
            .setPositiveButton(getString(R.string.account_modify_save)) { dialog, id ->

                viewModel.manageUser()
                /*
                setResult(RESULT_OK, Intent().apply {
                    putExtra(ARG_USER_SAVED, viewModel.appUser)
                })*/
                finish()
                overridePendingTransition(R.anim.exit_nothing, R.anim.exit_to_right)
            }
            .setNegativeButton(getString(android.R.string.cancel)) { dialog, id ->
                setResult(RESULT_CANCELED)
                finish()
                overridePendingTransition(R.anim.exit_nothing, R.anim.exit_to_right)
            }
            .create()
            .show()
    }


    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Glide.with(binding.chooseImage)
                    .load(it)
                    .into(binding.chooseImage)

                viewModel.appUser?.photoUri = it // save
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
                viewModel.appUser?.photoUri = photoUri // save
            }
        }
}