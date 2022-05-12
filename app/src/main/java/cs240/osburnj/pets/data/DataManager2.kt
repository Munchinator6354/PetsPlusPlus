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
 * app if there have been any changes. This DataManager is designated for the Pets.
 *
 * @author Jim Osburn
 * @author Ryan Isaacson
 * @since 1.0 03/16/2022
 *
 */
object DataManager2 {

    var petId: String? = null
    var userId = DataManager.userId
    var viewingUsersPets: String? = null
    private var listReg: ListenerRegistration? = null
    private val listeners: MutableSet<DataListener> = mutableSetOf()
    val pets: MutableList<Pet> = mutableListOf()
    var breederPosition: Int = -1

    interface DataListener {
        fun updateData()
    }

    /**
     * This registers a listener for Pets for changes in Firebase.
     *
     * @param fragment The Pet List Fragment
     */
    fun registerListener(fragment: DataListener) {
        listeners.add(fragment)
    }

    /**
     * This initiates the DataListener to start listening for changes to the Breeder's Pets
     * collection in Firebase.
     */
    fun startListening() {
        if (petId == null) return
        val db = Firebase.firestore
        val ref = db.collection("users")
            .document(viewingUsersPets!!)
            .collection("pets")
        listReg = ref.addSnapshotListener { result, e ->
            if (e != null) {
                Log.w("DB_ERROR", "Error getting documents", e)
                return@addSnapshotListener
            }
            pets.clear()
            for (document in result!!) {
                pets.add(document.toObject())
            }
            for (listener in listeners) {
                listener.updateData()
            }
        }
    }

    /**
     * This function counts the amount of pets the currently signed in breeder has and handles the
     * changing amounts as a user adds pets of deletes pets.
     *
     * @param adding This passed in Boolean is true if the Breeder is adding a Pet, but will be
     * false if the Breeder is subtracting a Pet from their total count via deletion.
     */
    private fun countPets(adding: Boolean) {
        if (petId == null) return
        Firebase.firestore
            .collection("users")
            .document(viewingUsersPets!!)
            .get()
            .addOnSuccessListener { result ->
                val tempBreeder = result.toObject<Breeder>()
                if (tempBreeder != null) {
                    if (adding) {
                        tempBreeder.numberOfPets += 1
                    } else {
                        tempBreeder.numberOfPets -= 1
                    }
                    DataManager.update(tempBreeder)
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
     * This function updates a given Pet's information in the Firebase database.
     *
     * @param pet The Pet desired to be edited.
     */
    fun update(pet: Pet) {
        Log.d("PetID", "PetID = $petId")
        pet.petId = petId!!
        val db = Firebase.firestore
        db.collection("users")
            .document(viewingUsersPets!!)
            .collection("pets")
            .document(petId!!)
            .set(pet)
    }

    /**
     * This function adds a Pet to the currently signed in Breeder's inventory of Pets.
     *
     */
    fun add() {
        val tempPet = Pet()
        DataManager.userId
        countPets(true)
        val db = Firebase.firestore
        db.collection("users")
            .document(viewingUsersPets!!)
            .collection("pets")
            .add(tempPet)
        Log.d("DB_pets_collection", "Pet added")
        db.collection("users")
            .document(viewingUsersPets!!)
            .collection("pets")
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    var tempDocData = document.data.toString()
                    tempDocData =
                        tempDocData.substring(tempDocData.indexOf("petId="), tempDocData.length - 1)
                    tempDocData = tempDocData.substring(6, tempDocData.indexOf(","))
                    Log.d("Document", "tempDocData: $tempDocData")
                    if (tempDocData == "") {
                        petId = document.id
                        update(tempPet)
                    }
                }
            }
    }

    /**
     * This function deletes a Pet from the currently signed in Breeder's Pet inventory.
     *
     * @param id The id of the Pet the user wants to delete.
     */
    fun delete(id: String) {
        countPets(false)
        val db = Firebase.firestore
        db.collection("users")
            .document(viewingUsersPets!!)
            .collection("pets")
            .document(id)
            .delete()
    }
}