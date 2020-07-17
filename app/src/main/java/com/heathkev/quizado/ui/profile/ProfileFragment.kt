package com.heathkev.quizado.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizado.R
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        TODO DataBinding
        val currentUser  = FirebaseAuth.getInstance().currentUser

        val userPhotoUrl = currentUser?.photoUrl?.buildUpon()?.scheme("https")?.build()
        Glide.with(profile_photo.context)
            .load(userPhotoUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder_image)
            )
            .into(profile_photo)
    }
}