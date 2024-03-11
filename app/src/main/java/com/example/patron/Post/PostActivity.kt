package com.example.patron.Post

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.patron.HomeActivity
import com.example.patron.Model.Post
import com.example.patron.Model.User

import com.example.patron.databinding.ActivityPostBinding
import com.example.patron.utils.POST
import com.example.patron.utils.POST_FOLDER
import com.example.patron.utils.USER_NODE
import com.example.patron.utils.USER_PROFILE_FOLDER
import com.example.patron.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class PostActivity : AppCompatActivity() {
    private lateinit var binding :ActivityPostBinding


    var imageUrl :String?=null
    val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

        uri?.let {

            uploadImage(uri, POST_FOLDER) {
                    url ->
                if (url != null) {
                    binding.selectImage.setImageURI(uri)
                    imageUrl = url
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        binding.materialToolbar.setNavigationOnClickListener {
            startActivity(Intent(this@PostActivity,HomeActivity::class.java))
            finish()
        }

        binding.selectImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@PostActivity,HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                val user = it.toObject<User>()!!

                val post: Post = Post(postUrl = imageUrl!!, caption = binding.caption.editText?.text.toString(),uid=Firebase.auth.currentUser!!.uid,time =System.currentTimeMillis().toString())

                Firebase.firestore.collection(POST).document().set(post).addOnSuccessListener {
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document()
                        .set(post).addOnSuccessListener {
                        startActivity(Intent(this@PostActivity, HomeActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}