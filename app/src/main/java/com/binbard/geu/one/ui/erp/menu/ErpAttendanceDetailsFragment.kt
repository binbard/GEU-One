package com.binbard.geu.one.ui.erp.menu

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentErpAttendanceDetailsBinding
import com.binbard.geu.one.ui.erp.ErpViewModel
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

        val subjectCode = arguments?.getString("SubjectCode")
        val subject = arguments?.getString("Subject")
        val employee = arguments?.getString("Employee")
        val totalLecture = arguments?.getString("TotalLecture")
        val totalPresent = arguments?.getString("TotalPresent")
        val percentage = arguments?.getString("Percentage")

        val employeeName = employee?.replace("\\s+".toRegex(), " ")?.split(" ")?.joinToString(" ") {
            it.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
        }

        binding.tvAttendance.text = getSpannableAttendanceTitle(
            subject,
            subjectCode,
            percentage,
            totalPresent,
            totalLecture,
            employeeName
        )

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)

        if (evm.subjectAttendanceData.value == null) {
            binding.calendarView.visibility = View.GONE
            evm.erpRepository?.fetchSubjectAttendance(
                evm,
                subjectID!!,
                periodAssignID!!,
                ttid!!,
                lectureTypeID!!,
                sdf.format(dateFrom),
                sdf.format(dateTo)
            )
        }

        evm.subjectAttendanceData.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            binding.progressBar.visibility = View.GONE
            binding.calendarView.visibility = View.VISIBLE
            val presentAbsent = it.presentAbsent
            val presentDays = presentAbsent.filter { it.present == "P" }.map { it.date }
            val absentDays = presentAbsent.filter { it.present == "A" }.map { it.date }

            Log.d("PresentDays", presentDays.toString())
            Log.d("AbsentDays", absentDays.toString())

            binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, data: CalendarDay) {
                    if (data.position != DayPosition.MonthDate) {
                        container.textView.text = ""
                        container.textView.setBackgroundColor(Color.TRANSPARENT)
                        return
                    }
                    container.textView.text = data.date.dayOfMonth.toString()
                    val mDate =
                        Date.from(data.date.atStartOfDay(ZoneId.systemDefault()).toInstant())

                    if (mDate in presentDays) {
                        val period = presentAbsent.find { it.date == mDate }?.period
                        container.textView.tooltipText = "Present\nPeriod - $period"
                        container.textView.setTextColor(Color.WHITE)
                        container.textView.setBackgroundResource(R.drawable.rounded_corner_present)
                    } else if (mDate in absentDays) {
                        val period = presentAbsent.find { it.date == mDate }?.period
                        container.textView.tooltipText = "Absent\nPeriod - $period"
                        container.textView.setTextColor(Color.WHITE)
                        container.textView.setBackgroundResource(R.drawable.rounded_corner_absent)
                    } else {
                        container.textView.setBackgroundColor(Color.TRANSPARENT)
                    }
                }
            }
//            binding.calendarView.notifyCalendarChanged()
        }

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                if (data.position != DayPosition.MonthDate) {
                    container.textView.text = ""
                    container.textView.setBackgroundColor(Color.TRANSPARENT)
                    return
                }
                container.textView.text = data.date.dayOfMonth.toString()
                container.textView.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        binding.calendarView.monthScrollListener = {
            val txt1 = it.yearMonth.month.getDisplayName(
                TextStyle.FULL,
                Locale.ENGLISH
            ) + " " + it.yearMonth.year.toString()
            binding.tvMonthYear.text = txt1
        }

        binding.tvPrevBtn.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }
        binding.tvNextBtn.setOnClickListener {
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

    private fun getSpannableAttendanceTitle(
        subject: String?,
        subjectCode: String?,
        percentage: String?,
        totalPresent: String?,
        totalLecture: String?,
        employeeName: String?
    ): SpannableString {
        val subjectSpan = SpannableString("$subject \n")
        val ap = if(totalPresent.toString().length > 5) "" else "($totalPresent/$totalLecture)"
        val attendanceSpan =
            SpannableString("Attendance: $percentage% $ap \n")
        val facultySpan = SpannableString("Faculty: $employeeName")

        subjectSpan.setSpan(
            android.text.style.RelativeSizeSpan(1.2f),
            0,
            subjectSpan.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        subjectSpan.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            0,
            subjectSpan.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        attendanceSpan.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            attendanceSpan.indexOf(":") + 2,
            attendanceSpan.indexOf("%") + 1,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        attendanceSpan.setSpan(
            android.text.style.UnderlineSpan(),
            attendanceSpan.indexOf(":") + 2,
            attendanceSpan.indexOf("%"),
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        facultySpan.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.ITALIC),
            facultySpan.indexOf(":") + 2,
            facultySpan.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return SpannableString(TextUtils.concat(subjectSpan, attendanceSpan, facultySpan))
    }


}