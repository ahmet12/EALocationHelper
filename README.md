# EALocationHelper

A Location tracking helper library.


### Gradle
```gradle
implementation 'com.ahmetkilic.ealocationhelper:ealocationhelper:1.0.0'
```
```java
    private EALocationHelper helper;

      private void initHelper(){
        helper = new EALocationHelper(this, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                final String msg = "Updated Location: \n" +
                        Double.toString(location.getLatitude()) + "\n" +
                        Double.toString(location.getLongitude());
                Log.v("LocationChanged", msg);
            }
        });
    }
        
    @Override
    protected void onResume() {
        super.onResume();
        helper.startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        helper.stopLocationUpdates();
    }
```
