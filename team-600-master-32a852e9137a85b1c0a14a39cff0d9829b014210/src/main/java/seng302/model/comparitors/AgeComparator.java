package seng302.model.comparitors;


import seng302.model.person.DonorReceiver;
import java.util.Comparator;


/**
 * Comparator which compares two account objects by age.
 */
public class AgeComparator implements Comparator<DonorReceiver> {


    /**
     * Compares the age of two account holders. Returns less than 0 if donorReceiverA has
     * lower age, greater than 0 if the opposite is true, and 0 if both ages are equal.
     *
     * @param donorReceiverA The first account to be compared.
     * @param donorReceiverB The second account to be compared.
     * @return Negative if donorReceiverA has lower age, positive if donorReceiverB has lower age, or
     * 0 if both ages are equal.
     */
    @Override
    public int compare(DonorReceiver donorReceiverA, DonorReceiver donorReceiverB) {

        return donorReceiverA.calculateAge() - donorReceiverB.calculateAge();

    }


}