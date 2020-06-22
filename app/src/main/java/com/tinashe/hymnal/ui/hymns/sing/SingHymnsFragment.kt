package com.tinashe.hymnal.ui.hymns.sing

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentSingBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.ui.AppBarBehaviour
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingHymnsFragment : Fragment() {

    private val viewModel: SingHymnsViewModel by viewModels()

    private var pagerAdapter: SingFragmentsAdapter? = null
    private var binding: FragmentSingBinding? = null

    private var appBarBehaviour: AppBarBehaviour? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appBarBehaviour = context as? AppBarBehaviour
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.hymn_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentSingBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val number = arguments?.getInt(getString(R.string.argument_selected_hymn)) ?: 1

        viewModel.statusLiveData.observeNonNull(viewLifecycleOwner) {
        }
        viewModel.hymnalTitleLiveData.observeNonNull(viewLifecycleOwner) {
            appBarBehaviour?.setAppBarTitle(it)
        }
        viewModel.hymnListLiveData.observeNonNull(viewLifecycleOwner) {
            pagerAdapter = SingFragmentsAdapter(this, it)
            binding?.viewPager?.apply {
                adapter = pagerAdapter
                setCurrentItem(number - 1, false)
            }
        }

        binding?.apply {
            numberPadView.setOnNumSelectedCallback {
                hideNumPad()
                viewPager.setCurrentItem(it.minus(1), false)
            }

            fabNumber.setOnClickListener {
                numberPadView.onShown()
                fabNumber.isExpanded = true
                scrimOverLay.isVisible = true
            }
            scrimOverLay.setOnTouchListener { _, _ ->
                if (fabNumber.isExpanded) {
                    hideNumPad()
                }
                true
            }
        }
    }

    private fun hideNumPad() {
        binding?.apply {
            scrimOverLay.isVisible = false
            fabNumber.isExpanded = false
        }
    }

    fun didHandleBackPress(): Boolean {
        return binding?.let {
            if (it.fabNumber.isExpanded) {
                hideNumPad()
                true
            } else {
                false
            }
        } ?: false
    }
}
