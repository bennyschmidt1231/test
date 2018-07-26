package seng302.model.comparitors;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.model.person.DonorReceiver;


/**
 * This file contains a series of JUnit tests for the AgeComparator class.
 */
public class AgeComparatorTest {

  // Class attributes.
  List<DonorReceiver> list;
  DonorReceiver donorReceiverA;
  DonorReceiver donorReceiverB;
  DonorReceiver donorReceiverC;


  /**
   * Initialise variables before each test.
   */
  @Before
  public void setup() {

    donorReceiverA = new DonorReceiver("William", "", "Fox", LocalDate.of(1812, 9, 2), "PNZ0002");
    donorReceiverB = new DonorReceiver("Edward", "'Clone'", "Stafford", LocalDate.of(1819, 4, 23),
        "PNX0003");
    donorReceiverC = new DonorReceiver("Edward", "", "Stafford", LocalDate.of(1819, 4, 23),
        "PNZ0003");

    list = Arrays.asList(donorReceiverA, donorReceiverB, donorReceiverC);

  }


  /**
   * Set all variables to null.
   */
  @After
  public void tearDown() {

    list = null;
    donorReceiverA = null;
    donorReceiverB = null;
    donorReceiverC = null;

  }


  /**
   * Attempts to sort a list by age where two elements are in the incorrect position. The correct
   * order is either donorReceiverB, donorReceiverC, donorReceiverA OR donorReceiverC,
   * donorReceiverB, donorReceiverA (as B and C are of equal age).
   */
  @Test
  public void compareTestWithIncorrectOrder() {

    list.sort(new AgeComparator());

    Assert.assertTrue(list.get(0).equals(donorReceiverB) || list.get(0).equals(donorReceiverC));
    Assert.assertTrue(list.get(1).equals(donorReceiverB) || list.get(1).equals(donorReceiverC));
    Assert.assertEquals(donorReceiverA, list.get(2));

  }


  /**
   * Attempts to sort a list of accounts which is already in the correct order by age. The order of
   * the list should not change.
   */
  @Test
  public void compareTestWithCorrectOrder() {

    // Place list elements in correct order.
    list = Arrays.asList(donorReceiverB, donorReceiverC, donorReceiverA);

    // Sort and evaluate.
    list.sort(new AgeComparator());

    Assert.assertTrue(list.get(0).equals(donorReceiverB) || list.get(0).equals(donorReceiverC));
    Assert.assertTrue(list.get(1).equals(donorReceiverB) || list.get(1).equals(donorReceiverC));
    Assert.assertEquals(donorReceiverA, list.get(2));

  }


}
