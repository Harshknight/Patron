package com.example.patron

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.patron.Model.User
import com.example.patron.databinding.ActivitySignUpBinding
import com.example.patron.utils.USER_NODE
import com.example.patron.utils.USER_PROFILE_FOLDER
import com.example.patron.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var user: User
    private var isEditMode: Boolean = false
    val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) {
                if (it != null) {
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    private fun updateUserProfile() {
        user.name = binding.name.editText?.text.toString()
        user.password = binding.password.editText?.text.toString()
        user.email = binding.email.editText?.text.toString()

        Firebase.firestore.collection(USER_NODE)
            .document(Firebase.auth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text =
            "<font color=#FFFFFFFF> Already Have an Account</font> <font color=#1E88E5>login?</font>"
        binding.login.setText(Html.fromHtml(text))
        user = User()

        if (intent.hasExtra("MODE")) {
            if (intent.getIntExtra("MODE", -1) == 1) {
                isEditMode = true
                binding.signup.text = "Update Profile"
                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
                    .get().addOnSuccessListener { document ->
                        val currentUser: User? = document.toObject<User>()

                        // Update UI with existing user data
                        currentUser?.let {
                            if (!it.image.isNullOrEmpty()) {
                                Picasso.get().load(it.image).into(binding.profileImage)
                            }
                            binding.name.editText?.setText(it.name)
                            binding.email.editText?.setText(it.email)
                            binding.password.editText?.setText(it.password)
                        }
                    }
            }
        }
        binding.signup.setOnClickListener {
            if (isEditMode) {
                // If in edit mode, update only the profile image if changed
                if (user.image != null) {
                    Firebase.firestore.collection(USER_NODE)
                        .document(Firebase.auth.currentUser!!.uid)
                        .update("image", user.image)
                        .addOnSuccessListener {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                } else {
                    // Handle other profile updates
                    updateUserProfile()
                }
            }
            else{
            if (binding.name.editText?.text.toString().equals("") or
                binding.email.editText?.text.toString().equals("") or
                binding.email.editText?.text.toString().equals("")
            ) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Please Fill all Information ",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.email.editText?.text.toString(),
                    binding.password.editText?.text.toString()
                ).addOnCompleteListener { result ->

                    if (result.isSuccessful) {
                        user.name = binding.name.editText?.text.toString()
                        user.password = binding.password.editText?.text.toString()
                        user.email = binding.email.editText?.text.toString()

                        Firebase.firestore.collection(USER_NODE)
                            .document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            }
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            result.exception?.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

        binding.add.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
            finish()
        }
    }
}