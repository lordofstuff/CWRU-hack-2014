package com.appsofawesome.soundcontrol;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

/**
 * incomplete support for the locale/tasker API.
 * @author Stephen
 *
 */
public class EditActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_edit, container,
					false);
			return rootView;
		}
	}
	
	public void cancel(View view) { 
		Intent i = new Intent();
		i.putExtra("Result", Activity.RESULT_CANCELED);
//		i.
	}
	
	public void ok(View view) {
		final Intent resultIntent = new Intent();

        /*
         * This extra is the data to ourselves: either for the Activity or the BroadcastReceiver. Note
         * that anything placed in this Bundle must be available to Locale's class loader. So storing
         * String, int, and other standard objects will work just fine. Parcelable objects are not
         * acceptable, unless they also implement Serializable. Serializable objects must be standard
         * Android platform objects (A Serializable class private to this plug-in's APK cannot be
         * stored in the Bundle, as Locale's classloader will not recognize it).
         */
		String message = "MESSAGE!";
        final Bundle resultBundle =
                PluginBundleManager.generateBundle(getApplicationContext(), message);
        resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);

        /*
         * The blurb is concise status text to be displayed in the host's UI.
         */
        final String blurb = ("Loud mode");
        resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, blurb);

        setResult(RESULT_OK, resultIntent);
	}

}
