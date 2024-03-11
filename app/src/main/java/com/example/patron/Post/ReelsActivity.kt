package com.example.patron.Post

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.patron.HomeActivity
import com.example.patron.Model.Post
import com.example.patron.Model.Reel
import com.example.patron.Model.User
import com.example.patron.R
import com.example.patron.databinding.ActivityReelsBinding
import com.example.patron.utils.POST
import com.example.patron.utils.POST_FOLDER
import com.example.patron.utils.REEL_FOLDER
import com.example.patron.utils.REEl
import com.example.patron.utils.USER_NODE
import com.example.patron.utils.uploadImage
import com.example.patron.utils.uploadVideo
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class ReelsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityReelsBinding
     lateinit var progressDialog:ProgressDialog

    var videoUrl :String?=null
    val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

        uri?.let {

            uploadVideo(uri, REEL_FOLDER, progressDialog) {
                    url ->
                if (url != null) {

                    videoUrl = url
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReelsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog=ProgressDialog(this)
        binding.selectReel.setOnClickListener {
            launcher.launch("video/*")
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@ReelsActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                val user: User = it.toObject<User>()!!
                val reel: Reel = Reel(videoUrl!!,binding.caption.editText?.text.toString(),user.image!!)

                Firebase.firestore.collection(REEl).document().set(reel).addOnSuccessListener {
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ REEl).document().set(reel).addOnSuccessListener {
                        startActivity(Intent(this@ReelsActivity,HomeActivity::class.java))
                        finish()
                    }
                }
            }

        }

    }
}