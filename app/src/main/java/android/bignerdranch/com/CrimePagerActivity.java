package android.bignerdranch.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {
  private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

  private ViewPager2 mViewPager;
  private List<Crime> mCrimes;
  private FragmentStateAdapter mAdapter;
  private Button mFirstCrime;
  private Button mLastCrime;

  public static Intent newIntent(Context packageContext, UUID crimeId) {
    Intent intent = new Intent(packageContext, CrimePagerActivity.class);
    intent.putExtra(EXTRA_CRIME_ID, crimeId);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_crime_pager);

    UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

    mViewPager = (ViewPager2) findViewById(R.id.crime_view_pager);

    mFirstCrime = (Button) findViewById(R.id.first_crime);
    mFirstCrime.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                       mViewPager.setCurrentItem(0);
                                     }
                                   });

    mLastCrime = (Button) findViewById(R.id.last_crime);
    mLastCrime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mViewPager.setCurrentItem(mCrimes.size() - 1);
      }
    });

    mCrimes = CrimeLab.get().getCrimes();
    FragmentManager fragmentManager = getSupportFragmentManager();
    mViewPager.setAdapter(new Adapter(this));

    mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
          super.onPageSelected(position);
          mFirstCrime.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
          mLastCrime.setVisibility(position < mCrimes.size() ? View.VISIBLE : View.GONE);
        }
    });


    for (int i = 0 ; i < mCrimes.size(); ++i){
      if (mCrimes.get(i).getId().equals(crimeId)) {
        mViewPager.setCurrentItem(i);
        break;
      }
    }

  }

  private class Adapter extends FragmentStateAdapter {
    public Adapter(FragmentActivity fa) {
      super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
      Crime crime = mCrimes.get(position);
      return CrimeFragment.newInstance(crime.getId());
    }

    @Override
    public int getItemCount() {
      return mCrimes.size();
    }
  }
}
