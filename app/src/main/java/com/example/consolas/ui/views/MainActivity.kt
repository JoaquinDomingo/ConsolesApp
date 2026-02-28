package com.example.consolas.ui.views

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.example.consolas.R
import com.example.consolas.data.local.SessionManager
import com.example.consolas.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        drawerLayout = binding.drawerLayout

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupNavigationUI()

        // 1. CREAR EL CANAL DE NOTIFICACIONES AL INICIAR
        createNotificationChannel()
    }

    override fun onResume() {
        super.onResume()
        loadUserDataInHeader()
    }

    // --- LÓGICA DE NOTIFICACIONES ---

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones de Consolas"
            val descriptionText = "Avisos de cambios en la colección"
            // IMPORTANTE: IMPORTANCE_HIGH es lo que permite el Pop-up (Heads-up)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("CONSOLAS_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun triggerNotification(titulo: String, mensaje: String) {
        // Verificar si el usuario activó notificaciones en SettingsFragment
        if (!sessionManager.areNotificationsEnabled()) return

        val builder = NotificationCompat.Builder(this, "CONSOLAS_CHANNEL")
            .setSmallIcon(R.drawable.images) // Asegúrate de tener este icono
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Para versiones anteriores a Android 8.0
            .setDefaults(NotificationCompat.DEFAULT_ALL)   // Sonido y vibración
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Usamos el tiempo actual como ID para que no se pisen si llegan varias
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }

    // --- LÓGICA DE USUARIO Y UI ---

    fun loadUserDataInHeader() {
        val headerView = binding.navigationView.getHeaderView(0)
        val tvName = headerView.findViewById<TextView>(R.id.tvUserName)
        val tvEmail = headerView.findViewById<TextView>(R.id.tvUserEmail)
        val imgUser = headerView.findViewById<ImageView>(R.id.imgUser)

        val currentEmail = sessionManager.userEmail()
        val currentName = sessionManager.userName()

        tvName.text = currentName.ifEmpty { currentEmail.substringBefore("@") }.uppercase()
        tvEmail.text = currentEmail

        sessionManager.getProfileImage()?.let { uriString ->
            Glide.with(this)
                .load(Uri.parse(uriString))
                .circleCrop()
                .placeholder(R.drawable.images)
                .error(R.drawable.images)
                .into(imgUser)
        } ?: run {
            imgUser.setImageResource(R.drawable.images)
        }
    }

    fun updateNavHeaderData() {
        loadUserDataInHeader()
    }

    private fun setupNavigationUI() {
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.FragmentJoaquin, R.id.crudFragment, R.id.profileFragment),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigationView.setupWithNavController(navController)
        binding.bottomNav.setupWithNavController(navController)

        binding.navigationView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.logout) {
                sessionManager.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            } else {
                val handled = NavigationUI.onNavDestinationSelected(item, navController)
                if (handled) drawerLayout.closeDrawers()
                handled
            }
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
}