package com.example.appsimetria

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_menu_opciones.*

class MenuNavegacion : AppCompatActivity() {

    private lateinit var resultScanner : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_opciones)

        loadData()

        window.statusBarColor = ContextCompat.getColor(this, R.color.secondary_statusbar)

        imagenAtras.setOnClickListener {
            val intentAtras = Intent(this, MainActivity::class.java)
            startActivity(intentAtras)

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        imagenmenu.setOnClickListener {
            val popupmenu : PopupMenu = PopupMenu(this, imagenmenu)
            popupmenu.menuInflater.inflate(R.menu.menu, popupmenu.menu)
            popupmenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.itemMenu1 -> Toast.makeText(this, "Primer Item del Menu", Toast.LENGTH_SHORT).show()
                    R.id.itemMenu2 -> Toast.makeText(this, "Segundo Item del Menu", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, "Tercer Item del Menu", Toast.LENGTH_SHORT).show()
                }
                true
            })
            popupmenu.show()
        }

        cardAltaOpciones.setOnClickListener {
            initScanner()
        }

        cardBajaOpciones.setOnClickListener {
            initScanner()
        }

        cardModificarOpciones.setOnClickListener {
            initScanner()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "No se ha podido escanear", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Dispositivo Escaneado: " + result.contents.substring(0, result.contents.length - 4), Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
        resultScanner = result.contents.substring(0, result.contents.length - 4)
        saveData(resultScanner)
        loadData()
    }

    private fun initScanner(){
        IntentIntegrator(this).initiateScan()
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
        textoUltimaModificacion.text = ("Último dispositivo modificado: $resultScanner")
    }

}