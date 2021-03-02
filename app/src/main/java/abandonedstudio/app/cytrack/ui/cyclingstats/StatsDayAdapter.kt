package abandonedstudio.app.cytrack.ui.cyclingstats

import abandonedstudio.app.cytrack.R
import abandonedstudio.app.cytrack.databinding.CyclingRideInDayItemBinding
import abandonedstudio.app.cytrack.model.CyclingRide
import abandonedstudio.app.cytrack.utils.ConvertersUI
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class StatsDayAdapter : RecyclerView.Adapter<StatsDayAdapter.StatsDayViewHolder>() {

    private var ridesInDayList = mutableListOf<CyclingRide>()
    private lateinit var listener: OnDeleteItemListener

    inner class StatsDayViewHolder(val binding: CyclingRideInDayItemBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(list: MutableList<CyclingRide>){
        ridesInDayList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatsDayAdapter.StatsDayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CyclingRideInDayItemBinding.inflate(inflater)
        return StatsDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatsDayAdapter.StatsDayViewHolder, position: Int) {
        val ride = ridesInDayList[holder.adapterPosition]
        holder.binding.avgSpeedRideItemTextView.text = ConvertersUI.formatAvgSpeed((ride.distance/ride.duration)*60)
        holder.binding.durationRideItemTextView.text = ConvertersUI.formatTimeFromMinutes(ride.duration)
        holder.binding.distanceRideItemTextView.text = ConvertersUI.formatDistance(ride.distance)
        holder.binding.destinationRideItemTextView.text = ride.destination
        Glide.with(holder.itemView.context).load(ride.img).into(holder.binding.cyclingRideImageView)

        holder.binding.deleteRideImageButton.setOnClickListener {
            MaterialAlertDialogBuilder(holder.itemView.context)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete)
                .setNeutralButton(R.string.cancel){dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.ok){ _, _ ->
                    listener.delete(ride)
                    ridesInDayList.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                    notifyItemRangeChanged(holder.adapterPosition, ridesInDayList.size)
                }.show()
        }
    }

    override fun getItemCount(): Int {
        return ridesInDayList.size
    }

//    passing CyclingRide to CyclingStatsFragment and deleting it from there (viewModel)
    interface OnDeleteItemListener{
        fun delete(ride: CyclingRide)
    }

    fun setOnDeleteItemListener(listener: OnDeleteItemListener){
        this.listener = listener
    }

}