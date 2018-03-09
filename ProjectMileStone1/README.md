# Project Repo Name

## Executive Summary
Problem statement text goes here

## Project Goals
* Bulleted list item 1
* Bulleted list item 2
* etc

## User stories
As a **user/role**, I want to **goal** so I can **rationale**.
**Acceptance Criteria:**
* Insert criteria 1 here
* Insert criteria 2 here
* etc

## Misuser stories
As a **misuser/misuser-role**, I want to **misuse goal** so I can **bad rationale**.
**Mitigations:**
* Mitigation technique 1 to be used goes here
* Mitigation technique 2 to be used goes here
* etc

## High Level Design
![Tooltip for visually disabled](./path-to-image-file.imgextension)

## Component List
### Component 1 Name here
Component description here

#### Sub-component 1.1 name here
Sub component description here

#### Sub-component 1.2 name here
Sub component description here

### Component 2 Name here
Component 2 description here

#### Sub-component 2.1 name here
Sub component description here

#### Sub-component 2.2 name here
Sub component description here

## Security analysis
Text describing high level diagram with red or other callouts identifying problem points or attacks.
![Tooltip for visually disabled](./path-to-image-file.imgextension)

| Component name | Category of vulnerability | Issue Description | Mitigation |
|----------------|---------------------------|-------------------|------------|
| Component 1 name | Privilege Escalation | This component exposes an interface to integrated webviews that might allow malicious javascript to read and modify protected data on the component that it shouldn't have access it. | Sandboxing techniques should encapsulate access permissions and capabilities for webviews individually to prevent privilege escalation.|

