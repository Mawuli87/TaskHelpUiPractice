package com.example.taskhelper.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.taskhelper.R
import com.example.taskhelper.databinding.ActivityHomeScreenBinding
import com.example.taskhelper.ui.checkList.CheckListFragment
import com.example.taskhelper.ui.contact.ContactsFragment
import com.example.taskhelper.ui.task.TasksFragment
import com.example.taskhelper.ui.whether.WeatherFragment
import com.example.taskhelper.utils.getGreetingMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeScreen : AppCompatActivity() {

    lateinit var binding:ActivityHomeScreenBinding


    companion object {
        const val ID_TASKS = 1
        const val ID_CHECKLIST = 2
        const val ID_CONTACT = 3
        const val ID_WEATHER = 4
    }
    private var selectedFragment:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.bottomNavigation .add(MeowBottomNavigation.Model(ID_TASKS, R.drawable.ic_calendar))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(ID_CHECKLIST, R.drawable.ic_iconfinder_check_list))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(ID_CONTACT, R.drawable.ic_iconfinder_contact_user))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(ID_WEATHER, R.drawable.ic_snowflake))
        selectFragmentWhenBack()

        val greetingCode = intent.getIntExtra("ToastMessage" , 0)
        if (greetingCode == 101) getGreetingMessage()
        binding.bottomNavigation.setOnShowListener  {
            when(it.id){
                ID_TASKS ->{
                    val taskFragment = TasksFragment()
                    replaceFragment(taskFragment)
                }
                ID_CHECKLIST -> {
                    val checkListFragment = CheckListFragment()
                    replaceFragment(checkListFragment)
                }
                ID_CONTACT -> {
                    val contactFragment = ContactsFragment()
                    replaceFragment(contactFragment)

                }
                ID_WEATHER -> {
                    val weatherFragment = WeatherFragment()
                    replaceFragment(weatherFragment)
                }
            }
        }

    }
    private fun selectFragmentWhenBack(){

        selectedFragment = intent.getIntExtra("ArrowKey" , 0)
        when(selectedFragment){
            ID_CHECKLIST ->{
                binding.bottomNavigation.show(ID_CHECKLIST, true)
            }
            ID_CONTACT ->{
                binding.bottomNavigation.show(ID_CONTACT, true)
            }
            else ->{
                binding.bottomNavigation.show(ID_TASKS, true)
            }
        }
    }

    private fun replaceFragment(fragment:androidx.fragment.app.Fragment){
        CoroutineScope(Dispatchers.Main).launch {
            val fragmentTransition = supportFragmentManager.beginTransaction()
            fragmentTransition.replace(R.id.fragment_holder, fragment)
                .disallowAddToBackStack()
                .commit()
        }

    }
}