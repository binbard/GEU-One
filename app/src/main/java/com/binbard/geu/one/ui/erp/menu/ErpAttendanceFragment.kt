package com.binbard.geu.one.ui.erp.menu

import android.os.Bundle
import android.text.SpannableString
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import android.widget.TableRow
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentErpAttendanceBinding
import com.binbard.geu.one.ui.erp.ErpViewModel
import java.util.*


class ErpAttendanceFragment : Fragment() {
    private lateinit var binding: FragmentErpAttendanceBinding
    private lateinit var evm: ErpViewModel
    private var selectedRow = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpAttendanceBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        if (!evm.isCacheEnabled) {
//            Toast.makeText(requireContext(), "Cache Disabled", Toast.LENGTH_SHORT).show()
            evm.attendanceData.value = null
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView4, ErpAttendanceListFragment())
            .addToBackStack(null)
            .commit()


        return binding.root
    }

}