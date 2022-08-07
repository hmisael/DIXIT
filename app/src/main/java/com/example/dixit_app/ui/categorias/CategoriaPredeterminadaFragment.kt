package com.example.dixit_app.ui.categorias

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentCategoriaPredeterminadaBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Categoria
import com.example.dixit_app.repository.PictogramaRepository
import com.example.dixit_app.ui.pictogramas.PictogramaImportadoAdapter
import com.example.dixit_app.ui.pictogramas.PictogramaSeleccionAdapter
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory
import java.io.File
import java.io.FileOutputStream


class CategoriaPredeterminadaFragment : Fragment(R.layout.fragment_categoria_predeterminada) {

    val PICK_IMAGE = 1
    private var _binding: FragmentCategoriaPredeterminadaBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentCategoria: Categoria

    private lateinit var contexto: Context

    private lateinit var listaUri: ArrayList<Uri>

    //Instancia contexto de navegación de este Fragment
    private val args: CategoriaPredeterminadaFragmentArgs by navArgs()

    //un View Model para Pictogramas.
    private lateinit var pictogramaViewModel: PictogramaViewModel

private lateinit var imagenUri : Uri




    //Adapter de pictoramas seleccionados desde almacenamiento
    private lateinit var pictogramaAdapter: PictogramaImportadoAdapter





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
        _binding = FragmentCategoriaPredeterminadaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application

        val pictogramaRepository = PictogramaRepository(
                DixitDatabase(application)
        )

        val viewModelProviderFactory =
            PictogramaViewModelFactory(application, pictogramaRepository)

        pictogramaViewModel = ViewModelProvider(
                this, viewModelProviderFactory).get(PictogramaViewModel::class.java)

        //Capturar la entidad Categoria de la elección de la lista Categorias
        currentCategoria = args.categoria!!

        //pictogramaAdapter = PictogramaImportadoAdapter()


        //OJJO ACA ANULO ADAPTER PARA HACERLO DESPUES MANDANDOLE COSO
       //*************************






        binding.btnAbrirGaleria.setOnClickListener {
            //cargarImagenes()

            //NUEVO------------------------------------------------------
            val intent = Intent (Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            imagenesLauncher.launch(intent)
            //NUEVO------------------------------------------------------
        }

    }


    //NUEVO



        private var imagenesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data : Intent? = result.data
                //val extras = data?.clipData
                val extras = data?.clipData
                //val uris= ArrayList<Uri>()

                val uris : LinkedHashSet<Uri> = LinkedHashSet()
                if (extras != null){
                    for (i in 0 until extras.itemCount) {
                        val imageUri : Uri = extras.getItemAt(i).uri
                        if (imageUri != null){uris.add(imageUri)}

                        //val nombre = extras.getItemAt(i).text
                        Log.i("PRUEBA","BUCLE 1 size: "+uris.size+" el uri: "+imageUri+"y nombre: "+extras.getItemAt(i).text)
                    }
                }
                else {
                    val datan : Intent? = result.data

                    val imageUri = datan?.data!!
                    uris.add(imageUri)
                    Log.i("PRUEBA","BUCLE 2 size: "+uris.size)
                }
                binding.pictogramasPredeterminadosRV.adapter = PictogramaImportadoAdapter(uris)
            }
    }














    /*
    private fun guardarImagenes(){

        //Recorrer el
        val contador = pictogramaAdapter.bitmapList.size
        for (i in 0..contador - 1) {

            rutaImagen= guardarImagenDCIM(
                binding.root,
                this.requireContext(),
                //Necesito una lista de Bitmap solo accesible desde Adapter
                pictogramaAdapter.bitmapList[i],
                //Necesito armar la lista de pictogramas desde este Fragment
                listaPictogramas[i]!!.nombrePictograma
            )
            //Guardo el pictograma en ROOM
            val pictograma = Pictograma(0, listaPictogramas[i]!!.nombrePictograma, rutaImagen, currentCategoria.nombreCategoria)
            pictogramaViewModel.insertPictograma(pictograma)
        }


        //MODIFICACIÓN PARA GUARDAR LA IMAGEN EN ALMACENAMIENTO FÍSICO
        //Previamente, se ejecutaba sentencia sin guardarla en val id
        //la variable "id" me sirve para obtener el último id registrado en Room




*/






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





    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        //inflate layout XML
        inflater.inflate(R.menu.menu_guardar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //accedo al layout a través de su id
            R.id.menu_guardar -> {
                //Comprobar permisos
                //checkForPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, "WRITE", WRITE)
                //HOY estaba esto
                //savePictograma(mView)
                //guardarImagenes()
                val direction =
                    CategoriaPredeterminadaFragmentDirections
                        .actionCategoriaPredeterminadaFragmentToCategoriaDetalleFragment(currentCategoria)
                view?.findNavController()?.navigate(direction)
            }
        }
        return super.onOptionsItemSelected(item)
    }






    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }





}