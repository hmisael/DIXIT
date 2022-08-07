package com.example.dixit_app.ui.pictogramas

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.dixit_app.CategoriasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentPictogramaNuevoBinding
import com.example.dixit_app.helper.toast
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.repository.PictogramaRepository

import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.io.*


class PictogramaNuevoFragment : Fragment(R.layout.fragment_pictograma_nuevo) {

    private var _binding: FragmentPictogramaNuevoBinding? = null
    private val binding get() = _binding!!

    private lateinit var pictogramaViewModel: PictogramaViewModel
    private lateinit var mView: View

    private val args: PictogramaNuevoFragmentArgs by navArgs()
    private lateinit var categoriaNombre: String

    //variable URI dirección almacenamiento físico de la imagen (revisar definición)
    private var imagenUri: Uri? = null
    private var rutaImagen = ""

    //Gestión de permisos
    val CAMERA = 100
    val READ = 101
    val WRITE = 102

    //CODIGO BOTONES


    private fun checkForPermissions(permission: String, name: String, requestCode: Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            when {
                checkSelfPermission(
                        this.requireContext(), permission) == PackageManager.PERMISSION_GRANTED ->{
                            Toast.makeText(this.requireContext(), "$name : permisos garantizados", Toast.LENGTH_SHORT).show()
                            when (name){
                                "CAMERA" -> seleccionarCamara()
                                "READ" -> seleccionarGaleria()
                                "WRITE" -> savePictograma(mView)
                                else -> Toast.makeText(context, "No sé qué pasó", Toast.LENGTH_SHORT).show()
                            }
                        }
                        shouldShowRequestPermissionRationale(permission) -> showDialog(permission, name, requestCode)
                        else -> ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(permission),requestCode)
                }
            }
        }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        fun innerCheck(name: String){
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(context, "Permisos $name rechazados", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context, "Permisos $name garantizados", Toast.LENGTH_SHORT).show()
            }
        }

        when (requestCode){
            CAMERA -> innerCheck("CAMERA")
            READ -> innerCheck("READ")
            WRITE -> innerCheck("WRITE")
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int){
        val builder = AlertDialog.Builder(this.requireContext())
        builder.apply {
            setMessage("Se requiere el permiso de $name para usar esta función")
            setTitle("Solicitud de permisos")
            setPositiveButton("OK"){
                dialog, which ->
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission),requestCode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar layout del fragment
        _binding = FragmentPictogramaNuevoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as CategoriasActivity).supportActionBar?.title = getString(R.string.agregar_pictograma)

        //Capturo argumento (nombre de Categoria) del fragmento definido en su Navigation
        categoriaNombre = args.categoriaNombre!!
        //Data Binding de layout
        val application = requireNotNull(this.activity).application
        val pictogramaRepository = PictogramaRepository(DixitDatabase(application))

        //Configuro viewmodel
        val viewModelProviderFactory =
            PictogramaViewModelFactory(application, pictogramaRepository)
        pictogramaViewModel = ViewModelProvider(
            this, viewModelProviderFactory
        )
            .get(PictogramaViewModel::class.java)

        //Click Listener del campo imagen para cargarle una foto almacenada o de cámara
        binding.imagenPictograma.setOnClickListener{
        //requestPermission(view)//solicitud de permisos
            val pictureDialog = AlertDialog.Builder(this.requireContext())
            pictureDialog.setTitle("Nueva imagen desde:")
            val pictureDialogItem = arrayOf("Galería de imágenes", "Abrir cámara")
            //Abrir dialog con dos opciones: abrir cámara o seleccionar de galería
            pictureDialog.setItems(pictureDialogItem){ dialog, which->
                when (which){
                    0 -> checkForPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, "READ", READ)
                    1 -> checkForPermissions(Manifest.permission.CAMERA, "CAMERA", CAMERA)
                }
            }
            pictureDialog.show()
        }
        mView = view
    }

    //*****Seleccionar imagen de galería*****
    private val seleccionarImagen = registerForActivityResult(ActivityResultContracts.GetContent()){
        Glide.with(binding.imagenPictograma.context)
                .load(it)
                .centerCrop()
                .into(binding.imagenPictograma)
    }

    fun seleccionarGaleria() {
        //requestPermission(binding.root)
        seleccionarImagen.launch("image/*")
    }

    //*****Abrir cámara y tomar fotografía*****
    private val abrirCamara = registerForActivityResult(ActivityResultContracts.TakePicture()){ ok ->
        if (ok) {
            binding.imagenPictograma.setImageURI(imagenUri)
        }
    }

    fun seleccionarCamara(){
        imagenUri = FileProvider.getUriForFile(this.requireContext(),
                "com.example.dixit_app.provider", crearImagen().also {
            rutaImagen = it.absolutePath
        })
        abrirCamara.launch(imagenUri)
    }

    private fun crearImagen():File{
        //Ver Provider (file_paths.xml), siendo el directorio para temps: files/Pictures
        val directorio = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imagen = File.createTempFile("temp_imagen", ".jpeg", directorio)
        return imagen
    }

    //*****Solicitud de permisos a través de un diálogo (-> expresión lambda)*****

    //NEW
    /*private val requestPermissionLauncherGaleria =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ isGranted ->
        isGranted.entries.forEach { //Al aceptar los permisos
            //Log.e("DEBUG", "${it.key} = ${it.value}")
            seleccionarGaleria()
        }
    }*/

    //Solicitud de permisos a través de un diálogo (-> expresión lambda)
    //NEW
    /*
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
        */

    // Guardar el pictograma
    private fun savePictograma(view: View){
        //Guardar la imagen del ImageView en almacenamiento interno y la entidad Pictograma en ROOM

        //Capturo el texto de TextView
        val nombrePictograma = binding.pictogramaTxt.text.toString().trim()
        //A partir de acá se pone pantanoso el asunto :)
        if (nombrePictograma.isNotEmpty()) {//Chequear que
            //Instancia de Pictograma con sus datos
            val bitmapDrawable = binding.imagenPictograma.drawable
            val bitmap = bitmapDrawable.toBitmap()

            rutaImagen= guardarImagenDCIM(
                binding.root,
                this.requireContext(),
                bitmap,
                nombrePictograma
            )
            val pictograma = Pictograma(0, nombrePictograma, rutaImagen, categoriaNombre)

            //MODIFICACIÓN PARA GUARDAR LA IMAGEN EN ALMACENAMIENTO FÍSICO
            //Previamente, se ejecutaba sentencia sin guardarla en val id
            //la variable "id" me sirve para obtener el último id registrado en Room
            pictogramaViewModel.insertPictograma(pictograma)

            Snackbar.make(
                view, "Pictograma guardado exitosamente",
                Snackbar.LENGTH_SHORT
            ).show()
            view.findNavController().navigate(R.id.action_pictogramaNuevoFragment_to_categoriaDetalleFragment)
        } else {
            activity?.toast("Por favor ingrese nombre de Pictograma")
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        //inflate layout XML
        inflater.inflate(R.menu.menu_guardar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //accedo al layout a través de su id
            R.id.menu_guardar -> {
                //Comprobar permisos
                checkForPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, "WRITE", WRITE)
                //HOY estaba esto
            //savePictograma(mView)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //fun guardarImagenDCIM(view: View, context: Context, bmap: Bitmap, name: String): File? {
    fun guardarImagenDCIM(view: View, context: Context, bmap: Bitmap, name: String): String {
        //Ver Provider (file_paths.xml), siendo directorio imágenes guardadas: files/DCIM
        val archivoPath = File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), name + ".jpeg")
        //val fos: FileOutputStream
        var fos: FileOutputStream? = null

        //Solo si los permisos están habilitados, procedo a abrir la galería
        fos = FileOutputStream(archivoPath)
        //Usar método de compresión en el objeto Bitmap para escribir imagen en OutputStream
        bmap.compress(Bitmap.CompressFormat.JPEG, 50, fos)

        return archivoPath.absolutePath
    }

}