package fr.xebia.futurecity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ListView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

public class NavigationActivity extends BaseActivity implements BeaconConsumer {

    private static final String TAG = "NavigationActivity";

    private static final String XEBIA_KONTACT_IO_UUID = "37e6a92a-f8ce-11e5-9ce9-5e5517507c66";
    private static final Region ALL_BEACONS_REGION = new Region(XEBIA_KONTACT_IO_UUID, null, null, null);
    private static final String KONTAKT_IO_BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";

    private BeaconManager beaconManager;
    private BeaconListAdapter adapter;
    List<Beacon> targetBeacons = new ArrayList<>();

    @Bind(R.id.destination) TextView destination;
    @Bind(R.id.device_list) ListView beaconList;
    @Bind(R.id.beacon_state) TextView beaconState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Configure device beaconList
        adapter = new BeaconListAdapter(this);
        beaconList.setAdapter(adapter);

        // Configure BeaconManager
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(KONTAKT_IO_BEACON_LAYOUT));
        beaconManager.bind(this);
    }

    private void checkPermissionAndStart() {
        int checkSelfPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (PackageManager.PERMISSION_GRANTED == checkSelfPermissionResult) {
            startScan();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //we should show some explanation for user here
//                showExplanationDialog();
            } else {
                //request permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (100 == requestCode) {
                //same request code as was in request permission
                startScan();
            }

        } else {
            //not granted permission
            //show some explanation dialog that some features will not work
        }
    }

    private void startScan() {
        beaconManager.setRangeNotifier((beacons, region) -> runOnUiThread(() -> {
            targetBeacons.clear();
            for (Beacon b : beacons) {
                if (b.getId1().equals(Identifier.parse(XEBIA_KONTACT_IO_UUID))) {
                    targetBeacons.add(b);
                }

            }
            adapter.replaceWith(targetBeacons);
        }));
        try {
            beaconManager.startRangingBeaconsInRegion(ALL_BEACONS_REGION);
        } catch (RemoteException e) {
            // void implementation
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.replaceWith(Collections.<Beacon>emptyList());
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(false);
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconState.setText(R.string.beacon_state_scanning);
        checkPermissionAndStart();
    }
}
