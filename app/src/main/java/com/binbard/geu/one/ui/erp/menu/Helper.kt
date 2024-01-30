package com.binbard.geu.one.ui.erp.menu

import android.R
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.size
import com.binbard.geu.one.models.MidtermMarks
import com.binbard.geu.one.models.SubjectAttendance
import java.text.SimpleDateFormat
import java.util.*

object Helper {
    fun convertToHumanDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun getColDivider(context: Context): View {
        val divider = View(context)
        divider.layoutParams = TableRow.LayoutParams(1, TableRow.LayoutParams.MATCH_PARENT)
        divider.setBackgroundResource(com.google.android.material.R.color.material_divider_color)
        return divider
    }

    fun createAttendanceRow(context: Context, count: Int, attendance: SubjectAttendance?): TableRow {
        val row = TableRow(context)
        val counter = TextView(context)
        val subject = TextView(context)
        val percentage = TextView(context)
        val present = TextView(context)
        val subjectCode = TextView(context)
        val employee = TextView(context)

        row.setPadding(6, 6, 6, 6)
        row.gravity = android.view.Gravity.CENTER_VERTICAL

        row.isClickable = true
        row.isFocusable = true

        val attrs = intArrayOf(android.R.attr.selectableItemBackground)
        val typedArray = context.obtainStyledAttributes(attrs)
        val backgroundResource = typedArray.getResourceId(0, 0)
        row.setBackgroundResource(backgroundResource)
        typedArray.recycle()

        subject.maxWidth = 400
        subject.setPadding(6, 0, 6, 0)
        if(count!=0) subject.setTypeface(null, android.graphics.Typeface.BOLD)
        subjectCode.setPadding(6, 0, 6, 0)
        percentage.setPadding(6, 0, 6, 0)
        if(count!=0) percentage.setTypeface(null, android.graphics.Typeface.BOLD)
        present.setPadding(6, 0, 6, 0)
        employee.setPadding(6, 0, 6, 0)

        counter.textSize = 17f
        subject.textSize = 17f
        percentage.textSize = 17f
        present.textSize = 17f
        subjectCode.textSize = 17f
        employee.textSize = 17f

        if (attendance == null) {
            counter.text = "#"
            subject.text = "Subject"
            percentage.text = "Percentage"
            present.text = "Present"
            subjectCode.text = "Subject Code"
            employee.text = "Faculty"

            for (i in 0 until row.size) {
                val view = row.getChildAt(i)
                if (view is TextView) view.setTypeface(null, android.graphics.Typeface.BOLD)
            }
        } else {
            counter.text = "$count."
            subject.text = attendance.subject
            percentage.text = attendance.percentage
            present.text = "${attendance.totalPresent}/${attendance.totalLecture}"
            subjectCode.text = attendance.subjectCode
            employee.text = attendance.employee

            counter.setTextColor(context.resources.getColor(com.google.android.material.R.color.m3_dynamic_dark_highlighted_text))
            if(subject.text.endsWith("(L)")) subject.setTextColor(context.resources.getColor(com.google.android.material.R.color.m3_ref_palette_dynamic_neutral50))
            else subject.setTextColor(context.resources.getColor(com.google.android.material.R.color.m3_ref_palette_dynamic_neutral60))
            percentage.setTextColor(context.resources.getColor(com.google.android.material.R.color.m3_dynamic_dark_highlighted_text))
        }

        row.addView(counter)
        row.addView(getColDivider(context))
        row.addView(subject)
        row.addView(getColDivider(context))
        row.addView(subjectCode)
        row.addView(getColDivider(context))
        row.addView(percentage)
        row.addView(getColDivider(context))
        row.addView(present)
        row.addView(getColDivider(context))
        row.addView(employee)

        return row
    }

    fun createMidtermMarksRow(context: Context, count: Int, midtermMarks: MidtermMarks): TableRow{
        val row = TableRow(context)

        if(count%2==0) row.setBackgroundResource(com.google.android.material.R.color.material_divider_color)

        val subject = midtermMarks.subject
        val subjectSplit = subject.split("/")
        var (subjectCode, subjectName) = Pair("", "")
        if(subjectSplit.size!=2) {
            subjectCode = ""
            subjectName = subjectSplit[0]
        } else{
            subjectCode = subjectSplit[0]
            subjectName = subjectSplit[1]
        }

        val marks = midtermMarks.marks
        val maxMarks = midtermMarks.maxMarks

        val tvSubjectCode = TextView(context)
        tvSubjectCode.textSize = 18f
        tvSubjectCode.text = subjectCode
        tvSubjectCode.setPadding(10, 10, 10, 10)
        tvSubjectCode.gravity = View.TEXT_ALIGNMENT_CENTER

        val colSeparator0 = View(context)
        colSeparator0.layoutParams = TableRow.LayoutParams(4, TableRow.LayoutParams.MATCH_PARENT)
        colSeparator0.setBackgroundResource(R.color.black)

        val tvSubjectName = TextView(context)
        tvSubjectName.textSize = 18f
        tvSubjectName.text = subjectName
        tvSubjectName.tooltipText = subjectName
        tvSubjectName.setPadding(10, 10, 10, 10)
        tvSubjectName.gravity = View.TEXT_ALIGNMENT_CENTER
        tvSubjectName.maxWidth = 420
        tvSubjectName.maxLines = 1
        tvSubjectName.ellipsize = TextUtils.TruncateAt.END

        val colSeparator1 = View(context)
        colSeparator1.layoutParams = TableRow.LayoutParams(4, TableRow.LayoutParams.MATCH_PARENT)
        colSeparator1.setBackgroundResource(R.color.black)

        val tvMarks = TextView(context)
        tvMarks.textSize = 18f
        tvMarks.text = marks
        tvMarks.setPadding(10, 10, 10, 10)
        tvMarks.gravity = View.TEXT_ALIGNMENT_CENTER

        val colSeparator2 = View(context)
        colSeparator2.layoutParams = TableRow.LayoutParams(4, TableRow.LayoutParams.MATCH_PARENT)
        colSeparator2.setBackgroundResource(R.color.black)

        val tvMaxMarks = TextView(context)
        tvMaxMarks.textSize = 18f
        tvMaxMarks.text = maxMarks
        tvMaxMarks.setPadding(10, 10, 10, 10)
        tvMaxMarks.gravity = View.TEXT_ALIGNMENT_CENTER

        if(count==0){
            tvSubjectCode.setTypeface(null, android.graphics.Typeface.BOLD)
            tvSubjectName.setTypeface(null, android.graphics.Typeface.BOLD)
            tvMarks.setTypeface(null, android.graphics.Typeface.BOLD)
            tvMaxMarks.setTypeface(null, android.graphics.Typeface.BOLD)
        }

        row.addView(tvSubjectCode)
        row.addView(colSeparator0)
        row.addView(tvSubjectName)
        row.addView(colSeparator1)
        row.addView(tvMarks)
        row.addView(colSeparator2)
        row.addView(tvMaxMarks)

        return row
    }
}