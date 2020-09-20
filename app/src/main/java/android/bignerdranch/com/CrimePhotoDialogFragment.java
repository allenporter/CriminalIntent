package android.bignerdranch.com;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class CrimePhotoDialogFragment extends DialogFragment {
  private static final String ARG_CRIME_ID = "crime_id";

  private File mPhotoFile;
  private ImageView mPhotoView;

  public static CrimePhotoDialogFragment newInstance(UUID crimeId) {
    Bundle args = new Bundle();
    args.putSerializable(ARG_CRIME_ID, crimeId);

    CrimePhotoDialogFragment fragment = new CrimePhotoDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
    Crime crime = CrimeLab.get(getActivity()).getCrime(crimeId);
    mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(crime);

    setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    View view = LayoutInflater.from(getActivity())
      .inflate(R.layout.fragment_crime_photo, null);

    mPhotoView = (ImageView) view.findViewById(R.id.crime_photo_zoom);
    ViewTreeObserver observer = mPhotoView.getViewTreeObserver();
    observer.addOnGlobalLayoutListener(() -> updatePhotoView());
    mPhotoView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });

    return view;
  }

  private void updatePhotoView() {
    if (mPhotoFile == null | !mPhotoFile.exists()) {
      mPhotoView.setImageDrawable(null);
    } else {
      Bitmap bitmap = BitmapUtils.getScaledBitmap(
        mPhotoFile.getPath(), getActivity());
      mPhotoView.setImageBitmap(bitmap);
    }
  }
}
