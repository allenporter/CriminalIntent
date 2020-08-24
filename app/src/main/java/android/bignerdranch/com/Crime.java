package android.bignerdranch.com;

import java.util.Date;
import java.util.UUID;

final class Crime {
  private UUID mId;
  private String mTitle;
  private Date mDate;
  private boolean mSolved;

  Crime() {
    mId = UUID.randomUUID();
    mDate = new Date();
  }
}
