exports.handleDeviceCapabilities = function(request, response) {
    var body = { 
        "DeviceInfo": {
            "DeviceId": { 
                "TypeAllocationCode" : " 01196499" 
            },
            "Capabilities": { 
                "Name" : "LGE CU920", 
                "Vendor":  "LGE", 
                "Model":  "CU920", 
                "FirmwareVersion":  "CU920-MSM4090201D-V10h-FEB-05", 
                "UaProf":     "http://gsm.lge.com/html/gsm/LG-CU920.xml", 
                "MmsCapable":  "Y", 
                "AssistedGps":  "Y", 
                "LocationTechnology":  "SUPL2", 
                "DeviceBrowser" : "safari", 
                "WapPushCapable" : "Y" 
            } 
        }
    };  

    jbody = JSON.stringify(body);
    console.log('Sending DC reply: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}  
