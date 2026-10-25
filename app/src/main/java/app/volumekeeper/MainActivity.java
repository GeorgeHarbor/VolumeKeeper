package app.volumekeeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

/** Tiny launcher screen: shows whether the service is on and opens Accessibility settings. */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button open = findViewById(R.id.open_settings);
        open.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        open.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView status = findViewById(R.id.status);
        status.setText(isServiceEnabled() ? R.string.status_on : R.string.status_off);
    }

    private boolean isServiceEnabled() {
        String flat = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return flat != null && flat.contains(getPackageName() + "/");
    }
}
