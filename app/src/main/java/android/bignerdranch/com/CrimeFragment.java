package android.bignerdranch.com;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public final class CrimeFragment extends Fragment {
  private static final String ARG_CRIME_ID = "crime_id";
  private static final String DIALOG_DATE = "DialogDate";
  private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
  private static final String PHOTO_DIALOG_TAG = "photo_dialog";

  private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
  private static final int PERMISSIONS_REQUEST_CAMERA = 2;

  private static String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS};

  private static final int REQUEST_DATE = 0;
  private static final int REQUEST_CONTACT = 1;
  private static final int REQUEST_DIAL = 2;
  private static final int REQUEST_PHOTO = 3;

  private Crime mCrime;
  private File mPhotoFile;
  private EditText mTitleField;
  private Button mDateButton;
  private CheckBox mSovledCheckBox;
  private Button mDeleteButton;
  private Button mSuspectButton;
  private Button mReportButton;
  private Button mCallSuspectButton;
  private ImageButton mPhotoButton;
  private ImageView mPhotoView;
  private Callback mCallbacks;

  public interface Callback {
    void onCrimeUpdated(Crime crime);
  }

  public static CrimeFragment newInstance(UUID crimeId) {
    Bundle args = new Bundle();
    args.putSerializable(ARG_CRIME_ID, crimeId);

    CrimeFragment fragment = new CrimeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    mCallbacks = (Callback) context;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mCallbacks = null;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
    mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
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
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mCrime.setTitle(charSequence.toString());
        updateCrime();
      }

      @Override
      public void afterTextChanged(Editable editable) {
        //
      }
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

    mSovledCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
    mSovledCheckBox.setChecked(mCrime.isSolved());
    mSovledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mCrime.setSolved(b);
        updateCrime();
      }
    });

    mDeleteButton = (Button) v.findViewById(R.id.delete_button);
    mDeleteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        CrimeLab.get(getActivity()).removeCrime(mCrime.getId());
        mCallbacks.onCrimeUpdated(null);
      }
    });

    final Intent pickContact = new Intent(Intent.ACTION_PICK,
      ContactsContract.Contacts.CONTENT_URI);
    mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
    mSuspectButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        requestContactPermission();
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

    mCallSuspectButton = (Button) v.findViewById(R.id.call_suspect);
    mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // TODO: call suspect
        Intent intent = new Intent(Intent.ACTION_DIAL,
          Uri.parse(String.format("tel:%s", mCrime.getSuspectPhoneNumber())));
        startActivityForResult(intent, REQUEST_DIAL);
      }
    });


    mReportButton = (Button) v.findViewById(R.id.crime_report);
    mReportButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = ShareCompat.IntentBuilder.from(getActivity())
          .setType("text/plain")
          .setSubject(getString(R.string.crime_report_subject))
          .setText(getCrimeReport())
          .setChooserTitle(getString(R.string.send_report))
          .createChooserIntent();
        startActivity(i);
      }
    });
    PackageManager packageManager = getActivity().getPackageManager();
    final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // TODO(aporter): Ask for camera permissions.  This currently never resolves the activity right.
    //boolean canTakePhoto = mPhotoFile != null &&
    //  captureImage.resolveActivity(packageManager) != null;
    //mPhotoButton.setEnabled(canTakePhoto);

    mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
    mPhotoButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Uri uri = FileProvider.getUriForFile(getActivity(),
          "com.bignerdranch.android.criminalintent.fileprovider",
          mPhotoFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        List<ResolveInfo> cameraActivities = getActivity()
          .getPackageManager().queryIntentActivities(captureImage,
            PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo activity : cameraActivities) {
          getActivity().grantUriPermission(activity.activityInfo.packageName,
            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        startActivityForResult(captureImage, REQUEST_PHOTO);
      }
    });

    mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
    mPhotoView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mPhotoFile != null && mPhotoFile.exists()) {
          CrimePhotoDialogFragment fragment = CrimePhotoDialogFragment.newInstance(mCrime.getId());
          fragment.show(getFragmentManager(), PHOTO_DIALOG_TAG);
        }
      }
    });
    ViewTreeObserver observer = mPhotoView.getViewTreeObserver();
    observer.addOnGlobalLayoutListener(() -> updatePhotoView());

    updateState();

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
      updateCrime();
      updateState();
    }
    if (requestCode == REQUEST_CONTACT) {
      Uri contactUri = data.getData();
      String id = contactUri.getLastPathSegment();
      String[] queryFields = new String[] {
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.HAS_PHONE_NUMBER,
      };
      ContentResolver contentResolver = getActivity().getContentResolver();
      Cursor c = contentResolver.query(
        contactUri,  //contactUri,
        queryFields,
        null, null, null);
       // ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id }, null);
      String contactId;
      boolean hasPhoneNumber;
      try {
        if (c.getCount() == 0) {
          return;
        }
        c.moveToFirst();
        contactId = c.getString(0);
        String suspect = c.getString(1);
        hasPhoneNumber = (c.getInt(2) != 0);
        mCrime.setSuspect(suspect);
        updateCrime();
        mSuspectButton.setText(suspect);
      } finally {
        c.close();
      }
      if (contactId != null && hasPhoneNumber) {
        Cursor cursor = contentResolver.query(
          ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
          null,
          ContactsContract.CommonDataKinds.Phone._ID + " = " + contactId,
          null, null);
        try {
          if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            mCrime.setSuspectPhoneNumber(phoneNumber);
            updateCrime();
          }
        } finally {
          cursor.close();
        }
      }
      updateState();
    }
    if (requestCode == REQUEST_PHOTO) {
      Uri uri = FileProvider.getUriForFile(getActivity(),
      "com.bignerdranch.android.criminalintent.fileprovider",
        mPhotoFile);
      getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
      updateCrime();
      updatePhotoView();
    }
  }

  private void requestContactPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
          android.Manifest.permission.READ_CONTACTS)) {
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
          builder.setTitle("Read Contacts permission");
          builder.setPositiveButton(android.R.string.ok, null);
          builder.setMessage("Please enable access to contacts.");
          builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onDismiss(DialogInterface dialog) {
              requestPermissions(
                new String[]
                  {android.Manifest.permission.READ_CONTACTS}
                , PERMISSIONS_REQUEST_READ_CONTACTS);
            }
          });
          builder.show();
        } else {
          ActivityCompat.requestPermissions(getActivity(),
            new String[]{android.Manifest.permission.READ_CONTACTS},
            PERMISSIONS_REQUEST_READ_CONTACTS);
        }
      }
    }
  }


  private void requestCameraPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
          android.Manifest.permission.CAMERA)) {
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
          builder.setTitle("Cameras permission");
          builder.setPositiveButton(android.R.string.ok, null);
          builder.setMessage("Please enable access to camera.");
          builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onDismiss(DialogInterface dialog) {
              requestPermissions(
                new String[]
                  {Manifest.permission.CAMERA}
                , PERMISSIONS_REQUEST_CAMERA);
            }
          });
          builder.show();
        } else {
          ActivityCompat.requestPermissions(getActivity(),
            new String[]{android.Manifest.permission.CAMERA},
            PERMISSIONS_REQUEST_CAMERA);
        }
      }
    }
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

  private void updateCrime() {
    CrimeLab.get(getActivity()).updateCrime(mCrime);
    mCallbacks.onCrimeUpdated(mCrime);
  }

  private void updateState() {
    mDateButton.setText(DateFormat.getLongDateFormat(getContext()).format(mCrime.getDate()));
    int isVisible = (mCrime.getSuspectPhoneNumber() != null) ? View.VISIBLE : View.GONE;
    mCallSuspectButton.setVisibility(isVisible);
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
