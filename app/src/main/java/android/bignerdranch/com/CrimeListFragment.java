package android.bignerdranch.com;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public final class CrimeListFragment extends Fragment {
  private static final int REQUEST_CRIME = 1;
  private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

  private CrimeLab mCrimeLab;
  private RecyclerView mCrimeRecyclerView;
  private CrimeAdapter mAdapter;
  private boolean mSubtitleVisible = false;
  private Callback mCallbacks;

  public interface Callback {
    void onCrimeSelected(Crime crime);
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
    mCrimeLab = CrimeLab.get(getActivity());
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

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DeleteSwipeTouchHelper());
    itemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);

    updateUI();
    
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    mAdapter.setCrimes(mCrimeLab.getCrimes());
    updateUI();
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
        mCrimeLab.addCrime(crime);
        updateUI();
        mCallbacks.onCrimeSelected(crime);
        return true;
      case R.id.show_subtitle:
        mSubtitleVisible = !mSubtitleVisible;
        getActivity().invalidateOptionsMenu();
        updateUI();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void updateUI() {
    int crimeCount = mCrimeLab.getCrimes().size();

    String subtitle = getResources()
      .getQuantityString(R.plurals.subtitle_plurals, crimeCount, crimeCount);
    if (!mSubtitleVisible) {
      subtitle = null;
    }
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.getSupportActionBar().setSubtitle(subtitle);

    mAdapter = new CrimeAdapter(mCrimeLab.getCrimes());
    mCrimeRecyclerView.setAdapter(mAdapter);
    mAdapter.notifyDataSetChanged();
  }

  private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Crime mCrime;
    private TextView mTitleTextView;
    private TextView mDateTextView;
    private ImageView mSolvedImageView;

    CrimeHolder(LayoutInflater inflator, ViewGroup parent, int viewType) {
      super(inflator.inflate(viewType, parent, false));

      mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
      if (viewType == R.layout.list_item_crime) {
        mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
        mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        itemView.setOnClickListener(this);
      } else if (viewType ==  R.layout.list_item_crime_requires_police){
        mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
        // R.layout.list_item_crime_requires_police does not have the solved image
        mSolvedImageView = null;
        itemView.setOnClickListener(this);
      }
    }

    @Override
    public void onClick(View view) {
      if (mCrime == null) {
        return;
      }
      mCallbacks.onCrimeSelected(mCrime);
    }

    public void bindPlaceholder() {
      mCrime = null;
      mTitleTextView.setText(R.string.no_crimes_label);
    }

    public void bind(Crime crime) {
      mCrime = crime;
      mTitleTextView.setText(mCrime.getTitle());
      mDateTextView.setText(DateFormat.getLongDateFormat(getContext()).format(mCrime.getDate()));
      if (mSolvedImageView != null) {
        mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
      }
    }

    Crime getCrime() {
      return mCrime;
    }
  }

  private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
    private List<Crime> mCrimes;

    CrimeAdapter(List<Crime> crimes) {
      this.mCrimes = crimes;
    }

    public void setCrimes(List<Crime> crimes) {
      this.mCrimes = crimes;
    }

    @NonNull
    @Override
    public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
      return new CrimeHolder(layoutInflater, parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
      if (mCrimes.isEmpty()) {
        holder.bindPlaceholder();
        return;
      }
      Crime crime = mCrimes.get(position);
      holder.bind(crime);
    }

    @Override
    public int getItemCount() {
      if (mCrimes.isEmpty()) {
        return 1;
      }
      return mCrimes.size();
    }

    @Override
    public int getItemViewType(int position) {
      if (mCrimes.isEmpty()) {
        return R.layout.list_item_no_crimes;
      }
      Crime crime = mCrimes.get(position);
      if (crime.isRequiresPolice() && !crime.isSolved()) {
        return R.layout.list_item_crime_requires_police;
      }
      return R.layout.list_item_crime;
    }
  }

  private class DeleteSwipeTouchHelper extends ItemTouchHelper.SimpleCallback {

    DeleteSwipeTouchHelper() {
      super(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
      return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
      if (direction == ItemTouchHelper.LEFT) {
        CrimeHolder crimeHolder = (CrimeHolder) viewHolder;
        Crime crime = crimeHolder.getCrime();
        CrimeLab.get(getActivity()).removeCrime(crime.getId());
        updateUI();
        mCallbacks.onCrimeSelected(null);
      }
    }
  }



}
