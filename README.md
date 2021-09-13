# amTravelCar

## Architecture 

- 100% Kotlin
- MVVM and Hilt
- Flow / StateFlow / Coroutines
- Room with adapters
- Retrofit with Moshi adapters

For the database, I have choosen to use a junction table for the list of options, but it is possible to manage a List of String directly
on the Entity. (the code exists but it is not used.). It works, but it does not manage the case where the same option is in different cars.

The two tabs are using RecyclerView. For the car tab, it is normal, for the account tab, let's imagine you have a lot of differents fields, the XML is so much lighter
with a RecyclerView, and the UI is updated corresponding to what the user adds/remove in the ModifyAccount screen.
  
I use Flow to update the account screen, it detects each database update and update UI when needed.
  
The user can rotate, kill/restore the app, and be in offline mode, everything keeps displayed (if a least the list of cars is fetched one time)

## UI

### Car tab

The first screen with internet connection :

<img src="https://github.com/AntoineMarchaud/amTravelCar/blob/master/readme/left_normal.png" width="400" />

If the user is typing something, the list is updated :

<img src="https://github.com/AntoineMarchaud/amTravelCar/blob/master/readme/left_search.png" width="400" />

If the user is typing something with an error, the list is updated (works on a Pixel, not on my Xiaomi, I do not know why) :

<img src="https://github.com/AntoineMarchaud/amTravelCar/blob/master/readme/left_search_error.png" width="400" />

If the user clicks on a cell, the detail screen is displayed with an animation :

<img src="https://github.com/AntoineMarchaud/amTravelCar/blob/master/readme/left_detail.png" width="400" />

### Account tab

The default screen :

<img src="https://github.com/AntoineMarchaud/amTravelCar/blob/master/readme/right_default.png" width="400" />

The user clicks on "add account" button :

<img src="https://github.com/AntoineMarchaud/amTravelCar/blob/master/readme/right_add_account.png" width="400" />

The user adds a photo  :

<img src="https://github.com/AntoineMarchaud/amTravelCar/blob/master/readme/right_add_account_photo.png" width="400" />

The user adds its first name and last name :

<img src="https://github.com/AntoineMarchaud/amTravelCar/blob/master/readme/right_add_account_name.png" width="400" />

When the user adds its address the UI offers some choices (if connected to internet) :

<img src="https://github.com/AntoineMarchaud/amTravelCar/blob/master/readme/right_add_account_address.png" width="400" />

The user can add its birthday :

<img src="https://github.com/AntoineMarchaud/amTravelCar/blob/master/readme/right_add_account_birthday.png" width="400" />

At last, when the account is saved, this screen is displayed, it is possible to go to GMaps by clicking on the right arrow :

<img src="https://github.com/AntoineMarchaud/amTravelCar/blob/master/readme/right_add_account_sum_up.png" width="400" />

It is also possible to modify / remove the account.
