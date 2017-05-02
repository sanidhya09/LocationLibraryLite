Android Library with Sample Project
====

What's this?
----
Get lattitude and longitude from google fused Api in just few lines of code. It Checks for Error and Exceptions, Play services, GPS, Network, permission, etc. 


Benefits :
----

1. Reduces Code Boilerplate.
2. Can be used with any context providers like Activity, Services, Fragment, AppCompatActivity, Dialogs, etc.
3. Very light library size < 10kb.

Library Usage :
----
##### 1. For Current Location (One Time/Periodic):

### Starting

For getting current location one time:

````java

   final MbLocationService mbLocationService = MbLocationService.with(this);

        // Optional Params
        mbLocationService.setFastestInterval(1000 * 5);
        mbLocationService.setInterval(1000 * 10);
        mbLocationService.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mbLocationService.setOneFix(true);
        // mbLocationService.setDisplacement(15); // Default is OFF : 0
        
        mbLocationService.executeService(new MbLocationListener() {
            @Override
            public void onLocationUpdate(Location location) {
                Toast.makeText(MainActivity.this, "Latitude=" + location.getLatitude() + ", Longitude=" + location.getLongitude(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(MbLocationError mbLocationError) {
                Toast.makeText(MainActivity.this, "Error=" + mbLocationError.message, Toast.LENGTH_LONG).show();
                switch (mbLocationError.errorCode) {
                    case MbLocationUtil.LOCATION_PLAY_SERVICE_ERROR:
                        // Do your project specific stuff here..
                        break;
                    case MbLocationUtil.LOCATION_PROVIDER_ERROR:
                        // Do your project specific stuff here..
                        break;
                }
            }
        });
````

For getting current location (Periodic):

````java
      mbLocationService.setOneFix(false);
````

### Stopping

For stopping the location just use the stop method.

````java
MbLocationService.with(this).stopLocationUpdates();
````
      
                
````java      
// To get the human readable address
Address address = MbLocationUtil.with(context).getAddressFromLocation(location.getLatitude(), location.getLongitude());
        
// Check if the location services are enabled
MbLocationUtil.with(context).locationServicesEnabled();
        
// Check if any provider (network or gps) is enabled
MbLocationUtil.with(context).isAnyProviderAvailable();
        
// Check if GPS is available
MbLocationUtil.with(context).isGpsAvailable();
        
// Check if Network is available
MbLocationUtil.with(context).isNetworkAvailable();
        
// Check if the passive provider is available
MbLocationUtil.with(context).isPassiveAvailable();
        
// Check if the location is mocked
MbLocationUtil.with(context).isMockSettingEnabled();
````

