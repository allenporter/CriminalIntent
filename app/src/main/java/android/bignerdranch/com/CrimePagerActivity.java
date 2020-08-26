package android.bignerdranch.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {
  private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

  private ViewPager2 mViewPager;
  private List<Crime> mCrimes;

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

    mCrimes = CrimeLab.get().getCrimes();
    FragmentManager fragmentManager = getSupportFragmentManager();
    mViewPager.setAdapter(new FragmentStateAdapter(fragmentManager, getLifecycle()) {
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
    });

    for (int i = 0 ; i < mCrimes.size(); ++i){
      if (mCrimes.get(i).getId().equals(crimeId)) {
        mViewPager.setCurrentItem(i);
        break;
      }
    }
  }
}
