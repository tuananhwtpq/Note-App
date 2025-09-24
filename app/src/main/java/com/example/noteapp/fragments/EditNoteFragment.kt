package com.example.noteapp.fragments

import android.app.AlertDialog
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
import androidx.navigation.fragment.navArgs
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentEditNoteBinding
import com.example.noteapp.model.Note
import com.example.noteapp.viewmodel.NoteViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.getValue

@RequiresApi(Build.VERSION_CODES.O)
class EditNoteFragment : Fragment(), MenuProvider {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note

    private var isNoteUpdated = false

    private val args: EditNoteFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!

        binding.edTitle.setText(currentNote.title)
        binding.edContent.setText(currentNote.content)

        binding.floatingActionButton.setOnClickListener {
            updateNote()
        }

    }

    private fun updateNote() {
        val noteTitle = binding.edTitle.text.toString().trim()
        val noteContent = binding.edContent.text.toString().trim()

        if (noteTitle.isEmpty()) {
            Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm - d MMMM, yyyy")
        val formattedTime = currentDateTime.format(formatter)

        val note = Note(currentNote.id, noteTitle, noteContent, formattedTime)

        notesViewModel.updateNote(note)
        isNoteUpdated = true
        Toast.makeText(context, "Note updated successfully", Toast.LENGTH_SHORT).show()
        view?.findNavController()?.popBackStack()
    }

    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete note")
            setMessage("Do you want to delete this note?")
            setPositiveButton("Delete") { _, _ ->
                notesViewModel.deleteNote(currentNote)
                Toast.makeText(requireContext(), "Note deleted successfully", Toast.LENGTH_SHORT)
                    .show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)

            }
            setNegativeButton("Cannel", null)
        }.create().show()

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.deleteMenu -> {
                deleteNote()

                true
            }

            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!isNoteUpdated) {
            val noteTitle = binding.edTitle.text.toString().trim()
            val noteContent = binding.edContent.text.toString().trim()

            val hasChanged = noteTitle != currentNote.title || noteContent != currentNote.content
            if (hasChanged && noteTitle.isNotEmpty()) {
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy")
                val formattedTime = currentDateTime.format(formatter)

                val note = Note(currentNote.id, noteTitle, noteContent, formattedTime)
                notesViewModel.updateNote(note)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}