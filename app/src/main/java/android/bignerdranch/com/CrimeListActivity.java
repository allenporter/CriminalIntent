package android.bignerdranch.com;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public final class CrimeListActivity extends SingleFragmentActivity
  implements CrimeListFragment.Callback, CrimeFragment.Callback {

  @Override
  protected Fragment createFragment() {
    return new CrimeListFragment();
  }

  @Override
  protected int getLayoutResId() {
    return R.layout.activity_masterdetail;
  }

  @Override
  public void onCrimeSelected(@Nullable Crime crime) {
    if (findViewById(R.id.detail_fragment_container) == null) {
      if (crime != null) {
        Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
        startActivity(intent);
      }
    } else {
      FragmentManager manager = getSupportFragmentManager();
      if (crime != null) {
        Fragment newDetail = CrimeFragment.newInstance(crime.getId());
        manager.beginTransaction()
          .replace(R.id.detail_fragment_container, newDetail)
          .commit();
      } else {
        Fragment existing = manager.findFragmentById(R.id.detail_fragment_container);
        if (existing != null) {
          manager.beginTransaction()
            .remove(existing)
            .commit();
        }
      }
    }
  }

  @Override
  public void onCrimeUpdated(@Nullable Crime crime) {
    CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
      .findFragmentById(R.id.fragment_container);
    listFragment.updateUI();
    if (crime == null) {
      onCrimeSelected(null);
    }
  }
}
