package abandonedstudio.app.cytrack.ui.cyclingaddride

import abandonedstudio.app.cytrack.R
import abandonedstudio.app.cytrack.databinding.CyclingAddRideFragmentBinding
import abandonedstudio.app.cytrack.model.CyclingRideRepository
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CyclingAddRideFragment : Fragment(R.layout.cycling_add_ride_fragment) {

    private val viewModel: CyclingAddRideViewModel by viewModels()
//    private lateinit var binding: CyclingAddRideFragmentBinding
//    private val binding by viewBinding (CyclingAddRideFragmentBinding::bind)
    private var _binding: CyclingAddRideFragmentBinding? = null
    private val binding get() = _binding!!

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        viewModel = ViewModelProvider(this).get(CyclingAddRideViewModel::class.java)
//        return inflater.inflate(R.layout.cycling_add_ride_fragment, container, false)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CyclingAddRideFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set min and max values for number pickers
        setNumberPickers()

        //set autoCompleteEditText array for adapter and adapter
        // TODO: set adapter array as query from database
        val items = listOf("ccc", "ggg", "ddd")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, items)
        (binding.cyclingAddDestinationEditText.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        // Creating the MaterialDatePicker builder, setting the title, applying validator (past dates;today> and setting actions OnCLick
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .also { it.setTitleText(resources.getString(R.string.cycling_add_ride_date_picker_title)) }
            .setCalendarConstraints(
                CalendarConstraints.Builder().setValidator(
                    CompositeDateValidator.allOf(
                        arrayListOf<CalendarConstraints.DateValidator>(DateValidatorPointBackward.now())
                    )
                ).build()
            ).build()
        datePicker.addOnPositiveButtonClickListener {
            viewModel.rideDate = it
            binding.cyclingAddCurrentDateTextView.text = dateFormatting(viewModel.rideDate)
        }

        //set current date (today) to dateTextView
        binding.cyclingAddCurrentDateTextView.text = dateFormatting(viewModel.rideDate)

        //set dateTextView action OnClick - open MaterialDatePicker and allow user to choose date
        binding.cyclingAddCurrentDateTextView.setOnClickListener {
            datePicker.show(parentFragmentManager, "")
        }

//        binding = CyclingAddRideFragmentBinding.bind(view)
        binding.cyclingAddButton.setOnClickListener {
            when {
                binding.cyclingAddDistanceEditText.editText?.text.isNullOrEmpty() -> {
                    binding.cyclingAddDistanceEditText.error = "Required"
                }
                binding.cyclingAddDestinationEditText.editText?.text.isNullOrEmpty() -> {
                    binding.cyclingAddDistanceEditText.error = "Required"
                }
                else -> {
                    viewModel.distance = binding.cyclingAddDistanceEditText.editText?.text.toString().toFloat()
                    viewModel.destination = binding.cyclingAddDestinationEditText.editText?.text.toString()
                    viewModel.duration = binding.cyclingAddMinsNumberPicker.value + binding.cyclingAddHoursNumberPicker.value*60
                    binding.cyclingAddDistanceEditText.error = null
                    binding.cyclingAddDistanceEditText.error = null
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(resources.getString(R.string.cycling_add_dialog_title))
                        .setMessage("Date: ${dateFormatting(viewModel.rideDate)},\nDistance: ${viewModel.distance} km,\nDuration: ${viewModel.duration/60}:${String.format("%02d",viewModel.duration%60)},\nDestination: ${viewModel.destination} ?")
                        .setNeutralButton(resources.getString(R.string.cancel)){ dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                        // TODO: send to database
                            Toast.makeText(
                                requireContext(),
                                "Made ${viewModel.distance} km in ${viewModel.duration} mins and gone to ${viewModel.destination}",
                                Toast.LENGTH_SHORT
                            ).show()
                            // end of todo
                            setDefaultUIValues()
                        }.show()
                    view.clearFocus()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setNumberPickers(){
        binding.cyclingAddHoursNumberPicker.minValue = 0
        binding.cyclingAddHoursNumberPicker.maxValue = 1000
        binding.cyclingAddMinsNumberPicker.minValue = 0
        binding.cyclingAddMinsNumberPicker.maxValue = 59
    }

    private fun setDefaultUIValues(){
        binding.cyclingAddDistanceEditText.editText?.text?.clear()
        binding.cyclingAddDestinationEditText.editText?.text?.clear()
        binding.cyclingAddHoursNumberPicker.value = 0
        binding.cyclingAddMinsNumberPicker.value = 0
    }
    private fun dateFormatting(milis: Long): String{
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(milis)
    }
}