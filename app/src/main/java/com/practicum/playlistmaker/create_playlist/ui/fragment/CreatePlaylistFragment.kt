package com.practicum.playlistmaker.create_playlist.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.practicum.playlistmaker.create_playlist.ui.view_model.CreatePlaylistViewModel
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : BindingFragment<FragmentCreatePlaylistBinding>() {

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private var uriImgPlaylist: Uri? = null
    private var absoluteImgPath: String? = null

    private fun saveImageToPrivateStorage(uri: Uri?) {
        if (uri == null) return
        //создаём экземпляр класса File, который указывает на нужный каталог
        val imgPath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        absoluteImgPath = imgPath.absolutePath
        //создаем каталог, если он не создан
        if (!imgPath.exists()) {
            imgPath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val img = File(imgPath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(img)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }


    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.imgPlaylist.setImageURI(uri)
                uriImgPlaylist = uri
            }
        }

        binding.imgPlaylist.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylist.setOnClickListener {
            if (uriImgPlaylist != null) {
                saveImageToPrivateStorage(uriImgPlaylist)
                viewModel.savePlaylist(absoluteImgPath, binding.namePlaylist.text.toString(), binding.descriptionPlaylist.text.toString())
                Toast.makeText(requireContext(), "Плейлист создан!", Toast.LENGTH_LONG).show()
            }
        }
    }

}
