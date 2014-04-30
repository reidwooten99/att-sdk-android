var url = require('url');
var querystring = require('querystring');

var util = require('./util');
var tokenlib = require('./oauth_token');
var cfg = require('../config.js');

function generateToken() {
    // generate token
    var possible = '123456789abcdef';
        
    var accessToken = util.randomstr(32, possible);
    while (tokenlib.getToken(accessToken) != null) {
        accessToken = util.randomstr(32, possible);
    }

    var token = new tokenlib.OAuthToken(
        accessToken,
        cfg.tokenExpiry,
        util.randomstr(32, possible),
        [ 'scope' ]
    );

    return token;
}

function validateToken(request, response) {
    return true; // todo: tmp fix, remove later?
    var headers = request.headers;
    if (!('authorization' in headers)) {
        util.sendError('No access token');
        return false;
    }

    var authHeader = request.headers['authorization'];
    var accessToken = authHeader.substring(7);
    var token = tokenlib.getToken(accessToken);
    if (token == null) {
        response.writeHead(401, {'Content-Type': 'application/json'});
        response.write("{error:\'Invalid access token.\'}");
        response.end();
        return false;
    }

    if (token.isExpired()) {
        response.writeHead(401, {'Content-Type': 'application/json'});
        response.write("{error:\'Expired access token.\'}");
        response.end();
        return false;
    }

    return true;
}

function handleCode(request, response) {
    var url_parts = url.parse(request.url, true);
    var query = url_parts.query;

    var redirect_uri = query['redirect_uri'];
    redirect_uri = redirect_uri + '?code=12345';
    response.writeHead(302, {'Location': redirect_uri});
    response.end();
}

function handleToken(request, response) {
    util.postBody(request, function(body) {
        console.log('Request for OAuth token received.');

        var query = querystring.parse(body);
        if (!('client_id' in query))
            util.sendError(response, 'no client id!');
        if (!('client_secret') in query)
            util.sendError(response, 'no client secret!');
            
        var token = generateToken();
        tokenlib.addToken(token);

        console.log('Sending token: ' + token.toJSON());
        response.writeHead(200, {'Content-Type': 'application/json'});
        response.write(token.toJSON());
        response.end();
    });
}

// export functions
exports.validateToken = validateToken;
exports.handleCode = handleCode;
exports.handleToken = handleToken;
