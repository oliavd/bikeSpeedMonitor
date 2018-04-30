## Repository Page for CYBR 8480 Project: Bike Coach

## Executive Summary
Cycling also known as biking is one of the most common activity to stay in shape. In some cases, it is  merely a mean of transportation. An estimated [12,4 %](https://www.statista.com/statistics/631280/kinds-of-sports-people-engage-in-regularly/) Americans bike regularly. The ubiquitiousness of wearable devices enabled many applications in several areas. Using Wearable devices in fitness is very common because they allow us to gather fitness data for analysis and give us the opportunity to improve our fitness posture. "BikeSpeedMonitor" is designed in combination with MetaWearRG sensor to provide a measure of cycling speed, cadence and pace then send feedback to the user so he can adjust his/her speed according to a defined threshold.

## Project Goals
*  Record sensor data from MetaWearRG
* Build a mobile app with a simple interface that display speed, cadence, pace, distance and workout time to the user
* Send haptic feedback to user if speed lower than specified threshold
* Test application to ensure the functionality

## Application Requirements

### User stories
As a **cyclist**, I want to **monitor my cycling performance** so I can **analyze and improve on  my fitness**.

**Acceptance Criteria:**
* A user will be able to view the following cycling metrics on the phone interface:current speed, average speed, max speed, RPM, distance and time.

As a **cyclist**, I want to **set a desired speed** so I can **maintain that speed on my workout**.

**Acceptance Criteria:**
* A user will be able to input a speed that will constitute the threshold speed a user need to keep up with


As a **cyclist**, I want to **receive haptic feedback** so I can **increase my current speed over the the specified threshold**.

**Acceptance Criteria:**
* The coin Vibe Motor of the MetaWear board will vibrate everytime the current speed is lower than the defined threshold



### Misuser stories
As a **malicious actor**, I want to **conduct a bluetooth man in the middle attack** so I can get **unauthorized access to sensor data**.

**Mitigations:**
* Use end to end encryption for device connection
* The Wearable device should operate in a secure mode

As a **malicious actor**, I want to **conduct a denial of service attack on the wearable device** so I can **impede usage**.

**Mitigations:**
* By default, wearable device should be in non discoverable mode
* Adjust BLE power so device is only visible in very close range



## High Level Design

The following diagram is a high level design of the architecture.
![Design](https://www.lucidchart.com/publicSegments/view/301a8ece-7d7b-48ab-95de-91e9de4ff27b/image.jpeg)

## Components List
### Mobile Device
The mobile device will be the host of the application used to display fitness data to the user

#### Android application
The role of the mobile application is to receive raw sensor data, convert and display them to the user in a meaningful manner.
##### MetaWear API
The MetaWear API allows us to communicate (send and receive) data from/to the MetaWear board.

##### User Interface Design
[picture]

### MetawearRG Board
#### Accelerometer + Gyrometer Sensor
This sensors are used to measure the position and rotation of the wearable device.
#### Coin Vibe Motor
This lightweight and compact motor can be used to vibrate the wearable device.


## Security analysis

In the following misuse diagram, an malicious actor have two possible angle of attacks: He could use a man in the middle attack  by exploiting a vulnerability in the system to get unauthorized access to data or he could conduct a denial of service to impede the use of the system.
![Misuse Case Design](https://www.lucidchart.com/publicSegments/view/752b06ba-49f0-4614-b2e0-86bc399fc74a/image.jpeg)

| Component name | Category of vulnerability | Issue Description | Mitigation |
|----------------|---------------------------|-------------------|------------|
| Wearable Device(MetaWear) | Denial of Service | This component pair with the mobile device using BLE and might allow an attacker to jam the connection setup mechanism until the component battery is depleted. | Wearable device should by default be in non discoverable mode. Although this will not completely mitigate this kind of attack, it will slow down an attacker that will have to craft a packet with the correct Lower Address Part .|
| Wearable Device (MetaWear)|Information Disclosure | This component sends raw sensor data to the mobile device through BLE and might allow an attacker to perform a man in the middle attack and gained access to unauthorized user data or even alter the communication between the mobile phone and the waerable device. | The wearable device should operate in a secure mode (service level enforced security or link level enforced security).|
|Wearable Device (MetaWear)| Spoofing| This component is paired to the mobile device and stream data to the mobile app using BLE. This gives the opportunity to an attacker to spoof the MetaWear and feed wrong sensor data to the end user. This result in integrity compromise from the user perspective. For example, a user might think he is going at a lower velocity than he is actually going. | To mitigate this vulnerability, the pairing process should implement some sort of PIN authentication to authenticate the MetaWear device.|

### Hardware and Software Requirements
* Android (M) 6.0 (API Level 23 +) witl Bluetooth Low Energy Capable smartphone or tablet
* [MetaWear MetaMotionR+](https://mbientlab.com/product/metamotionrp-bundle/)
* [Android Studio](https://developer.android.com/studio/)
* [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

### Installation and Getting Started

#### Installation
##### Quick Installation
Download the [app apk](https://github.com/oliavd/bikeSpeedMonitor/blob/dev/Final%20MileStones/apk/bikeCoach.apk?raw=true) directly on a compatible android phone and install

##### Using Android Studio

* Clone this repository using `git clone https://github.com/oliavd/bikeSpeedMonitor.git`
* Open the repository folder in Android Studio
* Under the Build tab in Android Studio:
    * Click on Clean Project
    * Click on Rebuild Project
* Run the app on a targeted Android device through Android Device Bridge (ADB)


#### Getting Started
1. Open the app on your mobile device.
2. Connect your MetaWear Device to the app via BLE (make sure you connect the right board by verifying your board MAC address).
3. After connecting your board, you will be presented with a dashboard.
4. Set your Metawear in a safe place (your pocket or on your wrist using a [watchband](https://mbientlab.com/product/metamotionrp-bundle/#single/0)) 
5. Place your phone on a phone bike mount.
6. If you desire to set a threshold speed, click on the `target icon` and enter a speed you would like to maintain.
7. Press the start button and start biking. Your current speed, time. distance and current temperature will be displayed on the dashboard.
8. Press the pause button to pause your workout.
9. Press the stop button to stop your workout.

### Final Presentation Slides
[View Me on Google Slides](https://docs.google.com/presentation/d/e/2PACX-1vSvg5km-5GBNq_aorOgf-qqlfTs6fJCsSxN5V-AGFx_0ikSqs8AlXXwfG3Nz4PRyLYucDGM_Alh2vdc/pub?start=false&loop=true&delayms=3000)
