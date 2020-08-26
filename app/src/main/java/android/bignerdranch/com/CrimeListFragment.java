package android.bignerdranch.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment {
  private static final int REQUEST_CRIME = 1;

  private RecyclerView mCrimeRecyclerView;
  private CrimeAdapter mAdapter;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

    mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
    mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mCrimeRecyclerView.setAdapter(getAdapter());

    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    getAdapter().notifyDataSetChanged();
  }

  private CrimeAdapter getAdapter() {
    if (mAdapter == null) {
      CrimeLab lab = CrimeLab.get();
      List<Crime> crimes = lab.getCrimes();
      mAdapter = new CrimeAdapter(crimes);
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
    private List<Crime> mCrimes;

    CrimeAdapter(List<Crime> crimes) {
      mCrimes = crimes;
    }

    @NonNull
    @Override
    public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
      return new CrimeHolder(layoutInflater, parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
      Crime crime = mCrimes.get(position);
      holder.bind(crime);
    }

    @Override
    public int getItemCount() {
      return mCrimes.size();
    }

    @Override
    public int getItemViewType(int position) {
      Crime crime = mCrimes.get(position);
      if (crime.isRequiresPolice() && !crime.isSolved()) {
        return R.layout.list_item_crime_requires_police;
      }
      return R.layout.list_item_crime;
    }
  }
}
