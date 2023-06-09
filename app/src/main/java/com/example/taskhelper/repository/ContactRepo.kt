package com.example.taskhelper.repository

import androidx.lifecycle.LiveData
import com.example.taskhelper.data.database.dao.ContactDao
import com.example.taskhelper.data.entities.ContactEntities
import com.example.taskhelper.data.entities.TaskEntities

import kotlinx.coroutines.flow.Flow

class ContactRepo(private val contactDao: ContactDao) {

    var getAllContact:LiveData<List<ContactEntities>> = contactDao.getAllContacts()

    suspend fun saveNewContact(contactEntities: ContactEntities) = contactDao.saveNewContact(contactEntities)

    suspend fun updateCurrentContact(contactEntities: ContactEntities) = contactDao.updateCurrentContact(contactEntities)

    suspend fun deleteCurrentContact(contactEntities: ContactEntities) = contactDao.deleteSelectedContact(contactEntities)

    fun searchInContactDataBase(query:String):Flow<List<ContactEntities>>{
        return contactDao.searchInContactDataBase(query)
    }

    fun getTasksWithFriend(friendID:Int):LiveData<List<TaskEntities>>{
        return contactDao.getTaskWithFriend(friendID)
    }
}