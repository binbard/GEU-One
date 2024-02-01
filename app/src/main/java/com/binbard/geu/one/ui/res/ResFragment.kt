package com.binbard.geu.one.ui.res

import android.os.Bundle
import android.view.*
import com.google.android.material.button.MaterialButton
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.MenuCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentResBinding
import com.binbard.geu.one.databinding.ItemResCardBinding
import com.binbard.geu.one.helpers.PdfUtils

class ResFragment : Fragment() {
    private lateinit var binding: FragmentResBinding
    private lateinit var rvm: ResViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResBinding.inflate(inflater, container, false)

        rvm = ViewModelProvider(this).get(ResViewModel::class.java)

        setHasOptionsMenu(true)

        val intent = CustomTabsIntent.Builder().build()

        rvm.resRepository = rvm.resRepository ?: ResRepository(requireContext())

        rvm.resList.observe(viewLifecycleOwner) {
            if (it == null) {
                rvm.resRepository?.fetchResources(rvm)
                return@observe
            }

            binding.root.removeAllViews()
            for (resSection in it) {
                val resTitle = resSection.title
                val resObjList = resSection.content

                val resCardBinding = ItemResCardBinding.inflate(inflater, container, false)
                resCardBinding.tvResCardTitle.text = resTitle

                resCardBinding.flResCardBody.removeAllViews()

                for (resObj in resObjList) {
                    val button = MaterialButton(requireContext())
                    button.text = resObj.name
                    val params = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(10, 10, 10, 10)
                    button.layoutParams = params

                    if (resObj.type == "pdf") {
                        button.setOnClickListener {
                            PdfUtils.openOrDownloadPdf(requireContext(), resObj.url, isExternalSource =  true)
                        }
                    } else if (resObj.type == "link") {
                        button.setOnClickListener {
                            intent.launchUrl(it.context, resObj.url.toUri())
                        }
                    }

                    resCardBinding.flResCardBody.addView(button)
                }
                binding.root.addView(resCardBinding.root)
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_res_top, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_res_clearfiles -> {
                PdfUtils.clearAllFiles(requireContext())
                Toast.makeText(requireActivity(), "Cleared Files", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

}