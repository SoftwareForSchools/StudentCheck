package com.hva.symposiumcheckin.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.hva.symposiumcheckin.MainActivity;
import com.hva.symposiumcheckin.R;

/**
 * Created by jelle on 24-1-2018.
 */

public class CheckInStudentNumberDialogFragment extends DialogFragment implements View.OnClickListener {
    private Button addStudentNumber;
    private EditText studentNumberText;

    // Reference to main activity
    private MainActivity mContext;

    public static CheckInStudentNumberDialogFragment newInstance() {
        return new CheckInStudentNumberDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // We need a reference to the main activity, we get this in onAttach
        // When a developer opens this fragment from a wrong class, the exception is thrown
        if (context instanceof MainActivity) {
            mContext = (MainActivity) context;
        } else {
            throw new IllegalStateException("Start this function from the MainActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_in_via_student_number_fragment, container, false);

        Button cancelAddingStudent = view.findViewById(R.id.cancel_adding_student_number);
        cancelAddingStudent.setOnClickListener(this);

        addStudentNumber = view.findViewById(R.id.check_in_student_number);
        addStudentNumber.setOnClickListener(this);

        studentNumberText = view.findViewById(R.id.student_number_input);
        studentNumberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Student numbers now have a length of 9, so an user can't accept when the length is not 9
                if (s.length() != 9) {
                    addStudentNumber.setEnabled(false);
                    addStudentNumber.setTextColor(getResources().getColor(R.color.colorAccentTransparent));
                } else {
                    addStudentNumber.setEnabled(true);
                    addStudentNumber.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_adding_student_number:
                dismiss();
                break;
            case R.id.check_in_student_number:
                String studentNumber = studentNumberText.getText().toString();
                // Check if the student numbers really contain numbers
                if (studentNumber.matches("[0-9]+")) {
                    mContext.dbHelper.checkStudentIn(studentNumber);
                    dismiss();
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.student_numbers_only_numbers), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        // When the fragment is dismissed it is closed, so change boolean to false
        mContext.checkInFragmentOpen = false;
    }
}
