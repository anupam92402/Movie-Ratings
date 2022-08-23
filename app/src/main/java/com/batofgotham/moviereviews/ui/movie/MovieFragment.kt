package com.batofgotham.moviereviews.ui.movie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.batofgotham.moviereviews.R
import com.batofgotham.moviereviews.data.model.MovieEntity
import com.batofgotham.moviereviews.databinding.FragmentMovieBinding
import com.batofgotham.moviereviews.utils.BottomDialogInterface
import com.batofgotham.moviereviews.utils.Utils
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w200"

@AndroidEntryPoint
class MovieFragment : Fragment(), BottomDialogInterface {

    private val TAG = this.javaClass.simpleName

    private var _binding: FragmentMovieBinding? = null

    private val binding: FragmentMovieBinding
        get() = _binding!!

    private lateinit var detailBottomSheet: ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ViewGroup>

    private lateinit var pagingAdapter: MovieAdapter

    private lateinit var viewReference: View

    private val viewModel: MovieViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMovieBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupDetailBottomSheet()
        viewReference = view
        val recyclerView = binding.moviesRecyclerView
        val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager
        gridLayoutManager.spanCount = 3

        pagingAdapter = MovieAdapter(this)
        recyclerView.adapter = pagingAdapter

        // Activities can use lifecycleScope directly, but Fragments should instead use
        // viewLifecycleOwner.lifecycleScope.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getMovies().asFlow().collectLatest {
                pagingAdapter.submitData(it)
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collectLatest { loadStates ->
                Log.i(TAG,"Load State: ${loadStates.refresh}")
                when(loadStates.refresh){
                    is LoadState.Error -> showNetworkError()
                    is LoadState.Loading -> {
                        showProgressBar()
                    }
                    is LoadState.NotLoading -> {
                        showData()
                    }
                }
            }
        }

        binding.retryButton.setOnClickListener {
            pagingAdapter.refresh()
        }

        binding.swipeRefresh.setOnRefreshListener {
            pagingAdapter.refresh()
        }

    }

    private fun showNetworkError(){
        Log.i("MovieFragment","Show Network Error Called")
        binding.moviesRecyclerView.visibility = View.GONE
        binding.detailBottomSheetContainer.visibility = View.GONE
        binding.networkErrorView.visibility = View.VISIBLE
    }

    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
        binding.moviesRecyclerView.visibility = View.GONE
        binding.detailBottomSheetContainer.visibility = View.GONE
    }

    private fun showData(){
        binding.moviesRecyclerView.visibility = View.VISIBLE
        binding.detailBottomSheetContainer.visibility = View.VISIBLE
        binding.networkErrorView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.swipeRefresh.isRefreshing = false
    }

    private fun setupDetailBottomSheet(){
        detailBottomSheet = binding.detailBottomSheetContainer
        bottomSheetBehavior = BottomSheetBehavior.from(detailBottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        hideBottomSheet()
    }

    private val bottomSheetCallback = object: BottomSheetBehavior.BottomSheetCallback(){
        override fun onStateChanged(bottomSheet: View, newState: Int) {
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    private fun showBottomSheet(){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideBottomSheet(){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onMovieSelected(movie: MovieEntity?) {
        bottomSheetBehavior.state=BottomSheetBehavior.STATE_COLLAPSED
        loadBottomDialog(movie)
    }

    private fun loadBottomDialog(movie: MovieEntity?) {
        movie?.apply {
            binding.titleTextView.text = title
            binding.posterImageView.setImageResource(R.drawable.ic_launcher_background)
            binding.releaseYearTextView.text = Utils.getYear(Utils.getDate(releaseDate)).toString()
            binding.overviewTextView.text = overview
            binding.ivOpen.setOnClickListener {
                Navigation.findNavController(viewReference)
                    .navigate(R.id.action_movieFragment_to_detailScreenFragment)
            }
            binding.ratingTextView.text = voteAverage.toString()
        }

        val posterUrl = IMAGE_BASE_URL + movie?.posterPath

        Glide.with(requireContext())
            .load(posterUrl)
            .into(binding.posterImageView)
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}