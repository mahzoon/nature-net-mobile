package net.nature.client.activities;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;

//	
public class WaitDialog extends Dialog{

	public WaitDialog(Activity a){
		super(a);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ListView listview = new ListView(getContext());
		listview.setLayoutParams(new LayoutParams(200,200));
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		setContentView(listview);
		setTitle("Getting List of Users...");
	}
}