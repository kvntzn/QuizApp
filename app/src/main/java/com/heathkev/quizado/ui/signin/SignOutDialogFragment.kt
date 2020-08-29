package com.heathkev.quizado.ui.signin

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizado.R
import com.heathkev.quizado.databinding.DialogSignOutBinding
import com.heathkev.quizado.ui.LoginActivity
import com.heathkev.quizado.utils.executeAfter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignOutDialogFragment : AppCompatDialogFragment() {

    private lateinit var binding: DialogSignOutBinding

    private val signOutViewModel: SignOutViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // We want to create a dialog, but we also want to use DataBinding for the content view.
        // We can do that by making an empty dialog and adding the content later.
        return MaterialAlertDialogBuilder(requireContext()).create()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // In case we are showing as a dialog, use getLayoutInflater() instead.
        binding = DialogSignOutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        signOutViewModel.onSignOutAction.observe(viewLifecycleOwner, Observer {
            activity?.run {
                finish()
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
            }

            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(context, LoginActivity::class.java))
        })

        binding.executeAfter {
            viewModel = signOutViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        if (showsDialog) {
            (requireDialog() as AlertDialog).setView(binding.root)
        }
    }
}