package cs240.osburnj.pets.model

/**
 * This class outlines the data information for a Breeder.
 *
 * @author Jim Osburn
 * @author Ryan Isaacson
 * @since 1.0 03/16/2022
 *
 */
data class Breeder(
    var id: String,
    var contactFirstName: String,
    var contactLastName: String,
    var phoneNumber: Int,
    var email: String,
    var addressStreet: String,
    var addressCity: String,
    var addressState: String,
    var addressZip: String,
    var numberOfPets: Int,
    var breederGreeting: String,

    ) {
    constructor() : this(
        "", "", "", -1, "",
        "", "", "", "", 0, ""
    )
}
