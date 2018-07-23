package seng302.model.comparitors;


import seng302.model.person.DonorReceiver;

import java.util.Comparator;


/**
 * Simplified version of NameComparator which sorts names alphabetically.
 */
public class NameAlphabeticalComparator implements Comparator<DonorReceiver> {


    /**
     * Compares the name of two accounts and returns -1 if the first name is
     * alphabetically ahead of the second. 1 is returned if the opposite is
     * true. If both names are alphabetically equal, 0 is returned.
     *
     * @param donorReceiverA The first account to be examined.
     * @param donorReceiverB The second account to be examined.
     * @return -1 if donorReceiverA should come before donorReceiverB, 1 if the opposite is
     * true, or 0 if both donorReceiverA and donorReceiverB are equal.
     */
    @Override
    public int compare(DonorReceiver donorReceiverA, DonorReceiver donorReceiverB) {

        // Convert lower case.
        String accountAName = (donorReceiverA.getFirstName() + donorReceiverA.getMiddleName()
                + donorReceiverA.getLastName()).replaceAll("\\s"," ").toLowerCase();
        String accountBName = (donorReceiverB.getFirstName() + donorReceiverB.getMiddleName()
                + donorReceiverB.getLastName()).replaceAll("\\s"," ").toLowerCase();

        // Return comparison.
        return accountAName.compareTo(accountBName);

    }


}
