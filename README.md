# api-home-security
API - Home Security 

[![Build Status](https://travis-ci.org/antoine-aumjaud/api-home-security.svg?branch=master)](https://travis-ci.org/antoine-aumjaud/api-home-security)


My Synology Surveillance Station send a hook every time a motion event is detected by Camera to this "SMS provider". 
This micro-service is not an SMS provider, but it is the only way I've found to send an event to an URL when a motion event is detected by Surveillance Station.

In this micro-service, I have a state to indicate if my security is activated or not. If it is the case, I do some stuff with my [Nabaztag](http://nabaztag.com) and send an alert to my mobile with Freemobile SMS API and in Synology Chat (by using [this micro-service](https://github.com/antoine-aumjaud/api-synology-chatbot/)). 

This state can be de/activated by my Nabaztag or by the Synlogy Chat.
