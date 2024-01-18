package com.example.ivcareapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ivcareapp.database.User
import com.example.ivcareapp.database.UserDatabase
import com.example.ivcareapp.database.UserRepository
import com.example.ivcareapp.databinding.ActivityMainBinding
import com.example.ivcareapp.display.MyRecyclerViewAdapter
import com.example.ivcareapp.display.UserViewModel
import com.example.ivcareapp.display.UserViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: MyRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val dao = UserDatabase.getInstance(application).userDAO
        val repository = UserRepository(dao)
        val factory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this,factory)[UserViewModel::class.java]
        binding.myViewModel = userViewModel
        binding.lifecycleOwner = this

        initRecyclerView()

        userViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun initRecyclerView(){
        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyRecyclerViewAdapter({selectedItem:User->listItemClicked(selectedItem)})
        binding.userRecyclerView.adapter = adapter
        displayUsersList()
    }
    private fun displayUsersList(){
        userViewModel.users.observe(this, Observer {
            Log.i("MYTAG",it.toString())
            //binding.subscriberRecyclerView.adapter = MyRecyclerViewAdapter(it,{selectedItem:Subscriber->listItemClicked(selectedItem)})
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }
    private fun listItemClicked(user: User){
        Toast.makeText(this,"selected name is ${user.name}", Toast.LENGTH_LONG).show()
        userViewModel.initUpdateAndDelete(user)
    }
}