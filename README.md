<!--
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2017 ForgeRock AS.
-->
# Set Profile Authentication Node

A simple authentication node for ForgeRock's [Identity Platform][forgerock_platform] 5.5 and above. This node sets user profile attributes. Supports setting profile attributes from shared state values or literal strings. Caution: since this node can write to user profile attributes it should only be used towards the end of an authentication journey, after the user has reached a sufficient authentication level.

## Installation

Download the jar from the releases tab on github [here](https://github.com/ForgeRock/set-profile-property-auth-tree-node/releases/latest).

Copy the .jar file from the ../target directory into the ../web-container/webapps/openam/WEB-INF/lib directory where AM is deployed.  Restart the web container to pick up the new node.  The node will then appear in the authentication trees components palette.

## Usage

### Set Profile Property Configuration
* **Properties** - The user profile attributes to set. `Key` = `profile attribute name`, `Value` = `Shared State Object
 Name` or "literal".
* **Transient Properties** -  The user profile attributes to set. `Key` = `profile attribute name`, `Value` = `Shared
 State Object Name` or "literal". If the value is not literal, then transient state is used to lookup the values.
* **Add Attributes** - When enabled, the new attribute values will be added to the existing attribute values instead of replacing them.

**Note** - This node can handle both `String` and `List<String>` values stored in Access Management sharedState.

## To Build

The code in this repository has binary dependencies that live in the ForgeRock maven repository. Maven can be configured to authenticate to this repository by following the following [ForgeRock Knowledge Base Article](https://backstage.forgerock.com/knowledge/kb/article/a74096897).

Edit the necessary SetProfilePropertyNode.java as appropriate.  To rebuild, run "mvn clean install" in the directory containing the pom.xml  

![ScreenShot](./set-property.png)  

## Disclaimer
The sample code described herein is provided on an "as is" basis, without warranty of any kind, to the fullest extent permitted by law. ForgeRock does not warrant or guarantee the individual success developers may have in implementing the sample code on their development platforms or in production configurations.

ForgeRock does not warrant, guarantee or make any representations regarding the use, results of use, accuracy, timeliness or completeness of any data or information relating to the sample code. ForgeRock disclaims all warranties, expressed or implied, and in particular, disclaims all warranties of merchantability, and warranties related to the code, or any service or software related thereto.

ForgeRock shall not be liable for any direct, indirect or consequential damages or costs of any type arising out of any action taken by you or others related to the sample code.  

[forgerock_platform]: https://www.forgerock.com/platform/  
