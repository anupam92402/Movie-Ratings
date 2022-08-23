package com.batofgotham.moviereviews.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.batofgotham.moviereviews.R
import com.batofgotham.moviereviews.databinding.FragmentDetailScreenBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DetailScreenFragment : BottomSheetDialogFragment() {

    companion object{
        val TAG = "DetailScreenFragment"
    }

    private lateinit var binding: FragmentDetailScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetailScreenBinding.inflate(inflater,container,false)

        binding.titleTextView.text = "Doctor Strange: Multiverse of Madness"
        binding.posterImageView.setImageResource(R.drawable.ic_launcher_background)
        binding.releaseYearTextView.text = "2022"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        val bottomSheetBehavior = BottomSheetBehavior.from(binding.detailBottomSheetContainer)
//
//        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.GONE
    }


    private val bottomSheetCallback = object: BottomSheetBehavior.BottomSheetCallback(){
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            Log.i(TAG,newState.toString())
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            Log.i(TAG,"Sliding...")
        }
    }

}