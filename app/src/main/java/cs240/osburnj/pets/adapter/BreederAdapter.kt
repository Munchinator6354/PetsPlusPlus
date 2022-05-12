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
import cs240.osburnj.pets.ui.BreederListFragmentDirections

/**
 * This class acts as the bridge between the underlying data for a Breeder and the views that show
 * that data to the user.
 *
 * @author Jim Osburn
 * @author Ryan Isaacson
 * @since 1.0 03/16/2022
 *
 */
class BreederAdapter :
    RecyclerView.Adapter<BreederAdapter.ItemViewHolder>() {

    private val breederList = DataManager.breeders

    /**
     * This class grabs the associated views and text fields associated with a Breeder and manages
     * the buttons that are clickable for a Breeder. This ViewHolder helps the DataManager2 know
     * when to stop and start listening for changes to the data in Firebase.
     *
     * @author Jim Osburn
     * @author Ryan Isaacson
     * @since 1.0 03/16/2022
     *
     */
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // This grabs and sets all the views associated with a breeder so they can be edited.
        val txtFirstName: TextView = view.findViewById(R.id.txt_first_name_display)
        val txtLastName: TextView = view.findViewById(R.id.txt_last_name_display)
        val txtAddressStreet: TextView = view.findViewById(R.id.txt_address_street)
        val txtAddressCity: TextView = view.findViewById(R.id.txt_address_city)
        val txtAddressState: TextView = view.findViewById(R.id.txt_address_state)
        val txtAddressZip: TextView = view.findViewById(R.id.txt_address_zip)
        val txtPetNum: TextView = view.findViewById(R.id.txt_num_pets)
        val txtEmail: TextView = view.findViewById(R.id.txt_contact_email)
        val txtBreederGreeting: TextView = view.findViewById(R.id.txt_breeder_greeting)
        val btnEditBreeder: Button = view.findViewById(R.id.btn_edit_breeder)
        val context = view.context!!

        init {
            view.setOnClickListener {
                val index = adapterPosition
                DataManager2.viewingUsersPets = DataManager2.userId
                DataManager.lastBreederClicked = index
                val action =
                    BreederListFragmentDirections.actionBreederListingPageToPetListFragment(index = adapterPosition)
                // Firebase usage to get the userId
                val id = FirebaseAuth.getInstance().currentUser?.uid
                val db = Firebase.firestore
                DataManager2.stopListening()
                db.collection("users")
                    .get()
                    .addOnSuccessListener { result ->
                        if (result.documents[index].toString().contains(id.toString())) {
                            Log.d("DB_FOUND", "You clicked on your user card:  $id")
                        } else {
                            Log.d(
                                "DB_FOUND",
                                "You clicked on someone else's card. Here's that person's id: ${
                                    result.documents[index].toString().substring(27, 55)
                                }"
                            )
                            DataManager2.viewingUsersPets =
                                result.documents[index].toString().substring(27, 55)
                        }
                        DataManager2.startListening()
                        DataManager2.breederPosition = adapterPosition
                        view.findNavController().navigate(action)
                    }.addOnFailureListener { exception ->
                        Log.d("DB_ERROR", "Error getting documents: ", exception)
                    }
            }
            btnEditBreeder.setOnClickListener {
                val action =
                    BreederListFragmentDirections.actionBreederListingPageToEditBreederFragment(
                        index = adapterPosition
                    )
                view.findNavController().navigate(action)
            }
        }
    }

    /**
     * This function is called when the Breeder recycler view requires a new ViewHolder of the
     * passed in viewType and returns an appropriate ItemViewHolder.
     *
     * @param parent This is the ViewGroup where the returned view will end up residing.
     * @param viewType The type of the newly required view.
     * @return The holder of a Breeder object card.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_breeder, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    /**
     * This function allows the data to be displayed at a a specified view on the Breeder card.
     *
     * @param holder The holder is the representative card for a particular Breeder.
     * @param position The position of card in the recycler view.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // This is where the app binds the data to the particular card in the recycler view.
        holder.txtFirstName.text = breederList[position].contactFirstName
        holder.txtLastName.text = breederList[position].contactLastName
        holder.txtAddressStreet.text = breederList[position].addressStreet
        holder.txtAddressCity.text = breederList[position].addressCity
        holder.txtAddressState.text = breederList[position].addressState
        holder.txtAddressZip.text = breederList[position].addressZip
        holder.txtEmail.text = breederList[position].email
        holder.txtBreederGreeting.text = """"${breederList[position].breederGreeting}""""
        holder.txtPetNum.text = "Pets:  ${breederList[position].numberOfPets}"
        // If the card id matches the user id of the user logged into Firebase, change button to visible
        if (breederList[position].id == FirebaseAuth.getInstance().uid) {
            holder.btnEditBreeder.visibility = View.VISIBLE
        }
    }

    /**
     * This function returns amount of Breeders in the list.
     *
     * @return The number of Breeders.
     */
    override fun getItemCount(): Int {
        return breederList.size
    }
}