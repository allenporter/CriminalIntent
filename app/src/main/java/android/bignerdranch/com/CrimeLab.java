package android.bignerdranch.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.io.File;
import java.lang.reflect.Array;
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
  private final Context mContext;

  public static CrimeLab get(Context context) {
    if (sCrimeLab == null) {
      SQLiteDatabase db = new CrimeBaseHelper(context).getWritableDatabase();
      sCrimeLab = new CrimeLab(context.getApplicationContext(), db);
    }
    return sCrimeLab;
  }

  private CrimeLab(Context context, SQLiteDatabase mDb) {
    this.mContext = context;
    this.mDb = mDb;
  }

  public List<Crime> getCrimes() {
    List<Crime> crimes = new ArrayList<>();
    CrimeCursorWrapper cursor = queryCrimes(null, null);
    try {
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        crimes.add(cursor.getCrime());
        cursor.moveToNext();
      }
    } finally {
      cursor.close();
    }
    return crimes;
  }

  @Nullable
  public Crime getCrime(UUID id) {
    CrimeCursorWrapper cursor = queryCrimes(
      CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
      new String[] { id.toString() });
    try {
      if (cursor.getCount() == 0) {
        return null;
      }
      cursor.moveToFirst();
      return cursor.getCrime();
    } finally {
      cursor.close();
    }
  }

  public void addCrime(Crime c) {
    ContentValues values = getContentValues(c);
    mDb.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
  }

  public void updateCrime(Crime crime) {
    String uuidString = crime.getId().toString();
    ContentValues values = getContentValues(crime);
    mDb.update(CrimeDbSchema.CrimeTable.NAME, values,
      CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
      new String[] { uuidString });
  }

  public void removeCrime(UUID id) {
    String uuidString = id.toString();
    mDb.delete(CrimeDbSchema.CrimeTable.NAME,
      CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
      new String[] { uuidString });
  }

  public File getPhotoFile(Crime crime) {
    File filesDir = mContext.getFilesDir();
    return new File(filesDir, crime.getPhotoFilename());
  }

  private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
    Cursor cursor = mDb.query(
      CrimeDbSchema.CrimeTable.NAME,
      null,
      whereClause,
      whereArgs,
      null,
      null,
      null
    );
    return new CrimeCursorWrapper(cursor);
  }

  private static ContentValues getContentValues(Crime crime) {
    ContentValues values = new ContentValues();
    values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
    values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
    values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
    values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
    values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());
    values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT_PHONE_NUMBER, crime.getSuspectPhoneNumber());
    return values;
  }
}
