package com.example.consolas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.consolas.databinding.FragmentDetailBinding
import com.example.consolas.dao.DaoConsolas

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = args.consolePosition
        val console = DaoConsolas.myDao.getDataConsoles()[position]

        Glide.with(this)
            .load(console.image)
            .into(binding.tvDetailImage)
        binding.tvDetailName.text = console.name
        binding.tvDetailCompany.text = console.company
        binding.tvDetailDate.text = console.releasedate
        binding.tvDetailDescription.text = console.description
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}