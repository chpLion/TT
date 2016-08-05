/**
 * 
 * Mobile Rich-media Ad Interface Definitions (MRAID) v.2.0
 * 
 * The Interactive Advertising Bureau("IAB"), its members and other significant contributors 
 * joined together to create this document, a standard interface specification for mobile rich 
 * media ads. The goal of the Mobile Rich-media Ad Interface Definition (MRAID) project is to 
 * address known interoperability issues between publisher mobile applications, different ad 
 * servers and different rich media platforms.
 * 
 */
(function() {
    var mraid = window.mraid = {};

    // CONSTANTS ///////////////////////////////////////////////////////////////
    var VERSIONS = mraid.VERSIONS = {
        V1: '1.0',
        V2: '2.0'
    };

    var PLACEMENTS = mraid.PLACEMENTS = {
        UNKNOWN: 'unknown',
        INLINE: 'inline',
        INTERSTITIAL: 'interstitial'
    };

    var ORIENTATIONS = mraid.ORIENTATIONS = {
        NONE: 'none',
        PORTRAIT: 'portrait',
        LANDSCAPE: 'landscape'
    };

    var CLOSEPOSITIONS = mraid.CLOSEPOSITIONS = {
        TOPLEFT: 'top-left',
        TOPRIGHT: 'top-right',
        BOTTOMLEFT: 'bottom-left',
        BOTTOMRIGHT: 'bottom-right',
        CENTER: 'center'
    };

    var STATES = mraid.STATES = {
        UNKNOWN: 'unknown',

        LOADING: 'loading',
        DEFAULT: 'default',
        RESIZED: 'resized',
        EXPANDED: 'expanded',
        HIDDEN: 'hidden'
    };

    var EVENTS = mraid.EVENTS = {
        INFO: 'info',
        ORIENTATIONCHANGE: 'orientationChange',

        READY: 'ready',
        ERROR: 'error',
        STATECHANGE: 'stateChange',
        VIEWABLECHANGE: 'viewableChange',
        CALENDAREVENTADDED: 'calendarEventAdded',
        PICTUREADDED: 'pictureAdded',
        SIZECHANGE: 'sizeChange',
    };

    var FEATURES = mraid.FEATURES = {
        SMS: 'sms',
        PHONE: 'phone',
        EMAIL: 'email',
        CALENDAR: 'calendar',
        STOREPICTURE: 'storePicture',
        INLINEVIDEO: 'inlineVideo'
    };

    // PRIVATE PROPERTIES (sdk controlled) //////////////////////////////////////////////////////
    var state = STATES.UNKNOWN;

    var placementType = PLACEMENTS.UNKNOWN;

    var size = {
        width: 0,
        height: 0
    };

    var defaultPosition = {
        x: 0,
        y: 0,
        width: 0,
        height: 0
    };

    var maxSize = {
        width: 0,
        height: 0
    };

    var expandProperties = {
        width: 0,
        height: 0,
        useCustomClose: false,
        isModal: true,
        allowOrientationChange: true,
        forceOrientation: ORIENTATIONS.NONE
    };

    var resizeProperties = {
        width: 0,
        height: 0,
        customClosePosition: CLOSEPOSITIONS.TOPRIGHT,
        offsetX: 0,
        offsetY: 0,
        allowOffscreen: true
    };

    var supports = {
        'sms': true,
        'phone': true,
        'email': true,
        'calendar': true,
        'storePicture': true,
        'inlineVideo': true,
        'orientation': true
    };

    var orientation = -1;
    var mraidVersion = VERSIONS.UNKNOWN;
    var screenSize = null;

    // PRIVATE PROPERTIES (internal) //////////////////////////////////////////////////////
    var intervalID = null;

    //@TODO: don't think I need dimension validators anymore
    var dimensionValidators = {
        x: function(value) {
            return ! isNaN(value);
        },
        y: function(value) {
            return ! isNaN(value);
        },
        width: function(value) {
            return ! isNaN(value) && value >= 0;
        },
        height: function(value) {
            return ! isNaN(value) && value >= 0;
        }
    };

    //@TODO: ok to allow ads that are larger than maxSize
    var sizeValidators = {
        width: function(value) {
            return ! isNaN(value) && value >= 0 && value <= maxSize.width;
        },
        height: function(value) {
            return ! isNaN(value) && value >= 0 && value <= maxSize.height;
        }
    };

    var expandPropertyValidators = {
        isModal: function(value) {
            return (value === true);
        },
        useCustomClose: function(value) {
            return (value === true || value === false);
        },
        width: function(value) {
            return ! isNaN(value) && value >= 0;
        },
        height: function(value) {
            return ! isNaN(value) && value >= 0;
        },
        allowOrientationChange: function(value) {
            return (value === true || value === false);
        },
        forceOrientation: function(value) {
            return (value in ORIENTATIONS);
        } //@TODO
    };

    var resizePropertyValidators = {
        width: function(value) {
            return ! isNaN(value) && value >= 0;
        },
        height: function(value) {
            return ! isNaN(value) && value >= 0;
        },
        offsetX: function(value) {
            return ! isNaN(value) && value >= 0;
        },
        offsetY: function(value) {
            return ! isNaN(value) && value >= 0;
        },
        allowOffscreen: function(value) {
            return (value === true || value === false);
        },
        customClosePosition: function(value) {
            return (value in CLOSEPOSITIONS);
        } //@TODO
    }

    var changeHandlers = {
        version: function(val) {
            mraidVersion = val;
        },
        placement: function(val) {
            placementType = val;
        },
        state: function(val) {
            console.log('state listener. state=' + state + ':new=' + val);
            if (state == STATES.UNKNOWN && val != STATES.UNKNOWN) {
                broadcastEvent(EVENTS.INFO, 'controller initialized');
            }
            if (state == STATES.LOADING && val != STATES.LOADING) {
                mraid.signalReady();
            } else {
                broadcastEvent(EVENTS.INFO, 'setting state to ' + stringify(val));
                state = val;
                broadcastEvent(EVENTS.STATECHANGE, state);
            }
        },
        size: function(val) {
            broadcastEvent(EVENTS.INFO, 'setting size to ' + stringify(val));
            size = val;
            broadcastEvent(EVENTS.SIZECHANGE, size.width, size.height);
        },
        defaultPosition: function(val) {
            broadcastEvent(EVENTS.INFO, 'setting default position to ' + stringify(val));
            defaultPosition = val;
        },
        maxSize: function(val) {
            broadcastEvent(EVENTS.INFO, 'setting maxSize to ' + stringify(val));
            maxSize = val;
        },
        expandProperties: function(val) {
            broadcastEvent(EVENTS.INFO, 'merging expandProperties with ' + stringify(val));
            for (var i in val) {
                expandProperties[i] = val[i];
            }
        },
        supports: function(val) {
            broadcastEvent(EVENTS.INFO, 'setting supports to ' + stringify(val));
            supports = {};
            for (var key in FEATURES) {
                supports[FEATURES[key]] = contains(FEATURES[key], val);
            }
        },
        orientation: function(val) {
            broadcastEvent(EVENTS.INFO, 'setting orientation to ' + stringify(val));
            orientation = val;
            broadcastEvent(EVENTS.ORIENTATIONCHANGE, orientation);
        },
        screenSize: function(val) {
            broadcastEvent(EVENTS.INFO, 'setting screenSize to ' + stringify(val));
            screenSize = val;
            broadcastEvent(EVENTS.SCREENCHANGE, screenSize.width, screenSize.height);
        }
    };

    var listeners = {};

    var EventListeners = function(event) {
        this.event = event;
        this.count = 0;
        var listeners = {};

        this.add = function(func) {
            var id = String(func);
            if (!listeners[id]) {
                listeners[id] = func;
                this.count++;
                if (this.count == 1) {
                    broadcastEvent(EVENTS.INFO, 'activating ' + event);
                    mraidview.activate(event);
                }
            }
        };
        this.remove = function(func) {
            var id = String(func);
            if (listeners[id]) {
                listeners[id] = null;
                delete listeners[id];
                this.count--;
                if (this.count == 0) {
                    broadcastEvent(EVENTS.INFO, 'deactivating ' + event);
                    mraidview.deactivate(event);
                }
                return true;
            } else {
                return false;
            }
        };
        this.removeAll = function() {
            for (var id in listeners) this.remove(listeners[id]);
        };
        this.broadcast = function(args) {
            for (var id in listeners) listeners[id].apply({},
            args);
        };
        this.toString = function() {
            var out = [event, ':'];
            for (var id in listeners) out.push('|', id, '|');
            return out.join('');
        };
    };

    // PRIVATE METHODS ////////////////////////////////////////////////////////////
    mraidview.addEventListener('change',
    function(properties) {
        for (var property in properties) {
            var handler = changeHandlers[property];
            handler(properties[property]);
        }
    });

    mraidview.addEventListener('error',
    function(message, action) {
        broadcastEvent(EVENTS.ERROR, message, action);
    });

    var clone = function(obj) {
        var f = function() {};
        f.prototype = obj;
        return new f();
    };

    var stringify = function(obj) {
        if (typeof obj == 'object') {
            if (obj.push) {
                var out = [];
                for (var p = 0; p < obj.length; p++) {
                    out.push(obj[p]);
                }
                return '[' + out.join(',') + ']';
            } else {
                var out = [];
                for (var p in obj) {
                    out.push('\'' + p + '\':' + obj[p]);
                }
                return '{' + out.join(',') + '}';
            }
        } else {
            return String(obj);
        }
    };

    var valid = function(obj, validators, action, full) {
        if (full) {
            if (obj === undefined) {
                broadcastEvent(EVENTS.ERROR, 'Required object missing.', action);
                return false;
            } else {
                for (var i in validators) {
                    if (obj[i] === undefined) {
                        broadcastEvent(EVENTS.ERROR, 'Object missing required property ' + i, action);
                        return false;
                    }
                }
            }
        }
        for (var i in obj) {
            if (!validators[i]) {
                broadcastEvent(EVENTS.ERROR, 'Invalid property specified - ' + i + '.', action);
                return false;
            } else if (!validators[i](obj[i])) {
                broadcastEvent(EVENTS.ERROR, 'Value of property ' + i + ' is not valid type.', action);
                return false;
            }
        }
        return true;
    };

    var contains = function(value, array) {
        for (var i in array) if (array[i] == value) return true;
        return false;
    };

    var broadcastEvent = function() {
        var args = new Array(arguments.length);
        for (var i = 0; i < arguments.length; i++) args[i] = arguments[i];
        var event = args.shift();
        if (listeners[event]) listeners[event].broadcast(args);
    }

    // PUBLIC METHODS ////////////////////////////////////////////////////////////////////
    mraid.signalReady = function() {
        broadcastEvent(EVENTS.INFO, 'READY SIGNAL, setting state to ' + stringify(STATES.DEFAULT));
        state = STATES.DEFAULT;
        broadcastEvent(EVENTS.STATECHANGE, state);
        broadcastEvent(EVENTS.INFO, 'ready eventListener triggered');
        broadcastEvent(EVENTS.READY, 'ready event fired');
        window.clearInterval(intervalID);
    };

    mraid.getVersion = function() {
        return (mraidVersion);
    };

    mraid.info = function(message) {
        broadcastEvent(EVENTS.INFO, message);
    };

    mraid.error = function(message) {
        broadcastEvent(EVENTS.ERROR, message);
    };

    mraid.addEventListener = function(event, listener) {
        if (!event || !listener) {
            broadcastEvent(EVENTS.ERROR, 'Both event and listener are required.', 'addEventListener');
        } else if (!contains(event, EVENTS)) {
            broadcastEvent(EVENTS.ERROR, 'Unknown event: ' + event, 'addEventListener');
        } else {
            if (!listeners[event]) listeners[event] = new EventListeners(event);
            listeners[event].add(listener);
        }
    };

    mraid.removeEventListener = function(event, listener) {
        if (!event) {
            broadcastEvent(EVENTS.ERROR, 'Must specify an event.', 'removeEventListener');
        } else {
            if (listener && (!listeners[event] || !listeners[event].remove(listener))) {
                broadcastEvent(EVENTS.ERROR, 'Listener not currently registered for event', 'removeEventListener');
                return;
            } else {
                listeners[event].removeAll();
            }
            if (listeners[event].count == 0) {
                listeners[event] = null;
                delete listeners[event];
            }
        }
    };

    mraid.getState = function() {
        return state;
    };

    mraid.getPlacementType = function() {
        return placementType;
    };

    /* @TODO: simulate load off screen and change isViewable */
    mraid.isViewable = function() {
        return true;
    };

    mraid.open = function(URL) {
        if (!URL) {
            broadcastEvent(EVENTS.ERROR, 'URL is required.', 'open');
        } else {
            mraidview.open(URL);
        }
    };

    mraid.expand = function(dimensions, URL) {
        if (dimensions === undefined) {
            dimensions = {
                width: mraid.getMaxSize().width,
                height: mraid.getMaxSize().height,
                x: 0,
                y: 0
            };
        }
        broadcastEvent(EVENTS.INFO, 'expanding to ' + stringify(dimensions));
        if (valid(dimensions, dimensionValidators, 'expand', true)) {
            mraidview.expand(dimensions, URL);
        }
    };

    mraid.getExpandProperties = function() {
        return clone(expandProperties);
    };

    mraid.setExpandProperties = function(properties) {
        if (valid(properties, expandPropertyValidators, 'setExpandProperties')) {
            mraidview.setExpandProperties(properties);
        }
    };

    mraid.close = function() {
        mraidview.close();
    };

    mraid.useCustomClose = function() {
        //@TODO
    }

    mraid.resize = function(width, height) {
        if (width == null || height == null || isNaN(width) || isNaN(height) || width < 0 || height < 0) {
            broadcastEvent(EVENTS.ERROR, 'Requested size must be numeric values between 0 and maxSize.', 'resize');
        } else if (width > maxSize.width || height > maxSize.height) {
            broadcastEvent(EVENTS.ERROR, 'Request (' + width + ' x ' + height + ') exceeds maximum allowable size of (' + maxSize.width + ' x ' + maxSize.height + ')', 'resize');
        } else if (width == size.width && height == size.height) {
            broadcastEvent(EVENTS.ERROR, 'Requested size equals current size.', 'resize');
        } else {
            mraidview.resize(width, height);
        }
    };

    mraid.getResizeProperties = function() {
        return clone(resizeProperties);
    };

    mraid.setResizeProperties = function(properties) {
        if (valid(properties, resizePropertyValidators, 'setResizeProperties')) {
            mraidview.setResizeProperties(properties);
        }
    };

    mraid.getCurrentPosition = function() {
        return clone(currentPosition); //@TODO
    };

    mraid.getMaxSize = function() {
        return clone(maxSize);
    };

    mraid.getDefaultPosition = function() {
        return clone(defaultPosition);
    };

    mraid.getScreenSize = function() {
        return clone(screenSize);
    };

    mraid.supports = function(feature) {
        if (supports[feature]) {
            return true;
        } else {
            return false;
        }
    };

    mraid.storePicture = function(url) {
        if (!supports[FEATURES.STOREPICTURE]) {
            broadcastEvent(EVENTS.ERROR, 'Method not supported by this client.', 'storePicture');
            broadcastEvent(EVENTS.PICTUREADDED, false);
        } else if (!url || typeof url !== 'string') {
            broadcastEvent(EVENTS.ERROR, 'Valid url required.', 'storePicture');
            broadcastEvent(EVENTS.PICTUREADDED, false);
        } else {
            mraidview.storePicture(url);
        }
    };

    mraid.createCalendarEvent = function(params) {
        if (!supports[FEATURES.CALENDAR]) {
            broadcastEvent(EVENTS.ERROR, 'Method not supported by this client.', 'createCalendarEvent');
            broadcastEvent(EVENTS.CALENDAREVENTADDED, false);
        } else if (!params || typeof date != 'object') {
            broadcastEvent(EVENTS.ERROR, 'Valid params required.', 'createCalendarEvent');
            broadcastEvent(EVENTS.CALENDAREVENTADDED, false);
        } else {
            mraidview.createCalendarEvent(params);
        }
    };

    mraid.playVideo = function(url) {
        if (supports[FEATURES.INLINEVIDEO]) {
            broadcastEvent(EVENTS.INFO, 'Inline video is available. playVideo will use native player.');
        }
        if (!url || typeof url != 'string') {
            broadcastEvent(EVENTS.ERROR, 'Valid url required.', 'playVideo');
        } else {
            mraidview.playVideo(url);
        }
    };

    mraid.getOrientation = function() {
        if (!supports[FEATURES.ORIENTATION]) {
            broadcastEvent(EVENTS.ERROR, 'Method not supported by this client.', 'getOrientation');
        }
        return orientation;
    };

})();
