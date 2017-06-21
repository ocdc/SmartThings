/**
 *
 *  Virtual Switch to Illuminance
 *  Copyright 2017 wosl
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
		definition (name: "Virtual Switch to Illuminance", namespace: "ocdc", author: "ocdc") {
		
	    capability "Sensor"
	    capability "Actuator"
	    capability "Switch"
        capability "Illuminance Measurement"
	}

	preferences {
		input "luxValueOff", "number", 
			title: "Lux value (off)", 
			defaultValue: 0, 
			displayDuringSetup: true, 
			required: false
		input "luxValueOn", "number", 
			title: "Lux value (on)", 
			defaultValue: 1000, 
			displayDuringSetup: true, 
			required: false
	}

	tiles {
 		standardTile("switch", "device.switch", width: 2, height: 2, decoration: "flat") {
			state "off", label: 'Off', action: "switch.on", icon: "https://raw.githubusercontent.com/wosl/SmartThings/master/DeviceTypes/OnOffButton/icon-off.png", backgroundColor: "#ffffff", nextState: "on"
			state "on", label: 'On', action: "switch.off", icon: "https://raw.githubusercontent.com/wosl/SmartThings/master/DeviceTypes/OnOffButton/icon-on.png", backgroundColor: "#79b821", nextState: "off"
		}
        standardTile("lux", "device.lux", width: 1, height: 1, decoration: "flat") {
            state "dark", label:'Dark', backgroundColor: "#ffffff", icon:"st.illuminance.illuminance.dark" 
            state "bright", label:'Bright', backgroundColor: "#ecf23a", icon:"st.illuminance.illuminance.bright" 
        }
        valueTile("illuminance", "device.illuminance", inactiveLabel: false, width: 1, height: 1) {
           state "luminosity", label:'${currentValue} lux', unit:"lux", backgroundColors:[
                	[value: luxValueOff, color: "#ffffff"],
                    [value: luxValueOn, color: "#79b821"]
               ]
		}
        main(["switch"])
        details(["switch","lux","illuminance"])
 	}
}

def on(){
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "illuminance", value: luxValueOn)
    sendEvent(name: "lux", value: "bright")
}

def off(){
	sendEvent(name: "switch", value: "off")
    sendEvent(name: "illuminance", value: luxValueOff)
    sendEvent(name: "lux", value: "dark")
}