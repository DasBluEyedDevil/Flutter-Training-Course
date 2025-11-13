# Lesson 5: Maps and Location

## What You'll Learn
- Integrating Google Maps in Flutter
- Getting user's current location
- Adding markers and custom pins
- Drawing routes and polylines
- Geocoding (address ↔ coordinates)
- Building a location-based app

## Concept First: Why Maps and Location?

### Real-World Analogy
Think of adding maps to your app like giving it **eyes and a GPS**:
- **Geolocation** = Knowing where you are (like a compass)
- **Google Maps** = Seeing the world (like a detailed paper map)
- **Markers** = Sticky notes on the map
- **Polylines** = Drawing routes with a highlighter

Just like how "You Are Here" signs help you navigate a mall, location features help users navigate the real world through your app.

### Why This Matters
Location features power essential apps:

1. **Ride-Sharing**: Uber, Lyft (find drivers, track rides)
2. **Food Delivery**: DoorDash, Uber Eats (track deliveries)
3. **Dating Apps**: Tinder, Bumble (find nearby matches)
4. **Fitness**: Strava, RunKeeper (track running routes)
5. **Real Estate**: Zillow (find properties near you)

According to Google, 76% of people who search for something nearby visit a business within a day!

---

## Setting Up

### 1. Get Google Maps API Key

**Android:**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a project
3. Enable "Maps SDK for Android"
4. Create credentials → API Key
5. Add to `android/app/src/main/AndroidManifest.xml`:

```xml
<manifest>
    <application>
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="YOUR_API_KEY_HERE"/>
    </application>
</manifest>
```

**iOS:**
1. Same Google Cloud Console project
2. Enable "Maps SDK for iOS"
3. Add to `ios/Runner/AppDelegate.swift`:

```swift
import UIKit
import Flutter
import GoogleMaps

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GMSServices.provideAPIKey("YOUR_API_KEY_HERE")
    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}
```

### 2. Add Dependencies

**pubspec.yaml:**
```yaml
dependencies:
  flutter:
    sdk: flutter
  google_maps_flutter: ^2.13.1  # Google Maps widget
  geolocator: ^14.0.2  # Location services
  permission_handler: ^11.3.1  # Permission management
  geocoding: ^3.0.0  # Address ↔ Coordinates
```

```bash
flutter pub get
```

### 3. Configure Permissions

**Android (`android/app/src/main/AndroidManifest.xml`):**
```xml
<manifest>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

    <application>
        <!-- ... -->
    </application>
</manifest>
```

**iOS (`ios/Runner/Info.plist`):**
```xml
<dict>
    <key>NSLocationWhenInUseUsageDescription</key>
    <string>We need your location to show nearby places.</string>

    <key>NSLocationAlwaysUsageDescription</key>
    <string>We need your location for background tracking.</string>
</dict>
```

---

## Basic Google Maps Integration

### Simple Map Display

```dart
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class BasicMapScreen extends StatefulWidget {
  @override
  State<BasicMapScreen> createState() => _BasicMapScreenState();
}

class _BasicMapScreenState extends State<BasicMapScreen> {
  late GoogleMapController _mapController;

  // Initial camera position (San Francisco)
  final CameraPosition _initialPosition = CameraPosition(
    target: LatLng(37.7749, -122.4194),
    zoom: 12,
  );

  void _onMapCreated(GoogleMapController controller) {
    _mapController = controller;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Google Maps')),
      body: GoogleMap(
        onMapCreated: _onMapCreated,
        initialCameraPosition: _initialPosition,
        myLocationEnabled: true,  // Show user's location
        myLocationButtonEnabled: true,  // Show location button
        mapType: MapType.normal,  // normal, satellite, hybrid, terrain
      ),
    );
  }
}
```

---

## Getting User's Current Location

### Using Geolocator

