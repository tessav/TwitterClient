package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.codepath.apps.restclienttemplate.R;

/**
 * Created by tessavoon on 10/1/17.
 */

public class SaveDraftFragment extends DialogFragment {

    public final static boolean SAVE = true;
    public final static boolean DELETE = false;

    public SaveDraftFragment() {

    }

    public static SaveDraftFragment newInstance() {
        SaveDraftFragment frag = new SaveDraftFragment();
        return frag;
    }

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        public void onItemSelected(boolean isSave);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_save_draft, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnYes = (Button) view.findViewById(R.id.btnYes);
        Button btnNo = (Button) view.findViewById(R.id.btnNo);
        btnYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.onItemSelected(SAVE);
                removeFragment();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.onItemSelected(DELETE);
                removeFragment();
            }
        });

    }

    private void removeFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement SaveDraftFragment.OnItemSelectedListener");
        }
    }

}
