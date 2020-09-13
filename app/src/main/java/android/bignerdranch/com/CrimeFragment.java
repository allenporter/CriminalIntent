package android.bignerdranch.com;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Date;
import java.util.UUID;

public final class CrimeFragment extends Fragment {
  private static final String ARG_CRIME_ID = "crime_id";
  private static final String DIALOG_DATE = "DialogDate";
  private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

  private static final int REQUEST_DATE = 0;
  private static final int REQUEST_CONTACT = 1;

  private Crime mCrime;
  private EditText mTitleField;
  private Button mDateButton;
  private CheckBox mSovledCheckBox;
  private Button mDeleteButton;
  private Button mSuspectButton;
  private Button mReportButton;

  public static CrimeFragment newInstance(UUID crimeId) {
    Bundle args = new Bundle();
    args.putSerializable(ARG_CRIME_ID, crimeId);

    CrimeFragment fragment = new CrimeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
    mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
  }

  @Override
  public void onPause() {
    super.onPause();
    CrimeLab.get(getActivity()).updateCrime(mCrime);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_crime, container, false);

    mTitleField = (EditText) v.findViewById(R.id.crime_title);
    mTitleField.setText(mCrime.getTitle());
    mTitleField.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mCrime.setTitle(charSequence.toString());
      }

      @Override
      public void afterTextChanged(Editable editable) { }
    });

    mDateButton = (Button) v.findViewById(R.id.crime_date);
    mDateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        FragmentManager manager = getFragmentManager();
        DatePickerFragment dialog = DatePickerFragment
          .newInstance(mCrime.getDate());
        dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
        dialog.show(manager, DIALOG_DATE);
      }
    });
    updateDate();

    mSovledCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
    mSovledCheckBox.setChecked(mCrime.isSolved());
    mSovledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mCrime.setSolved(b);
      }
    });

    mDeleteButton = (Button) v.findViewById(R.id.delete_button);
    mDeleteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        CrimeLab.get(getActivity()).removeCrime(mCrime.getId());
        getActivity().finish();
      }
    });

    final Intent pickContact = new Intent(Intent.ACTION_PICK,
      ContactsContract.Contacts.CONTENT_URI);
    mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
    mSuspectButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivityForResult(pickContact, REQUEST_CONTACT);
      }
    });
    if (mCrime.getSuspect() != null) {
      mSuspectButton.setText(mCrime.getSuspect());
    }
    // TODO(allen): This is meant to hide the contact picker when unsupported by the device,
    //  however it also hides it in the simulator which does have a contact app.  Figure out
    //  how to add this back in, fix the simulator, or do without since most devices should have
    //  a contact picker.
    /*
    PackageManager packageManager = getActivity().getPackageManager();
    if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
      mSuspectButton.setEnabled(false);
    }
    */

    mReportButton = (Button) v.findViewById(R.id.crime_report);
    mReportButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
        i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
        i = Intent.createChooser(i, getString(R.string.send_report));
        startActivity(i);
      }
    });

    return v;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (resultCode != Activity.RESULT_OK || data == null) {
      return;
    }
    if (requestCode == REQUEST_DATE) {
      Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
      mCrime.setDate(date);
      updateDate();
    }
    if (requestCode == REQUEST_CONTACT) {
      Uri contactUri = data.getData();
      String[] queryFields = new String[] {
        ContactsContract.Contacts.DISPLAY_NAME
      };
      Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
      try {
        if (c.getCount() == 0) {
          return;
        }
        c.moveToFirst();
        String suspect = c.getString(0);
        mCrime.setSuspect(suspect);
        mSuspectButton.setText(suspect);
      } finally {
        c.close();
      }
    }
  }

  private void updateDate() {
    mDateButton.setText(DateFormat.getLongDateFormat(getContext()).format(mCrime.getDate()));
  }

  private String getCrimeReport() {
    String solvedString;
    if (mCrime.isSolved()) {
      solvedString = getString(R.string.crime_report_solved);
    } else {
      solvedString = getString(R.string.crime_report_unsolved);
    }
    String dateString = DateFormat.getLongDateFormat(getContext()).format(mCrime.getDate());
    String suspect;
    if (mCrime.getSuspect() == null) {
      suspect = getString(R.string.crime_report_no_suspect);
    } else {
      suspect = getString(R.string.crime_report_suspect, mCrime.getSuspect());
    }

    return getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
  }
}
