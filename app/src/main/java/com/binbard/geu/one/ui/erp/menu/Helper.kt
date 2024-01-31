package com.binbard.geu.one.ui.erp.menu

import android.R
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.size
import com.binbard.geu.one.models.ExamMarks
import com.binbard.geu.one.models.MidtermMarks
import com.binbard.geu.one.models.SubjectAttendance
import com.binbard.geu.one.ui.notes.PdfUtils
import java.text.SimpleDateFormat
import java.util.*

object Helper {
    fun convertToHumanDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getColDivider(context: Context): View {
        val divider = View(context)
        divider.layoutParams = TableRow.LayoutParams(5, TableRow.LayoutParams.MATCH_PARENT)
        divider.setBackgroundResource(com.google.android.material.R.color.material_divider_color)
        return divider
    }

    fun getColDividerBlack(context: Context): View {
        val divider = View(context)
        divider.layoutParams = TableRow.LayoutParams(5, TableRow.LayoutParams.MATCH_PARENT)
        divider.setBackgroundResource(R.color.black)
        return divider
    }

    fun getRowDivider(context: Context): View {
        val divider = View(context)
        divider.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 5)
        divider.setBackgroundResource(com.google.android.material.R.color.material_divider_color)
        return divider
    }

    fun getRowDividerBlack(context: Context): View {
        val divider = View(context)
        divider.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 5)
        divider.setBackgroundResource(R.color.black)
        return divider
    }

    fun createAttendanceRow(
        context: Context,
        count: Int,
        attendance: SubjectAttendance?
    ): TableRow {
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
        if (count != 0) subject.setTypeface(null, android.graphics.Typeface.BOLD)
        subjectCode.setPadding(6, 0, 6, 0)
        percentage.setPadding(6, 0, 6, 0)
        if (count != 0) percentage.setTypeface(null, android.graphics.Typeface.BOLD)
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
            if (subject.text.endsWith("(L)")) subject.setTextColor(context.resources.getColor(com.google.android.material.R.color.m3_ref_palette_dynamic_neutral50))
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

    fun createMidtermMarksRow(context: Context, count: Int, midtermMarks: MidtermMarks): TableRow {
        val row = TableRow(context)

        if (count % 2 == 0) row.setBackgroundResource(com.google.android.material.R.color.material_divider_color)

        val subject = midtermMarks.subject
        val subjectSplit = subject.split("/")
        var (subjectCode, subjectName) = Pair("", "")
        if (subjectSplit.size != 2) {
            subjectCode = ""
            subjectName = subjectSplit[0]
        } else {
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

        val tvSubjectName = TextView(context)
        tvSubjectName.textSize = 18f
        tvSubjectName.text = subjectName
        tvSubjectName.tooltipText = subjectName
        tvSubjectName.setPadding(10, 10, 10, 10)
        tvSubjectName.gravity = View.TEXT_ALIGNMENT_CENTER
        tvSubjectName.maxWidth = 420
        tvSubjectName.maxLines = 1
        tvSubjectName.ellipsize = TextUtils.TruncateAt.END

        val tvMarks = TextView(context)
        tvMarks.textSize = 18f
        tvMarks.text = marks
        tvMarks.setPadding(10, 10, 10, 10)
        tvMarks.gravity = View.TEXT_ALIGNMENT_CENTER

        val tvMaxMarks = TextView(context)
        tvMaxMarks.textSize = 18f
        tvMaxMarks.text = maxMarks
        tvMaxMarks.setPadding(10, 10, 10, 10)
        tvMaxMarks.gravity = View.TEXT_ALIGNMENT_CENTER

        if (count == 0) {
            tvSubjectCode.setTypeface(null, android.graphics.Typeface.BOLD)
            tvSubjectName.setTypeface(null, android.graphics.Typeface.BOLD)
            tvMarks.setTypeface(null, android.graphics.Typeface.BOLD)
            tvMaxMarks.setTypeface(null, android.graphics.Typeface.BOLD)
        }

        row.addView(tvSubjectCode)
        row.addView(getColDividerBlack(context))
        row.addView(tvSubjectName)
        row.addView(getColDividerBlack(context))
        row.addView(tvMarks)
        row.addView(getColDividerBlack(context))
        row.addView(tvMaxMarks)

        row.isFocusable = true
        row.isClickable = true

        return row
    }

    fun createExamMarksRow(
        context: Context,
        count: Int,
        examMarks: ExamMarks,
        erpHostUrl: String,
        regID: String,
        pRollNo: String,
        cookies: String
    ): TableRow {
        val row = TableRow(context)

        if (count % 2 == 0) row.setBackgroundResource(com.google.android.material.R.color.material_divider_color)

        val yearSem = examMarks.yearSem
        val sgpa = examMarks.sgpa
        val totalBack = examMarks.totalBack
        val result = examMarks.result
        val marks = examMarks.marks
        val totalSubject = examMarks.totalSubject

        val tvYearSem = TextView(context)
        tvYearSem.textSize = 18f
        tvYearSem.text = yearSem
        tvYearSem.setPadding(10, 10, 10, 10)
        tvYearSem.gravity = View.TEXT_ALIGNMENT_CENTER

        val tvSgpa = TextView(context)
        tvSgpa.textSize = 18f
        tvSgpa.text = sgpa
        tvSgpa.setPadding(10, 10, 10, 10)
        tvSgpa.gravity = View.TEXT_ALIGNMENT_CENTER

        val tvTotalBack = TextView(context)
        tvTotalBack.textSize = 18f
        tvTotalBack.text = totalBack
        tvTotalBack.setPadding(10, 10, 10, 10)
        tvTotalBack.gravity = View.TEXT_ALIGNMENT_CENTER

        val tvResult = TextView(context)
        tvResult.textSize = 18f
        tvResult.text = result
        tvResult.setPadding(10, 10, 10, 10)
        tvResult.gravity = View.TEXT_ALIGNMENT_CENTER

        val tvTotalSubject = TextView(context)
        tvTotalSubject.textSize = 18f
        tvTotalSubject.text = totalSubject
        tvTotalSubject.setPadding(10, 10, 10, 10)
        tvTotalSubject.gravity = View.TEXT_ALIGNMENT_CENTER

        val urlMarksheet = "${erpHostUrl}Web_StudentAcademic/FillMarksheet"
        val tvMarksheetView = getMarkSheetLinkSpan(context, count, urlMarksheet, yearSem, regID, cookies, "View Detail", "$pRollNo-$yearSem-MS")
        if (count == 0) tvMarksheetView.text = "Marksheet View"

        val urlGrade = "${erpHostUrl}Web_StudentAcademic/FillMarksheet_ChoiceBase"
        val tvGradeView = getMarkSheetLinkSpan(context, count, urlGrade, yearSem, regID, cookies, "View ($yearSem)", "$pRollNo-$yearSem")
        if (count == 0) tvGradeView.text = "Grade View"

        val tvMarks = TextView(context)
        tvMarks.textSize = 18f
        tvMarks.text = marks
        tvMarks.setPadding(10, 10, 10, 10)
        tvMarks.gravity = View.TEXT_ALIGNMENT_CENTER

        if (count == 0) {
            tvYearSem.setTypeface(null, android.graphics.Typeface.BOLD)
            tvSgpa.setTypeface(null, android.graphics.Typeface.BOLD)
            tvSgpa.setPadding(10, 0, 60, 0)
            tvTotalBack.setTypeface(null, android.graphics.Typeface.BOLD)
            tvResult.setTypeface(null, android.graphics.Typeface.BOLD)
            tvMarks.setTypeface(null, android.graphics.Typeface.BOLD)
            tvTotalSubject.setTypeface(null, android.graphics.Typeface.BOLD)
            tvMarksheetView.setTypeface(null, android.graphics.Typeface.BOLD)
            tvGradeView.setTypeface(null, android.graphics.Typeface.BOLD)
        }

        row.addView(tvYearSem)
        row.addView(getColDivider(context))
        row.addView(tvSgpa)
        row.addView(getColDivider(context))
        row.addView(tvTotalBack)
        row.addView(getColDivider(context))
        row.addView(tvResult)
        row.addView(getColDivider(context))
        row.addView(tvTotalSubject)
        row.addView(getColDivider(context))
        row.addView(tvMarks)
        row.addView(getColDivider(context))
        row.addView(tvMarksheetView)
        row.addView(getColDivider(context))
        row.addView(tvGradeView)
        row.addView(getColDivider(context))

        return row
    }

    private fun getMarkSheetLinkSpan(
        context: Context,
        count: Int,
        link: String,
        yearSem: String,
        regID: String,
        cookies: String,
        linkTxt: String,
        saveName: String,
    ): TextView {
        val tvMarksheetView = TextView(context)
        tvMarksheetView.textSize = 18f
        tvMarksheetView.setPadding(10, 10, 10, 10)
        tvMarksheetView.gravity = View.TEXT_ALIGNMENT_CENTER
        if (count != 0) {
            val payload = mapOf("YearSem" to yearSem, "Regid" to regID)
            val spannableString = SpannableString(linkTxt)
            val clickSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {}
            }
            spannableString.setSpan(clickSpan, 0, linkTxt.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvMarksheetView.text = spannableString
            tvMarksheetView.setOnClickListener {
                PdfUtils.openFollowDownloadPdf(context, link, payload, cookies, saveName)
            }
        }
        return tvMarksheetView
    }
}