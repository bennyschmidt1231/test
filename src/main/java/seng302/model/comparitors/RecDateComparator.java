package seng302.model.comparitors;

import java.time.LocalDateTime;
import java.util.Comparator;
import seng302.model.ReceiverRecord;

public class RecDateComparator implements Comparator<ReceiverRecord> {


  /**
   * Compares the name of two accounts and returns -1 if the first name is alphabetically ahead of
   * the second. 1 is returned if the opposite is true. If both names are alphabetically equal, 0 is
   * returned.
   *
   * @param accountA The first account to be examined.
   * @param accountB The second account to be examined.
   * @return -1 if accountA should come before accountB, 1 if the opposite is true, or 0 if both
   * accountA and accountB are equal.
   */
  @Override
  public int compare(ReceiverRecord accountA, ReceiverRecord accountB) {

    // Convert lower case.
    LocalDateTime accountAName = accountA.getTimestamp();
    LocalDateTime accountBName = accountB.getTimestamp();

    // Return comparison.
    return accountAName.compareTo(accountBName);

  }
}
