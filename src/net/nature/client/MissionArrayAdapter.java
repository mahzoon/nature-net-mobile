package net.nature.client;

import net.nature.client.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class MissionArrayAdapter extends ArrayAdapter<Mission>{
	 private final Context context;
	  private final Mission[] values;

	  public MissionArrayAdapter(Context context, Mission[] values) {
	    super(context, R.layout.mission_item, values);
	    this.context = context;
	    this.values = values;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.mission_item, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.textViewQuestion);
//	    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
	    textView.setText(values[position].getQuestion());
	    // Change the icon for Windows and iPhone
//	    Mission s = values[position];
//	    if (s.startsWith("iPhone")) {
//	      imageView.setImageResource(R.drawable.no);
//	    } else {
//	      imageView.setImageResource(R.drawable.ok);
//	    }

	    return rowView;
	  }

}
