/**
 *
 *  Shutter
 *  Copyright 2017 ocdc
 *
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *	in compliance with the License. You may obtain a copy of the License at:
 *
 *			http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *	on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *	for the specific language governing permissions and limitations under the License.
 *
 */

metadata {
	definition (name: "Shutter", namespace: "ocdc", author: "ocdc") {
		capability "Actuator"
		capability "Sensor"
		capability "Configuration"
		capability "Refresh"
		capability "Polling"
		capability "Switch"
		
		command "shutdown"
		command "reboot"
		command "logoff"
		command "lock"
		command "sleep"
		command "hibernate"
		command "monitorOff"
		command "screenSaver"
		command "mute"
		command "unmute"
	}

	preferences {
		input "shutterHost", "text",
			title: "Host:",
			defaultValue: "192.168.0.1",
			displayDuringSetup: true,
			required: true
		input "shutterPort", "text",
			title: "Port:",
			defaultValue: "1999",
			displayDuringSetup: true,
			required: true
		input "shutterUsername", "text",
			title: "Username:",
			displayDuringSetup: true,
			required: false
		input "shutterPassword", "password",
			title: "Password:",
			displayDuringSetup: true,
			required: false
	}
		
	tiles(scale: 2) {   
		standardTile("shutdown", "generic", width: 3, height: 3, decoration: "flat") {
				state "default", label: 'Shutdown', action: "shutdown", icon: "https://github.com/ocdc/SmartThings/raw/master/devicetypes/ocdc/shutter.src/icon-shutdown.png"
		}
		standardTile("reboot", "generic", width: 3, height: 3, decoration: "flat") {
				state "default", label: 'Reboot', action: "reboot", icon: "https://github.com/ocdc/SmartThings/raw/master/devicetypes/ocdc/shutter.src/icon-restart.png"
		}
		standardTile("logoff", "generic", width: 2, height: 2, decoration: "flat") {
				state "default", label: 'Logoff', action: "logoff", icon: "https://github.com/ocdc/SmartThings/raw/master/devicetypes/ocdc/shutter.src/icon-logoff.png"
		}		
		standardTile("lock", "generic", width: 2, height: 2, decoration: "flat") {
				state "default", label: 'Lock', action: "lock", icon: "https://github.com/ocdc/SmartThings/raw/master/devicetypes/ocdc/shutter.src/icon-lock.png"
		}
		standardTile("sleep", "generic", width: 2, height: 2, decoration: "flat") {
				state "default", label: 'Sleep', action: "sleep", icon: "https://github.com/ocdc/SmartThings/raw/master/devicetypes/ocdc/shutter.src/icon-sleep.png"
		}
        standardTile("hibernate", "generic", width: 2, height: 2, decoration: "flat") {
				state "default", label: 'Hibernate', action: "hibernate", icon: "https://github.com/ocdc/SmartThings/raw/master/devicetypes/ocdc/shutter.src/icon-hibernate.png"
		}
        standardTile("monitorOff", "generic", width: 2, height: 2, decoration: "flat") {
				state "default", label: 'Monitor Off', action: "monitorOff", icon: "https://github.com/ocdc/SmartThings/raw/master/devicetypes/ocdc/shutter.src/icon-monitoroff.png"
		}		
		standardTile("screenSaver", "generic", width: 2, height: 2, decoration: "flat") {
				state "default", label: 'Screen Saver', action: "screenSaver", icon: "https://github.com/ocdc/SmartThings/raw/master/devicetypes/ocdc/shutter.src/icon-screensaver.png"
		}
		standardTile("mute", "generic", width: 2, height: 2, decoration: "flat") {
				state "default", label: 'Mute', action: "mute", icon: "https://github.com/ocdc/SmartThings/raw/master/devicetypes/ocdc/shutter.src/icon-mute.png"
		}
        standardTile("unmute", "generic", width: 2, height: 2, decoration: "flat") {
				state "default", label: 'Unmute', action: "unmute", icon: "https://github.com/ocdc/SmartThings/raw/master/devicetypes/ocdc/shutter.src/icon-unmute.png"
		}
        main(["shutdown"])
	}
}

def shutdown() {
	logDebug "Executing Shutdown Command"
	sendEvent(name: "status", value: "shutting down", isStateChange: true)
	return executeShutterCommand("Shutdown")
}

def reboot() {
	logDebug "Executing Reboot Command"
	sendEvent(name: "status", value: "rebooting", isStateChange: true)
	return executeShutterCommand("Reboot")
}

def logoff() {
	logDebug "Executing Logoff Command"
	sendEvent(name: "status", value: "logging off", isStateChange: true)
	return executeShutterCommand("Logoff")
}

def lock() {
	logDebug "Executing Lock Command"
	sendEvent(name: "status", value: "locked", isStateChange: true)
	return executeShutterCommand("Lock")
}

def sleep() {
	logDebug "Executing Sleep Command"
	sendEvent(name: "status", value: "sleeping", isStateChange: true)
	return executeShutterCommand("Sleep")
}

def hibernate() {
	logDebug "Executing Hibernate Command"
	sendEvent(name: "status", value: "hibernating", isStateChange: true)
	return executeShutterCommand("Hibernate")
}

def monitorOff() {
	logDebug "Executing MonitorOff Command"
	sendEvent(name: "status", value: "monitor off", isStateChange: true)
	return executeShutterCommand("MonitorOff")
}

def screenSaver() {
	logDebug "Executing ScreenSaver Command"
	sendEvent(name: "status", value: "screensaver", isStateChange: true)
	return executeShutterCommand("ScreenSaver")
}

def mute() {
	logDebug "Executing Mute Command"
	sendEvent(name: "status", value: "muted", isStateChange: true)
	return executeShutterCommand("VolumeMute")
}

def unmute() {
	logDebug "Executing Unmute Command"
	sendEvent(name: "status", value: "unmuted", isStateChange: true)
	return executeShutterCommand("VolumeUnmute")
}

private executeShutterCommand(cmd) {
	def result
	if (settings.shutterHost && settings.shutterPort){
		logInfo "Sending $cmd command to the Shutter webserver"
			result = new physicalgraph.device.HubAction(
				method: "GET",
				path: "/action?name=${cmd}",
				headers: getHeaders(),
				query: []
		)
	}
	else {
		log.warn "You must specify a Host and Port in order to use the ${cmd} command."
	}
	return result
}

private getHeaders() {
    def headers = [HOST: "${settings.shutterHost}:${settings.shutterPort}"]
	if (settings.shutterUsername) {
		def encodedCredentials = "${settings.shutterUsername}:${settings.shutterPassword}".getBytes().encodeBase64()
		headers.Authorization = "Basic $encodedCredentials"
	}
	return headers
}

private logDebug (msg) {
	log.debug msg
}

private logInfo(msg) {
	log.info msg
}