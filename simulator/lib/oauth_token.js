var util = require('./util');

/** Array of valid tokens. */
var validTokens = [];

/**
 * Adds token to a list of valid tokens.
 *
 * @param {OAuthToken} token token to add
 */
function addToken(token) {
    validTokens.push(token);
}

/**
 * Returns an OAuthToken object that contains the specified access string.
 *
 * @param {string} accessToken access token
 * @return {OAuthToken} OAuth token or null if none
 */
function getToken(accessToken) {
    for (var i = 0; i < validTokens.length; ++i) {
        if (accessToken == validTokens[i].accessToken) {
            return validTokens[i];
        }
    }

    return null;
}

function OAuthToken(accessToken, expiresIn, refreshToken, scope) {
    this.accessToken = accessToken;
    this.expiresIn = expiresIn;
    this.expiry = util.xtimestamp() + expiresIn;
    this.refreshToken = refreshToken;
    this.scope = scope;

    this.isExpired = function() {
        return util.xtimestamp() >= this.expiry;
    }

    this.toJSON = function() {
        var token = {
            'access_token': this.accessToken,
            'expires_in': this.expiresIn,
            'refresh_token': this.refreshToken
        };

        return JSON.stringify(token);
    }
}

// export functions
exports.addToken = addToken;
exports.getToken = getToken;
exports.OAuthToken = OAuthToken;
