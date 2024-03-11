package com.example.patron.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.patron.Adapter.ReelAdapter
import com.example.patron.Model.Reel
import com.example.patron.databinding.FragmentReelBinding
import com.example.patron.utils.REEl
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class ReelFragment : Fragment() {

    private lateinit var binding:FragmentReelBinding
    lateinit var adapter: ReelAdapter
    var reelList = ArrayList<Reel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReelBinding.inflate(inflater, container, false)
        adapter = ReelAdapter(requireContext(),reelList)
        binding.viewPager.adapter=adapter

        Firebase.firestore.collection(REEl).get().addOnSuccessListener {
            var tempList = ArrayList<Reel>()
            reelList.clear()

            for (i in it.documents){
                var reel = i.toObject<Reel>()!!
                tempList.add(reel)
            }
            reelList.addAll(tempList)
            reelList.reverse()
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    companion object {

    }
}