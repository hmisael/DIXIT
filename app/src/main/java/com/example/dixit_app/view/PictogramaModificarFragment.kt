package com.example.dixit_app.view

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentPictogramaModificarBinding
import com.example.dixit_app.helper.toast
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.repository.PictogramaRepository
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory
import java.io.File


class PictogramaModificarFragment : Fragment(R.layout.fragment_pictograma_modificar) {

    private var _binding: FragmentPictogramaModificarBinding? = null
    private val binding get() = _binding!!

    private val args: PictogramaModificarFragmentArgs by navArgs()
    private lateinit var currentPictograma: Pictograma
    private lateinit var pictogramaViewModel: PictogramaViewModel

    //variable URI dirección almacenamiento físico de la imagen (revisar definición)
    private var imagenUri: Uri? = null
    private var tempImageFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        // Inflar layout del fragment
        _binding = FragmentPictogramaModificarBinding.inflate(inflater, container,false)
        return binding.root
    }

    //Funciones para abrir seleccionar imagen de galería
    private val seleccionarImagen = registerForActivityResult(ActivityResultContracts.GetContent()){
        //binding.imagenPictogramaMod.setImageURI(it)
        Glide.with(binding.imagenPictogramaMod.context)
            .load(it)
            .centerCrop()
            .into(binding.imagenPictogramaMod)
    }

    fun seleccionarGaleria() {
        requestPermission(binding.root)
        seleccionarImagen.launch("image/*")
    }

    //Funciones para abrir cámara y tomar fotografía
    private val abrirCamara = registerForActivityResult(ActivityResultContracts.TakePicture()){ ok ->
        if (ok) {
            binding.imagenPictogramaMod.setImageURI(imagenUri)
        }
    }

    fun seleccionarCamara(){
        imagenUri = FileProvider.getUriForFile(this.requireContext(),
            "com.example.dixit_app.provider", crearImagen().also {
                tempImageFilePath = it.absolutePath
            })
        abrirCamara.launch(imagenUri)
    }

    private fun crearImagen():File{
        //val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        /*val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_imagen", ".jpg", storageDir)*/
        val directorio = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imagen = File.createTempFile("temp_imagen", ".jpg", directorio)
        return imagen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Capturo argumento (nombre de Categoria) del fragmento definido en su Navigation
        currentPictograma = args.pictograma!!
        //Data Binding de layout
        val application = requireNotNull(this.activity).application
        val pictogramaRepository = PictogramaRepository(DixitDatabase(application))

        //Configuro viewmodel
        val viewModelProviderFactory =
                PictogramaViewModelFactory(application, pictogramaRepository)
        pictogramaViewModel = ViewModelProvider(
                this,
                viewModelProviderFactory)
                .get(PictogramaViewModel::class.java)

        //Cargar datos del pictograma a cada view
        binding.pictogramaModTxt.setText(currentPictograma.nombrePictograma)
        //Con campo "nombre"+jpeg cargo imagen almacenada en el dispositivo invocando path
        val archivoPath = File(context?.getExternalFilesDir(Environment.DIRECTORY_DCIM), currentPictograma.nombrePictograma + ".jpeg")
        if (archivoPath.exists()) {//Si encontró el archivo
            val bitmap = BitmapFactory.decodeFile(archivoPath.absolutePath)
            binding.imagenPictogramaMod.setImageBitmap(bitmap)
        }
        else {//Si no encontró el archivo, asigno una imagen random
            binding.imagenPictogramaMod.setImageURI(Uri.parse("android.resource://com.example.dixit_app/drawable/placeholder"))
        }

        //CHECK: Salida URI almacenada
        Toast.makeText(requireContext(),"Imagen ROOM:"+currentPictograma.imagen, Toast.LENGTH_LONG).show()

        //Click listener para modificar la imagen de pictograma
        binding.imagenPictogramaMod.setOnClickListener{
            //requestPermission(view)//solicitud de permisos
            val pictureDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
            pictureDialog.setTitle("Modificar imagen desde:")
            val pictureDialogItem = arrayOf("Galería de imágenes", "Abrir cámara")
            //Abrir dialog con dos opciones: abrir cámara o seleccionar de galería
            pictureDialog.setItems(pictureDialogItem){ dialog, which->
                when (which){
                    0 -> requestPermissionGaleria(view)//Solicitar permisos para abrir galería
                    1 -> requestPermission(view)//Solicitar permisos para abrir cámara
                }
            }
            pictureDialog.show()
        }

        //DIFERENCIA: PictogramaNuevoFragment tiene botón en barra superior
        //Click Listener de botón flotante (FAB) para Guardar
        binding.fabDoneModPictograma.setOnClickListener{
            val nombrePictograma = binding.pictogramaModTxt.text.toString().trim()
            //Se puede guardar solo si se ingresó un nombre para el Pictograma
            if (nombrePictograma.isNotEmpty()) {
                //El pictograma solo puede cambiar el nombrePictograma... por ahora (debe cambiar también imagen)
                val pictograma = Pictograma(currentPictograma.idPictograma, nombrePictograma, currentPictograma.imagen, currentPictograma.nombreCategoria)
                pictogramaViewModel.updatePictograma(pictograma)

                //ACA FALTA BORRAR FOTO URI VIEJA Y AGREGAR UNA FOT O URI NUEVA

                view.findNavController().navigate(R.id.action_pictogramaModificarFragment_to_categoriaDetalleFragment)

            } else {
                activity?.toast("Ingrese un nombre de Categoría por favor")
            }
        }
    }


    //Permisos
    private fun requestPermissionGaleria(view: View) {
        //Nivel de API (M es Marshmallow para API 23)
        when {
            ContextCompat.checkSelfPermission(
                view.rootView.context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(requireContext(),
                    "Permisos de lectura habilitados",
                    Toast.LENGTH_SHORT).show()
                //Solo si los permisos están habilitados, procedo a abrir la galería
                seleccionarGaleria()
            }
            //Si shouldShowRequestPermissionRationale() es TRUE, explicar al usuario por qué el necesario el permiso
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ) -> {
                Toast.makeText(requireContext(),
                    "Permisos de escritura habilitado",
                    Toast.LENGTH_LONG).show()
                requestPermissionLauncherGaleria.launch(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
            //Si falta habilitar el permiso, lo solicito a través de val requestPermissionLauncher
            else -> {
                requestPermissionLauncherGaleria.launch(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
        }
    }

    //Solicitud de permisos a través de un diálogo (-> expresión lambda)
    private val requestPermissionLauncherGaleria =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ isGranted ->
            isGranted.entries.forEach { //Al aceptar los permisos
                //Log.e("DEBUG", "${it.key} = ${it.value}")
                seleccionarGaleria()
            }
        }

    private fun requestPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                view.rootView.context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(requireContext(),
                    "Permisos de cámara habilitados",
                    Toast.LENGTH_LONG).show()
                //Solo si los permisos están habilitados, procedo a abrir la cámara
                seleccionarCamara()
            }
            //Si shouldShowRequestPermissionRationale() es TRUE, explicar al usuario por qué el necesario el permiso
            /*ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CAMERA
            ) -> {
                /*
                Toast.makeText(requireContext(),
                    "Permisos de escritura habilitado",
                    Toast.LENGTH_LONG).show() */
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }*/
            //Si falta habilitar el permiso, lo solicito a través de val requestPermissionLauncher
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    //Solicitud de permisos a través de un diálogo (-> expresión lambda)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted: Boolean ->
            if (isGranted) { //Al aceptar los permisos
                Toast.makeText(activity, "Permisos habilitados", Toast.LENGTH_LONG).show()
                //Brindar más información, si el usuario rechaza el permiso por primera vez
                //es ideal enviar a configuración
                //shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                seleccionarCamara()
            } else {//Al rechazar los permisos
                Toast.makeText(activity, "Permisos denegados", Toast.LENGTH_LONG).show()
            }
        }

    private fun deletePictograma() {
        AlertDialog.Builder(activity).apply {
            setTitle("Eliminar Pictograma")
            setMessage("¿Está seguro que desea eliminar este Pictograma?")
            setPositiveButton("ELIMINAR") { _, _ ->
                //Eliminar imagen asociada al URI de almacenamiento físico
                //val file = File(context?.filesDir, currentPictograma.imagen)
                val file = File(requireContext().filesDir, currentPictograma.imagen)
                file.delete()
                //Eliminar Pictograma de BD ROOM
                pictogramaViewModel.deletePictograma(currentPictograma)

                view?.findNavController()?.navigate(
                        R.id.action_pictogramaModificarFragment_to_categoriaDetalleFragment
                )
            }
            setNegativeButton("CANCELAR", null)
        }.create().show()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_borrar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                deletePictograma()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
