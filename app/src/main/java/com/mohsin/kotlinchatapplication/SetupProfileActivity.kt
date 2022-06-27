package com.mohsin.kotlinchatapplication

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mohsin.kotlinchatapplication.databinding.ActivitySetupProfileBinding
import java.util.*
import kotlin.collections.HashMap

class SetupProfileActivity : AppCompatActivity() {
    private var _binding : ActivitySetupProfileBinding? = null
    private val binding get() = _binding!!
    var auth : FirebaseAuth? = null
    var database : FirebaseDatabase? = null
    var storage : FirebaseStorage? = null
    var selectedImg : Uri? = null
    var dialog : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Uploading Profile...")
        dialog!!.setCancelable(false)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()

        binding.apply {
            profileImg.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type ="image/*"
                startActivityForResult(intent, 45)
            }

            setupBtn.setOnClickListener {
                val name : String = editName.text.toString()
                if (name.isEmpty()) {
                    editName.setError("Please type your name")
                }
                else {
                    if (selectedImg != null) {
                        dialog!!.show()
                        val reference = storage!!.reference.child("Profile").child(auth!!.uid!!)
                        reference.putFile(selectedImg!!).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                reference.downloadUrl.addOnCompleteListener { uri ->
                                    val uid = auth!!.uid
                                    val name: String = binding.editName.text.toString()
                                    val phone = auth!!.currentUser!!.phoneNumber
                                    val imageUrl = uri.result.toString()
                                    val user = User(uid, name, phone, imageUrl)

                                    Log.d("selectedImg", "" + uri.result)

                                    database!!.reference
                                        .child("users")
                                        .child(uid!!)
                                        .setValue(user)
                                        .addOnCompleteListener {
                                            dialog!!.dismiss()
                                            val intent = Intent(
                                                this@SetupProfileActivity,
                                                MainActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        }
                                }
                            } else {
                                val uid = auth!!.uid
                                val name: String = binding.editName.text.toString()
                                val phone = auth!!.currentUser!!.phoneNumber
                                val user = User(uid, name, phone, "No Image")

                                database!!.reference
                                    .child("users")
                                    .child(uid!!)
                                    .setValue(user)
                                    .addOnCompleteListener {
                                        dialog!!.dismiss()
                                        val intent = Intent(
                                            this@SetupProfileActivity,
                                            MainActivity::class.java
                                        )
                                        startActivity(intent)
                                        finish()
                                    }
                            }
                        }
                    }
                    else {
                        Toast.makeText(
                            this@SetupProfileActivity,
                            "Please select an image for profile.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (data.data != null) {
                val uri = data.data
//                val storage = FirebaseStorage.getInstance()
//                val time = Date().time
//                val reference = storage.reference
//                    .child("Profile")
//                    .child(time.toString() + "")
//
//                reference.putFile(uri!!).addOnCompleteListener { task->
//                    if (task.isSuccessful) {
//                        reference.downloadUrl.addOnCompleteListener { uri->
//                            val filePath = uri.toString()
//                            val obj = HashMap<String, Any>()
//                            obj["image"] = filePath
//                            database!!.reference
//                                .child("users")
//                                .child(FirebaseAuth.getInstance().uid!!)
//                                .updateChildren(obj).addOnCompleteListener {
//
//                                }
//                        }
//                    }
//                }

                binding.profileImg.setImageURI(uri)
                selectedImg = data.data
            }
        }
        else {
            Toast.makeText(
                this,
                "No image selected!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}