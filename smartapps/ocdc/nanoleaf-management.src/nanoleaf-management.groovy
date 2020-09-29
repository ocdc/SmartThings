/**
 *
 *  Nanoleaf Management SmartApp v1.1
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
 
 /* Todo List
	1. Set IP with update device ID
    2. Set port
    3. Rename pages
    4. Add all data to info page
    5. Make logging more consistant
    6. Remove scene favorites (not used for anything really)
    7. Refactor and optimise DTH
 */

definition(
    name: "Nanoleaf Management",
    namespace: "ocdc",
    author: "OC",
    description: "Nanoleaf Management SmartApp",
    category: "Convenience",
    iconUrl: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/icon.png",
    iconX2Url: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/icon-2x.png",
    iconX3Url: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/icon-3x.png",
    pausable: false
)

preferences {
    page(name: "displayDetails")
    page(name: "pickScene", title: "Select a Scene")   
    page(name: "pageGetApi", title: "Get API Key")
    page(name: "pageClearApi", title: "Clear API Key")
    page(name: "setScene", title: "Scene Set")    
    page(name: "newLeaf", title: "Nanoleaf Selection")
    page(name: "leafStatus", title: "Nanoleaf Information")
    page(name: "idPanels", title: "Panel Identification")
    page(name: "pageMissingData", title: "Missing Data")
    page(name: "pageAcknowledgements", title: "Acknowledgements")
    page(name: "pageSetupHelp", title: "Setup Help")
    page(name: "pageRefresh", title: "Refresh")
    page(name: "pageClearPresetsAndPanelIds", title: "Clear Presets and IDs")
}

def installed() {
	log.debug "Installed with settings: ${settings}"
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
}

