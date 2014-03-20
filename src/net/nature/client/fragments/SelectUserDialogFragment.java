package net.nature.client.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SelectUserDialogFragment extends DialogFragment {

	static SelectUserDialogFragment newInstance(int num) {
    	SelectUserDialogFragment f = new SelectUserDialogFragment();

        // Supply num input as an argument.
//        Bundle args = new Bundle();
//        args.putInt("num", num);
//        f.setArguments(args);

        return f;
    }

	
//	@Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
//        View tv = v.findViewById(R.id.text);
//        ((TextView)tv).setText("Dialog #" + mNum + ": using style "
//                + getNameForNum(mNum));
//
//        // Watch for button clicks.
//        Button button = (Button)v.findViewById(R.id.show);
//        button.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                // When button is clicked, call up to owning activity.
//                ((FragmentDialog)getActivity()).showDialog();
//            }
//        });
//
//        return v;
//    }
}
