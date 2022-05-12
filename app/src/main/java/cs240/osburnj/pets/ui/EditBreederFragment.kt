package cs240.osburnj.pets.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cs240.osburnj.pets.data.DataManager
import cs240.osburnj.pets.databinding.FragmentEditBreederBinding
import cs240.osburnj.pets.model.Breeder

/**
 * This class handles displaying the Edit Breeder Page within the app.
 *
 * @author Jim Osburn
 * @author Ryan Isaacson
 * @since 1.0 03/16/2022
 *
 */
class EditBreederFragment : Fragment() {
    private var _binding: FragmentEditBreederBinding? = null
    private val binding get() = _binding!!
    private var index: Int = -1
    private lateinit var breeder: Breeder
//    val REQUEST_IMAGE_CAPTURE = 1
//    val btnPicture = binding.btnReplacePhoto

    /**
     * This function does the initial creation of the Edit Breeder Fragment.
     *
     * @param savedInstanceState If this fragment is being created through a previously created
     * saved state, it is passed in as a saved state to work with.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            index = it.getInt("index")
        }
    }

    /**
     * This function creates and returns the view hierarchy associated with the Edit Breeder
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
        _binding = FragmentEditBreederBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * This function enables subclasses to initialize themselves once they know the view hierarchy
     * returned in onCreateView() has been completed and returned. This section binds all the views
     * of the Edit Breeder Page into usable values, validates all user input on the Edit Breeder
     * Page, as well as directs the page view when clicking on buttons within the Edit Breeder Page.
     *
     * @param view This is the view hierarchy returned by the onCreateView method above.
     * @param savedInstanceState If this parameter is not null then the fragment is being created
     * from a previously saved state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // This changes the page name at the top of the app screen
        activity?.title = "Edit Breeder Page"
        // This section binds the views/fields to these values
        val contactFirstName = binding.edtFirstName
        val contactLastName = binding.edtLastName
        val phoneNumber = binding.edtPhoneNumber
        val email = binding.edtContactEmail
        val addressStreet = binding.edtAddressStreet
        val addressCity = binding.edtAddressCity
        val addressState = binding.edtAddressState
        val addressZip = binding.edtAddressZip
        val numberOfPets = binding.txtNumPetsNum
        val breederGreeting = binding.edtBreederGreeting
        // This assigns the breeder and pet to be the breeder and pet at the correct indices
        val dm = DataManager
        breeder = dm.breeders[index]
        contactFirstName.setText(breeder.contactFirstName)
        contactLastName.setText(breeder.contactLastName)
        phoneNumber.setText(breeder.phoneNumber.toString())
        email.setText(breeder.email)
        addressStreet.setText(breeder.addressStreet)
        addressCity.setText(breeder.addressCity)
        addressState.setText(breeder.addressState)
        addressZip.setText(breeder.addressZip)
        numberOfPets.setText(breeder.numberOfPets.toString())
        breederGreeting.setText(breeder.breederGreeting)
        // Save button instructions
        val btnSave = binding.btnSave
        btnSave.setOnClickListener {
            // These are the validations for what the user is inputting
            var validInfo = true
            if (contactFirstName.text.isBlank()) {
                showErrorDialog("Please enter a first name.")
                validInfo = false
            }
            if (contactLastName.text.isBlank()) {
                showErrorDialog("Please enter a last name.")
                validInfo = false
            }
            if (email.text.isBlank() || !email.text.contains("@")) {
                showErrorDialog("Please enter a valid email address.")
                validInfo = false
            }
            if (phoneNumber.text.isBlank() || !phoneNumber.text.isDigitsOnly()) {
                showErrorDialog("Please enter a valid phone number. Numbers only, no punctuation.")
                validInfo = false
            }
            if (addressStreet.text.isBlank()) {
                showErrorDialog("Please enter a valid street address.")
                validInfo = false
            }
            if (addressCity.text.isBlank()) {
                showErrorDialog("Please enter a valid city.")
                validInfo = false
            }
            if (addressState.text.isBlank() || addressState.text.length != 2) {
                showErrorDialog("Please enter a valid state. 2 characters only.")
                validInfo = false
            }
            if (addressZip.text.isBlank() || !addressZip.text.isDigitsOnly() || addressZip.text.length != 5) {
                showErrorDialog("Please enter a valid zip code. 5 digits only.")
                validInfo = false
            }
            if (breederGreeting.text.isBlank()) {
                showErrorDialog("Please enter a greeting.")
                validInfo = false
            }
//            btnPicture.setOnClickListener {
//                fun bitmapToString(photo: Bitmap): String {
//                    val os = ByteArrayOutputStream()
//                    photo.compress(Bitmap.CompressFormat.PNG, 100, os)
//                    val ba: ByteArray = os.toByteArray()
//                    val photoStr: String = Base64.encodeToString(ba, Base64.DEFAULT)
//                    return photoStr;
//                }
//            }

            // Handles the input data if it is all valid by setting that breeders data to the
            // new data.
            if (validInfo) {
                if (index >= 0) {
                    Log.d("GOOD_INDEX", "Breeder's index:  $index")
                    breeder.contactFirstName = contactFirstName.text.toString()
                    breeder.contactLastName = contactLastName.text.toString()
                    breeder.phoneNumber = phoneNumber.text.toString().toInt()
                    breeder.email = email.text.toString()
                    breeder.addressStreet = addressStreet.text.toString()
                    breeder.addressCity = addressCity.text.toString()
                    breeder.addressState = addressState.text.toString()
                    breeder.addressZip = addressZip.text.toString()
                    breeder.addressZip = addressZip.text.toString()
                    breeder.breederGreeting = breederGreeting.text.toString()
                    breeder.numberOfPets = numberOfPets.text.toString().toInt()
                    DataManager.update(breeder)
                } else {
                    Log.d("BAD_INDEX", "This user doesn't exist")
                }
                findNavController().navigate(EditBreederFragmentDirections.actionEditBreederFragmentToBreederListingPage())
            }
        }
        // Cancel button instructions
        val btnCancel = binding.btnCancel
        btnCancel.setOnClickListener {
            findNavController().navigate(EditBreederFragmentDirections.actionEditBreederFragmentToBreederListingPage())
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
     * entered "invalid" data on the Edit Breeder Page.
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

//    Implementing the photo taking
//
//    /**
//     * HAS TO DO WITH IMAGING?
//     *
//     */
//    private fun dispatchTakePictureIntent() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        try {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//        } catch (e: ActivityNotFoundException) {
//            // display error state to the user
//        }
//    }
//
//    /**
//     * HAS TO DO WITH IMAGING?
//     *
//     */
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            val imageBitmap = data.extras.get("data") as Bitmap
//            btnPicture.setImageBitmap(imageBitmap)
//        }
//    }

}