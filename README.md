Android Location Library
====

What's this?
----
Get lattitude and longitude from google fused Api in just few lines of code. It Checks for Error and Exceptions, Play services, GPS, Network, permission, etc. 


Benefits :
----

1. Reduces Code Boilerplate and saves development time.

2. Very light size < 10kb.

3. Get Current Location.

4. Get Periodic Location.

5. Checks for Play Services.

6. Checks for location on/off.

7. In-app GPS enable dialog.

8. Inbuilt Location Permission Request.

9. Single errorCallback for most of the errors.

10. Can be used with any context providers like Activity, Services, Fragment, AppCompatActivity, Dialogs, etc.

11. Get Address of a location.

Library Usage :
----
##### 1. For Current Location (One Time/Periodic):

### Starting

For getting current location one time:

````java

    MbLocationServices mbLocationServices = MbLocationServices.with(this);

           // Optional Params
           mbLocationServices.setFastestInterval(1000 * 5);
           mbLocationServices.setInterval(1000 * 10);
           mbLocationServices.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
           mbLocationServices.setOneFix(true);

           mbLocationServices.init(new MbLocationListener() {
               @Override
               public void onLocationUpdate(Location location) {
                   Toast.makeText(MainActivity.this, "Latitude=" + location.getLatitude() + ", Longitude=" + location.getLongitude(), Toast.LENGTH_LONG).show();
               }

               @Override
               public void onError(MbLocationError errorCode) {
                   Toast.makeText(MainActivity.this, "Error=" + errorCode.message, Toast.LENGTH_LONG).show();
                   switch (errorCode.errorCode) {
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
      mbLocationServices.setOneFix(false);
````

### Stopping

For stopping the location just use the stop method.

````java
MbLocationServices.with(this).stopLocationUpdates();
````
 ### Utility     
                
````java      
// To get the human readable address
Address address = MbLocationUtil.with(mContext).getAddressFromLocation(location.getLatitude(), location.getLongitude());
        
// Check if the location services are enabled
MbLocationUtil.with(mContext).locationServicesEnabled();
        
// Check if any provider (network or gps) is enabled
MbLocationUtil.with(mContext).isAnyProviderAvailable();
        
// Check if GPS is available
MbLocationUtil.with(mContext).isGpsAvailable();
        
// Check if Network is available
MbLocationUtil.with(mContext).isNetworkAvailable();
        
// Check if the passive provider is available
MbLocationUtil.with(mContext).isPassiveAvailable();
        
// Check if the location is mocked
MbLocationUtil.with(mContext).isMockSettingEnabled();
````

