package abandonedstudio.app.cytrack.ui.cyclingstats

import abandonedstudio.app.cytrack.databinding.CyclingStatsYearItemBinding
import abandonedstudio.app.cytrack.utils.ConvertersUI
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StatsYearAdapter : RecyclerView.Adapter<StatsYearAdapter.StatsYearViewHolder>() {

    private var yearsList = mutableListOf<Int>()
    private var totalTimeList = mutableListOf<Int>()
    private var totalDistanceList = mutableListOf<Float>()
    private var destinationsList = mutableListOf<String>()
    private var activeDaysList = mutableListOf<Int>()

    inner class StatsYearViewHolder(val binding: CyclingStatsYearItemBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitLists(yearsList: MutableList<Int>, totalTimeList: MutableList<Int>,
                    totalDistanceList: MutableList<Float>, destinationsList: MutableList<String>, activeDaysList: MutableList<Int>){
        this.yearsList = yearsList
        this.totalTimeList = totalTimeList
        this.totalDistanceList = totalDistanceList
        this.destinationsList = destinationsList
        this.activeDaysList = activeDaysList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsYearViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CyclingStatsYearItemBinding.inflate(inflater)
        return  StatsYearViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatsYearViewHolder, position: Int) {
        val year = yearsList[position]
        val totalTime = totalTimeList[position]
        val totalDistance = totalDistanceList[position]
        val avgSpeed = (totalDistance/totalTime)*60
        val destination = destinationsList[position]
        val activeDays = activeDaysList[position]

        holder.itemView.apply {
            holder.binding.yearTextView.text = "$year"
            holder.binding.avgSpeedCyclingYearItemTextView.text = ConvertersUI.formatAvgSpeed(avgSpeed)
            holder.binding.durationCyclingYearItemTextView.text = ConvertersUI.formatTimeFromMinutes(totalTime)
            holder.binding.distanceCyclingYearItemTextView.text = ConvertersUI.formatDistance(totalDistance)
            holder.binding.activeDaysCyclingItemTextView.text = "$activeDays"
            holder.binding.mostFreqDestCyclingYearItemTextView.text = destination
        }
    }

    override fun getItemCount(): Int {
        return yearsList.size
    }

}