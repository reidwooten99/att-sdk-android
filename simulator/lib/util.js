/**
 * Contains a list of utility functions.
 * @author Pavel Kazakov <pk9069@att.com>
 */

/**
 * Returns a random string of specified length and possible characters.
 * 
 * @param {integer} length length of string to return
 * @param {string} possible the generated string will contain only these characters
 * @return {string} random string
 */
function randomstr(length, possible) {
    var txt = '';
    for (var i = 0; i < length; ++i) { 
        txt += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return txt;
}

/**
 * Sends the specified body as a JSON object.
 *
 * @param response used for sending response
 * @param {string} body JSON body to send
 */
function sendJSONReply(response, body) {
    var jbody = JSON.stringify(body);
    console.log('Responding with: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}

function sendBufferedReply(response, buffer) {
    //response.writeHead(200, {'Content-Type': 'application/octet-stream'});
    response.end(buffer);
}

/**
 * Sends an error.
 *
 * @param response used for sending response
 * @param {string} errstr error string
 */
function sendError(response, errstr) {
    var err = {
        'error': errstr
    };
    jbody = JSON.stringify(err);
    response.writeHead(400, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}

/**
 * Generates a random hex string of specified length.
 *
 * The hex string generated will be all lowercase.
 *
 * @param {integer} length length of hex string to generate
 * @return {string} random hex string
 */
function hrandomstr(length) {
    var possible = '123456789abcdef';
    return exports.randomstr(length, possible);
}

/**
 * Gets the post body from the specified request and forwards it to the
 * specified callback function.
 *
 * @param request request to get post body from
 * @param {function} callback callback function to call after getting body
 */
function postBody(request, callback) {
    var body='';
    request.on('data', function (data) {
        body +=data;
    });
    request.on('end',function(){ 
        callback(body);
    });
};


/**
 * Gets the current time as a unix timestamp, in seconds.
 *
 * @return {integer} unix timestamp in seconds
 */
function xtimestamp() {
    return Math.floor(Date.now() / 1000);
}

// export functions
exports.randomstr = randomstr;
exports.sendJSONReply = sendJSONReply;
exports.sendBufferedReply = sendBufferedReply;
exports.sendError = sendError;
exports.hrandomstr = hrandomstr;
exports.postBody = postBody;
exports.xtimestamp = xtimestamp;

/* vim: set expandtab tabstop=4 shiftwidth=4 softtabstop=4: */
