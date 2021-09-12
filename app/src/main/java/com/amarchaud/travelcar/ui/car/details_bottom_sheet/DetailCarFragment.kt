package com.amarchaud.travelcar.ui.car.details_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amarchaud.travelcar.R
import com.amarchaud.travelcar.databinding.BottomSheetCarBinding
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.utils.setBottomPadding
import com.amarchaud.travelcar.utils.setTopBottomInsets
import com.amarchaud.travelcar.utils.toReadableString
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DetailCarFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    companion object {
        private const val ARG_CAR = "arg_car"

        fun newInstance(appCar: AppCar): DetailCarFragment {
            val fragment = DetailCarFragment()

            val args = Bundle()
            args.putParcelable(ARG_CAR, appCar)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: BottomSheetCarBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCarBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            root.setTopBottomInsets { _, bottom ->
                options.setBottomPadding(bottom)
            }

            requireArguments().getParcelable<AppCar>(ARG_CAR)?.let {
                Glide.with(binding.root)
                    .load(it.picture)
                    .placeholder(
                        R.drawable.ic_search
                    ).into(picture)

                this.year.text = it.year.toString()
                this.title.text = getString(R.string.double_string_with_space, it.make, it.model)
                this.options.text =
                    it.equipments?.toReadableString() ?: binding.root.context.getString(R.string.search_no_options)

            }


            cross.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}