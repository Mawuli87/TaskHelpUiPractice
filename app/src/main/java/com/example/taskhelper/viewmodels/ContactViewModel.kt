package com.example.taskhelper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskhelper.data.database.HelperDataBase
import com.example.taskhelper.data.entities.ContactEntities
import com.example.taskhelper.data.entities.TaskEntities
import com.example.taskhelper.repository.ContactRepo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewMode(application: Application): AndroidViewModel(application) {

    private var contactRepo: ContactRepo
    var getAllContacts: LiveData<List<ContactEntities>>

    init {
        val contactDao = HelperDataBase.getDataBaseInstance(application).contactDao()
        contactRepo = ContactRepo(contactDao)
        getAllContacts = contactDao.getAllContacts()
    }

    fun saveNewContact(contactEntities: ContactEntities) = viewModelScope.launch (Dispatchers.IO){
        contactRepo.saveNewContact(contactEntities)
    }

    fun updateCurrentContact(contactEntities: ContactEntities) = viewModelScope.launch (Dispatchers.IO){
        contactRepo.updateCurrentContact(contactEntities)
    }

    fun deleteCurrentContact(contactEntities: ContactEntities) = viewModelScope.launch (Dispatchers.IO){
        contactRepo.deleteCurrentContact(contactEntities)
    }

    fun searchInContactDataBase(queryString: String): LiveData<List<ContactEntities>> {
        return contactRepo.searchInContactDataBase(queryString).asLiveData()
    }

    fun getTasksWithFriend(id:Int): LiveData<List<TaskEntities>> {
        return contactRepo.getTasksWithFriend(id)
    }


}