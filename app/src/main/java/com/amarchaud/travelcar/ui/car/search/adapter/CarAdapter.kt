package com.amarchaud.travelcar.ui.car.search.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
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
import com.amarchaud.travelcar.R
import com.amarchaud.travelcar.databinding.ItemSearchCarBinding
import com.amarchaud.travelcar.databinding.ItemSearchErrorBinding
import com.amarchaud.travelcar.databinding.ItemSearchLoaderBinding
import com.amarchaud.travelcar.databinding.ItemSearchNothingBinding
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.ui.car.search.model.CarListItem
import com.amarchaud.travelcar.utils.extensions.toReadableString
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
    ListAdapter<CarListItem, CarAdapter.ViewHolder>(CarListItem.DIFF_CALLBACK) {

    private enum class Type {
        LOADING,
        NOTHING,
        ITEM,
        ERROR
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CarListItem.Loading -> Type.LOADING.ordinal
            is CarListItem.Nothing -> Type.NOTHING.ordinal
            is CarListItem.Car -> Type.ITEM.ordinal
            is CarListItem.Error -> Type.ERROR.ordinal
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Type.LOADING.ordinal -> ViewHolder.Loading(ItemSearchLoaderBinding.inflate(layoutInflater, parent, false))
            Type.NOTHING.ordinal -> ViewHolder.Nothing(ItemSearchNothingBinding.inflate(layoutInflater, parent, false))
            Type.ITEM.ordinal -> ViewHolder.Car(ItemSearchCarBinding.inflate(layoutInflater, parent, false))
            Type.ERROR.ordinal -> ViewHolder.Error(ItemSearchErrorBinding.inflate(layoutInflater, parent, false))
            else -> throw Exception("unknown viewType : $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payload: List<Any>) {
        val item = getItem(position)
        if (payload.isEmpty() || payload[0] !is Boolean) {
            item?.let { bind(holder, item) }
        } else {
            item?.let { update(holder, item, position) }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList())
    }

    private fun bind(holder: ViewHolder, item: CarListItem) {
        when (holder) {
            is ViewHolder.Loading -> {
                //nothing
            }
            is ViewHolder.Error -> holder.bind(item as CarListItem.Error)
            is ViewHolder.Car -> holder.bind(item as CarListItem.Car, listener)
        }
    }

    private fun update(holder: ViewHolder, item: CarListItem, position: Int) {
        when (holder) {
            is ViewHolder.Car -> {
                (item as? CarListItem.Car)?.let {
                    holder.update(it)
                }

            }
            else -> {
                // nothing
            }
        }
    }


    sealed class ViewHolder(viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {

        class Loading(binding: ItemSearchLoaderBinding) : ViewHolder(binding)

        class Nothing(binding: ItemSearchNothingBinding) : ViewHolder(binding)

        class Car(private val binding: ItemSearchCarBinding) : ViewHolder(binding) {

            /**
             * Hightlight all occurence of a string, no matter uppercase
             */
            private fun TextView.highLightText(text: String, filter: String?) {
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

                this.makeAndModel.highLightText(makeAndModel.toString(), item.filter)
                this.year.highLightText(item.appCar.year.toString(), item.filter)

                if (item.appCar.equipments == null) {
                    this.options.text = binding.root.context.getString(R.string.search_no_options)
                } else {
                    this.options.highLightText(
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
                                R.string.search_transition_name_picture,
                                layoutPosition
                            )
                        )
                    val p2: androidx.core.util.Pair<View, String> =
                        androidx.core.util.Pair(
                            makeAndModel,
                            binding.root.context.getString(
                                R.string.search_transition_make_and_model,
                                layoutPosition
                            )
                        )
                    val p3: androidx.core.util.Pair<View, String> =
                        androidx.core.util.Pair(
                            year,
                            binding.root.context.getString(
                                R.string.search_transition_year,
                                layoutPosition
                            )
                        )
                    val p4: androidx.core.util.Pair<View, String> =
                        androidx.core.util.Pair(
                            options,
                            binding.root.context.getString(
                                R.string.search_transition_options,
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


        class Error(private val binding: ItemSearchErrorBinding) : ViewHolder(binding) {
            fun bind(item: CarListItem.Error) = with(binding) {
                this.error.text = item.error
            }
        }
    }


}