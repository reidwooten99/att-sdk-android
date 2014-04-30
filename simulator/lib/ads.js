const clickURL = 'http://ads.advertising.bf.sl.attcompute.com/0/redir/54f98832-be65-11e2-a70a-a0369f1675cd/0/418135';
const image = 'http://http://localhost/ads/app1/images/att.gif';

var util = require('./util');
var oauth = require('./oauth');
var url = require('url');

//if (!('Content-Type'in request.headers) 
//        && request.headers['Content-Type'] != 'application/json') {
//
//    util.sendError(response, 'Invalid content type.');
//    return;
//}
//
function validateInput(request, response) {
    var headers = request.headers;
    if (!('user-agent' in headers)) {
        util.sendError(response, "No user agent!");
        return false;
    } else if (!('udid' in headers)) {
        util.sendError(response, "No UDID!");
        return false;
    }

    var url_parts = url.parse(request.url, true);
    var query = url_parts.query;

    
    console.log(query);
    if (!('Category' in query)) {
        util.sendError(response, "No Category");
        return false;
    }

    var param = 'AreaCode';

    if (!(param in query)) {
        return true;
    }

    var areaCodeRegex = /[1-9]+/;
    if (param.match(areaCodeRegex) == null) {
        var err = { 
            "RequestError": { 
                "ServiceException": { 
                    "MessageId": "SVC0003", 
                    "Text": "Invalid input value for message part %1, valid values are %2",
                    "Variables": "AreaCode , Integer" 
                } 
            }
        }
        util.sendError(response, err);
        return false;
    }

    return true;
}

exports.handleGetAd = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    if (!validateInput(request, response)) {
        return;
    }
    
    // we only return a single ad :)
    var rbody;
    var rand = Math.random();
    if (rand >= 0.5) {
        rbody =  {
            "AdsResponse": {
                "Ads": {
                    "Type": "thirdparty",
                    "ClickUrl": "http://ads.advertising.bf.sl.attcompute.com/1/redir/6dea9ca2-13fa-11e2-be80-001b21ccdb21/0/221707",
                    "TrackUrl": "http://bos-tapreq25.jumptap.com/a30/r/bos-tapreq25/1349997708/11468989/L",
                    "Text": "",
                    "Content":'<a href="http://ads.advertising.bf.sl.attcompute.com/1/redir/6dea9ca2-13fa-11e2-be80-001b21ccdb21/0/221707"><img src="http://i.jumptap.com/img/8749/1345061232746.jpg" alt="" width="320px" height="50px" /></a>\n<img src="http://bos-tapreq25.jumptap.com/a30/r/bos-tapreq25/1349997708/11468989/L" alt="" width="1px" height="1px" />'
                }
            }
        };
    } else {
        rbody = {
            "AdsResponse": {
                "Ads": {
                    "Type": "thirdparty",
                    "ClickUrl": "",
                    "Text": "",
                    "Content": '<a href="http://ads.advertising.bf.sl.attcompute.com/3/redir/10059819-13fa-11e2-8b3d-001b21ccdb19/0/221712">Test</a><br />'
                }
            }
        };
    }

    util.sendJSONReply(response, rbody);
}
