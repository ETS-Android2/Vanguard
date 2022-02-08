# Vanguard
App to explore places around you and much more.

Note: *Google maps api expired.*

# All features:
* Signup/ login with firebase authentication.
* Toggle between metric and imperial system.
* Determine the best route to any location.
* Estimate time and distance to desired destination.
* View nearby landmarks based on your search criteria e.g., “Show nearby schools”. 
* View current location on map.
* Create list of places you want to visit.

# Research

## Contents

[Introduction 3](#_Toc69848460)

[Detailed list of requirements 3](#_Toc69848461)

[Minimum requirements 3](#_Toc69848462)

[Register 3](#_Toc69848463)

[Login 3](#_Toc69848464)

[View nearby landmarks on a map 3](#_Toc69848465)

[Settings 3](#_Toc69848466)

[Display landmarks filtered 4](#_Toc69848467)

[Find user&#39;s current position 4](#_Toc69848468)

[List of favorite places 4](#_Toc69848469)

[Select landmark on map and show route 4](#_Toc69848470)

[Calculate the best route 4](#_Toc69848471)

[Estimated time and distance 4](#_Toc69848472)

[Display the travel distance according to user&#39;s measurement system 5](#_Toc69848473)

[Display route visually 5](#_Toc69848474)

[Optional features: 5](#_Toc69848475)

[Push notifications 5](#_Toc69848476)

[Picture in picture 5](#_Toc69848477)

[Design:Link back to concepts learned via the research section 5](#_Toc69848478)

[Login screen 5](#_Toc69848479)

[Register screen 5](#_Toc69848480)

[Home screen 6](#_Toc69848481)

[Favorite list 6](#_Toc69848482)

[Nearby landmarks screen 6](#_Toc69848483)

[Settings screen 6](#_Toc69848484)

[Search 6](#_Toc69848485)

[Detail 6](#_Toc69848486)

[Direction 6](#_Toc69848487)

[User interface design 6](#_Toc69848488)

[Description of screens 6](#_Toc69848489)

[Login 6](#_Toc69848490)

[Signup 6](#_Toc69848491)

[Settings 7](#_Toc69848492)

[Home 7](#_Toc69848493)

[Nearby 7](#_Toc69848494)

[Location 7](#_Toc69848495)

[Direction 7](#_Toc69848496)

[Fave list 7](#_Toc69848497)

[Search 7](#_Toc69848498)

[Data listing 7](#_Toc69848499)

[Project plan 7](#_Toc69848500)

[Conclusion 7](#_Toc69848501)

[References 8](#_Toc69848502)


# Introduction

With the help of travel technologies , people are more comfortable planning their vacation from their cell phones. &quot;Vanguard&quot; makes travelers or any tourist life easier. With the design and all features implemented in app, there is no need for an agent to assist in finding local attractive locations. 

# Detailed list of requirements

## Minimum requirements

### Register

Users must be able to register.

Implementation: When users sign up, authentication information(username and password) will be stored in _firebase authentication service_ (Firebase, 2021). Other information e.g., name, surname, address will be stored in _firebase Realtime database_, under the user ID given by authentication service (Firebase, 2021).

###

### Login

Users must be able to login and logout of the app.

Implementation: Users will enter their credentials(username and password). Once &quot;Done&quot; is clicked they will be passed to firebase authentication. The server is then going to verify those credentials and return response to client with will be the app (Firebase, 2021). If credentials are correct, then the user will be sent to home screen.

###

### View nearby landmarks on a map

App must show users nearby landmarks.

Implementation: A request will be made to google maps API. &quot;NearbySearch&quot; route(of API) will return list of nearby places in json. The API key, location and radius will be sent first because they are required parameters (Google, 2021). Once decentralization is done by app it will put all the coordinates on map as pins.

### Settings

User must be able to set and change settings. Settings include: choosing imperial or metric system, preferred type of landmark and more.

Implementing measurement system: a variable call &quot;isMetric&quot; of type Boolean will be created in firebase Realtime. If user chooses metric, then _true_ will be stored and if they choose imperial then _false_ will be stored.

Implementing &quot;preferred type of landmark&quot;: variable call &quot;preferredLandmark&quot; of type string will be created in firebase Realtime database. This will store just name of preferred landmark.

### Display landmarks filtered

App must be able to filter landmarks.

Implementation: Coordinates are going to be received from _firebase Realtime database_ under user id, under &quot;favoriteLandmarks&quot;. Then pins will be added to the map according to coordinates received from firebase (Google, 2021).

### Find user&#39;s current position

App must be able to get user current location.

Getting user location: Google play services is going to be used. Google play services in will be added to griddle then instantiate &quot;FusedLocationProviderClient&quot;. Using instance of &quot;FusedLocationProviderClient&quot;, &quot;getLastLocation()&quot; method is going to be used to get user current (Google, 2021). Then using received coordinates pin will be dropped on the map.

### List of favorite places

User must be able to store a list of favorite landmarks.

Implementation: 2D string array called &quot;favoriteLandmarks&quot; will be created in firebase Realtime database. Array 1 will store name of the favorite landmarks and array 2 will store coordinates. Then when user open &quot;Fave list&quot; screen data will be received from firebase Realtime database and will be loaded to recycler view.

### Select landmark on map and show route

User must be able to select landmark on map and get information about it.

Implementation: When user clicks and holds a pin is going to dropped. Using the coordinated of the dropped pin a request will be made to google maps API. Then the information will be displayed in landmark details screen (Google, 2021). The screen will be populated with data from API.

### Calculate the best route

User must be able to enter starting location and destination, then the app must show the best route.

Implementation: A Polyline will be added to map using google maps API (Google, 2021). The information on the path will be obtained using the directions route in google maps API (Google, 2021).

### Estimated time and distance

_Source: [link](https://developers.google.com/maps/documentation/distance-matrix/start)_

A ![](RackMultipart20220208-4-1szyxqr_html_6305bf601aaf0ab3.png) pp must display estimated time and distance.

Implementation: The distance matrix API will return estimated travel time and distance, after being supplied with API key, &quot;destination\_addresses&quot; and &quot;origin\_addresses&quot; (Google, 2021). This information can be displayed on the bottom of screen as card.

###

###

### Display the travel distance according to user&#39;s measurement system

The app must display the travel distance according to user&#39;s measurement system.

Implementation: Variable &quot;isMetric&quot; is stored in Realtime database. If &quot;isMetric&quot; is true then mile distance value will be converted to km by multiplying by _1.609344,_ otherwisenumber will stay the same (Google, 2021).

### Display route visually

App must be able to display route visually.

Implementation: Google maps API provides Polyline, which is an easy way to visually represent lines on the map (Google, 2019). Polyline will be used to display route visually.

## Optional features:

### Push notifications

Push notifications should be activated in the app. Whenever a new local landmark appears, users get the message &quot;check out for this new place, press on the message to be redirected &quot;. The notifications content and channel needs to be set using notificationCompat.builder. A channelID will be required afterwards because of compatibility with Android 8.0(API level 26) and higher. The content of the text should be larger hence why we will enable an expandable notification by adding style template with set Style() (Google, 2021).

###

### Picture in picture

Users must be able to have a choice to activate or deactivate Picture in Picture mode. Android 8.0 (API level 26) allows activities to launch in picture-in-picture (PIP) mode. To support PIP mode. We would need to register the video activity in the manifest by setting android:supportsPictureInPicture to true. To enter picture-in-picture mode, an activity must call enterPictureInPictureMode(). A logic should be included. For example, Google Maps switches to PIP mode if the user presses the home or recent button while the app is navigating. this is done by overriding onUserLeaveHint() (Google, 2021).

Design:Link back to concepts learned via the research section

Login screen

When user lunches application they are brought to the login screen which is how all 3 apps from research started. Apps required email and password for logging in and this has been included in the design. In research the other apps provided other options to login/sign(Facebook, Google) up and that option has been added here.

### Register screen

All three apps in research allowed you to register. At minimum you have option to enter name, surname, email, password, and profile picture. All these fields have been included in design.

### Home screen

TripAdvisor showed information for nearby places on home screen. In mockup, home screen shows nearby location ranked by distance. And this screen will be used as hub to connect to other screens E.g., settings, landmark detail etc.…

### Favorite list

Having a list of favorite landmarks is one of the requirements. All 3 apps had a favorite list(as you can see in poster under &quot;user list&quot;). Instead of making simple list with only the name of the landmark, we included: name of place, address, and picture) similar to research screenshots.

### Nearby landmarks screen

Viewing nearby landmarks is a requirement. In research we found TripAdvisor had option to view nearby locations but first it asked you for permission to access internet. This screen shows filtered(by hotels) landmarks. Also shows current location in blue.

### Settings screen

In research we found app have option to modify in app settings and profile. In research we found settings screen at minimum had profile user picture, username and user surname. As you can see all information was added but we essentially followed design approach of Airbnb.

Search

In research we found when searching for place it would give use suggestions and show history of what we entered. So in the design under the search bar app will give users suggestions while the bottom part will shows you search history.

### Detail

In research we found all 3 app at minimum included image of landmark, address, rating, and location features.

###

### Direction

This screen is responsible for calculating best route. Also displays the estimated time and distance. TripAdvisor and Foursquare had imbedded map to show the route to a destination. We added other information estimate distance and estimate time because its one of the minimum requirements.

# User interface design

Please find design in &quot;AppMockup&quot; folder.

## Description of screens

### Login

Users enter email and password(Stored in firebase) and if credentials are correct then they will sent to home screen. Alternatively, then can sign up.

### Signup

This screen allows users to create a new account by entering their details. If details have no errors, they will be sent to firebase. Measuring system will be determined by location.

### Settings

This is where users change from measuring system, and preferred type of landmark.

### Home

This is the hub for most of the other screens. Users can choose to go to settings,

### Nearby

This screen displays landmarks that are nearby.

### Location

Shows the details of the previously selected location.

### Direction

Shows user how to get to a destination.

### Fave list

Shows user their list of favorite places that they added.

### Search

Allows users to search for landmarks. Top part shows suggestions while bottom part show user history. If you click one of the items in list it will take to detail screen.

# Data listing

| Variable name | Variable type |
 |
| --- | --- | --- |
| userName | string | User name will be optional, this will be uses to personalize app experience by showing name in settings |
| userSurname | string | User surname will be optional, this will be uses to personalize app experience by showing name in settings |
| userEmail | string | Email in combination with password will be used to authenticate users. |
| userPassword | string | Password in combination with email will be used to authenticate users. |
| userAddress | string | This will be optional |
| userLandmarkType | string | In sign up screen users can choose their favorite landmark type by it will be from a drop down |
| userMeasurementSystem | Boolean |
 |

# Project plan

Please find file &quot;GanttChart.pdf&quot; in Part B folder.

# Conclusion

Before your next holiday destination, make sure to download &quot;Vanguard&quot;. As detailed in the list of requirements as well as all planning, this app is designed to make your life easy to find local landmarks.

# References

Firebase, 2021. _Firebase Authentication._ [Online]
 Available at: https://firebase.google.com/docs/auth
 [Accessed 17 April 2021].

Firebase, 2021. _https://firebase.google.com/docs/database._ [Online]
 Available at: https://firebase.google.com/docs/database
 [Accessed 17 April 2021].

Google, 2019. _Polyline._ [Online]
 Available at: https://developers.google.com/android/reference/com/google/android/gms/maps/model/Polyline
 [Accessed 18 April 2021].

Google, 2021. _Adding a Map with a Marker._ [Online]
 Available at: https://developers.google.com/maps/documentation/android-sdk/map-with-marker
 [Accessed 18 April 2021].

Google, 2021. _Create a Notification._ [Online]
 Available at: Create a Notification
 [Accessed 20 April 2021].

Google, 2021. _Get Started._ [Online]
 Available at: https://developers.google.com/maps/documentation/distance-matrix/start
 [Accessed 18 April 2021].

Google, 2021. _Get the last known location._ [Online]
 Available at: https://developer.android.com/training/location/retrieve-current
 [Accessed 18 April 2021].

Google, 2021. _Picture-in-picture Support._ [Online]
 Available at: https://developer.android.com/guide/topics/ui/picture-in-picture
 [Accessed 20 April 2021].

Google, 2021. _Place Search._ [Online]
 Available at: https://developers.google.com/maps/documentation/places/web-service/overview
 [Accessed 18 April 2021].

Google, 2021. _Polylines and Polygons to Represent Routes and Areas._ [Online]
 Available at: https://developers.google.com/maps/documentation/android-sdk/polygon-tutorial
 [Accessed 18 April 2021].

Google, 2021. _The Directions API quickstart._ [Online]
 Available at: https://developers.google.com/maps/documentation/directions/quickstart
 [Accessed 18 April 2021].
