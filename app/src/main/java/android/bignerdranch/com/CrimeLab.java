package android.bignerdranch.com;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton for holding crime records.
 *
 * Note, this class is not threadsafe.
 */
final class CrimeLab {
  private static CrimeLab sCrimeLab;
  private final SQLiteDatabase mDb;
  private final Map<UUID, Crime> mCrimes = new LinkedHashMap<>();

  public static CrimeLab get(Context context) {
    if (sCrimeLab == null) {
      SQLiteDatabase db = new CrimeBaseHelper(context).getWritableDatabase();
      sCrimeLab = new CrimeLab(db);
    }
    return sCrimeLab;
  }

  private CrimeLab(SQLiteDatabase mDb) {
    this.mDb = mDb;
  }

  public List<Crime> getCrimes() {
    return new ArrayList<Crime>(mCrimes.values());
  }

  @Nullable
  public Crime getCrime(UUID id) {
    return mCrimes.get(id);
  }

  public void addCrime(Crime c) {
    if (mCrimes.containsKey(c.getId())) {
      throw new IllegalArgumentException("Crime with id '" + c.getId() + "' already exists");
    }
    mCrimes.put(c.getId(), c);
  }

  public void removeCrime(UUID id) {
    mCrimes.remove(id);
  }
}
