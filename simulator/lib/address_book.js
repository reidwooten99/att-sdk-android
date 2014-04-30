var oauth = require('./oauth');
var util = require('./util');
var url = require('url');

var JSON = require('JSON');

exports.handleAddressBook = function(request, response) {
    // address book mappings

    mappings = [
        [ '^/addressBook/v1/contacts$', 'POST', exports.handleCreateContact ],
        [ '^/addressBook/v1/contacts$', 'GET', exports.handleGetContacts ],
        [ '^/addressBook/v1/contacts/[^/]+$', 'GET', exports.handleGetContact ],
        [ '^/addressBook/v1/contacts/[^/]+$', 'PATCH', exports.handleUpdateContact ],
        [ '^/addressBook/v1/myInfo$', 'PATCH', exports.handleUpdateContact ],
        [ '^/addressBook/v1/contacts/[^/]+$', 'DELETE', exports.handleDeleteContact ],
        [ '^/addressBook/v1/contacts/[^/]+/groups$', 'GET', exports.handleGetContactGroups ],
        [ '^/addressBook/v1/groups$', 'POST', exports.handleCreateGroup ],
        [ '^/addressBook/v1/groups$', 'GET', exports.handleGetGroups ],
        [ '^/addressBook/v1/groups/[^/]+$', 'DELETE', exports.handleDeleteGroup ],
        [ '^/addressBook/v1/groups/[^/]+$', 'PATCH', exports.handleUpdateGroup ],
        [ '^/addressBook/v1/groups/[^/]+$', 'GET', exports.handleGetGroup ],
        [ '^/addressBook/v1/groups/[^/]+/contacts$', 'POST', exports.handleAddContactsToGroup ],
        [ '^/addressBook/v1/groups/[^/]+/contacts$', 'DELETE', exports.handleRemoveContactsFromGroup ],
        [ '^/addressBook/v1/groups/[^/]+/contacts$', 'GET', exports.handleGetGroupContacts ]
    ];

    var pathname = url.parse(request.url).pathname;
    for (var i in mappings) {
        var mapping = mappings[i];

        var urlRegex = mapping[0];
        var verb = mapping[1];
        var func = mapping[2];

        if (new RegExp(urlRegex).exec(pathname) && request.method == verb) {
            func(request, response)
        }
    }
}

exports.handleCreateContact = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var date = new Date().toUTCString();
    var hostname = request.headers.host;

    var headers = {
        'cache-control': 'no-cache',
        'date': date,
        'last-modified': date,
        'location': 'http://' + hostname + '/addressBook/v1/contacts/eb765-923',
    };

    response.writeHead(201, headers);
    response.end();

}

exports.handleGetContact = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }
    
    console.log (request);

    var body = {
        "quickContact": {
            "contactId": "09876544321",
            "formattedName": "GOOD BEST BOY",
            "firstName": "BEST",
            "middleName": "BOY",
            "lastName": "GOOD",
            "prefix": "SR.",
            "suffix": "II",
            "nickName": "TEST",
            "organization": "ATT",
            "phone": {
                "type": "HOME",
                "number": "2223335555"
            },
            "address": {
                "type": "WORK",
                "preferred": "TRUE",
                "poBox": "3456",
                "addressLine1": "345 140TH PL NE",
                "addressLine2": "APT 456",
                "city": "REDMOND",
                "state": "WA",
                "zip": "90000",
                "country": "USA"
            },
            "email": {
                "type": "WORK",
                "emailAddress": "XYZ@ABC.COM"
            },
            "im": {
                "type": "AIM",
                "imUri": "ABC"
            }
        }
    };  

    util.sendJSONReply(response, body);
  
}

exports.handleGetContacts = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body = {
        "resultSet": {
            "currentPageIndex": "2",
            "totalRecords": "2",
            "totalPages": "1",
            "previousPage": "0",
            "nextPage": "1",
            "quickContacts": {
                "quickContact": [
                {
                    "contactId": "09876544321",
                    "formattedName": "GOOD BEST BOY",
                    "firstName": "BEST",
                    "middleName": "BOY",
                    "lastName": "GOOD",
                    "prefix": "SR.",
                    "suffix": "II",
                    "nickName": "TEST",
                    "organization": "ATT",
                    "phone": {
                        "type": "HOME",
                        "number": "2223335555"
                    },
                    "address": {
                        "type": "WORK",
                        "preferred": "TRUE",
                        "poBox": "3456",
                        "addressLine1": "345 140TH PL NE",
                        "addressLine2": "APT 456",
                        "city": "REDMOND",
                        "state": "WA",
                        "zip": "90000",
                        "country": "USA"
                    },
                    "email": {
                        "type": "WORK",
                        "emailAddress": "XYZ@ABC.COM"
                    },
                    "im": {
                        "type": "AIM",
                        "imUri": "ABC"
                    }
                },
                {
                    "contactId": "0987654432123",
                    "formattedName": "GOODD BEST BOYY",
                    "firstName": "BEST",
                    "middleName": "BOYY",
                    "lastName": "GOODD",
                    "prefix": "SR.",
                    "suffix": "II",
                    "nickName": "TEST",
                    "organization": "ATT",
                    "phone": {
                        "type": "HOME",
                        "number": "1223335555"
                    },
                    "address": {
                        "type": "WORK",
                        "preferred": "TRUE",
                        "poBox": "3456",
                        "addressLine1": "345 140TH PL NE",
                        "addressLine2": "APT 456",
                        "city": "REDMOND",
                        "state": "WA",
                        "zip": "90000",
                        "country": "USA"
                    },
                    "email": {
                        "type": "WORK",
                        "emailAddress": "XYZ@ABC.COM"
                    },
                    "im": {
                        "type": "AIM",
                        "imUri": "ABC"
                    }
                }
                ]
            }
        }
    };  

    util.sendJSONReply(response, body);
}

