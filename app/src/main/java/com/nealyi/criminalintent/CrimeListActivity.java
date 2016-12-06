package com.nealyi.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by nealyi on 16/12/6.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
