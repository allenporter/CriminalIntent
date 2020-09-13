package android.bignerdranch.com;

import java.util.Date;
import java.util.UUID;

final class Crime {
  private UUID mId;
  private String mTitle;
  private Date mDate;
  private boolean mSolved;
  private boolean mRequiresPolice;

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

  public void setTitle(String mTitle) {
    this.mTitle = mTitle;
  }

  public Date getDate() {
    return mDate;
  }

  public void setDate(Date mDate) {
    this.mDate = mDate;
  }

  public boolean isSolved() {
    return mSolved;
  }

  public void setSolved(boolean mSolved) {
    this.mSolved = mSolved;
  }

  public boolean isRequiresPolice() {
    return mRequiresPolice;
  }

  public void setRequiresPolice(boolean mRequiresPolice) {
    this.mRequiresPolice = mRequiresPolice;
  }
}
