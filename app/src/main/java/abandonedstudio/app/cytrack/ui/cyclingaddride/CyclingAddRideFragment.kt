package abandonedstudio.app.cytrack.ui.cyclingaddride

import abandonedstudio.app.cytrack.R
import abandonedstudio.app.cytrack.databinding.CyclingAddRideFragmentBinding
import abandonedstudio.app.cytrack.model.CyclingRide
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CyclingAddRideFragment : Fragment() {

    private val viewModel: CyclingAddRideViewModel by viewModels()

    private var _binding: CyclingAddRideFragmentBinding? = null
    private val binding get() = _binding!!

    private var distance = 0.0f
    private var duration = 0
    private var rideDate = System.currentTimeMillis()
    private var destination = ""


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

        //set autoCompleteEditText adapter and array for adapter as query from database
        viewModel.distinctDestinations.observe(viewLifecycleOwner, {
            (binding.cyclingAddDestinationEditText.editText as? AutoCompleteTextView)?.setAdapter(ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, it))
        })

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
            rideDate = it
            binding.cyclingAddCurrentDateTextView.text = dateFormatting(rideDate)
        }

//        set current date (today) to dateTextView
        binding.cyclingAddCurrentDateTextView.text = dateFormatting(rideDate)

//        set dateTextView action OnClick - open MaterialDatePicker and allow user to choose date
        binding.cyclingAddCurrentDateTextView.setOnClickListener {
            datePicker.show(parentFragmentManager, "")
        }

        binding.cyclingAddButton.setOnClickListener {
            when {
                binding.cyclingAddDistanceEditText.editText?.text.isNullOrEmpty() -> {
                    binding.cyclingAddDistanceEditText.error = "Required"
                }
                binding.cyclingAddDestinationEditText.editText?.text.isNullOrEmpty() -> {
                    binding.cyclingAddDestinationEditText.error = "Required"
                }
//                preventing to add ride with ride time 00:00 because it's impossible and will affect avg speed value
                binding.cyclingAddHoursNumberPicker.value==0 && binding.cyclingAddMinsNumberPicker.value==0 -> {
                    Toast.makeText(requireContext(), "Choose correct ride time", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    distance = binding.cyclingAddDistanceEditText.editText?.text.toString().toFloat()
                    destination = binding.cyclingAddDestinationEditText.editText?.text.toString()
                    duration = binding.cyclingAddMinsNumberPicker.value + binding.cyclingAddHoursNumberPicker.value*60
                    binding.cyclingAddDistanceEditText.error = null
                    binding.cyclingAddDistanceEditText.error = null
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(resources.getString(R.string.cycling_add_dialog_title))
                        .setMessage("Date: ${dateFormatting(rideDate)},\nDistance: $distance km,\nDuration: ${duration/60}:${String.format("%02d",duration%60)}," +
                                "\nDestination: $destination ?")
                        .setNeutralButton(resources.getString(R.string.cancel)){ dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                            viewModel.insert(CyclingRide(distance, duration, rideDate, destination))
                            Toast.makeText(
                                requireContext(),
                                "Saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
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

    private fun dateFormatting(millis: Long): String{
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(millis)
    }
}