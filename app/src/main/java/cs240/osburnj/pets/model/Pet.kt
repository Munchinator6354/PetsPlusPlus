package cs240.osburnj.pets.model

/**
 * This class outlines the data information for a Pet.
 *
 * @author Jim Osburn
 * @author Ryan Isaacson
 * @since 1.0 03/16/2022
 *
 */
data class Pet(
    var petId: String,
    var petName: String,
    var petAge: String,
    var petBreed: String,
    var petSex: String,
    var availableToBreed: String,
    var birthYear: Int,
    var birthMonth: Int,
    var birthDay: Int,
    var petType: String,
    var petGreeting: String,
    var ownedBy: String

) {
    constructor() : this(
        "", "", "", "", "",
        "", 1, 1, 1, "",
        "", ""
    )
}