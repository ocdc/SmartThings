/**
 *  NodOn Wall Switch (CWS-3-1-01)
 *  Copyright 2015 NodOn
 *
 *  Notes:
 *  Can be made to work with 4 types of click however only push (pushType 0) and hold (pushType 2) are currently used.
 *
 *  Changelog:
 *  1.1.1 - Removed central scene references
 *  1.1 - Fixed layout and removed central scene
 *  1.0 - First release
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
 
metadata {
	definition (name: "NodOn Wall Switch", namespace: "ocdc", author: "ocdc") {
		capability "Actuator"
		capability "Button"
		capability "Configuration"
		capability "Battery"
        
        attribute "label", "string"

		command "pushButtonOne"
		command "pushButtonTwo"
		command "pushButtonThree"
		command "pushButtonFour"
		command	"buttonEvent"
		command	"buttonPushed"
		command	"buttonPushed", [int]
		command	"refresh"

    	fingerprint deviceId: "0x0101", inClusters: "0x5E,0x85,0x59,0x80,0x5B,0x70,0x5A,0x72,0x73,0x86,0x84,0xEF,0x5E,0x5B,0x2B,0x27,0x22,0x20,0x26,0x84"
	}
    
	tiles(scale: 2) {
		multiAttributeTile(name:"button", type:"generic", width:6, height:4) {
  			tileAttribute("device.button", key: "PRIMARY_CONTROL"){
				attributeState "default", label:'Switch', backgroundColor:"#79b821", icon:"st.Home.home30"
			}
            tileAttribute ("device.battery", key: "SECONDARY_CONTROL") {
				attributeState "batteryLevel", label:'${currentValue} % battery', unit:"%"
			}
		}
        standardTile("pushButtonOne", "device.button", width: 2, height: 2) {
            state "default", label: "1", action: "pushButtonOne", defaultState: true, backgroundColor: "#ffa81e"
            state "pushed", label: "1", action: "pushButtonOne", backgroundColor: "#79b821"
        }
        standardTile("pushButtonTwo", "device.button", width: 2, height: 2) {
            state "default", label: "2", action: "pushButtonTwo", defaultState: true, backgroundColor: "#ffa81e"
            state "pushed", label: "2", action: "pushButtonTwo", backgroundColor: "#79b821"
        }
        standardTile("pushButtonThree", "device.button", width: 2, height: 2) {
            state "default", label: "3", action: "pushButtonThree", defaultState: true, backgroundColor: "#ffa81e"
            state "pushed", label: "3", action: "pushButtonThree", backgroundColor: "#79b821"
        }
        standardTile("pushButtonFour", "device.button", width: 2, height: 2) {
            state "default", label: "4", action: "pushButtonFour", defaultState: true, backgroundColor: "#ffa81e"
            state "pushed", label: "4", action: "pushButtonFour", backgroundColor: "#79b821"
        }
        standardTile("holdButtonOne", "device.button", width: 2, height: 2) {
            state "default", label: "1", action: "pushButtonOne", defaultState: true, backgroundColor: "#ffa81e"
            state "pushed", label: "1", action: "pushButtonOne", backgroundColor: "#00a0dc"
        }
        standardTile("holdButtonTwo", "device.button", width: 2, height: 2) {
            state "default", label: "2", action: "pushButtonTwo", defaultState: true, backgroundColor: "#ffa81e"
            state "pushed", label: "2", action: "pushButtonTwo", backgroundColor: "#00a0dc"
        }
        standardTile("holdButtonThree", "device.button", width: 2, height: 2) {
            state "default", label: "3", action: "pushButtonThree", defaultState: true, backgroundColor: "#ffa81e"
            state "pushed", label: "3", action: "pushButtonThree", backgroundColor: "#00a0dc"
        }
        standardTile("holdButtonFour", "device.button", width: 2, height: 2) {
            state "default", label: "4", action: "pushButtonFour", defaultState: true, backgroundColor: "#ffa81e"
            state "pushed", label: "4", action: "pushButtonFour", backgroundColor: "#00a0dc"
        }
        valueTile(name:"battery", attribute:"device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "battery", label:'${currentValue}% battery', unit:""
		}
        standardTile("refresh", "generic", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'', action: "refresh", icon:"st.secondary.refresh"
        }
        standardTile("configure", "device.Configuration", decoration: "flat", width: 2, height: 2) {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
        }
        valueTile("pushLabel", "device.label", width: 2, height: 4, inactiveLabel: false, decoration: "flat") {
			state "default", label:'Simulate\nSwitch\nPush'
		}
        valueTile("holdLabel", "device.label", width: 2, height: 4, inactiveLabel: false, decoration: "flat") {
			state "default", label:'Simulate\nSwitch\nHold'
		}
		main "button" 
		details(["button", "pushButtonOne", "pushButtonTwo", "pushLabel", "pushButtonThree", "pushButtonFour","holdButtonOne", "holdButtonTwo", "holdLabel", "holdButtonThree", "holdButtonFour", "refresh", "configure", "battery"])
	}
}

def initialize() {
    state.configureRefresh = 0
    state.batteryRefresh = 0
}

def installed() {
    initialize()
}

def updated() {
    initialize()
}

def parse(String description) {
	def results = []
    
	if (description.startsWith("Err")) {
	    results = createEvent(descriptionText:description, displayed:true)
	} else {
		def cmd = zwave.parse(description, [0x80: 1, 0x84: 1]) // battery, wake up
		if(cmd) results += zwaveEvent(cmd)
		if(!results) results = [descriptionText: cmd, displayed: false]
	}
	return results
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
	def results = [createEvent(descriptionText: "$device.displayName woke up", isStateChange: false)]
	def prevBattery = device.currentState("battery")
    
   	if (!prevBattery || (new Date().time - prevBattery.date.time) / 60000 >= 60 * 53 || state.batteryRefresh == 1) {
		results << response(zwave.batteryV1.batteryGet().format())
        createEvent(name: "battery", value: "10", descriptionText: "battery is now ${currentValue}%", isStateChange: true, displayed: true)
        state.batteryRefresh == 0
	}
    
    if (state.configureRefresh == 1) {
		results << response(zwave.configurationV1.configurationSet(parameterNumber: 8, scaledConfigurationValue: 3).format())
		results << response(zwave.associationV1.associationSet(groupingIdentifier: 1, nodeId:zwaveHubNodeId).format())
		state.configureRefresh = 0
    }
    
	results << response(zwave.wakeUpV1.wakeUpNoMoreInformation().format())
	return results
}

def zwaveEvent(physicalgraph.zwave.commands.sceneactivationv1.SceneActivationSet cmd) {
    Integer button = (cmd.sceneId / 10) as Integer
    Integer pressType = cmd.sceneId - (button * 10) as Integer
	buttonEvent(button, pressType)
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	def map = [ name: "battery", unit: "%" ]
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "${device.displayName} has a low battery"
	} else {
		map.value = cmd.batteryLevel
	}
	createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	[descriptionText: "$device.displayName: $cmd", linkText: device.displayName, displayed: false]
}

def buttonEvent(button, pressType) {
    button = button as Integer
    pressType = pressType as Integer
    
	if (pressType == 0) { // pushed
    	createEvent(name: "button", value: "pushed", data: [buttonNumber: button, action: "pushed"], descriptionText: "$device.displayName button $button was pressed", isStateChange: true, displayed: true)
    } else if (pressType == 2) { // held
    	createEvent(name: "button", value: "pushed", data: [buttonNumber: button, action: "held"], descriptionText: "$device.displayName button $button was held", isStateChange: true, displayed: true)
    }
}

def buttonPushed(button) {
	sendEvent(name: "button", value: "pushed", data: [buttonNumber: button, action: "pushed"], descriptionText: "$device.displayName virtual button $button was pressed", isStateChange: true)
}

def refresh() {
	state.batteryRefresh = 1
}

def configure() {
	state.configureRefresh = 1
}

def pushButtonOne() {
	buttonPushed(1)
}

def pushButtonTwo() {
	buttonPushed(2)
}

def pushButtonThree() {
	buttonPushed(3)
}

def pushButtonFour() {
	buttonPushed(4)
}
