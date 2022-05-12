package cs240.osburnj.pets.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cs240.osburnj.pets.adapter.BreederAdapter
import cs240.osburnj.pets.data.DataManager
import cs240.osburnj.pets.databinding.FragmentBreederListBinding

/**
 * This class handles displaying the recyclerView of Breeders and Breeder Listing Page within
 * the app.
 *
 * @author Jim Osburn
 * @author Ryan Isaacson
 * @since 1.0 03/16/2022
 *
 */
class BreederListFragment : Fragment(), DataManager.DataListener {
    private var _binding: FragmentBreederListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewBreeder: RecyclerView

    /**
     * This function creates and returns the view hierarchy associated with the Breeder List
     * Fragment.
     *
     * @param inflater This is the object that will be used to inflate the views in this fragment.
     * @param container This is the parent view that this fragment's UI is attached to.
     * @param savedInstanceState This is the saved instance of this fragment from previous usage
     * if not null.
     * @return The view hierarchy.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreederListBinding.inflate(inflater, container, false)
        val view = binding.root
        DataManager.registerListener(this)
        return view
    }

    /**
     * This function enables subclasses to initialize themselves once they know the view hierarchy
     * returned in onCreateView() has been completed and returned.
     *
     * @param view This is the view hierarchy returned by the onCreateView method above.
     * @param savedInstanceState If this parameter is not null then the fragment is being created
     * from a previously saved state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // This changes the page name at the top of the app screen
        activity?.title = "Breeder Listing Page"
        recyclerViewBreeder = binding.recyclerViewBreeders
        recyclerViewBreeder.layoutManager = LinearLayoutManager(context)
        recyclerViewBreeder.adapter = BreederAdapter()
    }

    /**
     * This function detaches this fragment from the view created in onViewCreated().
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * This function updates data in the breeder list recycler view.
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun updateData() {
        recyclerViewBreeder.adapter?.notifyDataSetChanged()
    }

}