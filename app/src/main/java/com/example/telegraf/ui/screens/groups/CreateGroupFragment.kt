package com.example.telegraf.ui.screens.groups

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.database.createGroupToDatabase
import com.example.telegraf.databinding.FragmentCreateGroupBinding
import com.example.telegraf.models.CommonModel
import com.example.telegraf.ui.screens.BaseFragment
import com.example.telegraf.ui.screens.main_list.MainListFragment
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.getPlurals
import com.example.telegraf.utilities.hideKeyboard
import com.example.telegraf.utilities.replaceFragment
import com.example.telegraf.utilities.showToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class CreateGroupFragment(private val listContacts:    List<CommonModel>) :
    BaseFragment(R.layout.fragment_create_group) {
    private var _binding: FragmentCreateGroupBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var createGroupRecycler: RecyclerView;
    private lateinit var createGroupAdapter: AddContactAdapter;
    private var groupPhotoUri = Uri.EMPTY;

    private lateinit var groupPhotoLauncher: ActivityResultLauncher<Any?>;
    private val groupPhotoContract = object: ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(250, 250)
                .setCropShape(CropImageView.CropShape.OVAL)
                .getIntent(APP_ACTIVITY)
        }
        override fun parseResult(resultCode: Int, intent: Intent?): Uri?{
            return CropImage.getActivityResult(intent)?.uri;
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupPhotoLauncher = registerForActivityResult(groupPhotoContract){uri: Uri? ->
            groupPhotoUri = uri;
            binding.createGroupPhoto.setImageURI(groupPhotoUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateGroupBinding.inflate(layoutInflater, container, false);
        return binding.root;
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Создать группу"
        hideKeyboard();
        initRecycler()
        binding.createGroupBtnDone.setOnClickListener{
           showToast("done !!! ")

        }
        binding.createGroupName.requestFocus();
        binding.createGroupCount.text = getPlurals(listContacts.size)

        binding.createGroupPhoto.setOnClickListener{
            setGroupPhoto()
        }
        binding.createGroupBtnDone.setOnClickListener{
            val groupName = binding.createGroupName.text.toString()
            if(groupName.isEmpty()){
                showToast("Введите название группы")
            }else{
                createGroupToDatabase(groupName, groupPhotoUri, listContacts){
                    replaceFragment(MainListFragment())
                }
            }
        }
    }

    private fun setGroupPhoto() {
        groupPhotoLauncher.launch(null);
    }

    private fun initRecycler() {
        createGroupRecycler = binding.createGroupRecyclerView;
        createGroupAdapter = AddContactAdapter();
        createGroupRecycler.adapter = createGroupAdapter;
        listContacts.forEach{
            createGroupAdapter.updateAddContactList(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}