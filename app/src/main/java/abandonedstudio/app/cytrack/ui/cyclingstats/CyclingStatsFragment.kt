package abandonedstudio.app.cytrack.ui.cyclingstats

import abandonedstudio.app.cytrack.databinding.CyclingStatsFragmentBinding
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CyclingStatsFragment: Fragment() {

    private val viewModel: CyclingStatsViewModel by viewModels()
    private var _binding: CyclingStatsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CyclingStatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.avgSpeed.observe(viewLifecycleOwner, {
            it?.let {
                binding.avgSpeedTextView.text = "${String.format("%.1f", it)} km/h"
            }
        })

        viewModel.totalRideDistance.observe(viewLifecycleOwner, {
            it?.let {
                binding.totalDistanceTextView.text = "$it km"
            }
        })

        viewModel.totalTime.observe(viewLifecycleOwner, {
            it?.let {
                binding.totalTimeTextView.text = "${it / 60}:${String.format("%02d", it % 60)}"
            }
        })

        viewModel.mostFrequentDestinations.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()){
                    binding.mostFrequentDestinationTextView.text = it.first()
                }
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}