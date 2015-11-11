package com.example.dagstaatje;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stofstik.dagstaatje.R;

public class ServerAddressDialogFragment extends DialogFragment {

	EditText etServer;
	EditText etPort;

	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface NoticeDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);

		public void onDialogNegativeClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	NoticeDialogListener mListener;

	// Override the Fragment.onAttach() method to instantiate the
	// NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (NoticeDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		View view = inflater.inflate(R.layout.dialog_server_address, null);
		etServer = (EditText) view.findViewById(R.id.etServerAddress);
		etPort = (EditText) view.findViewById(R.id.etServerPort);
		etServer.setText(ViewPagerActivity.serverAddress);
		etPort.setText("" + ViewPagerActivity.serverPort);
		builder.setView(view)
				.setTitle("Server instellen")
				// Add action buttons
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						ViewPagerActivity.serverAddress = etServer.getText()
								.toString();
						int port;
						try {
							port = Integer
									.parseInt(etPort.getText().toString());
						} catch (Exception e) {
							port = 1337;
							Toast.makeText(getActivity(), "Invalid port. Default port set", Toast.LENGTH_LONG).show();
						}
						ViewPagerActivity.serverPort = port;
						mListener
								.onDialogPositiveClick(ServerAddressDialogFragment.this);
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mListener
										.onDialogNegativeClick(ServerAddressDialogFragment.this);
							}
						});
		return builder.create();
	}
}
