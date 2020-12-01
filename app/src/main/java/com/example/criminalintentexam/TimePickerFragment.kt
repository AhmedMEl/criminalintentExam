package com.example.criminalintentexam

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*



private const val ARG_TIME = "time"
class TimePickerFragment: DialogFragment() {
    interface Callbacks {
        fun onTimeSelected(time: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val timeListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hours: Int, minutes: Int ->
                calendar.get(Calendar.HOUR_OF_DAY)
                calendar.get(Calendar.MINUTE)
                val resultTime = calendar.time
                targetFragment?.let { fragment ->
                    (fragment as TimePickerFragment.Callbacks).onTimeSelected(resultTime)
                }
            }

        val time = arguments?.getSerializable(ARG_TIME) as Date
        calendar.time = time
        val initialHour = calendar.get(Calendar.HOUR)
        val initialMinute = calendar.get(Calendar.MINUTE)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)
        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinute,
            true
        )
    }
    companion object {
        fun newInstance(time: Date): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TIME, time)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }
}