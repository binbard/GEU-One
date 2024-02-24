package com.binbard.geu.one.ui.erp.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentErpExamBinding
import com.binbard.geu.one.databinding.FragmentErpFeesBinding
import com.binbard.geu.one.databinding.FragmentErpMidtermMarksBinding
import com.binbard.geu.one.models.ExamMarks
import com.binbard.geu.one.models.FeeDetails
import com.binbard.geu.one.models.FeeHead
import com.binbard.geu.one.models.MidtermMarks
import com.binbard.geu.one.ui.erp.ErpViewModel
import com.binbard.geu.one.ui.erp.menu.Helper.createFeeInfoRow
import java.text.DecimalFormat
import kotlin.math.roundToInt

class ErpFeesFragment : Fragment() {
    private lateinit var binding: FragmentErpFeesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpFeesBinding.inflate(inflater, container, false)

        val evm: ErpViewModel = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        if (evm.studentData.value != null) {
            val mList = listOf("Course Fee", "Hostel Fee")
            val spAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mList)
            spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spFees.adapter = spAdapter
            binding.spFees.setSelection(0)
        }

        binding.spFees.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                evm.erpRepository?.fetchFeeDetails(evm, position + 1)
                binding.tblFeeInfo.removeAllViews()
                binding.tblFeeDetail.removeAllViews()
                binding.tvNoDataFees.visibility = View.GONE
                binding.pbFees.visibility = View.VISIBLE
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        evm.feeDetails.observe(viewLifecycleOwner) {
            binding.pbFees.visibility = View.GONE
            if (it == null) {
                binding.tvNoDataFees.visibility = View.VISIBLE
                return@observe
            }
            binding.tvNoDataFees.visibility = View.GONE
            val headData = if(binding.spFees.selectedItemPosition==0) it.headdata else it.headdatahostel

            binding.tblFeeInfo.addView(createFeeInfoRow(requireContext(), "Total Due (₹):", it.totaldue))
            binding.tblFeeInfo.addView(createFeeInfoRow(requireContext(), "Scholarship (₹):", it.adjust))
            binding.tblFeeInfo.addView(createFeeInfoRow(requireContext(), "Paid Amount (₹):", it.totalreceive))
            binding.tblFeeInfo.addView(createFeeInfoRow(requireContext(), "Excess Amount (₹):", it.excessfee))
            binding.tblFeeInfo.addView(createFeeInfoRow(requireContext(), "Balance Amount (₹):", it.totalbalance))


            binding.tblFeeDetail.addView(Helper.getRowDividerBlack(requireContext()))

            val header = FeeHead(
                "Sem/Year",
                "Fee Head",
                "Due Amount",
                "Scholarship",
                "Paid Amount",
                "Balance"
            )
            val headerRow = Helper.createFeeDetailsRow(requireContext(), 0, header)
            binding.tblFeeDetail.addView(headerRow)
            binding.tblFeeDetail.addView(Helper.getRowDividerBlack(requireContext()))

            for (i in headData.indices) {
                val row = Helper.createFeeDetailsRow(requireContext(), i + 1, headData[i])
                binding.tblFeeDetail.addView(row)
                binding.tblFeeDetail.addView(Helper.getRowDividerBlack(requireContext()))
            }

        }

        return binding.root
    }
}