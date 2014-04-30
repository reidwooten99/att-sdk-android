var oauth = require('./oauth');

exports.handleSendMMS = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body =  {
        "outboundMessageResponse":{
            "messageId":"MMSa9b4dd85737c0409",
            "resourceReference":{
                "resourceURL":"https://api.att.com/mms/v3/messaging/outbox/MMSa9b4dd85737c0409"
            }
        }
    };  

    jbody = JSON.stringify(body);
    console.log('Sending MMS send message: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}

exports.handleMMSDeliveryStatus = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body = { 
        "DeliveryInfoList": { 
            "DeliveryInfo": [ {  
                "Id" : "msg0", 
                "Address" : "tel:3500000992", 
                "DeliveryStatus" : "DeliveredToTerminal" } ] , 
            "ResourceUrl": 'https://api.att.com/mms/v3/messaging/outbox/MMSa9b192780378404c'
        }
    } 

    jbody = JSON.stringify(body);
    console.log('Sending MMS delivery status: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}
