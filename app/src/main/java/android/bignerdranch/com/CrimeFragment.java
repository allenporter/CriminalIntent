package android.bignerdranch.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.UUID;

public class CrimeFragment extends Fragment {
  private static final String ARG_CRIME_ID = "crime_id";
  private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

  private Crime mCrime;
  private EditText mTitleField;
  private Button mDateButton;
  private CheckBox mSovledCheckBox;
  
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
    mCrime = CrimeLab.get().getCrime(crimeId);
  }

  @Nullable
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
    mDateButton.setText(mCrime.getDate().toString());
    mDateButton.setEnabled(false);

    mSovledCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
    mSovledCheckBox.setChecked(mCrime.isSolved());
    mSovledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mCrime.setSolved(b);
      }
    });

    return v;
  }
}
