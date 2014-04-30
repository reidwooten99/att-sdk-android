var oauth = require('./oauth');
var ids = [];

var util = require('./util');
var url = require('url');

exports.handleCMSCreateSession = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var id = util.hrandomstr(32);
    while (ids.indexOf(id) >= 0) {
        id = util.hrandomstr(32);
    }
    ids.push(id);

    var body = { 
        "success": true,
        "id": id
    };

    util.sendJSONReply(response, body); 
}

exports.handleCMSSendSignal = function(request, response, pathname) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var pathname = url.parse(request.url).pathname;
    console.log("path" + pathname)
    //rest/1/Sessions/87ff9d46e34dceb8a68139631be32342/Signals
    var regex = /\/rest\/1\/Sessions\/([1-9a-f]+)\/Signals/; 
    var match = pathname.match(regex);
    console.log("match" + match)
    var id = match[1];
    var index = ids.indexOf(id);
    var body = {};

    if (index >= 0) {
        // remove element
        ids.splice(index, 1);
        var body = {
            "status": "QUEUED"
        };
    } else {
        var body = {
            "status": "NOTFOUND"
        };

    }

    util.sendJSONReply(response, body); 
};
