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

  private CrimeLab() {
    for (int i = 0; i < 100; ++i) {
      Crime crime = new Crime();
      crime.setTitle("Crime #" + i);
      crime.setSolved(i % 2 == 0);
      crime.setRequiresPolice(i % 5 == 0);
      mCrimes.put(crime.getId(), crime);
    }
  }

  public List<Crime> getCrimes() {
    return new ArrayList<Crime>(mCrimes.values());
  }

  @Nullable
  public Crime getCrime(UUID id) {
    return mCrimes.get(id);
  }
}
