package com.example.appsimetria

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_menu_opciones.imagenAtras
import kotlinx.android.synthetic.main.activity_menu_opciones.imagenmenu
import kotlinx.android.synthetic.main.activity_menu_opciones_preparado_maps.*
import kotlinx.android.synthetic.main.custom_toast_maps_1.*
import kotlinx.android.synthetic.main.custom_toast_opciones_1.*
import java.lang.Exception

class MenuOpcionesPreparadoMaps : AppCompatActivity() {

    private lateinit var resultScanner : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_opciones_preparado_maps)

        startFunctions()

        imagenAtras.setOnClickListener {
            val intentAtras = Intent(this, MainActivity::class.java)
            startActivity(intentAtras)

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        imagenMenu.setOnClickListener {
            val showPopUp = PopupMenu(this, imagenMenu)
            showPopUp.menuInflater.inflate(R.menu.menu, showPopUp.menu)

            showPopUp.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.itemMenu1 -> Toast.makeText(this, "Primer Item del Menu", Toast.LENGTH_SHORT).show()
                    R.id.itemMenu2 -> Toast.makeText(this, "Segundo Item del Menu", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, "Tercer Item del Menu", Toast.LENGTH_SHORT).show()
                }
                true
            }
            showPopUp.show()
        }

        cardEscaneoImagen.setOnClickListener {
            initScanner()
        }

        cardAltaMaps.setOnClickListener {
            saveDataAdd(resultScanner)
            loadAllData()
            val intentMaps = Intent(this, MapsActivity::class.java)
            startActivity(intentMaps)
        }

        cardEliminarMaps.setOnClickListener {
            saveDataDelete(resultScanner)
            loadAllData()
        }

        cardModificarMaps.setOnClickListener {
            saveDataModify(resultScanner)
            loadAllData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                toastPersonalizadoOpciones1()
            } else {
                Toast.makeText(this, "Dispositivo Escaneado: " + result.contents.substring(0, result.contents.length - 4), Toast.LENGTH_LONG).show()
                resultScanner = result.contents.substring(0, result.contents.length - 4)
                saveData(resultScanner)
                loadData()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startFunctions() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)

        loadData()
        loadAllData()
        loadLatLngData()
    }

    private fun initScanner(){
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("ESCANEA EL DISPOSITIVO DESEADO")
        integrator.setTimeout(15000)
        integrator.initiateScan()
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveData(datos : String){
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("datos", datos)
        editor.apply()
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)

        resultScanner = sharedPreferences.getString("datos", null).toString()
        textoEscaneoActivo.text = ("DISPOSITIVO EN MEMORIA: $resultScanner")
    }

    private fun saveDataAdd(datos : String){
        val sharedPreferences = getSharedPreferences("Add", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("add", datos)
        editor.apply()
    }

    private fun saveDataDelete(datos : String){
        val sharedPreferences = getSharedPreferences("Delete", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("delete", datos)
        editor.apply()
    }

    private fun saveDataModify(datos : String){
        val sharedPreferences = getSharedPreferences("Modify", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("modify", datos)
        editor.apply()
    }

    private fun loadAllData() {
        val sharedPreferencesAdd = getSharedPreferences("Add", Context.MODE_PRIVATE)
        val sharedPreferencesDelete = getSharedPreferences("Delete", Context.MODE_PRIVATE)
        val sharedPreferencesModify = getSharedPreferences("Modify", Context.MODE_PRIVATE)

        textoUltimoEscaneoAdd.text = sharedPreferencesAdd.getString("add", null).toString()
        textoUltimoEscaneoDelete.text = sharedPreferencesDelete.getString("delete", null).toString()
        textoUltimoEscaneoModify.text = sharedPreferencesModify.getString("modify", null).toString()
    }

    private fun loadLatLngData() {
        val sharedPreferences = getSharedPreferences("LatLng", Context.MODE_PRIVATE)

        val latitud : Double = sharedPreferences.getFloat("latitud", 0f).toDouble()
        val longitud : Double = sharedPreferences.getFloat("longitud", 0f).toDouble()
        prueba.append(latitud.toString() + "\n")
        prueba.append(longitud.toString())
    }

    private fun toastPersonalizadoOpciones1() {
        val layoutToast =  layoutInflater.inflate(R.layout.custom_toast_opciones_1, constraintToastOpciones1)
        Toast(this).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.TOP, 0, 0)
            view = layoutToast
        }.show()
    }

    private fun toastPersonalizadoOpciones2() {
        val layoutToast =  layoutInflater.inflate(R.layout.custom_toast_maps_1, constraintToastMaps1)
        Toast(this).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.BOTTOM, 0, 200)
            view = layoutToast
        }.show()
    }
}