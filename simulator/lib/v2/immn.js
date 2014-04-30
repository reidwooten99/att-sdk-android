/* vim: set expandtab tabstop=4 shiftwidth=4 softtabstop=4 */

var cfg = require('../../config');

var oauth = require('./../oauth');
var util = require('./../util');
var fs = require('fs');
var url = require('url');

function handleSendMessage(request, response) {
    var body = {
        "id":"MSGSx4948dkkdkdsxxb" 
    };  

    var jbody = JSON.stringify(body);
    console.log('Sending IMMN V2 send message: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}

function handleGetMessageList(request, response) {
    var body = { 
        "messageList": {
            "messages": [
            {  "messageId" : "WU23435",
                "from" : {"value": "+12065551212"},
                "recipients" : [{"value":"+14255551212"}, {"value":"someone@att.com"}],      
                "timeStamp" : "2012-01-14T12:00:00",
                "text" : "This is a Group MMS text only message",
                "isFavorite" : true,
                "isUnread" : false,
                "type" : "TEXT",
                "typeMetaData":{},
                "isIncoming" : "true"
            },
            {
                "messageId" : "WU123",
                "from" : {"value":"+12065551212"},
                "recipients" : [{"value":"+14255551212"}],
                "timeStamp" : "2012-01-14T12:01:00",
                "text" : "This is an SMS message",
                "isFavorite" : true,
                "isUnread" : false,
                "type" : "TEXT",
                "typeMetaData":{
                    "isSegmented": true,
                    "segmentationDetails":{
                        "segmentationMsgRefNumber":112,
                        "totalNumberOfParts":4,
                        "thisPartNumber":1
                    }

                },
                "isIncoming" : "true"
            },
            {
                "messageId" : "WU124",
                "from" : {"value":"+14255551212"},
                "recipients" : [{"value":"+14255551212"}, {"value":"someone@att.com"}],
                "subject" : "This is an MMS message with parts",
                "timeStamp" : "2012-01-14T12:00:00",
                "isFavorite" : true,
                "isUnread" : false,
                "type" : "MMS",
                "typeMetaData":{
                    "subject" : "Hello"
                },
                "isIncoming" : "false",
                "mmsContent" : [
                { "contentType" : "text/plain",
                    "contentName" : "part1.txt",
                    "contentUrl" : "/myMessages/v2/messages/0",
                    "type": "TEXT"
                },
                { "contentType" : "image/jpeg",
                    "contentName" : "sunset.jpg",
                    "contentUrl" : "/myMessages/v2/messages/1",
                    "type": "MMS"
                }
                ]
            }],
            "offset": 50,
            "limit": 10,
            "total": 3,
            "state":  "I:1291659705|H4sIAAAAAAAAAGNgYAAAEtlB_wMAAAA|:,S:1291659706|H4sIAAAAAAAAAGNkYqzhyWBgHIUYkAkIB9oNIx0yQTECMkPF4aKcjIMHsLIOtAsoBZgeAABR7gcNIgQAAA|:,t:1348690584.000019000.N.00000000:,r:1346267668.000005000.N.00000000:,u:1336148844.000009000.N.00000000:,p:1292019761.000702000.N.00000000:,",
            "cacheStatus": "INITIALIZED",
            "failedMessages": ["S334","S443"]
        }
    };

    var jbody = JSON.stringify(body);
    console.log('Sending IMMN V2 get message list: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}

function handleUpdateMessages(request, response) {
    console.log('Sending IMMN V2 update message: 204');
    response.writeHead(204);
    response.end();
}

function handleDeleteMessages(request, response) {
    console.log('Sending IMMN V2 delete message: 204');
    response.writeHead(204);
    response.end();
}

exports.handleMessages = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }
    if (request.method == 'POST') {
        handleSendMessage(request, response);
    } else if (request.method == 'GET') {
        handleGetMessageList(request, response);
    } else if (request.method == 'PUT') {
        handleUpdateMessages(request, response);
    } else if (request.method == 'DELETE') {
        handleDeleteMessages(request, response);
    } else {
        console.log('Invalid request method: ' + request.method);
    }
}

function handleGetMessage(request, response) {
    var body = {
        "message": {
            "messageId" : "WU124",
            "from" : {"value": "+12065551212"},
            "recipients" : [{"value":"+14255551212"}, {"value":"someone@att.com"}], 
            "timeStamp" : "2012-01-14T12:00:00",
            "isFavorite" : true,
            "isUnread" : false,
            "type" : "MMS",
            "typeMetaData":{
                "subject" : "This is an MMS message with parts"
            },
            "isIncoming" : "false",
            "mmsContent" : [
            { "contentType" : "text/plain",
                "contentName" : "part1.txt",
                "contentUrl" : "/myMessages/v2/messages/0",
                "type":"TEXT"
            },
            { "contentType" : "image/jpeg",
                "contentName" : "sunset.jpg",
                "contentUrl" : "/myMessages/v2/messages/1",
                "type":"IMAGE"
            }]
        }
    };  

    var jbody = JSON.stringify(body);
    console.log('Sending IMMN V2 get message: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}

function handleUpdateMessage(request, response) {
    console.log('Sending IMMN V2 update message: 204');
    response.writeHead(204);
    response.end();
}

function handleDeleteMessage(request, response) {
    console.log('Sending IMMN V2 delete message: 204');
    response.writeHead(204);
    response.end();
}

exports.handleMessage = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }
    if (request.method == 'GET') {
        handleGetMessage(request, response);
    } else if (request.method == 'PUT') {
        handleUpdateMessage(request, response);
    } else if (request.method == 'DELETE') {
        handleDeleteMessage(request, response);
    } else {
        console.log('Invalid request method: ' + request.method);
    }
}

exports.handleGetMessagesDelta = function(request, response) {
    var body = {
        "deltaResponse": {
            "state": "I:1291659705|H4sIAAAAAAAAAGNgYAAAEtlB_wMAAAA|:,S:1291659706|H4sIAAAAAAAAAGNkYqzhyWBgHIUYkAkIB9oNIx0yQTECMkPF4aKcjIMHsLIOtAsoBZgeAABR7gcNIgQAAA|:,t:1348690584.000019000.N.00000000:,r:1346267668.000005000.N.00000000:,u:1336148844.000009000.N.00000000:,p:1292019761.000702000.N.00000000:,",
            "delta": [
            {
                "adds": [
                {
                    "isFavorite": true,
                    "messageId": "t123",
                    "isUnread": false
                },
                {
                    "isFavorite": true,
                    "messageId": "t456",
                    "isUnread": false
                }
                ],
                    "deletes": [
                    {
                        "isFavorite": true,
                        "messageId": "t789",
                        "isUnread": false
                    }
                ],
                    "type": "TEXT",
                    "updates": [
                    {
                        "isFavorite": true,
                        "messageId": "t222",
                        "isUnread": false
                    },
                    {
                        "isFavorite": true,
                        "messageId": "t223",
                        "isUnread": false
                    }
                ]
            },
            {
                "adds": [],
                "deletes": [],
                "type": "MMS",
                "updates": []
            }

            ]
        }
    };

    jbody = JSON.stringify(body);
    console.log('Sending IMMN V2 get message deltas: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}

exports.handleGetMessageContent = function(request, response) {
    var pathname = url.parse(request.url).pathname;

    var fpath = "";
    var content_type = "";
    if (pathname.indexOf("image") > -1){
        fpath = cfg.image;
        content_type = cfg.image_type;
    }
    else if (pathname.indexOf("video") > -1) {
        fpath = cfg.video;
        content_type = cfg.video_type;
    }
    else if (pathname.indexOf("audio") > -1) {
        fpath = cfg.audio;
        content_type = cfg.audio_type;
    }
    else {
        fpath = cfg.text;
        content_type = cfg.text_type;
    }

    // spawn a callback to read file and return it
    fs.stat(fpath, function(err, stats){
        var len = stats.size;
        var buffer = new Buffer(len);

        var file = fs.openSync(fpath, 'r');
        var binary = fs.readSync(file, buffer, 0, len);

        response.writeHead(200, {'Content-Type': content_type});
        util.sendBufferedReply(response, buffer);
    });
    console.log('Sending IMMN V2 get message content: ');
    console.log('content-type: ' + content_type);
}


exports.handleCreateMessageIndex = function(request, response) {
    console.log('Sending IMMN V2 create message index');

    response.writeHead(202);
    response.end();
}

exports.handleGetMessageIndexInfo = function(request, response) {
    console.log('sending immn v2 get message index info');
    // todo: content length?

    var body = {
        "messageIndexInfo": {
            "status":"INITIALIZED",
            "state":"I:1358197744|H4sIAAAAAAAAAGNgYAAAEtlB_wMAAAA|:,S:1358197745|H4sIAAAAAAAAAGNkhAAmRhhggjMRYnDAicQGAONa5T87AAAA|:,t:1362168556.000004000.N.00000000:,r:1358196436.000033000.N.00000000:,u:1358534509.000040000.N.00000000:,p:1358534509.000043000.N.00000000:,", 
            "messageCount":164 
        }
    };

    var jbody = JSON.stringify(body);
    response.writeHead(200, {
        'Content-Type': 'application/json',
        'content-length' : jbody.length
    });
    response.write(jbody);
    response.end();
}

exports.handleGetNotificationConnectionDetails = function(request, response) {
    console.log('sending immn v2 get notification connection details');
    // todo: content length?

    var body = {
        "notificationConnectionDetails": {
            "username":"fa26fb5c-e577-41c5-b024-150e8ed671bf",
            "password":"b9c1a24e-b235-4d69-95bb-81271939e017",
            "httpsUrl":"https://sockjs.messages.att.net/stomp",
            "wssUrl":"wss://sockjs.messages.att.net/stomp/websocket",
            "queues": {
                "text":"/queue/afb193a9-c427-43c2-84d2-e4771800b6fe"
            }
        }
    };

    var jbody = JSON.stringify(body);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}
