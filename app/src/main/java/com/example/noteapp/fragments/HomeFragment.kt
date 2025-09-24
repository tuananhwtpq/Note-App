package com.example.noteapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.adapter.NoteAdapter
import com.example.noteapp.databinding.FragmentHomeBinding
import com.example.noteapp.model.Note
import com.example.noteapp.viewmodel.NoteViewModel


class HomeFragment : Fragment(), SearchView.OnQueryTextListener, MenuProvider {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private var searchView: SearchView? = null

    private var isListLayout: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        setupHomeRecyclerView()

        binding.floatingActionButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addNoteFragment)
        }

        binding.ivChangeLayout.setOnClickListener { handleChangeLayoutBtn() }

    }

    private fun setLayout(){
        if (isListLayout){
            binding.rvAllNote.layoutManager = LinearLayoutManager(requireContext())
        } else {
            binding.rvAllNote.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun handleChangeLayoutBtn(){

        isListLayout = !isListLayout

        if (isListLayout){
            binding.ivChangeLayout.setImageResource(R.drawable.list_layout)
        } else{
            binding.ivChangeLayout.setImageResource(R.drawable.grid)
        }

        noteAdapter.isListView = isListLayout

        setLayout()
        noteAdapter.notifyDataSetChanged()
    }

    private fun updateUI(note: List<Note>){
        if (note != null){
            if (note.isNotEmpty()){
                binding.ivEmptyState.visibility = View.GONE
                binding.rvAllNote.visibility = View.VISIBLE
            }else{
                binding.ivEmptyState.visibility = View.VISIBLE
                binding.rvAllNote.visibility = View.GONE
            }
        }
    }

    private fun setupHomeRecyclerView(){
        noteAdapter = NoteAdapter()

        setLayout()

        binding.rvAllNote.apply {
            setHasFixedSize(true)
            adapter = noteAdapter
        }

        activity?.let {
            notesViewModel.notes.observe(viewLifecycleOwner) { note ->

                val oldSize = noteAdapter.itemCount

                noteAdapter.differ.submitList(note){
                    val newSize = note.size
                    if (newSize > oldSize){
                        binding.rvAllNote.scrollToPosition(0)
                    }

                }
                updateUI(note)
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            notesViewModel.searchNotes(newText)
        }
        return true
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)

        val menuSearch = menu.findItem(R.id.searchMenu)
        searchView = menuSearch.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = false
        searchView?.setOnQueryTextListener(this)

        searchView?.setOnCloseListener {
            notesViewModel.clearSearch()
            false
        }

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView = null
        _binding = null
    }

    override fun onResume() {
        super.onResume()
    }

}