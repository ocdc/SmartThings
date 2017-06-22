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
        
		standardTile("switch", "device.switch", width: 2, height: 2) {
				state "off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
				state "on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}
		standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		standardTile("netflix", "generic", width: 2, height: 2) {
				state "default", label: 'Netflix', action: "openNetflix"
		}
		standardTile("vudu", "generic", width: 2, height: 2) {
				state "default", label: 'Vudu', action: "openVudu"
		}
		standardTile("hulu", "generic", width: 2, height: 2) {
				state "default", label: 'Hulu', action: "openHulu"
		}		
		standardTile("pandora", "generic", width: 2, height: 2) {
				state "default", label: 'Pandora', action: "openPandora"
		}
		standardTile("comcast", "generic", width: 2, height: 2) {
				state "default", label: 'Comcast', action: "openComcast"
		}        

		main "mediaMulti"
		details(["mediaMulti","switch","comcast","pandora","netflix","hulu","vudu","energy","power","refresh","reset"])
	}
}

private shutdown() {
	logDebug "Executing Shutdown Command"
	sendEvent(name: "status", value: "Shutting Down", isStateChange: true)
	return executeEventGhostCommand("forceShutdown")
}

def mute() {
	logDebug "Executing Reboot Command"
	sendEvent(name: "mute", value: "muted", isStateChange: true)
	return executeEventGhostCommand("mute")
}

def unmute() {
	logDebug "Executing unmute() Command"
	sendEvent(name: "mute", value: "unmuted", isStateChange: true)
	return executeEventGhostCommand("unmute")
}

def openNetflix() {
	logDebug "Executing openNetflix()"
	return executeShutterCommand("openNetflix")
}

def openVudu() {
	logDebug "Executing openVudu()"
	return executeShutterCommand("openVudu")
}

def openComcast() {
	logDebug "Executing openComcast()"
	return executeShutterCommand("openComcast")
}

def openHulu() {
	logDebug "Executing openHulu()"
	return executeShutterCommand("openHulu")
}

def closeFirefox() {
	logDebug "Executing closeFirefox()"
	return executeShutterCommand("closeFirefox")
}

def executeShutterCommand(cmd) {
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