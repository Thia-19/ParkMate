### Description

ParkMate is an interactive mobile application (Android only) designed to support users in managing their vehicle parking activities. 
The application enables users to register one or more vehicles, start and end parking sessions, and access both the current parking status and 
a complete history of previous parking records (accessible in a structured form, such as a chronological list, a map-based visualization, and 
various bar charts together with KPI). All data are stored locally on the device. The application provides an intuitive interface for
managing parking sessions in real time and includes background functionalities, such as notifications, or by means of the Activity Recognition API, 
the app detects when the user transitions from a vehicular activity (e.g., driving or cycling) to a pedestrian state, and either prompts them to
log a new park or offers to end an existing one for the corresponding vehicle. Furthermore, the module _Parking Effort_ measures the walking effort 
associated with each park (that is, the distance and time the user walks from the parked vehicle to their destination) and uses this data to build 
a personal map of parking convenience across different areas of the city.

<hr/>

### How to
Follow these steps to set up the project locally, build it, and run it on your Android device or emulator using Android Studio.

#### Prerequisites

Make sure you have the following installed on your system:
* [Android Studio](https://developer.android.com/studio) 
* Android SDK (managed through Android Studio)
* Git

#### Instructions
After cloning the repository source code, launch Android Studio and set up an emulator or connect a device. Once your emulator 
is running or your physical device is recognized, look at the top toolbar in Android Studio. Ensure your target device is selected in 
the device dropdown menu, and then click the Run button. Android Studio will build the APK, install it automatically on your device/emulator, 
and launch the application.
