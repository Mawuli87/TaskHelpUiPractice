package com.example.taskhelper.ui.task

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskhelper.ui.dailogs.CustomDeleteDialog
import com.example.taskhelper.R
import com.example.taskhelper.data.entities.ContactEntities
import com.example.taskhelper.data.entities.TaskEntities
import com.example.taskhelper.databinding.ActivityAddNewTaskBinding
import com.example.taskhelper.services.AlarmService
import com.example.taskhelper.ui.dailogs.CustomWebView
import com.example.taskhelper.ui.home.HomeScreen
import com.example.taskhelper.ui.task_friends.TaskBottomSheet
import com.example.taskhelper.utils.*
import com.example.taskhelper.viewmodels.TaskViewModel
import com.google.android.material.snackbar.Snackbar
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*

open class AddNewTask : AppCompatActivity(), View.OnClickListener, TaskBottomSheet.BottomSheetInterface {

    private lateinit var binding: ActivityAddNewTaskBinding
    private lateinit var taskViewModel: TaskViewModel
    private var taskColor: Int = 0
    private var taskID: Int = 0
    private var notifyTask: Int? = 0
    private var friendID: Int = 0
    private var taskAlarm: Long = 0L
    private var imageFilePath: String = "ImagePath"
    private lateinit var alarmService: AlarmService
    private val userPermission by lazy { UserPermission(this) }
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    //permission start
    private var hadPermissions: Boolean = false
    private lateinit var permissions: Array<String>
    private var permissionDeniedMessage: String? = null

    open fun getPermissionsToRequest(): Array<String> {
        return arrayOf()
    }

    private val snackBarContainer: View
        get() = rootView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        permissions = getPermissionsToRequest()
        hadPermissions = hasPermissions()
        permissionDeniedMessage = null


        //alarmService = AlarmService(this)
        taskID = intent.getIntExtra("ID", 0)