```dart
import 'package:geolocator/geolocator.dart';
import 'package:permission_handler/permission_handler.dart';

class LocationService {
  // Check if location services are enabled
  Future<bool> isLocationServiceEnabled() async {
    return await Geolocator.isLocationServiceEnabled();
  }

  // Request location permission
  Future<bool> requestLocationPermission() async {
    final permission = await Permission.location.request();
    return permission.isGranted;
  }

  // Get current position
  Future<Position?> getCurrentLocation() async {
    // Check if location service is enabled
    if (!await isLocationServiceEnabled()) {
      print('Location services are disabled');
      return null;
    }

    // Check/request permission
    if (!await requestLocationPermission()) {
      print('Location permission denied');
      return null;
    }

    // Get position
    try {
      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
      );
      return position;
    } catch (e) {
      print('Error getting location: $e');
      return null;
    }
  }

  // Watch position changes (for real-time tracking)
  Stream<Position> getPositionStream() {
    return Geolocator.getPositionStream(
      locationSettings: LocationSettings(
        accuracy: LocationAccuracy.high,
        distanceFilter: 10,  // Update every 10 meters
      ),
    );
  }

  // Calculate distance between two points
  double calculateDistance(
    double lat1,
    double lon1,
    double lat2,
    double lon2,
  ) {
    return Geolocator.distanceBetween(lat1, lon1, lat2, lon2);
  }
}
```

### Move Map to User's Location

```dart
class UserLocationMapScreen extends StatefulWidget {
  @override
  State<UserLocationMapScreen> createState() => _UserLocationMapScreenState();
}

class _UserLocationMapScreenState extends State<UserLocationMapScreen> {
  late GoogleMapController _mapController;
  final LocationService _locationService = LocationService();
  Position? _currentPosition;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _getCurrentLocation();
  }

  Future<void> _getCurrentLocation() async {
    final position = await _locationService.getCurrentLocation();

    if (position != null) {
      setState(() {
        _currentPosition = position;
        _isLoading = false;
      });

      // Move camera to user's location
      _mapController.animateCamera(
        CameraUpdate.newCameraPosition(
          CameraPosition(
            target: LatLng(position.latitude, position.longitude),
            zoom: 15,
          ),
        ),
      );
    } else {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('My Location')),
      body: _isLoading
          ? Center(child: CircularProgressIndicator())
          : GoogleMap(
              onMapCreated: (controller) => _mapController = controller,
              initialCameraPosition: CameraPosition(
                target: _currentPosition != null
                    ? LatLng(_currentPosition!.latitude, _currentPosition!.longitude)
                    : LatLng(37.7749, -122.4194),
                zoom: 15,
              ),
              myLocationEnabled: true,
              myLocationButtonEnabled: true,
            ),
      floatingActionButton: FloatingActionButton(
        onPressed: _getCurrentLocation,
        child: Icon(Icons.my_location),
      ),
    );
  }
}
```

---

## Adding Markers

```dart
class MarkersMapScreen extends StatefulWidget {
  @override
  State<MarkersMapScreen> createState() => _MarkersMapScreenState();
}

class _MarkersMapScreenState extends State<MarkersMapScreen> {
  late GoogleMapController _mapController;
  Set<Marker> _markers = {};

  final CameraPosition _initialPosition = CameraPosition(
    target: LatLng(37.7749, -122.4194),
    zoom: 12,
  );

  @override
  void initState() {
    super.initState();
    _addMarkers();
  }

  void _addMarkers() {
    setState(() {
      _markers = {
        Marker(
          markerId: MarkerId('marker_1'),
          position: LatLng(37.7749, -122.4194),
          infoWindow: InfoWindow(
            title: 'San Francisco',
            snippet: 'Beautiful city by the bay',
          ),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed),
        ),
        Marker(
          markerId: MarkerId('marker_2'),
          position: LatLng(37.8044, -122.2712),
          infoWindow: InfoWindow(
            title: 'Oakland',
            snippet: 'Across the bridge',
          ),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueBlue),
          onTap: () {
            print('Oakland marker tapped!');
          },
        ),
        Marker(
          markerId: MarkerId('marker_3'),
          position: LatLng(37.7849, -122.4064),
          infoWindow: InfoWindow(title: 'Chinatown'),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen),
        ),
      };
    });
  }

  void _addMarkerAtPosition(LatLng position) {
    final markerId = MarkerId('marker_${_markers.length + 1}');

    setState(() {
      _markers.add(
        Marker(
          markerId: markerId,
          position: position,
          infoWindow: InfoWindow(
            title: 'New Location',
            snippet: 'Lat: ${position.latitude}, Lng: ${position.longitude}',
          ),
        ),
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Markers (${_markers.length})'),
        actions: [
          IconButton(
            icon: Icon(Icons.delete_sweep),
            onPressed: () {
              setState(() => _markers.clear());
            },
          ),
        ],
      ),
      body: GoogleMap(
        onMapCreated: (controller) => _mapController = controller,
        initialCameraPosition: _initialPosition,
        markers: _markers,
        onLongPress: _addMarkerAtPosition,  // Add marker on long press
        myLocationEnabled: true,
      ),
    );
  }
}
```

