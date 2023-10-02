package com.example.dixit_app.view.categorias

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentCategoriaPredeterminadaBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Categoria
import com.example.dixit_app.model.entities.Pictograma
import com.example.dixit_app.model.repository.PictogramaRepository
import com.example.dixit_app.view.CategoriasActivity
import com.example.dixit_app.view.pictogramas.PictogramaImportadoAdapter
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class CategoriaPredeterminadaFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentCategoriaPredeterminadaBinding

    private lateinit var currentCategoria: Categoria

    //Instancia contexto de navegación de este Fragment
    private val args: CategoriaPredeterminadaFragmentArgs by navArgs()

    //un View Model para Pictogramas.
    private lateinit var pictogramaViewModel: PictogramaViewModel

    var urisLista : ArrayList<Uri> = ArrayList()
    var stringsLista : ArrayList<String> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCategoriaPredeterminadaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as CategoriasActivity).supportActionBar?.title = getString(R.string.importar_pictogramas)


        //Menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, lifecycle.currentState)

        val application = requireNotNull(this.activity).application

        //Capturar la entidad Categoria de la elección de la lista Categorias
        currentCategoria = args.categoria!!

        val pictogramaRepository = PictogramaRepository(
            DixitDatabase(application)
        )

        val viewModelProviderFactory =
            PictogramaViewModelFactory(application, pictogramaRepository)

        pictogramaViewModel = ViewModelProvider(
            this, viewModelProviderFactory).get(PictogramaViewModel::class.java)


        binding.btnAbrirGaleria.setOnClickListener {
            val intent = Intent (Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            imagenesLauncher.launch(intent)
        }
    }

    private var imagenesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val data : Intent? = result.data
            val extras = data?.clipData

            //Array para uris de imágenes seleccionadas
            val uris : ArrayList<Uri> = ArrayList()
            //Array para nombres string de archivo de imágenes seleccionadas
            val strings : ArrayList<String> = ArrayList()

            if (extras != null){
                for (i in 0 until extras.itemCount) {
                    //agrego uri a la lista
                    val imageUri : Uri = extras.getItemAt(i).uri
                    uris.add(imageUri)

                    //agrego nomre de archivo a la lista
                    val fileName = DocumentFile.fromSingleUri(requireContext(), imageUri)!!.name!!

                    //quitar números, caracteres especiales y la extensión al archivo imagen
                    strings.add(fileName.substring(0, fileName.lastIndexOf('.')))
                    //Log.i("PRUEBA","BUCLE 1 size: "+uris.size+" el uri: "+imageUri+"y nombre: "+extras.getItemAt(i).text)
                    Log.i("PRUEBA","BUCLE IF size: "+uris.size+" URI: "+imageUri+" y NOMBRE: "+fileName)
                    Log.i("PRUEBA","STRINGS "+i+ ": "+strings.get(i))

                }
            }
            else {
                Log.i("PRUEBA","BUCLE ELSE size: "+uris.size)
            }

            setUpRecyclerViewPictogramas(uris, strings)

            //asignación de nombres y uris seleccionadas para posteriormente ser guardados
            stringsLista=strings
            urisLista=uris

        }
    }


    //private fun setUpRecyclerViewPictogramas(uris : LinkedHashSet<Uri>, strings : LinkedHashSet<String>) {
    private fun setUpRecyclerViewPictogramas(uris : ArrayList<Uri>, strings : ArrayList<String>) {
        binding.pictogramasPredeterminadosRV.apply {
            val orientation = resources.configuration.orientation
            layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Orientación vertical
                GridLayoutManager(requireContext(), 3)
            } else {
                // Orientación horizontal
                GridLayoutManager(requireContext(), 5)
            }
            setHasFixedSize(true)
            adapter = PictogramaImportadoAdapter(uris, strings)
        }
    }

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        // Add menu items here
        menuInflater.inflate(R.menu.menu_guardar, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
                //accedo al layout a través de su id
                R.id.menu_guardar -> {
                    //Log.i("PRUEBA", "MENU CLICK STRIN SIZE: "+stringsLista.size)
                    //Inserto pictogramas de a uno
                    for (i in 0 .. stringsLista.size-1) {
                        val pictograma =
                            Pictograma(
                                0,
                                stringsLista.elementAt(i),
                                getFile(requireContext(),urisLista.elementAt(i)),
                                currentCategoria.nombreCategoria
                            )
                        pictogramaViewModel.insertPictograma(pictograma)
                    }

                    val direction =
                        CategoriaPredeterminadaFragmentDirections
                            .actionCategoriaPredeterminadaFragmentToCategoriaDetalleFragment(
                                currentCategoria
                            )
                    view?.findNavController()?.navigate(direction)
                    true
                }
            //Otro WHeN para icono
            else -> false
        }
    }


    @Throws(IOException::class)
    fun getFile(context: Context, uri: Uri): String {
        val destinationFilename =
            File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), queryName(context, uri)+".jpeg")
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                createFileFromStream(
                    ins!!,
                    destinationFilename
                )
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
        return destinationFilename.absolutePath
    }

    private fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)

        val sinExtension = name.substring(0, name.lastIndexOf('.'))

        returnCursor.close()
        return sinExtension
    }

}