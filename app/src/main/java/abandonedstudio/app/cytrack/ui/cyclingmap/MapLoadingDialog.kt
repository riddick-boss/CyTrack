package abandonedstudio.app.cytrack.ui.cyclingmap

import abandonedstudio.app.cytrack.R
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MapLoadingDialog {
    companion object{
        @SuppressLint("InflateParams")
        fun createDialog(context: Context): Dialog{
            val view = View.inflate(
                context,
                R.layout.map_loading_progress_dialog,
                null
            ) as View
            return MaterialAlertDialogBuilder(context)
                .setView(view)
                .setCancelable(false)
                .create()
        }
    }
}