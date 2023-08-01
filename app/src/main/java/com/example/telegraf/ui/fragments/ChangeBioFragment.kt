package com.example.telegraf.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentBaseBinding
import com.example.telegraf.databinding.FragmentChangeBioBinding
import com.example.telegraf.utilities.CHILD_BIO
import com.example.telegraf.utilities.NODE_USERNAMES
import com.example.telegraf.utilities.NODE_USERS
import com.example.telegraf.utilities.REF_DATABASE_ROOT
import com.example.telegraf.utilities.UID
import com.example.telegraf.utilities.USER
import com.example.telegraf.utilities.showToast


class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    private var _binding: FragmentChangeBioBinding? = null;
    private val binding get() = _binding!!;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeBioBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }

    override fun onResume() {
        super.onResume()
        binding.settingsInputBio.setText(USER.bio);
    }


    override fun change() {
        val newBio = binding.settingsInputBio.text.toString();
        REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_BIO).setValue(newBio)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(this.resources.getString(R.string.toast_data_update));
                    USER.bio = newBio;
                    this.parentFragmentManager.popBackStack();
                }
            }
    }
}