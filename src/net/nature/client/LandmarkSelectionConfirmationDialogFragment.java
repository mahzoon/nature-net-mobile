package net.nature.client;

import net.nature.client.database.Landmark;
import net.nature.client.database.LandmarkLibrary;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class LandmarkSelectionConfirmationDialogFragment extends DialogFragment {
			
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	final int id = getArguments() != null ? getArguments().getInt("id") : -1;    	
    	final Landmark landmark = (new LandmarkLibrary(getResources()))
    			.getLandmark(id);
    	
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Select this landmark?\n" + landmark.toString())
               .setPositiveButton("Yes/Select", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	// Send the positive button event back to the host activity
                       mListener.onDialogPositiveClick(LandmarkSelectionConfirmationDialogFragment.this, landmark);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	// Send the negative button event back to the host activity
                       mListener.onDialogNegativeClick(LandmarkSelectionConfirmationDialogFragment.this);
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, Landmark landmark);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    
 // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}