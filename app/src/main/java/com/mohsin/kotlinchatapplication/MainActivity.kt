package com.mohsin.kotlinchatapplication

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mohsin.kotlinchatapplication.adapter.UserAdapter
import com.mohsin.kotlinchatapplication.databinding.ActivityMainBinding
import com.mohsin.kotlinchatapplication.model.User

class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!

    var database :FirebaseDatabase? = null
    var users : ArrayList<User>? = null
    var userAdapter : UserAdapter? = null
    var dialog : ProgressDialog? = null
    var user : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Fetching Data...")
        dialog!!.setCancelable(false)

        database = FirebaseDatabase.getInstance()
        users = ArrayList<User>()
        userAdapter = UserAdapter(this, users!!)

        val layoutManager = GridLayoutManager(this, 2)
        binding.mRec.layoutManager = layoutManager

        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        binding.mRec.adapter = userAdapter
        database!!.reference.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users!!.clear()
                for (snapshot1 in snapshot.children) {
                    val user : User? = snapshot1.getValue(User::class.java)
                    if (!user!!.uid.equals(FirebaseAuth.getInstance().uid)) {
                        users!!.add(user)
                    }
                }
                userAdapter!!.notifyDataSetChanged()

                Log.d("MY_TAG", "User Data : " + user!!.profileImg)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Offline")
    }
}