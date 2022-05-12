package cs240.osburnj.pets.ui

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cs240.osburnj.pets.R
import cs240.osburnj.pets.data.DataManager
import cs240.osburnj.pets.data.DataManager2
import cs240.osburnj.pets.databinding.FragmentEditPetBinding
import cs240.osburnj.pets.model.Breeder
import cs240.osburnj.pets.model.Pet
import java.time.LocalDate
import java.time.Period

/**
 * This class handles displaying the Edit Pet Page within the app.
 *
 * @author Jim Osburn
 * @author Ryan Isaacson
 * @since 1.0 03/16/2022
 *
 */
class EditPetFragment : Fragment() {
    private var _binding: FragmentEditPetBinding? = null
    private val binding get() = _binding!!
    private var index: Int = -1
    private var petIndex: Int = -1
    private lateinit var pet: Pet
    private lateinit var breeder: Breeder

    /**
     * This function does the initial creation of the Edit Pet Fragment.
     *
     * @param savedInstanceState If this fragment is being created through a previously created
     * saved state, it is passed in as a saved state to work with.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            index = it.getInt("index")
            petIndex = it.getInt("petIndex")
        }
    }

    /**
     * This function creates and returns the view hierarchy associated with the Edit Pet
     * Fragment
     *
     * @param inflater This is the object that will be used to inflate the views in this fragment.
     * @param container This is the parent view that this fragment's UI is attached to.
     * @param savedInstanceState This is the saved instance of this fragment from previous usage
     * if not null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * This function enables subclasses to initialize themselves once they know the view hierarchy
     * returned in onCreateView() has been completed and returned. This section binds all the views
     * of the Edit Pet Page into usable values, validates all user input on the Edit Pet
     * Page, as well as directs the page view when clicking on buttons within the Edit Pet Page.
     *
     * @param view This is the view hierarchy returned by the onCreateView method above.
     * @param savedInstanceState If this parameter is not null then the fragment is being created
     * from a previously saved state.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // This changes the page name at the top of the app screen
        activity?.title = "Edit Pet Page"
        // This section binds the views/fields to these values
        val petName = binding.edtPetName
        val petBreed = binding.edtBreed
        val petType = binding.edtPetType
        val petBdayMonth = binding.txtBdayMonth
        val petBdayYear = binding.txtBdayYear
        val petBdayDay = binding.txtBdayDay
        val petGreeting = binding.edtPetGreeting
        // This assigns the breeder and pet to be the breeder and pet at the correct indices
        breeder = DataManager.breeders[index]
        pet = DataManager2.pets[petIndex]
        petName.setText(pet.petName)
        petBreed.setText(pet.petBreed)
        petType.setText(pet.petType)
        petBdayMonth.setText(pet.birthMonth.toString())
        petBdayDay.setText(pet.birthDay.toString())
        petBdayYear.setText(pet.birthYear.toString())
        petGreeting.setText(pet.petGreeting)
        // Handles saving the edited input data for this Pet
        val btnSave = binding.btnSave
        btnSave.setOnClickListener {
            // These are the validations for what the user is inputting
            var validInfo = true
            if (petName.text.isBlank()) {
                showErrorDialog("Please enter a pet name.")
                validInfo = false
            }
            if (petType.text.isBlank()) {
                showErrorDialog("Please enter a pet type (dog, cat, etc.).")
                validInfo = false
            }
            if (petBreed.text.isBlank()) {
                showErrorDialog("Please enter a pet breed.")
                validInfo = false
            }
            if (petGreeting.text.isBlank() || petGreeting.text.length >= 18) {
                showErrorDialog("Please enter a greeting. Character limit is 18 characters")
                validInfo = false
            }
            if (petBdayDay.text.isBlank() || petBdayDay.text.length >= 3 ||
                petBdayDay.text.length <= 0 || petBdayDay.text.toString().toInt() >= 32 ||
                petBdayDay.text.toString().toInt() < 1
            ) {
                showErrorDialog("Please enter a birthday 1-31, 2 digits long.")
                validInfo = false
            }
            if (petBdayMonth.text.isBlank() || petBdayMonth.text.length >= 3 ||
                petBdayMonth.text.length <= 0 || petBdayMonth.text.toString().toInt() >= 13 ||
                petBdayMonth.text.toString().toInt() < 1
            ) {
                showErrorDialog("Please enter a birthmonth 1-12, 2 digits long.")
                validInfo = false
            }
            if (petBdayYear.text.isBlank() || petBdayYear.text.length != 4) {
                showErrorDialog("Please enter a valid birthyear, 4 digits long.")
                validInfo = false
            }
            // This handles setting the data once the data has been confirmed as valid inputs
            if (validInfo) {
                if (index >= 0) {
                    // Sets the data locally
                    pet.petName = petName.text.toString()
                    pet.petBreed = petBreed.text.toString()
                    pet.petSex = when (binding.rbtnPetSex.checkedRadioButtonId) {
                        R.id.rbtn_male -> "Male"
                        else -> "Female"
                    }
                    pet.availableToBreed =
                        when (binding.rbtnAvailableToBreed.checkedRadioButtonId) {
                            R.id.rbtn_yes -> "Available"
                            else -> "Not available"
                        }
                    pet.petType = petType.text.toString()
                    pet.birthMonth = petBdayMonth.text.toString().toInt()
                    pet.birthDay = petBdayDay.text.toString().toInt()
                    pet.birthYear = petBdayYear.text.toString().toInt()
                    pet.petGreeting = petGreeting.text.toString()
                    val age = getAge(pet.birthYear, pet.birthMonth, pet.birthDay)
                    pet.petAge = "$age years old"
                    Log.d("Age", "This pet's age is: $age")
                    // Updates the data to Firebase
                    DataManager2.update(pet)
                } else {
                    Log.d("BAD_INDEX", "This user doesn't exist")
                }
                findNavController()
                    .navigate(
                        EditPetFragmentDirections
                            .actionEditPetFragmentToPetListFragment(index = index)
                    )
            }
        }
        // Handles the fragment redirection from clicking the cancel button
        val btnCancel = binding.btnCancel
        btnCancel.setOnClickListener {
            findNavController()
                .navigate(
                    EditPetFragmentDirections
                        .actionEditPetFragmentToPetListFragment(index = index)
                )
        }
    }

    /**
     * This function detaches this fragment from the view created in onViewCreated().
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * This function pops up an error dialog box indicating to the user what they did wrong if they
     * entered "invalid" data on the Edit Pet Page.
     *
     * @param message This is the message displayed to the user.
     */
    private fun showErrorDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this.context)
        alertDialog.apply {
            setIcon(android.R.drawable.ic_dialog_alert)
            setTitle("Input Error")
            setMessage(message)
            setNeutralButton("OK") { _, _ ->
            }
        }.create().show()
    }

    /**
     * This function calculates age of the pet in years. Will be 0 years if less than 1 year old.
     *
     * @param year The year the Pet was born.
     * @param month The month the Pet was born.
     * @param dayOfMonth The day of the month the Pet was born.
     * @return The age in years of the Pet.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAge(year: Int, month: Int, dayOfMonth: Int): Int {
        return Period.between(
            LocalDate.of(year, month, dayOfMonth),
            LocalDate.now()
        ).years
    }
}