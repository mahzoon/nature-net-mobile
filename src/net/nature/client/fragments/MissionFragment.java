package net.nature.client.fragments;

import net.nature.client.R;
import net.nature.client.R.id;
import net.nature.client.R.layout;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MissionFragment extends Fragment  {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.mission_item, null);

		Bundle bundle=getArguments(); 
		//Mission mission = (Mission) bundle.get("mission");
		String question = bundle.getString("question");
		String objective = bundle.getString("objective");

		TextView txt = (TextView) 
				rootView.findViewById(R.id.textViewQuestion);
		txt.setText(question);

		txt = (TextView) 
				rootView.findViewById(R.id.textViewObjective);
		txt.setText(objective);


		return rootView;
	}
}
