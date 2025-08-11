package com.practicum.playlistmaker.create_playlist.ui.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.create_playlist.ui.view_model.CreatePlaylistViewModel
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.playlist.ui.model.PlaylistUi
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

open class CreatePlaylistFragment : BindingFragment<FragmentCreatePlaylistBinding>() {

    open val viewModel by viewModel<CreatePlaylistViewModel>()

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private var simpleTextWatcher: TextWatcher? = null
    private var uriImgPlaylist: Uri? = null
    private var absoluteImgPath: String? = null

    protected fun saveImageToPrivateStorage(uri: Uri?) {
        if (uri == null) return
        //создаём экземпляр класса File, который указывает на нужный каталог
        val imgPath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "imgplaylists")
        //создаем каталог, если он не создан
        if (!imgPath.exists()) {
            imgPath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val img = File(imgPath, "img_${UUID.randomUUID()}")
        absoluteImgPath = img.absolutePath
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(img)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun isImageChanged(): Boolean {
        val currentDrawable = binding.imgPlaylist.drawable
        val defaultDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.plug_playlist)

        val currentBitmap = (currentDrawable as? BitmapDrawable)?.bitmap
        val defaultBitmap = (defaultDrawable as? BitmapDrawable)?.bitmap

        return currentBitmap != defaultBitmap
    }

    private fun handleBackPressed() {
        hideKeyboard()
        if (isImageChanged() || !binding.namePlaylist.text.isNullOrEmpty() || !binding.descriptionPlaylist.text.isNullOrEmpty()) {
            confirmDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val view = requireActivity().currentFocus ?: requireActivity().window.decorView
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        requireActivity().currentFocus?.clearFocus()
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            handleBackPressed()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { handleBackPressed() }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.createPlaylistDialogTitle)
            .setMessage(R.string.createPlaylistDialogDescription)
            .setNegativeButton(R.string.createPlaylistDialogCancel) { dialog, which -> }
                .setPositiveButton(R.string.createPlaylistDialogConfirm) { dialog, which ->
                findNavController().navigateUp()
            }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.imgPlaylist.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                binding.imgPlaylist.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                binding.imgPlaylist.setImageURI(uri)
                uriImgPlaylist = uri
            }
        }

        binding.imgFrame.setOnClickListener {
            hideKeyboard()
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylist.setOnClickListener {
            if (!binding.namePlaylist.text.isNullOrEmpty()) {
                saveImageToPrivateStorage(uriImgPlaylist)
                viewModel.savePlaylist(
                    absoluteImgPath,
                    binding.namePlaylist.text.toString(),
                    binding.descriptionPlaylist.text.toString()
                )
                Toast.makeText(requireContext(), "Плейлист ${binding.namePlaylist.text} создан!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Напишите название", Toast.LENGTH_SHORT).show()
            }
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.createPlaylist.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
                } else {
                    binding.createPlaylist.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.text_gray))
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        }

        simpleTextWatcher.let { binding.namePlaylist.addTextChangedListener(it) }

        binding.root.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        simpleTextWatcher.let { binding.namePlaylist.removeTextChangedListener(it) }
        simpleTextWatcher = null
    }

}
