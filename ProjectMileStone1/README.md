# Project Repo Name

## Executive Summary
Cycling also known as biking is one of the most common activity for stay in shape. In some cases, it is a merely a mean of transportation. An estimated 12,4 %[[https://www.statista.com/statistics/631280/kinds-of-sports-people-engage-in-regularly/] Americans bike regularly. The ubiquitiousness of wearable devices enabled many applications in several areas. Using Wearable devices in fitness is very common because they allow us to gather fitness data for analysis and give us the opportunity to improve our fitness posture. "BikeSpeedMonitor" is designed in combination with MetaWearRG sensor to provide a measure of cycling speed, cadence and pace then send feedback to the user so he ca adjust his/her speed according to a defined threshold.

## Project Goals
*  Record sensor data from MetaWearRG
* Build a mobile app with a simple interface that display speed, cadence, pace, distance and workout time to the user
* Send haptic feedback to user if speed lower than specified threshold
* Test application to ensure the functionality

## User stories
As a **cyclist**, I want to **monitor my cycling performance** so I can **analyze and improve on  my fitness**.
**Acceptance Criteria:**
* A user will be able to view the following cycling metrics on the phone interface:current speed, average speed, max speed, RPM, distance and time.

As a **cyclist**, I want to **set a desired speed** so I can **maintain that speed on my workout**.
**Acceptance Criteria:**
* A user will be able to input a speed that will constitute the threshold speed a user need to keep up with


As a **cyclist**, I want to **receive haptic feedback** so I can **increase my current speed over the the specified threshold**.
**Acceptance Criteria:**
* The coin Vibe Motor of the MetaWear board will vibrate everytime the current speed is lower than the defined threshold



## Misuser stories
As a **malicious actor**, I want to **conduct a bluetooth man in the middle attack** so I can **unauthorized access to sensor data**.
**Mitigations:**
* Use end to end encryption for device connection

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

User Interface Design
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
| Wearable Device (MetaWear)|Information Disclosure | This component sends raw sensor data to the mobile device through BLE and might allow an attacker to perform a man in the middle attack and gained access to unauthorized user data or even alter the communication between the mobile phone and the waerable device. | The wearable device should operate in a secure mode (service level enforced security or link level enforced security)|

