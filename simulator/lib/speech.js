var oauth = require('./oauth');
var cfg = require('../config');
var util = require('./util');
var fs = require('fs');

exports.handleSpeechRequest = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body = {
        "Recognition": {
            "NBest": [
                {
                    "Confidence": 0.18,
                    "Grade": "accept",
                    "Hypothesis": "text David california",
                    "LanguageId": "en-US",
                    "NluHypothesis": {
                        "OutComposite": [
                            {
                                "Grammar": "prefix",
                                "Out": "text David"
                            }, {
                                "Grammar": "generic",
                                "Out": "california"
                            }
                        ]
                    },
                    "ResultText": "text David california",
                    "WordScores": [0.07,0.109,0.699],
                    "Words": ["text","David","california"]
                }],
            "ResponseId": "754af22b24f3715965494907ca591b0e",
            "Status": "OK"
        }
    };  
    util.sendJSONReply(response, body);
}

exports.handleTtsRequest = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var fpath = cfg.ttsAudioFile;

    //spawn a callback to read file and return it
    fs.stat(fpath, function(err, stats){
        var len = stats.size;
        var buffer = new Buffer(len);

        var file = fs.openSync(fpath, 'r');

        var binary = fs.readSync(file, buffer, 0, len);

        util.sendBufferedReply(response, buffer);
    });
}

exports.handleCustomSpeechRequest = function(request, response) {
    if (!oauth.validateToken(request, response)) {
        return;
    }

    var body = {
        "Recognition": {
            "NBest": [
                {
                    "Confidence": 0.18,
                    "Grade": "accept",
                    "Hypothesis": "text David california",
                    "LanguageId": "en-US",
                    "NluHypothesis": {
                        "OutComposite": [
                            {
                                "Grammar": "prefix",
                                "Out": "text David"
                            }, {
                                "Grammar": "generic",
                                "Out": "california"
                            }
                        ]
                    },
                    "ResultText": "text David california",
                    "WordScores": [0.07,0.109,0.699],
                    "Words": ["text","David","california"]
                }],
            "ResponseId": "754af22b24f3715965494907ca591b0e",
            "Status": "OK"
        }
    };  
    util.sendJSONReply(response, body);
}
