package android.bignerdranch.com;

import java.util.Date;
import java.util.UUID;

final class Crime {
  private UUID mId;
  private String mTitle;
  private Date mDate;
  private boolean mSolved;
  private boolean mRequiresPolice;
  private String mSuspect;

  private String mSuspectPhoneNumber;

  public Crime() {
    this(UUID.randomUUID());
  }

  public Crime(UUID id) {
    mId = id;
    mDate = new Date();
  }

  public UUID getId() {
    return mId;
  }

  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String title) {
    this.mTitle = title;
  }

  public Date getDate() {
    return mDate;
  }

  public void setDate(Date date) {
    this.mDate = date;
  }

  public boolean isSolved() {
    return mSolved;
  }

  public void setSolved(boolean solved) {
    this.mSolved = solved;
  }

  public boolean isRequiresPolice() {
    return mRequiresPolice;
  }

  public void setRequiresPolice(boolean requiresPolice) {
    this.mRequiresPolice = requiresPolice;
  }

  public String getSuspect() {
    return mSuspect;
  }

  public void setSuspect(String suspect) {
    this.mSuspect = suspect;
  }

  public String getSuspectPhoneNumber() {
    return mSuspectPhoneNumber;
  }

  public void setSuspectPhoneNumber(String suspectPhoneNumber) {
    this.mSuspectPhoneNumber = suspectPhoneNumber;
  }

  public String getPhotoFilename() {
    return "IMG_" + getId().toString() + ".jpg";
  }
}
