package com.heathkev.quizado.ui.signin

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.heathkev.quizado.R
import com.heathkev.quizado.databinding.DialogSignOutBinding

class SignOutDialogFragment : AppCompatDialogFragment() {

    private lateinit var binding: DialogSignOutBinding

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
}