def displayDetails() {
	if (!theNanoleaf) {
		return dynamicPage(name: "displayDetails", title: "Nanoleaf Selection", install: true, uninstall: true) {
			section("Select your Nanoleaf") {
				input "theNanoleaf", "device.NanoleafAuroraSmarterAPI", multiple: false, required: true, title: "Nanoleaf?", submitOnChange: true
			}
		}
	}
	else {
        def presetsMap = null
    
        if (theNanoleaf.currentValue("presets")?.trim()) {
            presetsMap = new groovy.json.JsonSlurper().parseText(theNanoleaf.currentValue("presets"))
        }
    
        return dynamicPage(name: "displayDetails", uninstall: true, install: true) {
            section ("${theNanoleaf.name} Selected"){
                href(name: "leafChange", title: "Select a Different Nanoleaf", required: false, page: "newLeaf", description: "Tap to change Nanoleafs", image: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/menu-icons/select.png")
            }
            section ("Actions"){
                if (presetsMap && presetsMap.name.size() > 0) {
                    href(name: "sceneSelect", title: "Activate a Scene", required: false, page: "pickScene", description: "Tap to select a scene", image: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/menu-icons/activate.png")
                    href(name: "panels", title: "Identify Panels", required: false, page: "idPanels", description: "Tap to identify panels", image: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/menu-icons/identify.png")
                } else {
                    href(name: "pageMissingData", title: "Missing Data", required: false, page: "pageMissingData", description: "Tap for more information", image: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/menu-icons/warning.png")
                }
                href(name: "leafStats", title: "View ${theNanoleaf.name} Information", required: false, page: "leafStatus", description: "Tap to view ${theNanoleaf.name} information", image: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/menu-icons/information.png")
            }
            section ("Settings"){
                href(name: "pageSetApi", title: "Get an API Key", required: false, page: "pageGetApi", description: "Tap to get an API Key", image: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/menu-icons/get-key.png")
                href(name: "pageClearApi", title: "Clear the API Key", required: false, page: "pageClearApi", description: "Tap to clear the API Key", image: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/menu-icons/clear.png")
                href(name: "pageRefresh", title: "Refresh Data", required: false, page: "pageRefresh", description: "Tap to refresh data", image: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/menu-icons/refresh.png")
                href(name: "pageClearPresetsAndPanelIds", title: "Clear Presets and IDs", required: false, page: "pageClearPresetsAndPanelIds", description: "Tap to clear Presets and IDs", image: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/menu-icons/clear.png")
            }
            section ("Help"){
                href(name: "pageAcknowledgements", title: "Acknowledgements", required: false, page: "pageAcknowledgements", description: "", image: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/menu-icons/acknowledgements.png")
                href(name: "pageSetupHelp", title: "Setup Help", required: false, page: "pageSetupHelp", description: "", image: "https://github.com/ocdc/SmartThings/raw/master/smartapps/ocdc/nanoleaf-management.src/menu-icons/help.png")
            }
        }
    } 
}

def pickScene() {
    def presetsMap = new groovy.json.JsonSlurper().parseText(theNanoleaf.currentValue("presets"))
    return dynamicPage(name: "pickScene", nextPage: "setScene") {
        section ("${theNanoleaf.name} Scenes"){
            input name: "selectedScene", type: "enum", options: presetsMap.name, description: "Select the Scene to Activate", defaultValue: "" , required: no
        } 
    }
}

def setScene() {
	log.debug "Setting A Scene ${selectedScene}"
    theNanoleaf.changeScene(selectedScene)
    return dynamicPage(name: "setScene", nextPage: "displayDetails") {
        section("${theNanoleaf.name} Scene \"${selectedScene}\" Set"){
        }
    }
}

def pageClearPresetsAndPanelIds() {
	log.debug "Clearing Scenes and Panel IDs"
    theNanoleaf.clearPresetsAndPanelIds()
    return dynamicPage(name: "pageClearPresetsAndPanelIds", nextPage: "displayDetails") {
        section("All scenes and Panel IDs have been cleared, a data refresh will be needed sync current data"){
        }
    }
}

def pageRefresh() {
	log.debug "Refresh data"
    theNanoleaf.refresh()
    return dynamicPage(name: "pageRefresh", nextPage: "displayDetails") {
        section("A request to get current data has been sent"){
        }
    }
}

def pageGetApi() {
    theNanoleaf.requestAPIkey()
    return dynamicPage(name: "pageGetApi", nextPage: "displayDetails") {
        section ("An API Key for then ${theNanoleaf.name} has been requested"){
        }
    }
}

def pageClearApi() {
    theNanoleaf.clearApiKey()
    return dynamicPage(name: "pageClearApi", nextPage: "displayDetails") {
        section ("The API Key for the ${theNanoleaf.name} has been removed"){
        } 
    }
}

def newLeaf() {
    return dynamicPage(name: "newLeaf", title: "Nanoleaf Selection", nextPage: "displayDetails") {
        section("Select your Nanoleaf") {
            input "theNanoleaf", "device.NanoleafAuroraSmarterAPI", multiple: false, required: true, title: "Nanoleaf?"
        }
    }
}

def leafStatus() {
    def scene1 = theNanoleaf.currentValue("scene1")
    def scene2 = theNanoleaf.currentValue("scene2")
    def scene3 = theNanoleaf.currentValue("scene3")
    def curScene = theNanoleaf.currentValue("scene")
    def curDeviceInfo = theNanoleaf.currentValue("IPinfo")
    def curAPI = theNanoleaf.currentValue("retrievedAPIkey")
    def curAPIStatus = theNanoleaf.currentValue("apiKeyStatus")

    return dynamicPage(name: "leafStatus", title: "Nanoleaf Information", nextPage: "displayDetails") {
        section ("${theNanoleaf.name} Status Information") {
            paragraph "Device Info:  ${curDeviceInfo}\nRetrieved API Key:  ${curAPI}\nAPI Status:  ${curAPIStatus}" 
            paragraph "Curent scene: ${curScene}"  
            paragraph "Scene 1: ${scene1}\nScene 2: ${scene2}\nScene 3: ${scene3}" 
        }
    }
}

def idPanels() {
    def panels = theNanoleaf.currentValue("panelIds").split(",")
    def colors = ["Red","Blue","Green","Yellow","Orange","Pink","Purple","Black","White"]
    def setColor
    def colorIndex = 0
    def setNumber = 0
    def pageText = "${theNanoleaf.name} panels:"
    for (int i = 0; i < panels.size();i++) {
        if (colorIndex == 8) {setNumber++}
        colorIndex = i - (9*setNumber)	
        setColor = colors[colorIndex]
        pageText = "${pageText}\nPanel Id ${panels[i]} is ${colors[colorIndex]}"
        theNanoleaf.setPanelColor(panels[i], colors[colorIndex], false)
    }

    return dynamicPage(name: "idPanels", title: "${theNanoleaf.name} Panel Identification", nextPage: "displayDetails") {
        section ("${theNanoleaf.name} Panels") {
            paragraph pageText
        }
    }
}

private pageMissingData() {
	return dynamicPage(name: "pageMissingData", title: "Missing Data", nextPage: "displayDetails") {
        section() {
            paragraph "Data hasn't been fully loaded yet, assuming the details are correct you can use the refresh option to force a data refresh"
        }
    }
}

private pageSetupHelp() {
	return dynamicPage(name: "pageSetupHelp", title: "Setup Help", nextPage: "displayDetails") {
        section() {
            paragraph "To get setup there are a few steps:"
            paragraph "1. "
        }
    }
}

private pageAcknowledgements() {
	return dynamicPage(name: "pageAcknowledgements", title: "Acknowledgements", nextPage: "displayDetails") {
        section() {
            paragraph "Just a streamlined version of this: https://github.com/Mellit7/NanoleafAuroraHandler"
        }
    }
}