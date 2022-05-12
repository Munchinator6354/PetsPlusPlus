package cs240.osburnj.pets.data

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import cs240.osburnj.pets.model.Breeder
import cs240.osburnj.pets.model.Pet

/**
 * This DataManager object listens for updates from the firebase database and reports back to the
 * app if there have been any changes. This DataManager is designated for the Breeders.
 *
 * @author Jim Osburn
 * @author Ryan Isaacson
 * @since 1.0 03/16/2022
 *
 */
object DataManager {

    var userId: String? = null
    private var listReg: ListenerRegistration? = null
    private val listeners: MutableSet<DataListener> = mutableSetOf()
    val breeders: MutableList<Breeder> = mutableListOf()
    val pets: MutableList<Pet> = mutableListOf()
    var lastBreederClicked: Int = -1

    interface DataListener {
        fun updateData()
    }

    /**
     * This registers a listener for Breeders for changes in Firebase.
     *
     * @param fragment The Breeder List Fragment
     */
    fun registerListener(fragment: DataListener) {
        listeners.add(fragment)
    }

    /**
     * This initiates the DataListener to start listening for changes to the Breeders collection
     * in Firebase..
     */
    fun startListening() {
        if (userId == null) return
        val db = Firebase.firestore
        val ref = db.collection("users")
        listReg = ref.addSnapshotListener { result, e ->
            if (e != null) {
                Log.w("DB_ERROR", "Error getting documents", e)
                return@addSnapshotListener
            }
            breeders.clear()
            for (document in result!!) {
                breeders.add(document.toObject())
            }
            for (listener in listeners) {
                listener.updateData()
            }
        }
    }

    /**
     * This function tells the DataListener to stop listening for changes to the collection in
     * Firebase.
     */
    fun stopListening() {
        listReg?.remove()
    }

    /**
     * This function updates the signed in Breeder's given information in the Firebase database.
     *
     * @param breeder The Breeder currently signed in.
     */
    fun update(breeder: Breeder) {
        breeder.id = userId!!
        val db = Firebase.firestore
        db.collection("users")
            .document(userId!!)
            .set(breeder)
    }

    /**
     * This function adds a new Breeder to the collection of Breeder's in Firebase.
     *
     * @param breeder A breeder object to be added to the database.
     */
    fun add(breeder: Breeder) {
        breeder.id = userId!!
        val db = Firebase.firestore
        db.collection("users")
            .document(userId!!)
            .set(breeder)
    }
}