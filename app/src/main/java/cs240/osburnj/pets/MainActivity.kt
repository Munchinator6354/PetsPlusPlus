package cs240.osburnj.pets

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import cs240.osburnj.pets.data.DataManager
import cs240.osburnj.pets.data.DataManager2
import cs240.osburnj.pets.databinding.ActivityMainBinding
import cs240.osburnj.pets.model.Breeder

/**
 * This class handles the creation of the app as well the core functionalities of the app. This is
 * the foundation of all of the peripheral components.
 *
 * @author Jim Osburn
 * @author Ryan Isaacson
 * @since 1.0 03/16/2022
 *
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var authListener: FirebaseAuth.AuthStateListener? = null

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    /**
     * This function handles the creation of the app and also checks if we are logged in as a valid
     * Breeder, and if we are we document the Breeder's id in Firebase. If the user is not logged
     * in as a valid user the sign in intent is launched.
     *
     * @param savedInstanceState This holds the app's previously cataloged information and
     * presents it if not null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                DataManager.userId = user.uid
                DataManager2.userId = user.uid
                DataManager2.viewingUsersPets = user.uid
                DataManager2.petId = ""
                DataManager.startListening()
                DataManager2.startListening()
            } else {
                DataManager.userId = null
                DataManager2.userId = null
                DataManager.stopListening()
                DataManager2.stopListening()
                val signInIntent = AuthUI.getInstance().createSignInIntentBuilder().build()
                signInLauncher.launch(signInIntent)
            }
        }

    }

    /**
     * This function allows the user to navigate upwards in the app's activity hierarchy from the
     * action bar.
     *
     * @return Returns true if the app allows the user to navigate up activities, and returns false
     * if it does not.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * This function gets called when the app is touched upon again after viewing another
     * application and then resumes listening for changes in Firebase.
     *
     */
    override fun onResume() {
        super.onResume()
        DataManager.startListening()
        FirebaseAuth.getInstance().addAuthStateListener(authListener!!)
    }

    /**
     * This function handles when the user selects something outside of the app and redirects
     * away from Pets++. Listening for changes in Firebase is temporarily stopped to conserve
     * computational effort.
     *
     */
    override fun onPause() {
        super.onPause()
        DataManager.stopListening()
        DataManager2.stopListening()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener!!)
    }

    /**
     * This function handles the login page and determines if a user account has already been
     * created or not. If it has been created, that account will be logged into, if it has not been
     * created, it will be added to the list of user accounts.
     *
     * @param result This is the result of searching for the login validation credentials.
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        when (result.resultCode) {
            RESULT_OK -> {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show()
                // Grabs the user id
                val id = FirebaseAuth.getInstance().currentUser?.uid
                val db = Firebase.firestore
                db.collection("users").document(id!!)
                    .get()
                    .addOnSuccessListener { result ->
                        // Compares it with the list of already created breeders and if they don't
                        // already exist in the database, creates a new breeder
                        if (result.toObject<Breeder>() == null) {
                            // Adds a new breeder to the database
                            DataManager.add(Breeder())
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("DB_ERROR", "Error getting documents: ", exception)
                    }
            }
            RESULT_CANCELED -> {
                Toast.makeText(this, "Start over.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Sign-in error!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * This function inflates the menu options for the app.
     *
     * @param menu This is the action bar menu to be inflated and displayed.
     * @return Returns true once the menu bar is inflated and ready.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)
        return true
    }

    /**
     * This function handles the usage of the menu's option items button and the actions the menu
     * allows for.
     *
     * @param item This is the menu displayed when clicked.
     * @return Returns true once the user has selected an option.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_user -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.action_switch_user)
                    .setMessage(R.string.dialog_switch_message)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.dialog_switch_yes) { dialogInterface, which ->
                        findNavController(R.id.nav_host_fragment).navigate(R.id.breederListingPage)
                        FirebaseAuth.getInstance().signOut()
                    }
                    .setNegativeButton(R.string.dialog_switch_no) { dialogInterface, which ->
                        return@setNegativeButton
                    }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}