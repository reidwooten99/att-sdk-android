var oauth = require('./oauth');
var util = require('./util');

var JSON = require('JSON');

exports.handleSendSMS = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    util.postBody(request, function(pbody) {
        var body = {
            outboundSMSResponse : {
                messageId : "SMSa9b5f631c4f2f8ee"
            }
        };

        var json = JSON.parse(pbody);
        var notify = json.outboundSMSRequest.notifyDeliveryStatus;

        if (!notify) {
            body.outboundSMSResponse.resourceReference = {
                resourceURL : "https://api.att.com/sms/v3/messaging/outbox/SMSa9b5f631c4f2f8ee"
            }
        }

        util.sendJSONReply(response, body);
    });
}

exports.handleSMSDeliveryStatus = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body = { 
        "DeliveryInfoList": { 
            "DeliveryInfo": [ {  
                "Id" : "msg0", 
                "Address" : "tel:3500000992", 
                "DeliveryStatus" : "DeliveredToTerminal" } ] , 
            "ResourceUrl": 'https://api.att.com/sms/v3/messaging/outbox/MMSa9b192780378404c'
        }
    } 

    util.sendJSONReply(response, body);
}

exports.handleGetSMS = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body = 
    { 
        "InboundSmsMessageList": { 
            "InboundSmsMessage": [ 
            { 
                "MessageId":"msg0", 
                "Message":"Hello", 
                "SenderAddress":"tel:4257850159" 
            } 
            ], 
                "NumberOfMessagesInThisBatch":"1", 
                "ResourceUrl":"http://api.att.com/sms/v3/messaging/inbox/22627000", 
                "TotalNumberOfPendingMessages":"0" 
        } 
    }  

    util.sendJSONReply(response, body);
}
