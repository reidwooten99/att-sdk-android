var util = require('./util');
var url = require('url');
var oauth = require('./oauth');
var cfg = require('../config');

var docmap = new Array();

exports.handleNotary = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    util.postBody(request, function(body) {
        var jbody = JSON.parse(body);
        var redirect = jbody.MerchantPaymentRedirectUrl;

        var doc = util.hrandomstr(32);
        var sig = util.hrandomstr(32);

        docmap[doc] = redirect;

        var body = {
            'SignedDocument': doc,
            'Signature': sig
        };

        jbody = JSON.stringify(body);
        console.log('Sending Notary: ' + jbody);
        response.writeHead(200, {'Content-Type': 'application/json'});
        response.write(jbody);
        response.end();
    });
};

exports.handleNewTransaction = function(request, response) {
    console.log('Handling new transaction...');

    var url_parts = url.parse(request.url, true);
    var query = url_parts.query;
    var doc = query['SignedPaymentDetail'];

    redirect = docmap[doc] + '?TransactionAuthCode=12345';
    console.log('Redirecting to ' + redirect);
    response.writeHead(302, {'Location': redirect});
    response.end();
};

exports.handleNewSubscription = function(request, response) {
    console.log('Handling new subscription...');

    var url_parts = url.parse(request.url, true);
    var query = url_parts.query;
    var doc = query['SignedPaymentDetail'];
    redirect = docmap[doc] + '?SubscriptionAuthCode=12345';
    console.log('Redirecting to ' + redirect);
    response.writeHead(302, {'Location': redirect});
    response.end();
};

exports.handleGetTransaction = function(request, response) {
    var body = {
        "Channel":"MOBILE_WEB",
        "Description":"T20120104223242088",
        "Currency":"USD",
        "TransactionType":"SINGLEPAY",
        "TransactionStatus":"SUCCESSFUL",
        "ConsumerId":"7569ad74-e2e1-4c1e-9f49-455cdccfa315",
        "MerchantTransactionId":"T20120104223242088",
        "MerchantApplicationId":"79b33cf0ddf375044d6b6dada43f7d10",
        "TransactionId":"3013735686002133",
        "ContentCategory":"1",
        "MerchantProductId":"P20120104223242088",
        "MerchantIdentifier":"12345678-1234-1234-1234-1234567890abcdef",
        "Amount":"1.42",
        "Version":"1",
        "IsSuccess":"true"
    };

    jbody = JSON.stringify(body);
    console.log('Sending Notary: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
};

exports.handleGetSubscription = function(request, response) {
    var body = {
        "Version": "1",
        "IsSuccess": "true",
        "Amount": "1.35",
        "Channel": "MOBILE_WEB",
        "Description": "RECUR",
        "Currency": "USD",
        "SubscriptionType": "SUBSCRIPTION",
        "SubscriptionStatus": "SUCCESSFUL",
        "ConsumerId": "08660eb2-c9c4-48a6-93ea-1c440fa826e4",
        "MerchantTransactionId": "T20120619152559466",
        "MerchantApplicationId": "029c091549fd96788537c5c5cbbb94a3",
        "SubscriptionId": "6108486931402157",
        "OriginalTransactionId": "",
        "ContentCategory": "1",
        "MerchantProductId": "P20120619152559466",
        "MerchantId": "12345678-1234-1234-1234-1234567890abcdef",
        "MerchantSubscriptionId": "619152559466",
        "SubscriptionPeriodAmount": "1",
        "SubscriptionRecurrences": "99999",
        "SubscriptionPeriod": "MONTHLY",
        "IsAutoCommitted": "false"
    };

    jbody = JSON.stringify(body);
    console.log('Sending Notary: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
};

exports.handleGetSubscriptionDetails = function(request, response) {
    var body = {
        "Currency":"USD",
        "Status":"ACTIVE",
        "CreationDate":"2011-06-13T16:11:16.000+0000",
        "GrossAmount":0.05,
        "Recurrences":99999,
        "IsActiveSubscription":true,
        "CurrentStartDate":"2011-06-13T16:11:16.000+0000",
        "CurrentEndDate":"2011-07-13T16:11:16.000+0000",
        "RecurrencesLeft":2147483647,
        "Version":"1",
        "IsSuccess":true
    };
    jbody = JSON.stringify(body);
    console.log('Sending details: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
};

exports.handleRefundTransaction = function(request, response) {
    var body = {
        "IsSuccess": true,
        "Version": "1",
        "TransactionId": "MCKMCK6999352834302185",
        "TransactionStatus": "SUCCESSFUL",
        "OriginalPurchaseAmount": "0.01",
        "CommitConfirmationId": ""
    };

    jbody = JSON.stringify(body);
    console.log('Sending Refund transaction: ' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
};

exports.handleGenericTransaction = function(request, response) {
    if (request.method == 'PUT') {
        exports.handleRefundTransaction(request, response);
    }
};

exports.handleGetNotification = function(request, response) {
    var body = {
	    "GetNotificationResponse": {
		    "NotificationType": "SuccesfulRefund",
		    "TransactionDate": "2012-10-16T00:03:54.167Z",
		    "OriginalTransactionId": "qICS6NsAH61ID8IpUorPhG4jXCeMYLG5KAZ5",
		    "ConsumerId": "30eadde4-f26b-4f9b-92eb-6fb9172e86d3",
		    "RefundPriceAmount": "1.15",
		    "RefundCurrency": "USD",
		    "RefundReasonId": "1",
		    "RefundFreeText": "Customer was not happy",
		    "RefundTransactionId": "qICS6NsARsQzYpmqb0XkEJWRkd85TFsj5XwW",
		    "MerchantSubscriptionId": "015170205160",
		    "OriginalPriceAmount": "1.15",
		    "CurrentPeriodStartDate": "2012-10-16T00:02:45Z",
		    "CurrentPeriodEndDate": "2012-11-16T00:02:44.999Z",      
		    "SubscriptionPeriodAmount": "",
		    "SubscriptionPeriod": "",
		    "SubscriptionRecurrences": "",
		    "SubscriptionRemaining": ""
	    },
	    "Version": "1",
	    "IsSuccess": true
    };

    jbody = JSON.stringify(body);
    console.log('Sending notification info' + jbody);
    response.writeHead(200, {'Content-Type': 'application/json'});
    response.write(jbody);
    response.end();
}
