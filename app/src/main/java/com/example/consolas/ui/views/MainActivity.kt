package com.example.consolas.ui.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView // Importante añadir esto
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.consolas.R
import com.example.consolas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        drawerLayout = binding.drawerLayout

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        // --- INICIO DE LA PARTE NUEVA PARA EL HEADER ---

        // 1. Recuperamos los datos del Intent
        val userName = intent.getStringExtra("USER_NAME") ?: "Invitado"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: "sin@correo.com"

        // 2. Accedemos al HeaderView (la parte superior del menú lateral)
        val headerView = binding.navigationView.getHeaderView(0)

        // 3. Buscamos los TextViews por los IDs de tu XML del header
        val tvName = headerView.findViewById<TextView>(R.id.tvUserName)
        val tvEmail = headerView.findViewById<TextView>(R.id.tvUserEmail)

        // 4. Asignamos los datos dinámicamente
        tvName.text = userName
        tvEmail.text = userEmail

        // --- FIN DE LA PARTE NUEVA ---

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.FragmentJoaquin,
                R.id.crudFragment,
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navigationView.setupWithNavController(navController)
        binding.bottomNav.setupWithNavController(navController)

        binding.navigationView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.logout) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            } else {
                val handled = NavigationUI.onNavDestinationSelected(item, navController)
                if (handled) drawerLayout.close()
                handled
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_home -> {
                navController.navigate(R.id.FragmentJoaquin)
                true
            }
            R.id.menu_item_crud -> {
                navController.navigate(R.id.crudFragment)
                true
            }
            // Mantenemos la funcionalidad de los 3 puntos
            else -> item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}