### Custom Marker Icons

```dart
// Load custom marker from assets
BitmapDescriptor? _customIcon;

Future<void> _loadCustomMarker() async {
  _customIcon = await BitmapDescriptor.fromAssetImage(
    ImageConfiguration(devicePixelRatio: 2.5),
    'assets/images/custom_marker.png',
  );
}

// Use in marker
Marker(
  markerId: MarkerId('custom'),
  position: LatLng(37.7749, -122.4194),
  icon: _customIcon ?? BitmapDescriptor.defaultMarker,
)
```

---

## Drawing Polylines (Routes)

```dart
class PolylineMapScreen extends StatefulWidget {
  @override
  State<PolylineMapScreen> createState() => _PolylineMapScreenState();
}

class _PolylineMapScreenState extends State<PolylineMapScreen> {
  late GoogleMapController _mapController;
  Set<Polyline> _polylines = {};
  Set<Marker> _markers = {};

  // Sample route coordinates
  final List<LatLng> _routeCoordinates = [
    LatLng(37.7749, -122.4194),
    LatLng(37.7849, -122.4064),
    LatLng(37.7949, -122.3994),
    LatLng(37.8049, -122.4100),
  ];

  @override
  void initState() {
    super.initState();
    _createRoute();
  }

  void _createRoute() {
    setState(() {
      // Add start and end markers
      _markers = {
        Marker(
          markerId: MarkerId('start'),
          position: _routeCoordinates.first,
          infoWindow: InfoWindow(title: 'Start'),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen),
        ),
        Marker(
          markerId: MarkerId('end'),
          position: _routeCoordinates.last,
          infoWindow: InfoWindow(title: 'End'),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed),
        ),
      };

      // Draw route
      _polylines = {
        Polyline(
          polylineId: PolylineId('route'),
          points: _routeCoordinates,
          color: Colors.blue,
          width: 5,
          patterns: [PatternItem.dash(20), PatternItem.gap(10)],  // Dashed line
        ),
      };
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Route')),
      body: GoogleMap(
        onMapCreated: (controller) => _mapController = controller,
        initialCameraPosition: CameraPosition(
          target: _routeCoordinates.first,
          zoom: 13,
        ),
        markers: _markers,
        polylines: _polylines,
      ),
    );
  }
}
```

---

## Geocoding (Address ↔ Coordinates)

```dart
import 'package:geocoding/geocoding.dart';

class GeocodingService {
  // Address → Coordinates
  Future<LatLng?> getCoordinatesFromAddress(String address) async {
    try {
      final locations = await locationFromAddress(address);

      if (locations.isNotEmpty) {
        final location = locations.first;
        return LatLng(location.latitude, location.longitude);
      }
    } catch (e) {
      print('Geocoding error: $e');
    }

    return null;
  }

  // Coordinates → Address
  Future<String?> getAddressFromCoordinates(double lat, double lng) async {
    try {
      final placemarks = await placemarkFromCoordinates(lat, lng);

      if (placemarks.isNotEmpty) {
        final place = placemarks.first;
        return '${place.street}, ${place.locality}, ${place.country}';
      }
    } catch (e) {
      print('Reverse geocoding error: $e');
    }

    return null;
  }
}
```

**Usage Example:**
```dart
// Search for address
final coordinates = await GeocodingService().getCoordinatesFromAddress(
  '1600 Amphitheatre Parkway, Mountain View, CA',
);

if (coordinates != null) {
  _mapController.animateCamera(
    CameraUpdate.newLatLng(coordinates),
  );
}

// Get address from tap
void _onMapTap(LatLng position) async {
  final address = await GeocodingService().getAddressFromCoordinates(
    position.latitude,
    position.longitude,
  );

  print('Address: $address');
}
```

---

## Complete Example: Nearby Places Finder

