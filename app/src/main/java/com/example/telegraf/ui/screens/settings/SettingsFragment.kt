package com.example.telegraf.ui.screens.settings


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
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentSettingsBinding
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.database.AUTH
import com.example.telegraf.utilities.AppState
import com.example.telegraf.database.FOLDER_PROFILE_IMAGE
import com.example.telegraf.database.REF_STORAGE_ROOT
import com.example.telegraf.database.UID
import com.example.telegraf.database.USER
import com.example.telegraf.utilities.downloadAndSetImage
import com.example.telegraf.database.getUrlFromStorage
import com.example.telegraf.database.putFileToStorage
import com.example.telegraf.database.putUrlToDatabase
import com.example.telegraf.ui.screens.BaseFragment
import com.example.telegraf.utilities.replaceFragment
import com.example.telegraf.utilities.restartActivity
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

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle);
        cropImageLauncher = registerForActivityResult(cropImageContract) { uri: Uri? ->
            uri?.let {
                val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(UID)
                putFileToStorage(uri, path) {
                    getUrlFromStorage(path) { photoUrl ->
                        putUrlToDatabase(photoUrl) {
                            USER.photoUrl = photoUrl;
                            showToast(resources.getString(R.string.toast_data_update))
                            binding.settingsUserPhoto.downloadAndSetImage(photoUrl)
                            APP_ACTIVITY.mAppDrawer.updateHeader();
                        }
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
        binding.settingsStatus.text = USER.state;
        binding.settingsUsername.text = USER.username
        binding.settingsUserPhoto.downloadAndSetImage(USER.photoUrl)

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
                            AppState.updateState(AppState.OFFLINE)
                            AUTH.signOut();
                            restartActivity()
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