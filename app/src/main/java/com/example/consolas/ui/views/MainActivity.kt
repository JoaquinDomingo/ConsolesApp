package com.example.consolas.ui.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    }

    // Usamos onResume o llamamos a loadUserDataInHeader para asegurar
    // que al volver de un Login/Perfil los datos se refresquen.
    override fun onResume() {
        super.onResume()
        loadUserDataInHeader()
    }

    private fun loadUserDataInHeader() {
        val headerView = binding.navigationView.getHeaderView(0)
        val tvName = headerView.findViewById<TextView>(R.id.tvUserName)
        val tvEmail = headerView.findViewById<TextView>(R.id.tvUserEmail)
        val imgUser = headerView.findViewById<ImageView>(R.id.imgUser)

        // Obtenemos el email actual para que el Manager busque la foto correcta
        val currentEmail = sessionManager.userEmail()
        tvName.text = sessionManager.userName()
        tvEmail.text = currentEmail

        // El Manager ahora devolverá la URI vinculada a este email específico
        sessionManager.getProfileImage()?.let { uriString ->
            Glide.with(this)
                .load(Uri.parse(uriString))
                .circleCrop()
                .placeholder(R.drawable.images)
                .error(R.drawable.images)
                .into(imgUser)
        } ?: run {
            // Si el usuario no tiene foto guardada, reseteamos a la imagen por defecto
            imgUser.setImageResource(R.drawable.images)
        }
    }

    fun updateNavHeaderImage(newUri: Uri) {
        val headerView = binding.navigationView.getHeaderView(0)
        val imgUser = headerView.findViewById<ImageView>(R.id.imgUser)

        Glide.with(this)
            .load(newUri)
            .circleCrop()
            .into(imgUser)
    }

    private fun setupNavigationUI() {
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.FragmentJoaquin, R.id.crudFragment),
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