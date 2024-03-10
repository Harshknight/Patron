package com.example.patron.Post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.patron.Model.Post

import com.example.patron.databinding.ActivityPostBinding
import com.example.patron.utils.POST
import com.example.patron.utils.POST_FOLDER
import com.example.patron.utils.USER_PROFILE_FOLDER
import com.example.patron.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

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
            finish()
        }

        binding.selectImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.postButton.setOnClickListener {
            val post: Post = Post(imageUrl!!,binding.caption.editText?.text.toString())

            Firebase.firestore.collection(POST).document().set(post).addOnSuccessListener {
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document().set(post).addOnSuccessListener {
                    finish()
                }
            }
        }
    }
}