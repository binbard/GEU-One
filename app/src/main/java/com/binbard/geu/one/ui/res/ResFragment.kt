package com.binbard.geu.one.ui.res

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.MenuCompat
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.DialogAddResourceBinding
import com.binbard.geu.one.databinding.DialogFeedbackBinding
import com.binbard.geu.one.databinding.FragmentResBinding
import com.binbard.geu.one.databinding.ItemResCardBinding
import com.binbard.geu.one.helpers.NetUtils
import com.binbard.geu.one.helpers.PdfUtils
import com.binbard.geu.one.ui.erp.ErpCacheHelper
import com.binbard.geu.one.ui.erp.ErpViewModel
import com.binbard.geu.one.ui.erp.menu.Student
import com.binbard.geu.one.ui.notes.NotesFragment
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*


class ResFragment : Fragment() {
    private lateinit var binding: FragmentResBinding
    private lateinit var rvm: ResViewModel
    private lateinit var evm: ErpViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResBinding.inflate(inflater, container, false)

        rvm = ViewModelProvider(this)[ResViewModel::class.java]

        evm = ViewModelProvider(this)[ErpViewModel::class.java]
        evm.erpCacheHelper = evm.erpCacheHelper ?: ErpCacheHelper(requireContext())

        setHasOptionsMenu(true)

        rvm.resRepository = rvm.resRepository ?: ResRepository(requireContext())

        rvm.resList.observe(viewLifecycleOwner) {
            if (it == null) {
                rvm.resRepository?.fetchResources(rvm)
                return@observe
            }

            if (evm.studentData.value == null) evm.erpCacheHelper?.loadLocalStudentData(evm)

            binding.llResBox.removeAllViews()
            for (resSection in it) {
                val resTitle = resSection.title
                val resObjList = resSection.content

                if (resTitle == "Events") {
                    if (resObjList.isEmpty()) continue
                    val vpRes = ViewPager2(requireContext())
                    val params = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        220
                    )
                    vpRes.layoutParams = params
                    val events = mutableListOf<Pair<String, String>>()
                    for (resObj in resObjList) {
                        events.add(Pair(resObj.imgUrl ?: "", resObj.url))
                    }
                    vpRes.visibility = View.VISIBLE
                    vpRes.adapter = CarouselAdapter(requireContext(), events)
                    CoroutineScope(Dispatchers.Main).launch {
                        while (isActive) {
                            delay(8000)
                            vpRes.currentItem = (vpRes.currentItem + 1) % events.size
                        }
                    }
                    binding.llResBox.addView(vpRes)
                    continue
                }

                val filteredList =
                    resObjList.filter { shouldShowResObj(evm.studentData.value, it.onlyFor) }

                val resCardBinding = ItemResCardBinding.inflate(inflater, container, false)
                resCardBinding.tvResCardTitle.text = resTitle
                resCardBinding.rvResCardBody.layoutManager = GridLayoutManager(context, 3)

                resCardBinding.rvResCardBody.adapter = GridAdapter(filteredList)

                binding.llResBox.addView(resCardBinding.root)

            }
        }

        return binding.root
    }

    private fun shouldShowResObj(student: Student?, onlyFor: String?): Boolean {
        if (onlyFor == null) return true
        if (student == null) return false
        val course = student.course
        val specialization = student.courseSpecialization
        val yearSem = student.yearSem

        val iCourse = onlyFor.substringBefore('#').substringBefore('@')
        var iSpecialization =
            if (onlyFor.contains('@')) onlyFor.substringAfter('@').substringBefore('#') else null
        var iYearSem = if (onlyFor.contains('#')) onlyFor.substringAfter('#') else null

        if (iSpecialization == "") iSpecialization = null
        if (iYearSem == "") iYearSem = null

        if (iCourse != course) return false
        if (iSpecialization != null && iSpecialization != specialization) return false
        if (iYearSem != null && iYearSem != yearSem) return false
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_res_top, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_res_add -> {
                val dialogAddResourceBinding =
                    DialogAddResourceBinding.inflate(layoutInflater, null, false)

                val dsp = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val sigName = dsp.getString("signature", "Anonymous")

                dialogAddResourceBinding.etResAuthorName.setText(sigName)

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Add Resource")
                    .setMessage("You can suggest a resource here. This will be sent for review.")
                    .setView(dialogAddResourceBinding.root)
                    .setNegativeButton("Cancel") { dialog, which ->
                        // Negative btn pressed
                    }
                    .setPositiveButton("Send Email") { dialog, which ->
                        if (dialogAddResourceBinding.etResTitle.text.isEmpty()) return@setPositiveButton

                        val email = resources.getString(R.string.support_email)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            setPackage("com.google.android.gm")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                "Notes: ${dialogAddResourceBinding.etResTitle.text}"
                            )
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Have a look at this:\n\n" +
                                        "Resource: ${dialogAddResourceBinding.etResUrl.text}\n\n" +
                                        "Can you please review these resources and add it to the app?\n\n" +
                                        "Regards: ${dialogAddResourceBinding.etResAuthorName.text}"
                            )
                        }

                        if (intent.resolveActivity(requireActivity().packageManager) != null) {
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Email app is not installed",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                    .show()
                true
            }

            else -> false
        }
    }

}