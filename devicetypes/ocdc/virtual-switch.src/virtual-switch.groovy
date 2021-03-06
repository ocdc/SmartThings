/**
 *
 *  Virtual Switch
 *  Copyright 2017 ocdc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

metadata {
	definition (name: "Virtual Switch", namespace: "ocdc", author: "ocdc") {
		capability "Actuator"
		capability "Switch"
		capability "Sensor"
	}

	tiles {
		standardTile("button", "device.switch", width: 2, height: 2, decoration: "flat") {
			state "off", label: 'Off', action: "switch.on", icon: "https://raw.githubusercontent.com/wosl/SmartThings/master/DeviceTypes/OnOffButton/icon-off.png", backgroundColor: "#ffffff", nextState: "on"
			state "on", label: 'On', action: "switch.off", icon: "https://raw.githubusercontent.com/wosl/SmartThings/master/DeviceTypes/OnOffButton/icon-on.png", backgroundColor: "#79b821", nextState: "off"
		}
		main "button"
		details "button"
	}
}

def on() {
	sendEvent(name: "switch", value: "on")
}

def off() {
	sendEvent(name: "switch", value: "off")
}