package com.amarchaud.travelcar.ui.car.detail_car

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.SharedElementCallback
import com.amarchaud.travelcar.R
import com.amarchaud.travelcar.databinding.ActivityDetailCarBinding
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.utils.toReadableString

class DetailCarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCarBinding

    companion object {
        const val ARG_CAR = "arg_car"
        const val ARG_CAR_IMAGE_SAVED = "arg_car_image_saved"

        const val ARG_NAME_TRANSITION_PICTURE = "arg_transition_picture"
        const val ARG_NAME_TRANSITION_MAKE = "arg_transition_make"
        const val ARG_NAME_TRANSITION_YEAR = "arg_transition_year"
        const val ARG_NAME_TRANSITION_OPTIONS = "arg_transition_options"
    }

    private val appCar by lazy { intent.getParcelableExtra<AppCar>(ARG_CAR) }
    private val appImageSaved by lazy { intent.getParcelableExtra<Bitmap>(ARG_CAR_IMAGE_SAVED) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {

            appCar?.let {

                pictureDetail.setImageBitmap(appImageSaved)
                pictureDetail.transitionName = intent.getStringExtra(ARG_NAME_TRANSITION_PICTURE)

                val makeAndModel = StringBuilder()
                makeAndModel.append(it.make)
                makeAndModel.append(" ")
                makeAndModel.append(it.model)
                detailMakeAndModel.text = makeAndModel.toString()
                detailMakeAndModel.transitionName = intent.getStringExtra(ARG_NAME_TRANSITION_MAKE)

                detailYear.text = it.year.toString()
                detailYear.transitionName = intent.getStringExtra(ARG_NAME_TRANSITION_YEAR)

                detailOptions.text = it.equipments?.toReadableString()
                detailOptions.transitionName = intent.getStringExtra(ARG_NAME_TRANSITION_OPTIONS)
            }
        }

        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onSharedElementStart(
                sharedElementNames: List<String?>,
                sharedElements: List<View>,
                sharedElementSnapshots: List<View?>?
            ) {
                for (i in sharedElementNames.indices) {
                    val name = sharedElementNames[i]
                    if (intent.hasExtra(name)) {
                        sharedElements[i].setTag(
                            R.id.tag_transition_extra_properties,
                            intent.getBundleExtra(name)
                        )
                    }
                }
            }

            override fun onSharedElementEnd(
                sharedElementNames: List<String?>?,
                sharedElements: List<View>,
                sharedElementSnapshots: List<View?>?
            ) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)

                //clear all tags - without tags transition will use view's custom state
                for (view in sharedElements) {
                    view.setTag(R.id.tag_transition_extra_properties, null)
                }
            }
        })

    }
}