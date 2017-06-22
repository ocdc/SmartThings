/**
 *  Virtual Device Manager Child
 *
 *  Copyright 2015 Brian Keifer
 *	Modified 2017 wosl
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
definition(
    name: "Virtual Device Manager Child",
    namespace: "wosl",
    parent: "wosl:Virtual Device Manager",
    author: "wosl",
    description: "Virtual Device Manager Child SmartApp to create new virtual devices.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    page(name: "namePage", nextPage: "devicePage")
    page(name: "devicePage")
}

def devicePage() {
    dynamicPage(name: "devicePage", title: "New Virtual Device", install: true, uninstall: childCreated()) {
        if (!childCreated()) {
            section { inputDeviceType() }
        } else {
            section { paragraph "Devices can not be converted to a different type after installation.\n\n${app.label}" }
        }
    }
}

def namePage() {
    dynamicPage(name: "namePage", title: "New Virtual Device", install: false, uninstall: childCreated()) {
        section {
            label title: "Device Label:", required: true
        }
    }

}

def inputDeviceType() {
    input "deviceType", "enum", title: "Device Type:", required: true, options: ["Virtual Switch", "Virtual Switch to Illuminance"], defaultValue: "Virtual Switch"
}

def installed() {
    spawnChildDevice(app.label, settings.deviceType)
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {

}

def spawnChildDevice(deviceLabel, deviceType) {
    app.updateLabel(deviceLabel)
    if (!childCreated()) {
        def child = addChildDevice("wosl", deviceType, getDeviceID(), null, [name: getDeviceID(), label: deviceLabel, completedSetup: true])
    }
}

def uninstalled() {
    getChildDevices().each {
        deleteChildDevice(it.deviceNetworkId)
    }
}

private childCreated() {
    if (getChildDevice(getDeviceID())) {
        return true
    } else {
        return false
    }
}

private getDeviceID() {
    return "VSM_${app.id}"
}
