package contactui;

import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactUILoader {
	
	public ContactUILoader(){
	}

	public LinearLayout loadContactLayout(LinearLayout contactLL) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 5, 0, 0);
		contactLL.setLayoutParams(params);
		contactLL.setBackgroundColor(Color.WHITE);
		contactLL.setOrientation(LinearLayout.HORIZONTAL);
		return contactLL;
	}

	public TextView loadTextView(TextView contactTV, String contactName) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT, 1f);
		params.setMargins(10, 0, 0, 0);
		contactTV.setLayoutParams(params);
		contactTV.setText(contactName + "");
		/* Set parameters of new TextView */
		contactTV.setTextSize(20);
		contactTV.setGravity(Gravity.LEFT);
		return contactTV;
	}

	public TextView loadStatusTV(TextView acceptedTV, String text) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 50);
		params.setMargins(0, 0, 10, 0);
		acceptedTV.setLayoutParams(params);
		acceptedTV.setText(text);
		/* Set parameters of new TextView */
		acceptedTV.setTextSize(15);
		acceptedTV.setTextColor(Color.LTGRAY);
		acceptedTV.setGravity(Gravity.CENTER_VERTICAL);
		return acceptedTV;
	}

	public Button loadRequestButton(Button button, int color, String text, String id) {
		LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 50);
		button.setLayoutParams(btnparams);
		button.setBackgroundColor(color);
		button.setText(text);
		button.setTextSize(20);
		button.setId(Integer.parseInt(id));
		return button;
	}

	public Button loadStatusBT(Button acceptedBT, String text) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 50);
		params.setMargins(0, 0, 10, 0);
		acceptedBT.setLayoutParams(params);
		acceptedBT.setText(text);
		/* Set parameters of new TextView */
		acceptedBT.setTextSize(15);
		acceptedBT.setTextColor(Color.LTGRAY);
		acceptedBT.setGravity(Gravity.CENTER_VERTICAL);
		return acceptedBT;
	}
}
