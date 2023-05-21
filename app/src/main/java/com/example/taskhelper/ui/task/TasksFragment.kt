package com.example.taskhelper.ui.task

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario.launch
import com.example.taskhelper.R
import com.example.taskhelper.adapters.TaskAdapter
import com.example.taskhelper.data.entities.TaskEntities
import com.example.taskhelper.databinding.FragmentTasksBinding
import com.example.taskhelper.utils.startNewActivity
import com.example.taskhelper.utils.startSearchActivity
import com.example.taskhelper.viewmodels.TaskViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TasksFragment : Fragment(),View.OnClickListener  {
    lateinit var binding:FragmentTasksBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var imm: InputMethodManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTasksBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        displayTasksRecyclerView()

        var deletedItem: String?
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false

                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position: Int = viewHolder.adapterPosition
                    val listTasks: TaskEntities = taskAdapter.differ.currentList[position]
                    val listSize = taskAdapter.differ.currentList.size
                    deletedItem = "Are You Sure You Want To Delete This OR Undo Deleted .."
                    taskViewModel.deleteSelectedTask(listTasks)
                    taskAdapter.notifyDataSetChanged()
                    Snackbar.make(binding.tasksRecyclerView, deletedItem!!, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {
                            taskViewModel.saveNewTask(listTasks)
                            taskAdapter.notifyItemRangeInserted(listSize , taskAdapter.differ.currentList.size-1)
                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.tasksRecyclerView)

        binding.btnAddNewElement.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
    }

    private fun displayTasksRecyclerView(){
        taskAdapter = TaskAdapter(requireActivity())
        binding.tasksRecyclerView.apply {
            binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.tasksRecyclerView.setHasFixedSize(true)
            adapter = taskAdapter
        }
        taskViewModel.allTasksList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.emptyImageView.visibility = View.VISIBLE
                taskAdapter.differ.submitList(it)
            } else {
                binding.emptyImageView.visibility = View.GONE
                taskAdapter.differ.submitList(it)
            }
        }

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_AddNewElement -> {
             CoroutineScope(Dispatchers.Main).launch {
                    requireActivity().startNewActivity(AddNewTask::class.java, 1)
                }
            }
            R.id.btn_Search -> {
                requireActivity().startSearchActivity(1)
            }
        }
    }





}