package com.nealyi.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nealyi on 16/12/6.
 */

public class CrimeListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISSIBLE = "subtitle";
    private static final int REQUEST_DELETE = 1;

    @BindView(R.id.crime_recycle_view)
    RecyclerView mCrimeRecycleView;

    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        ButterKnife.bind(this, view);
        mCrimeRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISSIBLE);
        }
        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecycleView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISSIBLE, mSubtitleVisible);
    }

    class CrimeHolder extends RecyclerView.ViewHolder {
        private Crime mCrime;

        @BindView(R.id.list_item_crime_title_text_view)
        TextView mTitleTextView;
        @BindView(R.id.list_item_crime_date_text_view)
        TextView mDateTextView;
        @BindView(R.id.list_item_crime_solved_check_box)
        CheckBox mSolvedCheckBox;

        public CrimeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
                    startActivityForResult(intent, REQUEST_DELETE);
                }
            });
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }
    }

    class EmptyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btn_new_crime)
        Button mButtonNewCrime;

        public EmptyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mButtonNewCrime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Crime crime = new Crime();
                    CrimeLab.get(getActivity()).addCrime(crime);
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                    startActivityForResult(intent, REQUEST_DELETE);
                }
            });
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter {
        private static final int VIEW_TYPE = -1;

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view;
            if (viewType == VIEW_TYPE) {
                view = layoutInflater.inflate(R.layout.list_item_crime_empty, parent, false);
                return new EmptyHolder(view);
            }
            view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CrimeHolder) {
                Crime crime = mCrimes.get(position);
                ((CrimeHolder) holder).bindCrime(crime);
            }
        }

        @Override
        public int getItemCount() {
            return mCrimes.size() > 0 ? mCrimes.size() : 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (mCrimes.size() <= 0) {
                return VIEW_TYPE;
            }
            return super.getItemViewType(position);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivityForResult(intent, REQUEST_DELETE);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab cirmeLab = CrimeLab.get(getActivity());
        int crimeSize = cirmeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeSize, crimeSize);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(subtitle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DELETE) {
            UUID crimeId = (UUID) data.getSerializableExtra(CrimeFragment.DELETE_CRIME_ID);
            Crime crime = CrimeLab.get(getActivity()).getCrime(crimeId);
            CrimeLab.get(getActivity()).removeCrime(crime);
            updateUI();
        }
    }
}

