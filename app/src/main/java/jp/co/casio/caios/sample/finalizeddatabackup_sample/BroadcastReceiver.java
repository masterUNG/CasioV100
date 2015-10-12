package jp.co.casio.caios.sample.finalizeddatabackup_sample;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
	private final static String ACTION_ReginfoFinalized = "ReginfoFinalized";
	public final static String EXTRA_CONSECNUMBER = "CONSECNUMBER";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_ReginfoFinalized)) {
			// Set parameter to access database.
			String consecNumber = intent.getStringExtra(EXTRA_CONSECNUMBER);
			Log.d("test", "consecNumber ==> " + consecNumber);
			// Start database access service.
			Intent sendIntent = new Intent(context, DatabaseBackupService.class);
			sendIntent.putExtra(EXTRA_CONSECNUMBER, consecNumber);
			context.startService(sendIntent);
		}

	}

}
