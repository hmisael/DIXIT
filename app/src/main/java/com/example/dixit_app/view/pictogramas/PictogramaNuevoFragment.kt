package com.example.dixit_app.view.pictogramas

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.documentfile.provider.DocumentFile

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dixit_app.view.CategoriasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentPictogramaNuevoBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Categoria
import com.example.dixit_app.model.entities.Pictograma
import com.example.dixit_app.model.repository.PictogramaRepository

import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.io.*


class PictogramaNuevoFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentPictogramaNuevoBinding

    private lateinit var pictogramaViewModel: PictogramaViewModel

    private val args: PictogramaNuevoFragmentArgs by navArgs()
    private lateinit var categoria: Categoria

    //variable URI dirección almacenamiento físico de la imagen (revisar definición)
    private var imagenUri: Uri? = null
    private var rutaImagen = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                savedInstanceState: Bundle?): View {
        // Inflar layout del fragment
        binding = FragmentPictogramaNuevoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, lifecycle.currentState)

        (activity as CategoriasActivity).supportActionBar?.title = getString(R.string.agregar_pictograma)

        //Capturo argumento (nombre de Categoria) del fragmento definido en su Navigation
        categoria = args.categoria!!

        //Data Binding de layout
        val application = requireNotNull(this.activity).application
        val pictogramaRepository = PictogramaRepository(DixitDatabase(application))

        //Configuro viewmodel
        val viewModelProviderFactory = PictogramaViewModelFactory(application, pictogramaRepository)
        pictogramaViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory).get(PictogramaViewModel::class.java)


        //TODO chequear los comentarios siguientes:
        //Aclaración: estas líneas crean un archivo temp y son necesarias para que cámara funcione...
        //Nota: los archivos se titulan "temp...etc"
        //Nota 2: los temp de las fotos quedan, los de galería son de 0 bytes
        imagenUri = FileProvider.getUriForFile(this.requireContext(),
            "com.example.dixit_app.provider",  crearImagen().also {
                rutaImagen = it.absolutePath
            })

        //GALERIA

        val seleccionarImagen = registerForActivityResult(ActivityResultContracts.GetContent()){
            Glide.with(binding.imagenPictograma.context)
                .load(it)
                .centerCrop()
                .into(binding.imagenPictograma)

            val fileName = DocumentFile.fromSingleUri(requireContext(), it!!)!!.name!!
            binding.pictogramaTxt.setText(fileName.substring(0, fileName.lastIndexOf('.')))
        }

        //CAMARA

        val abrirCamara = registerForActivityResult(ActivityResultContracts.TakePicture()){ ok ->
            if (ok) {
                Glide.with(requireContext())
                    .load(imagenUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imagenPictograma)
            }
        }

        //Click Listener del campo imagen para cargarle una foto almacenada o de cámara
        binding.imagenPictograma.setOnClickListener{
            //requestPermission(view)//solicitud de permisos
            val pictureDialog = AlertDialog.Builder(this.requireContext())
            pictureDialog.setTitle("Agregar pictograma desde:")
            val pictureDialogItem = arrayOf("Galería de imágenes", "Cámara")
            //Abrir dialog con dos opciones: abrir cámara o seleccionar de galería
            pictureDialog.setItems(pictureDialogItem){ _, which->
                when (which){
                    0 -> seleccionarImagen.launch("image/*")//checkForPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, "READ", READ)
                    1 -> abrirCamara.launch(imagenUri)//checkForPermissions(Manifest.permission.CAMERA, "CAMERA", CAMERA)
                }
            }
            pictureDialog.show()
        }
    }

    private fun crearImagen(): File {
        //Ver Provider (file_paths.xml), siendo el directorio para temps: files/Pictures
        val directorio = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_imagen", ".jpeg", directorio)
    }

    private fun savePictograma(){
        //Guardar la imagen del ImageView en almacenamiento interno y la entidad Pictograma en ROOM
        val nombrePictograma = binding.pictogramaTxt.text.toString().trim()
        if (nombrePictograma.isNotEmpty()) {//Chequear que
            //Instancia de Pictograma con sus datos
            val bitmapDrawable = binding.imagenPictograma.drawable
            val bitmap = bitmapDrawable.toBitmap()
            rutaImagen = guardarImagenDCIM(
                        this.requireContext(),
                        bitmap,
                        nombrePictograma)

            val pictograma = Pictograma(0, nombrePictograma, rutaImagen, categoria.nombreCategoria)

            pictogramaViewModel.insertPictograma(pictograma)

            Log.i("PRUEBA","URI DE A UNO"+rutaImagen)

            Snackbar.make(
                requireView(), "Pictograma guardado exitosamente",
                Snackbar.LENGTH_SHORT
            ).show()

        } else {
            Toast.makeText(context, "Por favor ingrese nombre de Pictograma" , Toast.LENGTH_SHORT).show()

        }
    }

    private fun guardarImagenDCIM(context: Context, bmap: Bitmap, name: String): String {
        try{
            val archivoPath = File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), name + ".jpeg")
            val fos = FileOutputStream(archivoPath)
            bmap.compress(Bitmap.CompressFormat.JPEG, 50, fos)
            return archivoPath.absolutePath
        }
        catch(e: IOException) {
            e.printStackTrace()
            return ("NO se pudo")
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_guardar, menu)
    }

   override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId){
            //accedo al layout a través de su id
            R.id.menu_guardar -> {
                    savePictograma()
                    val direction =
                        PictogramaNuevoFragmentDirections.actionPictogramaNuevoFragmentToCategoriaDetalleFragment(
                            categoria
                        )
                    requireView().findNavController().navigate(direction)

                true
            }
            //Otro when si se agrega otro botón
            else -> false
        }
    }

    /*
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }*/

}