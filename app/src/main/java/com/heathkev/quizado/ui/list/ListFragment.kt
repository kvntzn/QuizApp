package com.heathkev.quizado.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.chip.Chip
import com.heathkev.quizado.R
import com.heathkev.quizado.databinding.FragmentListBinding

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {

    companion object {
        const val DEFAULT_CATEGORY = "All"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list, container, false
        )

        val viewModel = ViewModelProvider(this).get(QuizListViewModel::class.java)
        binding.quizListViewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = QuizListAdapter(QuizListAdapter.OnClickListener {
            viewModel.displayQuizListModelDetails(it)
        })

        val listView = binding.listView
        listView.setHasFixedSize(true)
        listView.adapter = adapter

        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        val listProgress = binding.listProgress

        viewModel.quizListModelData.observe(viewLifecycleOwner, Observer {
            it?.let {
                listView.startAnimation(fadeInAnimation)
                listProgress.startAnimation(fadeOutAnimation)

                adapter.submitList(it)
            }
        })

        viewModel.categoryList.observe(viewLifecycleOwner, object: Observer<List<String>> {
            override fun onChanged(data: List<String>?) {
                data ?: return
                val chipGroup = binding.catergoryList
                val inflator = LayoutInflater.from(chipGroup.context)

                val children = data.map { categoryName ->
                    val chip = inflator.inflate(R.layout.categories, chipGroup, false) as Chip
                    chip.text = categoryName
                    chip.tag = categoryName

                    // checked the default category
                    chip.isChecked = categoryName == DEFAULT_CATEGORY

                    chip.setOnCheckedChangeListener { button, isChecked ->
                        viewModel.onFilterChanged(button.tag as String, isChecked)
                    }
                    chip
                }

                chipGroup.removeAllViews()

                for (chip in children) {
                    chipGroup.addView(chip)
                }
            }
        })

        viewModel.category.observe(viewLifecycleOwner, Observer {
            val chips = binding.catergoryList.children
            val ids= chips.map { chip -> chip.id }
            for (id in ids) {
                val chip: Chip = binding.catergoryList.findViewById(id)
                chip.isChecked = chip.tag == it
            }
        })

        viewModel.navigateToSelectedQuizListModelPosition.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(
                    ListFragmentDirections.actionListFragmentToDetailFragment(
                        it
                    )
                )
                viewModel.displayQuizListModelDetailsComplete()
            }
        })

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }
}
