package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapter.SavemarkAdapter
import com.dicoding.asclepius.data.local.entity.SavemarkEntity
import com.dicoding.asclepius.databinding.FragmentSavemarkBinding
import com.dicoding.asclepius.factory.ViewModelFactory
import com.dicoding.asclepius.viewmodel.SavemarkViewModel

class SavemarkFragment : Fragment() {
    private var _binding: FragmentSavemarkBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavemarkBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvSavemark.layoutManager = layoutManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(requireActivity().application)
        val viewModel = ViewModelProvider(this, factory)[SavemarkViewModel::class.java]

        viewModel.getAllSavemark().observe(viewLifecycleOwner) { item ->
            setSavemark(item)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun setSavemark(savemark: List<SavemarkEntity>) {
        val adapter = SavemarkAdapter()
        adapter.submitList(savemark)
        binding.rvSavemark.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}