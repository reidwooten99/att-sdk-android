
exports.handleIMMNSendMsg = function(request, response) {
    var body = {
        "Id":"MSGSx4948dkkdkdsxxb" 
    };  
    jbody = JSON.stringify(body);
    console.log('Sending IMMN send message: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}

exports.handleIMMNMessageHeaders = function(request, response) {
    var body = { 
        "MessageHeadersList": { 
            "Headers": [ 
                { 
                    "MessageId":"WU23435", 
                    "From":"+14258028620", 
                    "To": [ 
                        "+14257850159", 
                        "developer@att.com" 
                    ], 
                    "Subject":"Hello", 
                    "Received":"2012-01-14T12:00:00", 
                    "Text":"This is a Group MMS text only message", 
                    "Favorite":"True", 
                    "Read":"False", 
                    "Type":"SMSTEXT", 
                    "Direction":"IN" 
                }, 
                { 
                    "MessageId":"WU123", 
                    "From":"+14258028620", 
                    "To": [ 
                        "+14257850159" 
                    ], 
                    "Received":"2012-01-14T12:01:00", 
                    "Text":"This is a sms message", 
                    "Favorite":"True", 
                    "Read":"False", 
                    "Type":"SMSTEXT", 
                    "Direction":"IN" 
                }, 
                { 
                    "MessageId":"WU124", 
                    "From":"+14257850159", 
                    "To": [ 
                        "+14257850159", 
                        "developer@att.com" 
                    ], 
                    "Subject":"This is an MMS message with parts", 
                    "Received":"2012-01-14T12:00:00", 
                    "Favorite":"True", 
                    "Read":"False", 
                    "Type":"MMS", 
                    "Direction":"OUT", 
                    "MmsContent": [ 
                        { 
                            "ContentType":"text/plain", 
                            "ContentName":"part1.txt", 
                            "PartNumber":"0" 
                        }, 
                        { 
                            "ContentType":"image/jpeg", 
                            "ContentName":"sunset.jpg", 
                            "PartNumber":"1" 
                        } 
                    ] 
                } 
            ], 
            "HeaderCount":"3", 
            "IndexCursor":"I:123:,T:2783:,R:1983:,U:1045:,P:885" 
        } 
    };
    jbody = JSON.stringify(body);
    console.log('Sending IMMN get message headers: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}  

exports.handleIMMNMessageContent = function(request, response) {
    var body = 'Test message.';
    console.log('Sending IMMN get message content: ' + body);
    response.writeHead(200, {'Content-Type': 'TEXT/PLAIN'});
    response.write(body);
    response.end();
};

exports.handleIMMNRequest = function(request, response) {
    if (request.method == 'GET') {
        exports.handleIMMNMessageHeaders(request, response);
    } else if(request.method == 'POST') {
        exports.handleIMMNSendMsg(request, response);
    }
};
