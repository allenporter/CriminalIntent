package android.bignerdranch.com;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CrimeListFragment extends Fragment {
  private RecyclerView mCrimeRecyclerView;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

    mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
    mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mCrimeRecyclerView.setAdapter(createAdapter());

    return view;
  }

  private CrimeAdapter createAdapter() {
    CrimeLab lab = CrimeLab.get();
    List<Crime> crimes = lab.getCrimes();
    return new CrimeAdapter(crimes);
  }

  private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Crime mCrime;
    private TextView mTitleTextView;
    private TextView mDateTextView;

    CrimeHolder(LayoutInflater inflator, ViewGroup parent, int viewType) {
      super(inflator.inflate(viewType, parent, false));

      mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
      mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      Toast.makeText(getActivity(),
        mCrime.getTitle() + " clicked!",
        Toast.LENGTH_SHORT)
        .show();
    }

    public void bind(Crime crime) {
      mCrime = crime;
      mTitleTextView.setText(mCrime.getTitle());
      mDateTextView.setText(mCrime.getDate().toString());
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
      if (crime.isRequiresPolice()) {
        return R.layout.list_item_crime_requires_police;
      }
      return R.layout.list_item_crime;
    }
  }
}
