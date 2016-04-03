package fr.xebia.futurecity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import fr.xebia.futurecity.compass.CompassView;
import fr.xebia.futurecity.model.Position;

public class NavigationActivity extends BaseActivity implements BeaconConsumer {

    private static final String TAG = "NavigationActivity";
    public static final String STATION_NAME = "STATION_NAME";
    public static final String LINE_NUMBER = "LINE_NUMBER";

    private static final String XEBIA_KONTACT_IO_UUID = "37e6a92a-f8ce-11e5-9ce9-5e5517507c66";
    private static final Region ALL_BEACONS_REGION = new Region(XEBIA_KONTACT_IO_UUID, null, null, null);
    private static final String KONTAKT_IO_BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";

    private BeaconManager beaconManager;
    private BeaconListAdapter adapter;
    List<Beacon> targetBeacons = new ArrayList<>();

    private LocationManager locationManager;
    private Location userLocation;

    @Bind(R.id.destination) TextView destination;
    //    @Bind(R.id.device_list) ListView beaconList;
    @Bind(R.id.beacon_current) TextView beaconState;
    @Bind(R.id.beacon_previous) TextView previousBeacon;
    @Bind(R.id.beacon_next) TextView nextBeacon;
    @Bind(R.id.compassView) CompassView compassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        destination.setText(getIntent().getStringExtra(STATION_NAME));
        int lineNumber = getIntent().getIntExtra(LINE_NUMBER, 0);
        if (lineNumber == 3) {
            previousBeacon.setBackgroundColor(getResources().getColor(R.color.line_3_light));
            beaconState.setBackgroundColor(getResources().getColor(R.color.line_3));
            nextBeacon.setBackgroundColor(getResources().getColor(R.color.line_3_light));
        } else if (lineNumber == 7) {
            previousBeacon.setBackgroundColor(getResources().getColor(R.color.line_7_light));
            beaconState.setBackgroundColor(getResources().getColor(R.color.line_7));
            nextBeacon.setBackgroundColor(getResources().getColor(R.color.line_7_light));
        } else if (lineNumber == 8) {
            previousBeacon.setBackgroundColor(getResources().getColor(R.color.line_8_light));
            beaconState.setBackgroundColor(getResources().getColor(R.color.line_8));
            nextBeacon.setBackgroundColor(getResources().getColor(R.color.line_8_light));
        } else {
            previousBeacon.setBackgroundColor(getResources().getColor(R.color.grey_light));
            beaconState.setBackgroundColor(getResources().getColor(R.color.grey));
            nextBeacon.setBackgroundColor(getResources().getColor(R.color.grey_light));
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Configure device beaconList
        adapter = new BeaconListAdapter(this);
//        beaconList.setAdapter(adapter);

        // Configure BeaconManager
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(KONTAKT_IO_BEACON_LAYOUT));
        beaconManager.bind(this);
    }

    private void checkPermissionAndStart() {
        int checkSelfPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (PackageManager.PERMISSION_GRANTED == checkSelfPermissionResult) {
            startRanging();
//            startMonitoring();
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
                startRanging();
            }
        } else {
            //not granted permission
            //show some explanation dialog that some features will not work
        }
    }

    private void startRanging() {
        beaconManager.setRangeNotifier((beacons, region) -> runOnUiThread(() -> {
//            targetBeacons.clear();
            for (Beacon b : beacons) {
                if (b.getId1().equals(Identifier.parse(XEBIA_KONTACT_IO_UUID))) {
                    if (targetBeacons.size() == 0) {
                        targetBeacons.add(b);
                    } else {
                        boolean shouldAdd = true;
                        int indexToReplace = 0;
                        for (int i = 0; i < targetBeacons.size(); i++) {
                            if (b.getId2().equals(targetBeacons.get(i).getId2())) {
                                shouldAdd = false;
                                indexToReplace = i;
                            }
                        }
                        if (!shouldAdd) {
                            Log.d(TAG, "Replaced");
                            targetBeacons.remove(indexToReplace);
                        }
                        targetBeacons.add(b);
                    }
                }
            }
            Collections.sort(targetBeacons, (o1, o2) -> ((Integer) o2.getRssi()).compareTo(o1.getRssi()));
            if (targetBeacons.size() != 0) {
                Beacon currentBeacon = targetBeacons.get(0);
                Position currentPosition = Position.getPositionByMajor(currentBeacon.getId2().toInt());

                if (currentPosition != null) {
                    beaconState.setText("Current\n" + currentPosition.name());
                    Integer previousPositionMajor = currentPosition.getPreviousPosition();
                    Integer nextPositionMajor = currentPosition.getNextPosition();
                    if (previousPositionMajor != null) {
                        previousBeacon.setText("Previous\n" + Position.getPositionByMajor(previousPositionMajor).name());
                    } else {
                        previousBeacon.setText("Previous\nNone");
                    }
                    if (nextPositionMajor != null) {
                        nextBeacon.setText("Next\n" + Position.getPositionByMajor(nextPositionMajor).name());
                    } else {
                        nextBeacon.setText("Nex\nNone");
                    }
                    setCompass(currentPosition.getAngleWithNext());
                } else {
                    beaconState.setText("Current\nNone");
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

    private void setCompass(Integer angleWithNext) {
        userLocation = getBestLastKnowLocation(locationManager);
        if (angleWithNext != null) {
            compassView.initializeCompass(userLocation, angleWithNext - azimuth, R.drawable.ic_navigation_white_48dp);
        } else {
            compassView.initializeCompass(userLocation, 0, R.drawable.ic_navigation_white_48dp);
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
        checkPermissionAndStart();
    }

    private Location getBestLastKnowLocation(LocationManager locationManager) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location == null)
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) location = new Location("");
            return location;
        }
        return null;
    }
}
