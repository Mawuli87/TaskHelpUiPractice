package com.example.taskhelper.ui.dailogs

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.taskhelper.R
import com.example.taskhelper.data.entities.ContactEntities
import com.example.taskhelper.data.entities.TaskEntities
import com.example.taskhelper.databinding.FragmentCustomDeleteDailogBinding
import com.example.taskhelper.ui.baseui.BaseDialogFragment
import com.example.taskhelper.ui.home.HomeScreen
import com.example.taskhelper.utils.startNewActivity
import com.example.taskhelper.viewmodels.ContactViewMode
import com.example.taskhelper.viewmodels.TaskViewModel


class CustomDeleteDialog(itemDeletedID: Int , selectedDialog:Int)  : BaseDialogFragment() , View.OnClickListener{

    private var _binding: FragmentCustomDeleteDailogBinding? = null
    private val binding get() = _binding!!
    private var itemDeletedID: Int? = null
    private var selectedDialog:Int = 0
    private var contactEntities = ContactEntities()
    private var taskEntities = TaskEntities()
  //  private var checkListEntity = CheckListEntity()
    private lateinit var taskViewModel: TaskViewModel
   // private lateinit var chListViewModel: ChListViewModel
    private lateinit var contactViewMode: ContactViewMode

    init {
        this.itemDeletedID = itemDeletedID
        this.selectedDialog = selectedDialog
    }
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentCustomDeleteDailogBinding.inflate(inflater, container, false)
        dialog!!.window!!.setWindowAnimations(R.style.AnimationDialog)
        taskViewModel = ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(TaskViewModel::class.java)

//        chListViewModel = ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
//            .get(ChListViewModel::class.java)

        contactViewMode = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(ContactViewMode::class.java)
        binding.btnExitDeleteDialog.setOnClickListener(this)
        binding.btnSetDeleteDialog.setOnClickListener(this)
        setDeleteDialogText(selectedDialog)
        binding.btnExitDeleteDialog.setOnClickListener(this)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setDeleteDialogText(selectText:Int){
        when(selectText){
            1 ->{
                binding.deleteDialogText.text = "Are You Sure You Want To Delete This Task OR Undo Deleted It .."
            }
            2 ->{
                binding.deleteDialogText.text = "Note that this action will delete all the SubLists that the list contains.If you think this to-do list is over.Let's delete it to devote time to other tasks. Let's get it over with and get some rest."
            }
            3 ->{
                binding.deleteDialogText.text = "Are You Sure You Want To Delete This Friend OR Undo Deleted It .."

            }
        }

    }
    private fun setDeleteDialogFun(itemID:Int , dialogID:Int){
        taskEntities.taskId = itemID
        contactEntities.contactId = itemID
       // checkListEntity.checkListId = itemID
        when(dialogID){
            1 ->{
                taskViewModel.deleteSelectedTask(taskEntities)
                requireActivity().startNewActivity(HomeScreen::class.java , 1)
            }
            2 ->{
                //chListViewModel.deleteCurrentChLIst(checkListEntity)
                requireActivity().startNewActivity(HomeScreen::class.java , 2)
            }
            3 ->{
                contactViewMode.deleteCurrentContact(contactEntities)
                requireActivity().startNewActivity(HomeScreen::class.java , 3)

            }
        }

    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        sheetContainer.layoutParams.width = width
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sheetContainer.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btn_ExitDeleteDialog -> {
               dialog!!.dismiss()
            }
            R.id.btn_SetDeleteDialog -> {
                setDeleteDialogFun(itemDeletedID!!, selectedDialog)
            }
        }
    }
}