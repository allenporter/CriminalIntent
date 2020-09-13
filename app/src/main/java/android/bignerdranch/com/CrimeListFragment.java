package android.bignerdranch.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

public final class CrimeListFragment extends Fragment {
  private static final int REQUEST_CRIME = 1;
  private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

  private RecyclerView mCrimeRecyclerView;
  private CrimeAdapter mAdapter;
  private boolean mSubtitleVisible = false;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

    if (savedInstanceState != null) {
      mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
    }

    mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
    mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mCrimeRecyclerView.setAdapter(getAdapter());
    updateSubtitle();
    
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    getAdapter().notifyDataSetChanged();
    updateSubtitle();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.fragment_crime_list, menu);
    MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
    if (mSubtitleVisible) {
      subtitleItem.setTitle(R.string.hide_subtitle);
    } else {
      subtitleItem.setTitle(R.string.show_subtitle);
    }
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.new_crime:
        Crime crime = new Crime();
        CrimeLab.get().addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
        startActivity(intent);
        return true;
      case R.id.show_subtitle:
        mSubtitleVisible = !mSubtitleVisible;
        getActivity().invalidateOptionsMenu();
        updateSubtitle();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void updateSubtitle() {
    CrimeLab crimeLab = CrimeLab.get();
    int crimeCount = crimeLab.getCrimes().size();

    String subtitle = getString(R.string.subtitle_format, crimeCount);
    if (!mSubtitleVisible) {
      subtitle = null;
    }
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.getSupportActionBar().setSubtitle(subtitle);
  }

  private CrimeAdapter getAdapter() {
    if (mAdapter == null) {
      mAdapter = new CrimeAdapter(CrimeLab.get());
    }
    return mAdapter;
  }

  private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Crime mCrime;
    private TextView mTitleTextView;
    private TextView mDateTextView;
    private ImageView mSolvedImageView;

    CrimeHolder(LayoutInflater inflator, ViewGroup parent, int viewType) {
      super(inflator.inflate(viewType, parent, false));

      mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
      mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
      if (viewType == R.layout.list_item_crime) {
        mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
      } else {
        // R.layout.list_item_crime_requires_police does not have the solved image
        mSolvedImageView = null;
      }

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
      startActivityForResult(intent, REQUEST_CRIME);
    }

    public void bind(Crime crime) {
      mCrime = crime;
      mTitleTextView.setText(mCrime.getTitle());
      mDateTextView.setText(DateFormat.getLongDateFormat(getContext()).format(mCrime.getDate()));
      if (mSolvedImageView != null) {
        mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
      }
    }
  }

  private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
    private CrimeLab mLab;

    CrimeAdapter(CrimeLab lab) {
      this.mLab = lab;
    }

    @NonNull
    @Override
    public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
      return new CrimeHolder(layoutInflater, parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
      Crime crime = mLab.getCrimes().get(position);
      holder.bind(crime);
    }

    @Override
    public int getItemCount() {
      return mLab.getCrimes().size();
    }

    @Override
    public int getItemViewType(int position) {
      Crime crime = mLab.getCrimes().get(position);
      if (crime.isRequiresPolice() && !crime.isSolved()) {
        return R.layout.list_item_crime_requires_police;
      }
      return R.layout.list_item_crime;
    }
  }
}