```dart
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geolocator/geolocator.dart';

class Place {
  final String name;
  final LatLng location;
  final String category;

  Place(this.name, this.location, this.category);
}

class NearbyPlacesScreen extends StatefulWidget {
  @override
  State<NearbyPlacesScreen> createState() => _NearbyPlacesScreenState();
}

class _NearbyPlacesScreenState extends State<NearbyPlacesScreen> {
  late GoogleMapController _mapController;
  Position? _currentPosition;
  Set<Marker> _markers = {};
  List<Place> _places = [];
  String _selectedCategory = 'All';

  // Sample places (in real app, fetch from API)
  final List<Place> _allPlaces = [
    Place('Coffee Shop A', LatLng(37.7749, -122.4194), 'Coffee'),
    Place('Restaurant B', LatLng(37.7849, -122.4064), 'Food'),
    Place('Coffee Shop C', LatLng(37.7649, -122.4294), 'Coffee'),
    Place('Gym D', LatLng(37.7549, -122.4394), 'Fitness'),
    Place('Restaurant E', LatLng(37.7949, -122.3994), 'Food'),
  ];

  @override
  void initState() {
    super.initState();
    _getCurrentLocation();
    _filterPlaces();
  }

  Future<void> _getCurrentLocation() async {
    final position = await Geolocator.getCurrentPosition();
    setState(() {
      _currentPosition = position;
    });
  }

  void _filterPlaces() {
    setState(() {
      _places = _selectedCategory == 'All'
          ? _allPlaces
          : _allPlaces.where((p) => p.category == _selectedCategory).toList();

      _updateMarkers();
    });
  }

  void _updateMarkers() {
    _markers.clear();

    for (var place in _places) {
      final distance = _currentPosition != null
          ? Geolocator.distanceBetween(
              _currentPosition!.latitude,
              _currentPosition!.longitude,
              place.location.latitude,
              place.location.longitude,
            )
          : 0.0;

      _markers.add(
        Marker(
          markerId: MarkerId(place.name),
          position: place.location,
          infoWindow: InfoWindow(
            title: place.name,
            snippet: '${(distance / 1000).toStringAsFixed(2)} km away',
          ),
          icon: _getMarkerIcon(place.category),
        ),
      );
    }

    setState(() {});
  }

  BitmapDescriptor _getMarkerIcon(String category) {
    switch (category) {
      case 'Coffee':
        return BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueOrange);
      case 'Food':
        return BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed);
      case 'Fitness':
        return BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen);
      default:
        return BitmapDescriptor.defaultMarker;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Nearby Places'),
        actions: [
          PopupMenuButton<String>(
            icon: Icon(Icons.filter_list),
            onSelected: (category) {
              _selectedCategory = category;
              _filterPlaces();
            },
            itemBuilder: (context) => [
              PopupMenuItem(value: 'All', child: Text('All')),
              PopupMenuItem(value: 'Coffee', child: Text('Coffee')),
              PopupMenuItem(value: 'Food', child: Text('Food')),
              PopupMenuItem(value: 'Fitness', child: Text('Fitness')),
            ],
          ),
        ],
      ),
      body: Column(
        children: [
          // Map
          Expanded(
            flex: 2,
            child: GoogleMap(
              onMapCreated: (controller) => _mapController = controller,
              initialCameraPosition: CameraPosition(
                target: LatLng(37.7749, -122.4194),
                zoom: 12,
              ),
              markers: _markers,
              myLocationEnabled: true,
              myLocationButtonEnabled: true,
            ),
          ),

          // Places list
          Expanded(
            child: ListView.builder(
              itemCount: _places.length,
              itemBuilder: (context, index) {
                final place = _places[index];
                final distance = _currentPosition != null
                    ? Geolocator.distanceBetween(
                        _currentPosition!.latitude,
                        _currentPosition!.longitude,
                        place.location.latitude,
                        place.location.longitude,
                      )
                    : 0.0;

                return ListTile(
                  leading: Icon(_getCategoryIcon(place.category)),
                  title: Text(place.name),
                  subtitle: Text(
                    '${place.category} • ${(distance / 1000).toStringAsFixed(2)} km',
                  ),
                  trailing: Icon(Icons.arrow_forward_ios, size: 16),
                  onTap: () {
                    _mapController.animateCamera(
                      CameraUpdate.newCameraPosition(
                        CameraPosition(
                          target: place.location,
                          zoom: 15,
                        ),
                      ),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  IconData _getCategoryIcon(String category) {
    switch (category) {
      case 'Coffee':
        return Icons.local_cafe;
      case 'Food':
        return Icons.restaurant;
      case 'Fitness':
        return Icons.fitness_center;
      default:
        return Icons.place;
    }
  }
}
```

