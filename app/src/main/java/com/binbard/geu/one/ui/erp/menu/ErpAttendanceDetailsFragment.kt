package com.binbard.geu.one.ui.erp.menu

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentErpAttendanceDetailsBinding
import com.binbard.geu.one.ui.erp.ErpViewModel
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*


class ErpAttendanceDetailsFragment : Fragment() {
    private lateinit var binding: FragmentErpAttendanceDetailsBinding
    private lateinit var evm: ErpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpAttendanceDetailsBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        val regID = arguments?.getString("regID")
        val subjectID = arguments?.getString("subjectID")
        val periodAssignID = arguments?.getString("periodAssignID")
        val ttid = arguments?.getString("ttid")
        val lectureTypeID = arguments?.getString("lectureTypeID")
        val dateFrom = arguments?.getSerializable("dateFrom") as Date
        val dateTo = arguments?.getSerializable("dateTo") as Date

        val subjectCode = arguments?.getString("subjectCode")
        val subject = arguments?.getString("Subject")
        val employee = arguments?.getString("Employee")
        val totalLecture = arguments?.getString("TotalLecture")
        val totalPresent = arguments?.getString("TotalPresent")
        val percentage = arguments?.getString("Percentage")

        val txt =
            "$subjectCode | $subject | Total Attendance: $percentage%\nFrom $dateFrom to $dateTo | $totalPresent/$totalLecture | $employee"

        binding.tvAttendance.text = txt


        val presentDays = listOf(3, 6, 14, 16, 19, 20)
        val absentDays = listOf(4, 7, 12, 18, 25)

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                if (data.position != DayPosition.MonthDate) {
                    container.textView.text = ""
                    container.textView.setBackgroundColor(Color.TRANSPARENT)
                    return
                }
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.date.dayOfMonth in presentDays) {
                    container.textView.setTextColor(Color.WHITE)
                    container.textView.setBackgroundResource(R.drawable.rounded_corner_present)
                } else if (data.date.dayOfMonth in absentDays) {
                    container.textView.setTextColor(Color.WHITE)
                    container.textView.setBackgroundResource(R.drawable.rounded_corner_absent)
                } else {
                    container.textView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }

//        binding.calendarView.monthScrollListener = {
//            val txt1 = it.yearMonth.month.getDisplayName(
//                TextStyle.FULL,
//                Locale.ENGLISH
//            ) + " " + it.yearMonth.year.toString()
//            binding.tvMonthYear.text = txt1
//        }

        binding.tvPrevBtn.setOnClickListener{
            binding.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }
        binding.tvNextBtn.setOnClickListener{
            binding.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        val daysOfWeek = daysOfWeek()
        binding.titlesContainer.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                val dayOfWeek = daysOfWeek[index]
                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textView.text = title
                textView.setTextColor(Color.GRAY)
            }


        val currentMonth = YearMonth.now()
        val startMonth =
            YearMonth.from(dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
        val endMonth =
            YearMonth.from(dateTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
        val firstDayOfWeek = firstDayOfWeekFromLocale()
        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)


        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            })

        return binding.root
    }

    class DayViewContainer(view: View) : ViewContainer(view) {
        val textView = view.findViewById<TextView>(R.id.calendarDayText)
    }

    class MonthViewContainer(view: View) : ViewContainer(view) {
        val titlesContainer = view as ViewGroup
    }


}