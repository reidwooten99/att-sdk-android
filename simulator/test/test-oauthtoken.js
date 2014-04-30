var tokenlib = require("../lib/oauth_token.js");

exports['constructor'] = function(test) {
    var token = new tokenlib.OAuthToken('1234', -1, '134', [ 'DC' ]);
    test.equal(token.accessToken, '1234');
    test.equal(token.refreshToken, '134');
    test.done();
}

exports['expiresIn'] = function(test) {
    var token = new tokenlib.OAuthToken('1234', -1, '1234', [ 'DC' ]);
    test.equal(token.isExpired(), true);

    var token = new tokenlib.OAuthToken('1234', 10000, '1234', [ 'DC' ]);
    test.equal(token.isExpired(), false);

    test.done();
}

exports['toJSON'] = function(test) {
    var token = new tokenlib.OAuthToken('1234', -1, '1234', [ 'DC' ]);

    test.equal(token.toJSON(),
            '{"access_token":"1234","expires_in":-1,"refresh_token":"1234"}');

    test.done();
}

exports['getToken'] = function(test) {
    var token = new tokenlib.OAuthToken('1234', -1, '1234', [ 'DC' ]);
    tokenlib.addToken(token);
    test.equal(tokenlib.getToken('1234').isExpired(), true);

    token = new tokenlib.OAuthToken('2234', 10000, '1234', [ 'DC' ]);
    tokenlib.addToken(token);
    test.equal(tokenlib.getToken('2234').isExpired(), false);

    token = new tokenlib.OAuthToken('5234', 10000, '1234', [ 'DC' ]);
    test.equal(tokenlib.getToken('5234'), null);

    test.done();
}
