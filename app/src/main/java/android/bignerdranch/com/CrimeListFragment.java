package android.bignerdranch.com;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

  private class CrimeHolder extends RecyclerView.ViewHolder {
    private Crime mCrime;
    private TextView mTitleTextView;
    private TextView mDateTextView;

    CrimeHolder(LayoutInflater inflator, ViewGroup parent) {
      super(inflator.inflate(R.layout.list_item_crime, parent, false));

      mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
      mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
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
      return new CrimeHolder(layoutInflater, parent);
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
  }
}
