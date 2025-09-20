package com.example.noteapp.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentAddNoteBinding
import com.example.noteapp.model.Note
import com.example.noteapp.viewmodel.NoteViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class AddNoteFragment : Fragment(), MenuProvider {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var addNoteView: View

    private var isNoteSaved = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        addNoteView = view

        binding.floatingActionButton.setOnClickListener {
            saveNote(addNoteView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveNote(view: View) {
        val noteTitle = binding.edTitle.text.toString().trim()
        val noteContent = binding.edContent.text.toString().trim()

        if (noteTitle.isEmpty()) {
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (noteTitle.length > 120) {
            Toast.makeText(
                requireContext(),
                "Title cannot be longer than 120 characters",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (noteContent.isEmpty()) {
            Toast.makeText(requireContext(), "Content is required", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy")
        val formattedTime = currentDateTime.format(formatter)

        val note = Note(0, noteTitle, noteContent, formattedTime)
        notesViewModel.addNote(note)
        isNoteSaved = true

        Toast.makeText(requireContext(), "Note saved successfully", Toast.LENGTH_SHORT).show()
        addNoteView.findNavController().popBackStack()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_note, menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.saveMenu -> {
                saveNote(addNoteView)
                true
            }

            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!isNoteSaved) {
            val noteTitle = binding.edTitle.text.toString().trim()
            val noteContent = binding.edContent.text.toString().trim()

            if (noteTitle.isNotEmpty() && noteContent.isNotEmpty() && noteTitle.length <= 120) {
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy")
                val formattedTime = currentDateTime.format(formatter)

                val note = Note(0, noteTitle, noteContent, formattedTime)
                notesViewModel.addNote(note)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}