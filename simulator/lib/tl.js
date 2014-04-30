var util = require('./util');
var oauth = require('./oauth');

exports.handleGetLocation = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body = {
        "accuracy": "719", 
        "latitude": "47.67998", 
        "longitude": "-122.14459", 
        "timestamp": "2012-09-10T11:50:14.000-05:00" 
    };  

    util.sendJSONReply(response, body);
}
