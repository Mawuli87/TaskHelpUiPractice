package com.example.taskhelper.ui.task_friends

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskhelper.R
import com.example.taskhelper.adapters.FriendsAdapter
import com.example.taskhelper.data.entities.ContactEntities
import com.example.taskhelper.databinding.FragmentTaskBottomSheetBinding
import com.example.taskhelper.utils.setToastMessage
import com.example.taskhelper.viewmodels.ContactViewMode
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class TaskBottomSheet(
    taskColor: Int?,
    time: String,
    date: String,
    titleTask: String,
    link: String,
    friendID: Int,
    taskID: Int
) :
    BottomSheetDialogFragment(), View.OnClickListener, FriendsAdapter.OnItemClickerListener {

    private var _binding: FragmentTaskBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var taskColor: Int? = null
    private var timeTask: String? = null
    private var dateTask: String? = null
    private var titleTask: String? = null
    private var link: String? = null
    private var friendID: Int = 0
    private var taskID: Int = 0
    private var taskAlarm: Long = 0L
    private lateinit var sheetListener: BottomSheetInterface
    private lateinit var friendAdapter: FriendsAdapter
    private lateinit var contactViewMode: ContactViewMode

    init {
        this.taskColor = taskColor
        this.timeTask = time
        this.dateTask = date
        this.titleTask = titleTask
        this.link = link
        this.friendID = friendID
        this.taskID = taskID
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBottomSheetBinding.inflate(inflater, container, false)

        contactViewMode = ViewModelProvider(this)[ContactViewMode::class.java]
        friendAdapter = FriendsAdapter(requireContext(), this)
        binding.enterLinkText.setText(link)
        binding.chlPaletteColor.setSelectedColor(taskColor!!)
        displayFriendsRecyclerView()
        binding.tvSheetTime.text = timeTask
        binding.tvSheetDate.text = dateTask

        binding.saveTaskInfo.setOnClickListener(this)
        binding.setTaskCalender.setOnClickListener(this)
        binding.chlPaletteColor.setOnColorSelectedListener { clr ->
            taskColor = clr
        }
        return binding.root
    }

    private fun displayFriendsRecyclerView() {
        binding.taskFiendRecycler.setHasFixedSize(true)
        binding.taskFiendRecycler.layoutManager = LinearLayoutManager(context)
        binding.taskFiendRecycler.adapter = friendAdapter
        contactViewMode.getAllContacts.observe(viewLifecycleOwner) {
            it.let { list ->
                friendAdapter.differ.submitList(list)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setAlarm() {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            DatePickerDialog(
                requireContext(),
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    TimePickerDialog(
                        requireContext(),
                        0,
                        { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            taskAlarm = this.timeInMillis
                            val timeFormatted = SimpleDateFormat("hh:mm a").format(this.time)
                            timeTask = timeFormatted.toString()
                            binding.tvSheetTime.text = timeTask
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()

                    val dateFormatted =
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(this.time)
                    dateTask = dateFormatted.toString()
                    binding.tvSheetDate.text = dateTask

                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun getTaskInfoFromBottomSheet() {
        if (checkValidateUrl() || binding.enterLinkText.text!!.isEmpty()) {
            val selectedColor = this.taskColor
            val link = binding.enterLinkText.text.toString()
            val time = binding.tvSheetTime.text.toString()
            val date = binding.tvSheetDate.text.toString()
            sheetListener.setTaskInfo(
                selectedColor.toString(), link, time, date, friendID, taskAlarm
            )
          // Toast.makeText(requireActivity(), "$friendID", Toast.LENGTH_SHORT).show()
            dismiss()
        } else {
            binding.enterLinkText.error = "Url is not valid!!"
            Toast.makeText(
                requireContext(),
                "please make sure that the url is valid or not.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkValidateUrl(): Boolean {
        val linkSample = ("((http|https)://)(www.)?"
                + "[a-zA-Z0-9@:%._\\+~#?&//=]"
                + "{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%"
                + "._\\+~#?&//=]*)")

        val uRL: Pattern = Pattern.compile(linkSample)

        if (uRL.matcher(binding.enterLinkText.text.toString()).matches()) {
            return true
        }

        return false
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        val height = (resources.displayMetrics.heightPixels)
        sheetContainer.layoutParams.height = height
        sheetContainer.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.saveTaskInfo -> {
                getTaskInfoFromBottomSheet()
            }
            R.id.setTaskCalender -> {
                setAlarm()
              //  requireActivity().setToastMessage("Notified Task ID $taskID")
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            sheetListener = context as BottomSheetInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() +
                        "must implement ExampleDialogListener"
            )
        }
    }

    interface BottomSheetInterface {
        fun setTaskInfo(
            color: String,
            lintText: String,
            time: String,
            date: String,
            friendID: Int,
            taskAlarm: Long
        )
    }

    override fun onDestroy() {

        super.onDestroy()
        _binding = null
    }

    override fun onClicked(contactEntities: ContactEntities, position: Int) {
        friendID = contactEntities.contactId
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClickListener(position: Int, view: View?) {
        friendAdapter.notifyDataSetChanged()
    }

}