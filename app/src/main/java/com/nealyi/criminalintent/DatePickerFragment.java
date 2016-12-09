package com.nealyi.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nealyi on 16/12/7.
 */

public class DatePickerFragment extends DialogFragment {

    private static final String TAG = DatePickerFragment.class.getSimpleName();
    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.nealyi.criminalintent.date";

    @BindView(R.id.dialog_date_date_picker)
    DatePicker mDatePicker;
    @BindView(R.id.btn_ok)
    TextView mBtnOk;

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Date date = (Date) getArguments().getSerializable(ARG_DATE);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
//        ButterKnife.bind(this, v);
//
//        mDatePicker.init(year, month, day, null);
//
//        return new AlertDialog.Builder(getActivity())
//                .setView(v)
//                .setTitle(R.string.date_picker_title)
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        int year = mDatePicker.getYear();
//                        int month = mDatePicker.getMonth();
//                        int day = mDatePicker.getDayOfMonth();
//                        Date date = new GregorianCalendar(year, month, day).getTime();
//                        sendResult(Activity.RESULT_OK, date);
//                    }
//                })
//                .create();
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_date_customize, container, false);
        ButterKnife.bind(this, view);

        initData();

        return view;
    }

    private void initData() {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mDatePicker.init(year, month, day, null);
    }

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, Date date) {
        Log.d(TAG, "sendResult:resultCode " + resultCode);
        Log.d(TAG, "sendResult:date " + DateFormat.format("E, MMMM dd, yyyy", date));
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    @OnClick(R.id.btn_ok)
    public void onClick() {
        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int day = mDatePicker.getDayOfMonth();
        Date date = new GregorianCalendar(year, month, day).getTime();
        sendResult(Activity.RESULT_OK, date);
        onStop();
    }
}
