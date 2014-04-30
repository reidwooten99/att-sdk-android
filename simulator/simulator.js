/*
 * Author: Pavel Kazakov
 * Author: Kyle Hill
 */

var program = require('commander');

var http = require('http');
var url = require('url');
var oauth = require('./lib/oauth');
var ads = require('./lib/ads');
var address_book = require('./lib/address_book');
var cms = require('./lib/cms');
var dc = require('./lib/dc');
var immn = require('./lib/immn');
var immn2 = require('./lib/v2/immn');
var mms = require('./lib/mms');
var payment = require('./lib/payment');
var sms = require('./lib/sms');
var tl = require('./lib/tl');
var speech = require('./lib/speech');
var util = require('./lib/util');

program.version('0.0.1').option('-d, --debug', 'Enable output of requests');

program.parse(process.argv);

const URL_MAPPING = {
    '/oauth/token': oauth.handleToken,
    '/oauth/authorize': oauth.handleCode,

    '/rest/1/ads': ads.handleGetAd,

    '/rest/2/Devices/Info': dc.handleDeviceCapabilities,

    '/rest/1/Sessions/': cms.handleCMSSendSignal,
    '/rest/1/Sessions': cms.handleCMSCreateSession,

    '/rest/1/MyMessages/': immn.handleIMMNMessageContent,
    '/rest/1/MyMessages': immn.handleIMMNRequest,

    '/mms/v3/messaging/outbox/': mms.handleMMSDeliveryStatus,
    '/mms/v3/messaging/outbox': mms.handleSendMMS,

    '/sms/v3/messaging/inbox/*':  sms.handleGetSMS,
    '/sms/v3/messaging/outbox/': sms.handleSMSDeliveryStatus,
    '/sms/v3/messaging/outbox': sms.handleSendSMS,

    '/speech/v3/speechToText': speech.handleSpeechRequest,
    '/speech/v3/textToSpeech': speech.handleTtsRequest,
    '/speech/v3/speechToTextCustom': speech.handleCustomSpeechRequest,

    // TODO: finish part id
    '^/myMessages/v2/notificationConnectionDetails/?$' : immn2.handleGetNotificationConnectionDetails,
    '^/myMessages/v2/messages/index/info/?$' : immn2.handleGetMessageIndexInfo,
    '^/myMessages/v2/messages/index/?$' : immn2.handleCreateMessageIndex,
    '^/myMessages/v2/delta/?$': immn2.handleGetMessagesDelta,
    '^/myMessages/v2/messages/[^/]*/parts/[0-9]/?$': immn2.handleGetMessageContent,
    '^/myMessages/v2/messages/[^/]*/?$': immn2.handleMessage,
    '^/myMessages/v2/messages/?$': immn2.handleMessages,

    '^/addressBook/v1/*' : address_book.handleAddressBook,

    '/Security/Notary/Rest/1/SignedPayload': payment.handleNotary,
    '/rest/3/Commerce/Payment/Notifications/*': payment.handleGetNotification,
    '/rest/3/Commerce/Payment/Transactions/TransactionAuthCode/12345': payment.handleGetTransaction,
    '/rest/3/Commerce/Payment/Transactions/MerchantTransactionId/T20120104223242088': payment.handleGetTransaction,
    '/rest/3/Commerce/Payment/Transactions/TransactionId/3013735686002133': payment.handleGetTransaction,
    '/rest/3/Commerce/Payment/Transactions/3013735686002133': payment.handleGenericTransaction,
    '/rest/3/Commerce/Payment/Subscriptions/SubscriptionAuthCode/12345': payment.handleGetSubscription,
    '/rest/3/Commerce/Payment/Subscriptions/SubscriptionId/6108486931402157': payment.handleGetSubscription,
    '/rest/3/Commerce/Payment/Subscriptions/MerchantTransactionId/T20120619152559466': payment.handleGetSubscription,
    '/rest/3/Commerce/Payment/Subscriptions/619152559466/Detail/08660eb2-c9c4-48a6-93ea-1c440fa826e4': 
        payment.handleGetSubscriptionDetails,
    '/rest/3/Commerce/Payment/Transactions/6108486931402157': payment.handleRefundTransaction,
    '/2/devices/location': tl.handleGetLocation,
    '/rest/3/Commerce/Payment/Transactions': payment.handleNewTransaction,
    '/rest/3/Commerce/Payment/Subscriptions': payment.handleNewSubscription
};

var handleRequest = function(request, response) {

    var pathname = url.parse(request.url).href;
    console.log('Received request for: ' + pathname);

    //log the headers and body
    if (program.debug) {
        var headers = request["headers"];
        util.postBody(request, function (body) {
            console.log("\nv============REQUEST============v");
            console.log("URL:");
            console.log(pathname);
            console.log("Headers:");
            console.log(headers);
            console.log("Body:\n" + body);
            console.log("^============REQUEST============^\n");
        });
    }

    for (var i in URL_MAPPING) {
        var reg = new RegExp(i);

        if (reg.exec(pathname)) {
            func = URL_MAPPING[i];
            func(request, response);
            return;
        }
    }

    response.writeHead(400, {'Content-Type': 'application/json'});
    response.write("{error:\'this is only a simulator, will robinson\'}");
    response.end();
}

console.log('Starting server...');
http.createServer(handleRequest).listen(8888);
console.log('done.');

/* vim: set expandtab ts=4 tw=4 sts=4: */
