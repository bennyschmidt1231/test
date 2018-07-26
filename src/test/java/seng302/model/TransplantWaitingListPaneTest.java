package seng302.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Test;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;

public class TransplantWaitingListPaneTest {


  @Test
  public void updateAccountReceivingTest() {
    DonorReceiver donorReceiver = new DonorReceiver("Steve", "Paul", "Jobs", LocalDate.now(),
        "AAA1111");
    assertFalse(donorReceiver.getReceiver());
    donorReceiver.updateOrgan(donorReceiver, "receiver", "liver", "true");
    TransplantWaitingList.updateAccountReceiving(donorReceiver);
    assertTrue(donorReceiver.getReceiver());
  }


  @Test
  public void formatDateToStringTest() {
    LocalDate localDate = LocalDate.of(2018, 5, 13);
    String date = TransplantWaitingList.formatDateToString(localDate);
    assertEquals("20180513", date);


  }

  @Test
  public void formatCreationDateTest() {
    LocalDateTime localDateTime = LocalDateTime.of(2018, 5, 13, 12, 30);
    String date = TransplantWaitingList.formatCreationDate(localDateTime);
    assertEquals("13-05-2018 12:30:00", date);

    localDateTime = null;
    date = TransplantWaitingList.formatCreationDate(localDateTime);
    assertEquals("", date);
  }

  @Test
  public void receiverFromRegionTest() {
    ReceiverRecord receiverRecord = new ReceiverRecord("AAA1111", "Canterbury", LocalDateTime.now(),
        "Liver");
    boolean answer = TransplantWaitingList.receiverFromRegion(receiverRecord, "Canterbury");
    assertTrue(answer);

    receiverRecord = new ReceiverRecord("AAA1111", "Canterbury", LocalDateTime.now(), "Liver");
    answer = TransplantWaitingList.receiverFromRegion(receiverRecord, "Otago");
    assertFalse(answer);

    receiverRecord = new ReceiverRecord("AAA1111", "Canterbury", LocalDateTime.now(), "Liver");
    answer = TransplantWaitingList.receiverFromRegion(receiverRecord, "Any Region");
    assertTrue(answer);
  }

  @Test
  public void removeOrganMistakeTest() {
    DonorReceiver donorReceiver = new DonorReceiver("Steve", "Paul", "Jobs", LocalDate.now(),
        "AAA1111");
    assertEquals(0, donorReceiver.getUpDateLog().size());
    assertFalse(donorReceiver.getReceiver());
    donorReceiver.updateOrgan(donorReceiver, "receiver", "liver", "true");

    ReceiverRecord receiverRecord = new ReceiverRecord("AAA1111", "Canterbury", LocalDateTime.now(),
        "Liver");
    AccountManager accountManager = new AccountManager();
    accountManager.getDonorReceivers().put(donorReceiver.getUserName(), donorReceiver);

    accountManager.setCurrentUser(donorReceiver);
    TransplantWaitingList.removeOrganMistake(receiverRecord, accountManager);

    LogEntry log = donorReceiver.getUpDateLog().get(1);
    int size = log.toString().length();
    //assertFalse(donorReceiver.getReceiver());
    assertEquals(2, donorReceiver.getUpDateLog().size());
    assertEquals(
        "User Being Modified: Steve Paul Jobs (NHI: AAA1111), Changed by User: Steve Paul Jobs (NHI: AAA1111), liverReceiving changed from 'true' to 'false, reason for removal: Mistake,' at ",
        log.toString().substring(0, size - 24));

  }

  @Test
  public void removeOrganCuredTest() {
    DonorReceiver donorReceiver = new DonorReceiver("Steve", "Paul", "Jobs", LocalDate.now(),
        "AAA1111");
    assertEquals(0, donorReceiver.getUpDateLog().size());
    assertFalse(donorReceiver.getReceiver());
    donorReceiver.updateOrgan(donorReceiver, "receiver", "liver", "true");

    ReceiverRecord receiverRecord = new ReceiverRecord("AAA1111", "Canterbury", LocalDateTime.now(),
        "Liver");
    AccountManager accountManager = new AccountManager();
    accountManager.getDonorReceivers().put(donorReceiver.getUserName(), donorReceiver);

    accountManager.setCurrentUser(donorReceiver);
    TransplantWaitingList.removeOrganCured(receiverRecord, accountManager);

    LogEntry log = donorReceiver.getUpDateLog().get(1);
    int size = log.toString().length();
    assertFalse(donorReceiver.getReceiver());
    assertEquals(2, donorReceiver.getUpDateLog().size());
    assertEquals(
        "User Being Modified: Steve Paul Jobs (NHI: AAA1111), Changed by User: Steve Paul Jobs (NHI: AAA1111), liverReceiving changed from 'true' to 'false, reason for removal: Cured,' at ",
        log.toString().substring(0, size - 24));
  }

  @Test
  public void removeOrganDeceasedTest() {
    DonorReceiver donorReceiver = new DonorReceiver("Steve", "Paul", "Jobs", LocalDate.now(),
        "AAA1111");
    assertEquals(0, donorReceiver.getUpDateLog().size());
    assertFalse(donorReceiver.getReceiver());
    donorReceiver.updateOrgan(donorReceiver, "receiver", "liver", "true");

    ReceiverRecord receiverRecord = new ReceiverRecord("AAA1111", "Canterbury", LocalDateTime.now(),
        "Liver");
    AccountManager accountManager = new AccountManager();
    accountManager.getDonorReceivers().put(donorReceiver.getUserName(), donorReceiver);

    accountManager.setCurrentUser(donorReceiver);
    TransplantWaitingList
        .removeOrganDeceased(receiverRecord, accountManager, LocalDate.now());

    LogEntry log = donorReceiver.getUpDateLog().get(1);
    int size = log.toString().length();
    assertFalse(donorReceiver.getReceiver());
    assertEquals(3, donorReceiver.getUpDateLog().size());
    assertEquals(
        "User Being Modified: Steve Paul Jobs (NHI: AAA1111), Changed by User: Steve Paul Jobs (NHI: AAA1111), All organ's receiving changed from 'unknown' to 'false, reason for removal: Death,' at ",
        log.toString().substring(0, size - 24));
  }


  @Test
  public void organConversionTest() {
    String organ = TransplantWaitingList.organConversion("middle ear");
    assertEquals("middleEars", organ);

    organ = TransplantWaitingList.organConversion("bone marrow");
    assertEquals("boneMarrow", organ);

    organ = TransplantWaitingList.organConversion("connective tissue");
    assertEquals("connectiveTissue", organ);
  }

  @Test
  public void receiverRequiresOrganTest() {
    ReceiverRecord receiverRecord = new ReceiverRecord("AAA1111", "Canterbury", LocalDateTime.now(),
        "Liver");
    boolean response = TransplantWaitingList
        .receiverRequiresOrgan(receiverRecord, "liver", "liver");
    assertTrue(response);

    response = TransplantWaitingList.receiverRequiresOrgan(receiverRecord, "liver", "something");
    assertTrue(response);

    response = TransplantWaitingList.receiverRequiresOrgan(receiverRecord, "something", "hello");
    assertFalse(response);

  }

}