exports.handleGetContactGroups = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body = {
        "resultSet": {
            "currentPageIndex": "2",
            "totalRecords": "2",
            "totalPages": "1",
            "previousPage": "0",
            "nextPage": "0",
            "groups": {
                "group": [
                {
                    "groupId": "12345",
                    "groupName": "COLLEGE",
                    "groupType": "USER"
                },
                {
                    "groupId": "12346",
                    "groupName": "SCHOOL",
                    "groupType": "USER"
                },
                {
                    "groupId": "12347",
                    "groupName": "OFFICE",
                    "groupType": "USER"
                },
                {
                    "groupId": "12348",
                    "groupName": "FAMILY",
                    "groupType": "USER"
                }
                ]
            }
        }
    };

    util.sendJSONReply(response, body);
}

exports.handleUpdateContact = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var date = new Date().toUTCString();

    var headers = {
        'cache-control': 'no-cache',
        'date': date,
        'last-modified': date,
    };

    response.writeHead(204, headers);
    response.end();
}

exports.handleDeleteContact = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    response.writeHead(204);
    response.end();
}

exports.handleCreateGroup = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var date = new Date().toUTCString();
    var hostname = request.headers.host;

    var headers = {
        'cache-control': 'no-cache',
        'date': date,
        'last-modified': date,
        'location': 'http://' + hostname + '/addressBook/v1/groups/09876541234',
    };

    response.writeHead(201, headers);
    response.end();
}

exports.handleGetGroups = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body = {
        "resultSet": {
            "currentPageIndex": "2",
            "totalRecords": "4",
            "totalPages": "2",
            "previousPage": "0",
            "nextPage": "0",
            "groups": {
                "group": [
                    {
                        "groupId": "12345",
                        "groupName": "COLLEGE",
                        "groupType": "USER"
                    },
                    {
                        "groupId": "12346",
                        "groupName": "SCHOOL",
                        "groupType": "USER"
                    },
                    {
                        "groupId": "12347",
                        "groupName": "OFFICE",
                        "groupType": "USER"
                    },
                    {
                        "groupId": "12348",
                        "groupName": "FAMILY",
                        "groupType": "USER"
                    }
                ]
            }
        }
    };

    util.sendJSONReply(response, body);
}

exports.handleGetGroup = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body = {
        "group": {
            "groupId": "12345",
            "groupName": "COLLEGE",
            "groupType": "USER"
        }
    };

    util.sendJSONReply(response, body);
}

exports.handleDeleteGroup = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var date = new Date().toUTCString();

    var headers = {
        'cache-control': 'no-cache',
        'content-encoding' : 'gzip,deflate',
        'date': date,
        'last-modified': date,
    };

    response.writeHead(204, headers);
    response.end();
}

exports.handleUpdateGroup = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var date = new Date().toUTCString();

    var headers = {
        'cache-control': 'no-cache',
        'date': date,
        'last-modified': date,
    };

    response.writeHead(204, headers);
    response.end();
}

exports.handleAddContactsToGroup = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }
    var date = new Date().toUTCString();

    var headers = {
        'cache-control': 'no-cache',
        'date': date,
        'last-modified': date,
    };

    response.writeHead(204, headers);
    response.end();
}

exports.handleRemoveContactsFromGroup = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }
    var date = new Date().toUTCString();

    var headers = {
        'cache-control': 'no-cache',
        'date': date,
        'last-modified': date,
    };

    response.writeHead(204, headers);
    response.end();
}

exports.handleGetGroupContacts = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body = {
        "contactIds": {
            "id": [ 
                "12344798987987"
                , "09890687576634"
                , "76868767189900"
                , "45545652576668"
                ]
        }
    }

    util.sendJSONReply(response, body);
}

/* vim: set expandtab ts=4 tw=4 sts=4: */
