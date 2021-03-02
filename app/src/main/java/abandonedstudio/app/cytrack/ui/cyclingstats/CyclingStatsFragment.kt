package abandonedstudio.app.cytrack.ui.cyclingstats

import abandonedstudio.app.cytrack.databinding.CyclingStatsFragmentBinding
import abandonedstudio.app.cytrack.model.CyclingRide
import abandonedstudio.app.cytrack.utils.ConvertersUI
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class CyclingStatsFragment: Fragment(), StatsDayAdapter.OnDeleteItemListener {

    private val viewModel: CyclingStatsViewModel by viewModels()
    private var _binding: CyclingStatsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var yearAdapter: StatsYearAdapter
    private lateinit var dayAdapter: StatsDayAdapter

    private var yearsList = mutableListOf<Int>()
    private var totalTimeList = mutableListOf<Int>()
    private var totalDistanceList = mutableListOf<Float>()
    private var destinationsList = mutableListOf<String>()
    private var activeDaysListInYear = mutableListOf<Int>()

    private var allActiveDays = mutableListOf<String>()
    private var currentIndex = 0

    private var calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CyclingStatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupYearsRV()
        setupDaysRV()
//        loading rides from today
        setupListForDaysRV(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())

        viewModel.avgSpeed.observe(viewLifecycleOwner, {
            it?.let {
                binding.avgSpeedTextView.text = ConvertersUI.formatAvgSpeed(it)
            }
        })

        viewModel.totalRideDistance.observe(viewLifecycleOwner, {
            it?.let {
                binding.totalDistanceTextView.text = ConvertersUI.formatDistance(it)
            }
        })

        viewModel.totalTime.observe(viewLifecycleOwner, {
            it?.let {
                binding.totalTimeTextView.text = ConvertersUI.formatTimeFromMinutes(it)
            }
        })

        viewModel.mostFrequentDestinations.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()){
                    binding.mostFrequentDestinationTextView.text = it.first()
                }
            }
        })

//        list of dates of active days - used to jump from day to day in date picker
        viewModel.activeDays.observe(viewLifecycleOwner, {
            allActiveDays = it.toMutableList()
            allActiveDays.sort()
            if (!allActiveDays.isNullOrEmpty()){
                currentIndex = allActiveDays.lastIndex

            }
        })

        binding.nextActiveDayImageButton.setOnClickListener {
            if (!allActiveDays.isNullOrEmpty()){
                if (currentIndex==allActiveDays.lastIndex){
                    Toast.makeText(requireContext(), "That's latest active day", Toast.LENGTH_SHORT).show()
                } else{
                    currentIndex +=1
                }
                val date = allActiveDays[currentIndex].split("-")
                binding.cyclingStatsDatePicker.updateDate(date[0].toInt(), date[1].toInt()-1, date[2].toInt())
            }
        }

        binding.prevActiveDayImageButton.setOnClickListener {
            if (!allActiveDays.isNullOrEmpty()){
                if (currentIndex==0){
                    Toast.makeText(requireContext(), "That's first ever active day", Toast.LENGTH_SHORT).show()
                } else{
                    currentIndex -=1
                }
                val date = allActiveDays[currentIndex].split("-")
                binding.cyclingStatsDatePicker.updateDate(date[0].toInt(), date[1].toInt()-1, date[2].toInt())
            }
        }

//        updating years RV in case of changes in db
        viewModel.rows.observe(viewLifecycleOwner, {
            setupListsForYearsRV()
            yearAdapter.submitLists(
                yearsList,
                totalTimeList,
                totalDistanceList,
                destinationsList,
                activeDaysListInYear
            )
        })

        dayAdapter.setOnDeleteItemListener(this)

        val dateSetListener = DatePicker.OnDateChangedListener { _, year, month, day ->
            val localDate = LocalDate.parse(
                "$year/${String.format("%02d", month+1)}/${String.format("%02d", day)}",
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
            )
            val milliseconds = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            setupListForDaysRV(milliseconds)
        }

        binding.cyclingStatsDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), dateSetListener)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupYearsRV() = binding.yearSummaryRecyclerView.apply {
        setupListsForYearsRV()
        yearAdapter = StatsYearAdapter()
        adapter = yearAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupListsForYearsRV(){
        totalTimeList.clear()
        totalDistanceList.clear()
        destinationsList.clear()
        activeDaysListInYear.clear()
        yearsList = viewModel.getDistinctYears().toMutableList()
        yearsList.sortDescending()
        for (year in yearsList){
            totalTimeList.add(viewModel.getTotalTimeInYear(year))
            totalDistanceList.add(viewModel.getTotalDistanceInYear(year))
            destinationsList.add(viewModel.getMostFrequentDestinationInYear(year))
            activeDaysListInYear.add(viewModel.getActiveDaysInYear(year))
        }
    }

    private fun setupDaysRV() = binding.ridesInDayRecyclerView.apply{
        dayAdapter = StatsDayAdapter()
        adapter = dayAdapter
        layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }

    private fun setupListForDaysRV(milliseconds: Long){
        val list = viewModel.getAllRidesInDay(milliseconds).toMutableList()
        if (list.isNullOrEmpty()){
            binding.noRidesIndicatorTextView.visibility = View.VISIBLE
        } else {
            binding.noRidesIndicatorTextView.visibility = View.INVISIBLE
        }
        dayAdapter.submitList(list)
    }


    override fun delete(ride: CyclingRide) {
        viewModel.delete(ride)
    }
}