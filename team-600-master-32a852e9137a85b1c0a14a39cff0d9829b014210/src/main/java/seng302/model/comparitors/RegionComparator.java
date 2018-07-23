package seng302.model.comparitors;


import seng302.model.person.DonorReceiver;
import java.util.Comparator;


/**
 * RegionComparator is a standalone comparator used to compare the region of
 * two accounts. It is used to sort accounts by region.
 */
public class RegionComparator implements Comparator<DonorReceiver> {


    /**
     * Compares the address region of two accounts and returns -1 if the region
     * of the first account is before the region of the second if ordered
     * alphabetically. 1 is returned if the opposite is true. If both regions
     * are equal, 0 is returned.
     *
     * @param donorReceiverA The first account to be examined.
     * @param donorReceiverB The second account to be examined.
     * @return -1 if donorReceiverA should come before donorReceiverB, 1 if the opposite is
     * true, or 0 if both donorReceiverA and donorReceiverB are equal.
     */
    @Override
    public int compare(DonorReceiver donorReceiverA, DonorReceiver donorReceiverB) {

        // Convert lower case.
        String regionA = donorReceiverA.getContactDetails().getAddress().getRegion().toLowerCase();
        String regionB = donorReceiverB.getContactDetails().getAddress().getRegion().toLowerCase();

        // Return comparison.
        return regionA.compareTo(regionB);

    }


}
