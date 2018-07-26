package seng302.model;

import java.time.LocalDateTime;

public class ReceiverRecord {

  String fullName;

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public ReceiverRecord(String fullName, String nhi, String region, LocalDateTime timestamp,
      String organ) {

    this.fullName = fullName;
    this.nhi = nhi;
    this.region = region;
    this.timestamp = timestamp;
    this.organ = organ;
  }

  String nhi;
  String region;
  LocalDateTime timestamp;
  String organ;

  public String getNhi() {
    return nhi;
  }

  public void setNhi(String nhi) {
    this.nhi = nhi;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getOrgan() {
    return organ;
  }

  public void setOrgan(String organ) {
    this.organ = organ;
  }

  public ReceiverRecord(String nhi, String region, LocalDateTime timestamp, String organ) {

    this.nhi = nhi;
    this.region = region;
    this.timestamp = timestamp;
    this.organ = organ;
  }
}
