package cs240.osburnj.pets.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import cs240.osburnj.pets.R
import cs240.osburnj.pets.data.DataManager
import cs240.osburnj.pets.data.DataManager2
import cs240.osburnj.pets.ui.PetListFragmentDirections

/**
 * This class acts as the bridge between the underlying data for a Pet and the views that show
 * that data to the user.
 *
 * @author Jim Osburn
 * @author Ryan Isaacson
 * @since 1.0 03/16/2022
 *
 */
class PetAdapter :
    RecyclerView.Adapter<PetAdapter.ItemViewHolder>() {

    private val petList = DataManager2.pets
    private val breederList = DataManager.breeders

    /**
     * This class grabs the associated views and text fields associated with a Pet and manages
     * the buttons that are clickable for a Pet. This ViewHolder helps the DataManager know
     * when to stop and start listening for changes to the data in Firebase.
     *
     * @author Jim Osburn
     * @author Ryan Isaacson
     * @since 1.0 03/16/2022
     *
     */
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtPetName: TextView = view.findViewById(R.id.txt_pet_name_display)
        val txtPetBreed: TextView = view.findViewById(R.id.txt_breed_display)
        val txtAnimalType: TextView = view.findViewById(R.id.txt_animal_type)
        val txtPetSex: TextView = view.findViewById(R.id.txt_sex)
        val txtPetGreeting: TextView = view.findViewById(R.id.txt_pet_greeting)
        val txtPetAvailabilityToBreed: TextView = view.findViewById(R.id.txt_available_to_breed)
        val txtPetOwnedBy: TextView? = view.findViewById(R.id.txt_pet_age)
        private val btnEditPet: Button = view.findViewById(R.id.btn_edit_pet)
        private val btnDeletePet: Button = view.findViewById(R.id.btn_delete_pet)
        val layButtons: View? = view.findViewById(R.id.layout_pet_buttons)
        val txtPetBirthDay: TextView? = view.findViewById(R.id.txt_bday_day)
        val txtPetBirthMonth: TextView? = view.findViewById(R.id.txt_bday_month)
        val txtPetBirthYear: TextView? = view.findViewById(R.id.txt_bday_year)
        val txtPetAge: TextView? = view.findViewById(R.id.txt_pet_age)
        val context = view.context!!

        init {
            view.setOnClickListener {
                //firebase stuff to get the userId
                val id = FirebaseAuth.getInstance().currentUser?.uid
                var petId: String
                val db = Firebase.firestore
                db.collection("users")
                    .document(DataManager2.viewingUsersPets!!)
                    .collection("pets")
                    .get()
                    .addOnSuccessListener { result ->
                        Log.d(
                            "DB_FOUND",
                            "Here is the documents.toString(): \n${result.documents}"
                        )
                        if (result.documents.toString().contains(id.toString())) {
                            petId = result.documents.toString().substring(62, 82)
                            DataManager2.petId = petId
                            Log.d("DB_FOUND", "You clicked on this pet:  $petId")
                        } else {
                            Log.d("DB_FOUND", "You clicked on someone else's pet")
                        }
                    }.addOnFailureListener { exception ->
                        Log.d("DB_ERROR", "Error getting documents: ", exception)
                    }
            }
            // This handles the functionality for clicking the Edit Pet button
            btnEditPet.setOnClickListener {
                Firebase.firestore
                    .collection("users")
                    .document(DataManager2.viewingUsersPets!!)
                    .collection("pets")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result.documents) {
                            var tempDocData = document.data.toString()
                            tempDocData = tempDocData.substring(
                                tempDocData.indexOf("petId="),
                                tempDocData.length - 1
                            )
                            tempDocData = tempDocData.substring(6, tempDocData.indexOf(","))
                            if (tempDocData == DataManager2.pets[adapterPosition].petId) {
                                Log.d(
                                    "Document",
                                    "This should be the pet you are looking for: $tempDocData"
                                )
                                DataManager2.petId = tempDocData
                            }
                        }
                        val action =
                            PetListFragmentDirections.actionPetListFragmentToEditPetFragment(
                                index = DataManager2.breederPosition,
                                petIndex = adapterPosition
                            )
                        view.findNavController().navigate(action)
                    }
            }
            // This handles the functionality for clicking the Delete Pet button
            btnDeletePet.setOnClickListener {
                Firebase.firestore
                    .collection("users")
                    .document(DataManager2.viewingUsersPets!!)
                    .collection("pets")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result.documents) {
                            var tempDocData = document.data.toString()
                            tempDocData = tempDocData.substring(
                                tempDocData.indexOf("petId="),
                                tempDocData.length - 1
                            )
                            tempDocData = tempDocData.substring(6, tempDocData.indexOf(","))
                            if (tempDocData == DataManager2.pets[adapterPosition].petId) {
                                Log.d(
                                    "Document",
                                    "This should be the pet you are looking for: $tempDocData"
                                )
                                Log.d("Document", "Adapter position: $adapterPosition")
                                DataManager2.delete(tempDocData)
                            }
                        }
                    }
            }
        }
    }

    /**
     * This function is called when the Pet recycler view requires a new ViewHolder of the
     * passed in viewType and returns an appropriate ItemViewHolder.
     *
     * @param parent This is the ViewGroup where the returned view will end up residing.
     * @param viewType The type of the newly required view.
     * @return The holder of a Pet object card.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pet, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    /**
     * This function allows the data to be displayed at a a specified view on the Pet card.
     *
     * @param holder The holder is the representative card for a particular Pet.
     * @param position The position of card in the recycler view.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.txtPetName.text = petList[position].petName
        holder.txtPetBreed.text = petList[position].petBreed
        holder.txtAnimalType.text = petList[position].petType
        holder.txtPetSex.text = petList[position].petSex
        //We tried to use the string literal from strings.xml, but we couldn't get it to work for the line below.
        holder.txtPetGreeting.text = "\"" + petList[position].petGreeting + "\""
        holder.txtPetAvailabilityToBreed.text = petList[position].availableToBreed
        holder.txtPetOwnedBy?.text = petList[position].ownedBy
        holder.txtPetBirthDay?.text = petList[position].birthDay.toString()
        holder.txtPetBirthMonth?.text = petList[position].birthMonth.toString()
        holder.txtPetBirthYear?.text = petList[position].birthYear.toString()
        holder.txtPetAge?.text = petList[position].petAge
        if (breederList[DataManager2.breederPosition].id == FirebaseAuth.getInstance().uid) {
            holder.layButtons!!.visibility = View.VISIBLE
        }
    }

    /**
     * This function returns amount of Pets in the list.
     *
     * @return The number of Pets.
     */
    override fun getItemCount(): Int {
        return petList.size
    }
}