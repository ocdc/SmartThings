/**
 *  Virtual Device Manager
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
    name: "Virtual Device Manager",
    namespace: "ocdc",
    author: "ocdc",
    description: "Create virtual devices for use in routines and other SmartApps",
    category: "My Apps",
    singleInstance: true,
    iconUrl: "https://github.com/wosl/SmartThings/raw/master/SmartApps/VirtualDeviceManager/icon.png",
    iconX2Url: "https://github.com/wosl/SmartThings/raw/master/SmartApps/VirtualDeviceManager/icon@2x.png",
    iconX3Url: "https://github.com/wosl/SmartThings/raw/master/SmartApps/VirtualDeviceManager/icon@3x.png"
)

preferences {
    page(name: "mainPage", title: "Installed Devices", install: true, uninstall: true,submitOnChange: true) {
        section {
            app(name: "virtualDevice", appName: "Virtual Device Manager Child", namespace: "wosl", title: "New Virtual Device", multiple: true)
        }
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    log.debug "There are ${childApps.size()} child smartapps"
    childApps.each {child ->
            log.debug "Child app: ${child.label}"
    }
}