        binding.btnBackToHomeMT.setOnClickListener(this)
        binding.btnOpenBottomSheet.setOnClickListener(this)
        binding.btnDeleteTask.setOnClickListener(this)
        binding.btnOpenMyGallery.setOnClickListener(this)
        binding.btnSaveTask.setOnClickListener(this)
        binding.tvShowTaskLink.setOnClickListener(this)
        selectAndCompressImage()
        binding.bottomView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.ColorDefaultNote, theme))
        val bundle = intent.extras
        notifyTask = bundle!!.getInt("TaskID", 0)
        if (taskID != 0) {

            displayTaskInfo(
                intent.getStringExtra("Title")!!,
                intent.getStringExtra("Content")!!,
                intent.getStringExtra("Date")!!,
                intent.getStringExtra("Time")!!,
                intent.getStringExtra("Link")!!,
                intent.getStringExtra("TaskImage")!!,
                intent.getStringExtra("Color")!!.toInt(),
                intent.getIntExtra("TaskFriendID", 0))
            friendID = intent.getIntExtra("TaskFriendID", 0)

        } else if (notifyTask != 0) {
            // setToastMessage("$notifyTask" , Color.RED)
            var taskEntities: TaskEntities
            taskViewModel.viewModelScope.launch {
                taskEntities = taskViewModel.getSingleTask(notifyTask!!)
                displayTaskInfo(
                    taskEntities.taskTitle,
                    taskEntities.taskDesc,
                    taskEntities.taskDate,
                    taskEntities.taskTime,
                    taskEntities.taskLink,
                    taskEntities.taskImage,
                    taskEntities.taskColor.toInt(),
                    taskEntities.taskFriendID
                )
                friendID = taskEntities.taskFriendID
                taskID = taskEntities.taskId
            }
        }
    }


    override fun setTaskInfo(
        color: String, lintText: String, time: String, date: String, friendID: Int, taskAlarm: Long
    ) {

        taskColor = color.toInt()
        binding.parentView.setBackgroundColor(taskColor)
        this@AddNewTask.taskAlarm = taskAlarm
        binding.bottomView.setBackgroundColor(taskColor)
        binding.tvShowTaskTime.text = time
        binding.tvShowTaskDate.text = date
        binding.tvShowTaskLink.text = lintText
        if (friendID != 0) {
            var contactEntities: ContactEntities
            taskViewModel.viewModelScope.launch {
                contactEntities = taskViewModel.getSingleContact(friendID)
                this@AddNewTask.friendID = contactEntities.contactId
                GlideApp.with(this@AddNewTask).asBitmap().load(contactEntities.contact_Image)
                    .into(binding.taskFriendImage).clearOnDetach()
                binding.tvShowTaskFriend.text = contactEntities.contact_Name
            }
        }
    }


    private fun displayTaskInfo(title: String?, desc: String?, date: String?,
                                time: String?, link: String?, imagePath: String?, colorOfTask: Int?, friendID: Int?) {

        binding.tiTaskTitle.setText(title!!)
        binding.tiTaskContent.setText(desc!!)
        binding.tvShowTaskTime.text = time!!
        binding.tvShowTaskDate.text = date!!
        binding.tvShowTaskLink.text = link!!
        taskColor = colorOfTask!!
        binding.parentView.setBackgroundColor(taskColor)
        binding.bottomView.setBackgroundColor(taskColor)
        imageFilePath = imagePath!!
        GlideApp.with(this@AddNewTask).asBitmap().load(imageFilePath).into(binding.taskImageView)
            .clearOnDetach()
        if (friendID != 0) {
            taskViewModel.viewModelScope.launch {
                val contact = taskViewModel.getSingleContact(friendID!!)
                GlideApp.with(this@AddNewTask).asBitmap().load(contact.contact_Image)
                    .into(binding.taskFriendImage).clearOnDetach()
                binding.tvShowTaskFriend.text = contact.contact_Name
            }
        }
        binding.btnDeleteTask.visibility = View.VISIBLE

    }

    private fun selectAndCompressImage() {
        cropActivityResultLauncher =
            registerForActivityResult(userPermission.openGalleryAndGetImage) {
                if (it != null) {
                    runBlocking {
                        withContext(Dispatchers.IO) {
                            imageFilePath = Compressor.compress(this@AddNewTask, it.toFile()).path
                        }
                        GlideApp.with(this@AddNewTask).load(imageFilePath)
                            .into(binding.taskImageView).clearOnDetach()
                    }
                } else {
                    setToastMessage("Selected Noun", Color.parseColor("#CB0003"))
                }
            }
    }



    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_BackToHomeMT -> {
                this.startNewActivity(HomeScreen::class.java, 1)
            }
            R.id.btn_OpenBottomSheet -> {
                TaskBottomSheet(
                    taskColor,
                    binding.tvShowTaskTime.text.toString(),
                    binding.tvShowTaskDate.text.toString(),
                    binding.tiTaskTitle.text.toString(),
                    binding.tvShowTaskLink.text.toString(),
                    friendID,
                    taskID
                ).show(supportFragmentManager, "TAG")
            }
            R.id.btn_DeleteTask -> {
                CustomDeleteDialog(taskID, 1).show(supportFragmentManager, "TAG")
            }
            R.id.btnOpenMyGallery -> {

                if (hasPermissions())  {
                    // request for gallery
                    cropActivityResultLauncher.launch("image/*")
                }else {
                    requestPermissions()
//                    requestMultiplePermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.CAMERA))
                }


            }
            R.id.tv_ShowTaskLink -> {
                if (binding.tvShowTaskLink.text.isNullOrEmpty()) {
                    setToastMessage(
                        "There is no link included at this task",
                        Color.parseColor("#F98404")
                    )
                } else {
                    CustomWebView(binding.tvShowTaskLink.text.toString()).show(
                        supportFragmentManager,
                        "TAG"
                    )
                }
            }
            R.id.btn_SaveTask -> {
                saveNewTask()
            }
        }
    }

    private fun saveNewTask() {
        val title = binding.tiTaskTitle.text.toString()
        val desc = binding.tiTaskContent.text.toString()
        val time = binding.tvShowTaskTime.text.toString()
        val date = binding.tvShowTaskDate.text.toString()
        val link = binding.tvShowTaskLink.text.toString()

        val taskEntities = TaskEntities(title, desc, time, date,
            link, taskColor.toString(), imageFilePath, friendID)
        val itemID = if (taskID !=  0) taskID else  intent.getIntExtra("ID", 0)

        when (taskID) {

            0 -> {
                if (title.isEmpty() || desc.isEmpty() || time.isEmpty() || date.isEmpty()) {
                    showingSnackBar(
                        binding.root,
                        "Please make sure all fields are filled",
                        "#F98404"
                    )
                } else {
                    taskViewModel.saveNewTask(taskEntities)
                    setFirstTimeAlarm(taskEntities.taskTitle)

                }
            }
            itemID -> {
                if (title == intent.getStringExtra("Title") && desc == intent.getStringExtra("Content") && time == intent.getStringExtra("Time")) {

                    showingSnackBar(
                        binding.root,
                        "Please make sure you've updated anything.",
                        "#F98404"
                    )
                } else {
                    taskEntities.taskId = taskID
                    taskViewModel.updateCurrentTask(taskEntities)
                    alarmService.setExactAlarm(
                        taskAlarm,
                        binding.tiTaskTitle.text.toString(),
                        "You have a new task  called ${taskEntities.taskTitle} with your friend ${binding.tvShowTaskFriend.text} .. let's go to do it.",
                        1,
                        taskID
                    )
                    this.startNewActivity(HomeScreen::class.java, 1)
                    setToastMessage("Task Updated ${taskEntities.taskId}", Color.parseColor("#6ECB63"))
                }
            }

        }
    }

    private fun setFirstTimeAlarm(title: String?){
        CoroutineScope(Dispatchers.IO).launch {
            //delay(1000)
            val taskEntities =taskViewModel.getSingleTaskByName(title!!)
            alarmService.setExactAlarm(
                taskAlarm,
                binding.tiTaskTitle.text.toString(),
                "You have a new task  called $title with your friend ${binding.tvShowTaskFriend.text} .. let's go to do it.",
                1,
                taskEntities.taskId
            )
            finish()
        }
        setToastMessage("Task Saved", Color.parseColor("#6ECB63"))
        this.startNewActivity(HomeScreen::class.java, 1)
    }

    protected open fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST)
    }

//permission start
private fun hasPermissions(): Boolean {
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(this,
                permission) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@AddNewTask, Manifest.permission.READ_EXTERNAL_STORAGE,
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this@AddNewTask, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        )
                    ) {
                        // User has deny from permission dialog
                       // setToastMessage("$permissionDeniedMessage!!", Color.parseColor("#CB0003"))
                        Snackbar.make(
                            snackBarContainer,
                            permissionDeniedMessage!!,
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction("Grant") { requestPermissions() }
                            .setActionTextColor(Color.parseColor("#CB0003")).show()
                    } else {
                        // User has deny permission and checked never show permission dialog so you can redirect to Application settings page
                        Snackbar.make(
                            snackBarContainer,
                            permissionDeniedMessage!!,
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction(R.string.action_settings) {
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts(
                                    "package",
                                    this@AddNewTask.packageName,
                                    null
                                )
                                intent.data = uri
                                startActivity(intent)
                            }.setActionTextColor(Color.parseColor("#CB0003")).show()
                    }
                    return
                }
            }
            hadPermissions = true
            //onHasPermissionsChanged(true)
        }
    }



    companion object {
        const val PERMISSION_REQUEST = 100
        const val BLUETOOTH_PERMISSION_REQUEST = 101
    }



}