---

## Best Practices

1. **Always Check Permissions**
   ```dart
   final hasPermission = await Permission.location.request();
   if (!hasPermission.isGranted) {
     // Show error or guide user to settings
     return;
   }
   ```

2. **Handle Location Services Disabled**
   ```dart
   if (!await Geolocator.isLocationServiceEnabled()) {
     await Geolocator.openLocationSettings();
   }
   ```

3. **Dispose Map Controller**
   ```dart
   @override
   void dispose() {
     _mapController.dispose();
     super.dispose();
   }
   ```

4. **Use Different Accuracy for Different Needs**
   ```dart
   // High accuracy (GPS) - battery intensive
   LocationAccuracy.high

   // Medium accuracy - balanced
   LocationAccuracy.medium

   // Low accuracy - battery friendly
   LocationAccuracy.low
   ```

5. **Cache Map Data for Offline Use**
   - Google Maps automatically caches viewed areas
   - For full offline support, consider OpenStreetMap alternatives

---

## Common Issues & Solutions

**Issue 1: Map shows blank/gray screen**
- **Solution**: Check API key is correct and enabled
- Verify billing is enabled in Google Cloud Console

**Issue 2: "Location services are disabled"**
- **Solution**: Guide user to enable in device settings
  ```dart
  await Geolocator.openLocationSettings();
  ```

**Issue 3: Markers not showing**
- **Solution**: Ensure markers Set is passed to GoogleMap widget
- Check zoom level isn't too far out

**Issue 4: App crashes on iOS when accessing location**
- **Solution**: Add usage descriptions to Info.plist
- Request permission before accessing location

---

## Quiz

**Question 1:** What's the difference between `LocationAccuracy.high` and `LocationAccuracy.low`?
A) High is slower but more accurate
B) High uses GPS (precise but battery-intensive); low uses network (less precise, battery-friendly)
C) There is no difference
D) Low accuracy is deprecated

**Question 2:** How do you convert an address to coordinates?
A) Use `geolocator` package
B) Use `locationFromAddress()` from geocoding package
C) Manually parse with regex
D) Google Maps does it automatically

**Question 3:** What is a Polyline used for?
A) Marking single locations
B) Drawing routes/paths on the map
C) Setting map boundaries
D) Clustering markers

---

## Exercise: Delivery Tracker

Build a delivery tracking app that:
1. Shows delivery driver's live location
2. Draws route from restaurant → customer
3. Updates ETA as driver moves
4. Shows distance remaining

**Bonus Challenges:**
- Add multiple delivery stops
- Show traffic conditions
- Send notifications when driver is nearby
- Estimate delivery time based on speed

---

## Summary

You've mastered maps and location in Flutter! Here's what we covered:

- **Google Maps Integration**: Setup and basic map display
- **Geolocation**: Getting user's current location with geolocator
- **Permissions**: Handling location permissions gracefully
- **Markers**: Adding custom pins and info windows
- **Polylines**: Drawing routes and paths
- **Geocoding**: Converting addresses ↔ coordinates
- **Complete App**: Nearby places finder with filtering

With these skills, you can build location-aware apps like ride-sharing, delivery tracking, and social discovery!

---

## Answer Key

**Answer 1:** B) High uses GPS (precise but battery-intensive); low uses network (less precise, battery-friendly)

`LocationAccuracy.high` uses GPS for precise location (±5-10 meters) but drains battery. `LocationAccuracy.low` uses WiFi/cell towers (±100-500 meters) but is battery-friendly. Choose based on your app's needs!

**Answer 2:** B) Use `locationFromAddress()` from geocoding package

The `geocoding` package provides `locationFromAddress()` for forward geocoding (address → coordinates) and `placemarkFromCoordinates()` for reverse geocoding (coordinates → address).

**Answer 3:** B) Drawing routes/paths on the map

Polylines draw connected lines between multiple LatLng points, perfect for showing routes, paths, or boundaries. Markers are for single points, not routes.
