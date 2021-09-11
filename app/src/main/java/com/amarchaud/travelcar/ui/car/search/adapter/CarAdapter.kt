package com.amarchaud.travelcar.ui.car.search.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.amarchaud.travelcar.databinding.ItemCarBinding
import com.amarchaud.travelcar.databinding.ItemErrorBinding
import com.amarchaud.travelcar.databinding.ItemLoaderBinding
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.ui.car.search.model.CarListItem
import com.amarchaud.travelcar.utils.toReadableString
import android.graphics.drawable.BitmapDrawable
import com.amarchaud.travelcar.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


interface CarListener {
    fun onCarClicked(
        appCar: AppCar,
        position: Int,
        carImage: Bitmap,
        vararg p: androidx.core.util.Pair<View, String>
    )
}

class CarAdapter(private val listener: CarListener?) :
    ListAdapter<CarListItem, CarAdapter.CarViewHolder>(CarListItem.DIFF_CALLBACK) {

    enum class Type(val v: Int) {
        LOADING(0),
        ITEM(1),
        ERROR(2)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CarListItem.Loading -> Type.LOADING.v
            is CarListItem.Car -> Type.ITEM.v
            is CarListItem.Error -> Type.ERROR.v
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Type.LOADING.v -> {
                CarViewHolder.Loading(
                    ItemLoaderBinding.inflate(layoutInflater, parent, false)
                )
            }
            Type.ITEM.v -> {
                CarViewHolder.Car(
                    ItemCarBinding.inflate(layoutInflater, parent, false)
                )
            }
            Type.ERROR.v -> {
                CarViewHolder.Error(
                    ItemErrorBinding.inflate(layoutInflater, parent, false)
                )
            }
            else -> throw Exception("unknown viewType : $viewType")
        }
    }

    override fun onBindViewHolder(
        holder: CarViewHolder,
        position: Int,
        payload: List<Any>
    ) {
        val item = getItem(position)
        if (payload.isEmpty() || payload[0] !is Boolean) {
            item?.let { bind(holder, item) }
        } else {
            item?.let { update(holder, item, position) }
        }
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList())
    }

    private fun bind(holder: CarViewHolder, item: CarListItem) {
        when (holder) {
            is CarViewHolder.Loading -> {
                //nothing
            }
            is CarViewHolder.Error -> {
                val error = (item as CarListItem.Error).error
                holder.bind(error)
            }
            is CarViewHolder.Car -> {
                (item as? CarListItem.Car)?.let {
                    holder.bind(it, listener)
                }
            }
        }
    }

    private fun update(holder: CarViewHolder, item: CarListItem, position: Int) {
        when (holder) {
            is CarViewHolder.Car -> {
                (item as? CarListItem.Car)?.let {
                    holder.update(it)
                }

            }
            else -> {
                // nothing
            }
        }
    }


    sealed class CarViewHolder(viewBinding: ViewBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        class Loading(binding: ItemLoaderBinding) : CarViewHolder(binding)

        class Car(private val binding: ItemCarBinding) : CarViewHolder(binding) {

            /**
             * Hightlight all occurence of a string, no matter uppercase
             */
            private fun TextView.hightLightText(text: String, filter: String?) {
                filter?.let {
                    if (text.contains(filter, true)) {

                        val spannableStr = SpannableString(text)
                        var start = text.indexOf(filter, 0, true)
                        while (start >= 0) {

                            val spanStart = start
                            val spanEnd = start + filter.length

                            spannableStr.setSpan(
                                BackgroundColorSpan(Color.YELLOW),
                                spanStart,
                                spanEnd,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )

                            start = text.indexOf(filter, spanEnd)
                        }

                        setText(spannableStr)

                    } else {
                        setText(text)
                    }
                } ?: setText(text)
            }

            private fun highLight(item: CarListItem.Car) = with(binding) {
                val makeAndModel = StringBuilder()
                makeAndModel.append(item.appCar.make)
                makeAndModel.append(" ")
                makeAndModel.append(item.appCar.model)

                this.makeAndModel.hightLightText(makeAndModel.toString(), item.filter)
                this.year.hightLightText(item.appCar.year.toString(), item.filter)

                if (item.appCar.equipments == null) {
                    this.options.text = binding.root.context.getString(R.string.no_options)
                } else {
                    this.options.hightLightText(
                        item.appCar.equipments.toReadableString(),
                        item.filter
                    )
                }
            }

            fun update(item: CarListItem.Car) {
                highLight(item)
            }

            fun bind(item: CarListItem.Car, listener: CarListener?) = with(binding) {

                root.setOnClickListener {

                    val p1: androidx.core.util.Pair<View, String> =
                        androidx.core.util.Pair(
                            picture,
                            binding.root.context.getString(
                                R.string.transition_name_picture,
                                layoutPosition
                            )
                        )
                    val p2: androidx.core.util.Pair<View, String> =
                        androidx.core.util.Pair(
                            makeAndModel,
                            binding.root.context.getString(
                                R.string.transition_make_and_model,
                                layoutPosition
                            )
                        )
                    val p3: androidx.core.util.Pair<View, String> =
                        androidx.core.util.Pair(
                            year,
                            binding.root.context.getString(
                                R.string.transition_year,
                                layoutPosition
                            )
                        )
                    val p4: androidx.core.util.Pair<View, String> =
                        androidx.core.util.Pair(
                            options,
                            binding.root.context.getString(
                                R.string.transition_options,
                                layoutPosition
                            )
                        )

                    val drawable = picture.drawable as BitmapDrawable
                    listener?.onCarClicked(
                        item.appCar,
                        layoutPosition,
                        drawable.bitmap,
                        p1,
                        p2,
                        p3,
                        p4
                    )
                }

                Glide.with(binding.root)
                    .load(item.appCar.picture)
                    .placeholder(
                        R.drawable.ic_search
                    )
                    .apply(RequestOptions().override(400, 400))
                    .into(picture)

                highLight(item)
            }
        }


        class Error(private val binding: ItemErrorBinding) : CarViewHolder(binding) {
            fun bind(error: String) = with(binding) {
                this.error.text = error
            }
        }
    }


}