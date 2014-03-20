package net.nature.client.activities;

import java.util.List;

import net.nature.client.R;
import net.nature.client.activities.SelectUserDialog.UserArrayAdapter.ViewHolder;
import net.nature.client.database.ApplicationData;
import net.nature.client.database.User;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

//	
public class SelectUserDialog extends Dialog{

	public interface OnUserSelectedListener{
		public void onUserSelected(User user);
	}

	public SelectUserDialog(Activity a){
		super(a);
	}

	private OnUserSelectedListener onUserSelectedListener; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ListView listview = new ListView(getContext());
		listview.setLayoutParams(new LayoutParams(200,200));
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setContentView(listview);
		
		List<User> users = ApplicationData.getAllUsers(getContext());
		User[] values = users.toArray(new User[]{});
		UserArrayAdapter adapter = new UserArrayAdapter(getContext(), values);
		listview.setAdapter(adapter);
		
		setTitle("Select a User");

		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				dismiss();

				ViewHolder holder = (UserArrayAdapter.ViewHolder) v.getTag();
				User user = holder.user;
				if (onUserSelectedListener != null){
					onUserSelectedListener.onUserSelected(user);
				}
			}

		});
	}

	public void setOnUserSelectedListener(OnUserSelectedListener onUserSelectedlistener) {
		this.onUserSelectedListener = onUserSelectedlistener;
	}
	
	static public class UserArrayAdapter extends ArrayAdapter<User> {
		private final Context context;
		private final User[] users;

		static public class ViewHolder {
			public TextView text;
			public ImageView image;
			public User user;
		}

		public UserArrayAdapter(Context context, User[] users) {
			super(context, R.layout.item_user, users);
			this.context = context;
			this.users = users;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				rowView = inflater.inflate(R.layout.item_user, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.text = (TextView) rowView.findViewById(R.id.textViewUsername);
				viewHolder.image = (ImageView) rowView
						.findViewById(R.id.imageViewAvatar);
				rowView.setTag(viewHolder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.user = users[position];
			
			String s = users[position].getName();
			holder.text.setText(s);

			String avatarName = users[position].getAvatarName();
			int id = context.getResources().getIdentifier(avatarName.toLowerCase(), "drawable", context.getPackageName());
			holder.image.setImageResource(id);
			return rowView;
		}
	} 
}