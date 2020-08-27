package android.bignerdranch.com;

import androidx.fragment.app.Fragment;

public final class CrimeListActivity extends SingleFragmentActivity {

  @Override
  protected Fragment createFragment() {
    return new CrimeListFragment();
  }
}
