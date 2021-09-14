package com.amarchaud.travelcar.ui.car.search

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.textservice.SentenceSuggestionsInfo
import android.view.textservice.SpellCheckerSession
import android.view.textservice.SuggestionsInfo
import android.view.textservice.TextInfo
import android.view.textservice.TextServicesManager
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amarchaud.travelcar.databinding.FragmentSearchBinding
import com.amarchaud.travelcar.ui.car.detail_car.DetailCarActivity
import com.amarchaud.travelcar.ui.car.search.adapter.CarAdapter
import com.amarchaud.travelcar.ui.car.search.adapter.CarListener
import com.amarchaud.travelcar.ui.car.search.model.AppCarUiModel
import com.amarchaud.travelcar.utils.extensions.textChanges
import com.amarchaud.travelcar.utils.transition.TextColorTransition
import com.amarchaud.travelcar.utils.transition.TextSizeTransition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class SearchFragment : Fragment(), CarListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchFragmentViewModel by viewModels()

    private val adapter = CarAdapter(this)

    companion object {
        const val TAG = "SearchFragment"

        const val SAVED_SEARCH = "saved_search"

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_SEARCH, binding.searchLayoutEditText.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSearchView()

        with(binding) {
            rvData.adapter = adapter

            savedInstanceState?.let {
                Log.d(TAG, "savedInstanceState")
                binding.searchLayoutEditText.setText(it.getString(SAVED_SEARCH))
            }
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.carsUiModel.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeSearchView() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(binding) {
                    searchLayoutEditText.textChanges().collectLatest {
                        viewModel.manageSearch(it)
                    }
                }
            }
        }
    }

    override fun onCarClicked(
        appCar: AppCarUiModel,
        position: Int,
        carImage: Bitmap,
        vararg p: Pair<View, String>
    ) {

        /**
         * p0 : ImageView
         * p1 p2 p3 : TextView
         */
        val textColorTransition = TextColorTransition(requireContext())
        TransitionManager.beginDelayedTransition(binding.root, textColorTransition)

        val textSizeTransition = TextSizeTransition(requireContext())
        TransitionManager.beginDelayedTransition(binding.root, textSizeTransition)

        val bundle1 = Bundle()
        val bundle2 = Bundle()
        val bundle3 = Bundle()

        // Give initial values to both Transitions
        textColorTransition.addExtraProperties(p[1].first as TextView, bundle1)
        textColorTransition.addExtraProperties(p[2].first as TextView, bundle2)
        textColorTransition.addExtraProperties(p[3].first as TextView, bundle3)

        textSizeTransition.addExtraProperties(p[1].first as TextView, bundle1)
        textSizeTransition.addExtraProperties(p[2].first as TextView, bundle2)
        textSizeTransition.addExtraProperties(p[3].first as TextView, bundle3)

        val intent = Intent(context, DetailCarActivity::class.java).apply {
            putExtra(DetailCarActivity.ARG_CAR, appCar)
            putExtra(DetailCarActivity.ARG_CAR_IMAGE_SAVED, carImage)
            putExtra(
                DetailCarActivity.ARG_NAME_TRANSITION_PICTURE,
                p[0].second
            ) // unique name for the transition for each element
            putExtra(DetailCarActivity.ARG_NAME_TRANSITION_MAKE, p[1].second)
            putExtra(DetailCarActivity.ARG_NAME_TRANSITION_YEAR, p[2].second)
            putExtra(DetailCarActivity.ARG_NAME_TRANSITION_OPTIONS, p[3].second)

            putExtra(p[1].second, bundle1)
            putExtra(p[2].second, bundle2)
            putExtra(p[3].second, bundle3)
        }

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), *p)
        startActivity(intent, options.toBundle())
    }
}