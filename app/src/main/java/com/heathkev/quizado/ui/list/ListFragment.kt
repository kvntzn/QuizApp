package com.heathkev.quizado.ui.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.chip.Chip
import com.heathkev.quizado.R
import com.heathkev.quizado.databinding.FragmentListBinding
import com.heathkev.quizado.ui.MainActivityViewModel
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.ui.signin.setupProfileMenuItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : MainNavigationFragment() {

    companion object {
        const val DEFAULT_CATEGORY = "All"

        enum class Level {
            BEGINNER
        }
    }

    private lateinit var binding: FragmentListBinding

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: QuizListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(
            inflater, container, false
        ).apply {
            quizListViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        (activity as AppCompatActivity?)!!.apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        val adapter = QuizListAdapter(QuizListAdapter.OnClickListener {
            viewModel.displayQuizListModelDetails(it)
        })

        val listView = binding.listView
        listView.setHasFixedSize(true)
        listView.adapter = adapter

        viewModel.quizList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.categoryList.observe(viewLifecycleOwner, object : Observer<List<String>> {
            override fun onChanged(data: List<String>?) {
                data ?: return
                val chipGroup = binding.catergoryList
                val inflator = LayoutInflater.from(chipGroup.context)

                val children = data.map { categoryName ->
                    val chip = inflator.inflate(R.layout.categories, chipGroup, false) as Chip
                    // checked the default category
                    chip.isChecked = categoryName == DEFAULT_CATEGORY

                    chip.text = categoryName
                    chip.tag = categoryName

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
            val ids = chips.map { chip -> chip.id }
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

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        binding.toolbar.setupProfileMenuItem(menu, inflater, mainActivityViewModel, this)
    }
}
