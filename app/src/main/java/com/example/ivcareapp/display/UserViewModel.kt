package com.example.ivcareapp.display

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivcareapp.database.User
import com.example.ivcareapp.database.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//userviewmodel has reference to user repo as constructor parameter
class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val users = repository.users
    private var isUpdateOrDelete = false
    private lateinit var userToUpdateOrDelete : User

    val inputName = MutableLiveData<String>()
    val inputEmail = MutableLiveData<String>()
    val inputStatus = MutableLiveData<String>()
    val inputRole = MutableLiveData<String>()
    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun saveOrUpdate(){

        if (inputName.value == null) {
            statusMessage.value = Event("Please enter your name")
        } else if (inputEmail.value == null) {
            statusMessage.value = Event("Please enter your company email")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Please enter a correct email address")
        } else if (inputStatus.value == null) {
              statusMessage.value = Event("Please enter your current status")
        } else if (inputRole.value == null) {
                statusMessage.value = Event("Please enter your company role")
        } else {
            if(isUpdateOrDelete){
                userToUpdateOrDelete.name = inputName.value!!
                userToUpdateOrDelete.email = inputEmail.value!!
                update(userToUpdateOrDelete)
            }else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                val status = inputStatus.value!!
                val role = inputRole.value!!
                insert(User(0, name, email, status, role))
                inputName.value = ""
                inputEmail.value = ""
                inputStatus.value = ""
                inputRole.value = ""
            }
        }
    }

    fun clearAllOrDelete(){
        if(isUpdateOrDelete){
            delete(userToUpdateOrDelete)
        }else {
            clearAll()
        }

    }
    private fun insert(user: User) = viewModelScope.launch(Dispatchers.IO) {
        val newRowId = repository.insert(user)
        withContext(Dispatchers.Main){
            if(newRowId > -1) {
                statusMessage.value = Event("User Inserted Successfully! $newRowId")
            }else{
                statusMessage.value = Event("Error Occurred!")
            }
        }
    }


    private fun update(user: User) = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRows = repository.update(user)
        withContext(Dispatchers.Main){
            if(numberOfRows > 0) {
                inputName.value = ""
                inputEmail.value = ""
                isUpdateOrDelete = false
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear All"
                statusMessage.value = Event("$numberOfRows Rows Updated Successfully!")
            }else{
                statusMessage.value = Event("Error Occurred!")
            }
        }
    }

    private fun delete(user: User) = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRowsDeleted = repository.delete(user)
        withContext(Dispatchers.Main){
            if(numberOfRowsDeleted > 0) {
                inputName.value = ""
                inputEmail.value = ""
                isUpdateOrDelete = false
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear All"
                statusMessage.value = Event("$numberOfRowsDeleted Rows Deleted Successfully!")
            } else {
                statusMessage.value = Event("Error Occurred!")
            }
        }
    }

    private fun clearAll() = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRowsDeleted = repository.deleteAll()
        withContext(Dispatchers.Main){
            if(numberOfRowsDeleted > 0) {
                statusMessage.value = Event("$numberOfRowsDeleted Rows Deleted Successfully!")
            }else{
                statusMessage.value = Event("Error Occurred!")
            }
        }
    }

    fun initUpdateAndDelete(user: User){
        inputName.value = user.name
        inputEmail.value = user.email
        inputStatus.value = user.status
        inputRole.value = user.role
        isUpdateOrDelete = true
        userToUpdateOrDelete = user
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }

}