package android.bignerdranch.com;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Singleton for holding crime records.
 *
 * Note, this class is not threadsafe.
 */
final class CrimeLab {
  private static CrimeLab sCrimeLab;
  private final Map<UUID, Crime> mCrimes = new LinkedHashMap<>();

  public static CrimeLab get() {
    if (sCrimeLab == null) {
      sCrimeLab = new CrimeLab();
    }
    return sCrimeLab;
  }

  private CrimeLab() { }

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
}
