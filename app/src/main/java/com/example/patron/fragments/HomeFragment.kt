package com.example.patron.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.patron.Adapter.FollowAdapter
import com.example.patron.Adapter.PostAdapter
import com.example.patron.MessageActivity
import com.example.patron.Model.Post
import com.example.patron.Model.User
import com.example.patron.NotificationActivity
import com.example.patron.R
import com.example.patron.databinding.FragmentHomeBinding
import com.example.patron.utils.FOLLOW
import com.example.patron.utils.POST
import com.example.patron.utils.USER_NODE
import com.example.patron.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private var postList= ArrayList<Post>()
    private lateinit var adapter: PostAdapter

    private lateinit var followAdapter: FollowAdapter
    private var followList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.transparent)
        }



        adapter = PostAdapter(requireContext(),postList)
        binding.postRv.layoutManager=LinearLayoutManager(requireContext())
        binding.postRv.adapter=adapter

        followAdapter = FollowAdapter(requireContext(),followList)
        binding.followRv.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.followRv.adapter=followAdapter

        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar2)

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ FOLLOW).get().addOnSuccessListener {
            var tempList=ArrayList<User>()
            followList.clear()
            for(i in it.documents){
                var user :User = i.toObject<User>()!!
                tempList.add(user)
            }
            followList.addAll(tempList)
            followAdapter.notifyDataSetChanged()
        }

        Firebase.firestore.collection(POST).get().addOnSuccessListener {
            var tempList=ArrayList<Post>()
            postList.clear()
            for(i in it.documents){
                var post :Post = i.toObject<Post>()!!
                tempList.add(post)
            }
            postList.addAll(tempList)
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    companion object {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.like -> {
                // Handle menu item 1 click
                    startActivity(Intent(requireContext(),NotificationActivity::class.java))
                return true
            }
            R.id.message -> {
                // Handle menu item 2 click
                startActivity(Intent(requireContext(),MessageActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
            val user:User = it.toObject<User>()!!


            if (!user.image.isNullOrEmpty()){
                Picasso.get().load(user.image).into(binding.myImage)
            }
        }
    }
}