package com.example.telegraf.ui.fragments


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.example.telegraf.MainActivity
import com.example.telegraf.R
import com.example.telegraf.activities.RegisterActivity
import com.example.telegraf.databinding.FragmentSettingsBinding
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AUTH
import com.example.telegraf.utilities.FOLDER_PROFILE_IMAGE
import com.example.telegraf.utilities.REF_STORAGE_ROOT
import com.example.telegraf.utilities.UID
import com.example.telegraf.utilities.USER
import com.example.telegraf.utilities.replaceActivity
import com.example.telegraf.utilities.replaceFragment
import com.example.telegraf.utilities.showToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null;
    private val binding get() = _binding!!

    private lateinit var cropImageLauncher: ActivityResultLauncher<Any?>;
    private val cropImageContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(600, 600)
                .setCropShape(CropImageView.CropShape.OVAL)
                .getIntent(APP_ACTIVITY)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri;
        }
    }

    override fun onCreate(bundle: Bundle?){
        super.onCreate(bundle);
        cropImageLauncher = registerForActivityResult(cropImageContract){uri: Uri? ->
            uri?.let{
                val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(UID)
                path.putFile(uri).addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        showToast(resources.getString(R.string.toast_data_update))
                    }
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false);
        val view = binding.root;
        return view;
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        addMenu();
        initFields();
        setListeners();

    }

    private fun initFields() {
        binding.settingsFullname.text = USER.fullname
        binding.settingsPhoneNumber.text = USER.phone;
        binding.settingsBio.text = USER.bio;
        binding.settingsStatus.text = USER.status;
        binding.settingsUsername.text = USER.username

    }

    private fun setListeners() {
        binding.settingsBtnChangeUsername.setOnClickListener {
            replaceFragment(ChangeUsernameFragment())
        }
        binding.settingsBtnChangeBio.setOnClickListener {
            replaceFragment(ChangeBioFragment());
        }
        binding.settingsChangePhoto.setOnClickListener {
            changePhotoUser();
        }
    }

    private fun changePhotoUser() {
        cropImageLauncher.launch(null);
    }



    private fun addMenu() {
        val menuHost = requireActivity();
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                    inflater.inflate(R.menu.settings_action_menu, menu);
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.settings_menu_exit -> {
                            AUTH.signOut();
                            (activity as MainActivity).replaceActivity(RegisterActivity())
                        }

                        R.id.settings_menu_change_name -> replaceFragment(ChangeNameFragment());
                    }
                    return true;
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}