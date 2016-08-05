/**
 * 
 * @author shsun
 * 
 */
(function() {



    var mraidview = window.mraidview = {};

    var expandProperties = {
    	width:-1,
		height:-1,
        useBackground:false,
        backgroundColor:'#ffffff',
        backgroundOpacity:'1.0',
        lockOrientation:false,
		useCustomClose : false,
    	isModal:true
    };
    
	var orientationProperties = {
		/* If set to 'true' then the container will permit device-based orientation changes; if set to false, then the container will ignore device-based orientation changes (e.g., the web view will not change even if the orientation of the device changes). Default is 'true.' The ad creative is always able to request a change of its orientation by setting the forceOrientation variable, regardless of how allowOrientationChange is set. */
		allowOrientationChange : true,
		/* can be set to a value of 'portrait', 'landscape or 'none' If forceOrientation is set then a view must open in the specified orientation, regardless of the orientation of the device. That is, if a user is viewing an ad in landscape mode, and taps to expand it, if the ad designer has set the forceOrientation orientation property to 'portrait' then the ad will open in portrait orientation. Default is 'none.' */
		forceOrientation : 'none'
    };
    
    var resizeProperties = {
    	/* (required) integer ¨C width of creative in pixels */
    	width : 0, 
    	/* height : (required) integer ¨C height of creative in pixels */
    	height : 0,
    	/* offsetX: (required) is the horizontal delta from the banner's upper left-hand corner where the upper left-hand corner of the expanded region should be placed; positive integers for expand right; negative for left */
		offsetX : 0, 
		/* offsetY: (required) is the vertical delta from the banner's upper left-hand corner where the upper left-hand corner of the expanded region should be placed; positive integers for expand down; negative for up */
		offsetY : 0, 
		/* customClosePosition: (optional) string ¨C either top-left, top-right, center, bottom-left, bottom-right, top-center, or bottom-center indicates the origin of the container-supplied close event region relative to the resized creative. If not specified or not one of these options, will default to top-right */
		customClosePosition : 'top-right', 
		/* 
		 * allowOffscreen: (optional) tells the container whether or not it should allow the resized creative to be drawn fully/partially offscreen
		 * 
		 * True (default): the container should not attempt to position the resized creative
		 * False: the container should try to reposition the resized creative to always fit in the getMaxSize() area 
		 */
		allowOffscreen : true
	};

    /** The set of listeners for ORMMA Native Bridge Events */
    var listeners = {};

    /** Holds the current dimension values */
    dimensions: {};

    /** A Queue of Calls to the Native SDK that still need execution */
    var nativeCallQueue = [];

    /** Identifies if a native call is currently in progress */
    var nativeCallInFlight = false;

    /** timer for identifying iframes */
    var timer;
    var totalTime;

    /**********************************************/
    /************* JAVA ENTRY POINTS **************/
    /**********************************************/

    /**
    * Called by the JAVA SDK when an asset has been fully cached.
    *
    * @returns string, 'OK'
    */
    mraidview.fireAssetReadyEvent = function(alias, URL) {
        var handlers = listeners['assetReady'];
        if (handlers != null) {
            for (var i = 0; i < handlers.length; i++) {
                handlers[i](alias, URL);
            }
        }

        return 'OK';
    };

    /**
    * Called by the JAVA SDK when an asset has been removed from the
	* cache at the request of the creative.
    *
    * @returns string, 'OK'
    */
    mraidview.fireAssetRemovedEvent = function(alias) {
        var handlers = listeners['assetRemoved'];
        if (handlers != null) {
            for (var i = 0; i < handlers.length; i++) {
                handlers[i](alias);
            }
        }

        return 'OK';
    };

    /**
    * Called by the JAVA SDK when an asset has been automatically
	* removed from the cache for reasons outside the control of the creative.
    *
    * @returns string, 'OK'
    */
    mraidview.fireAssetRetiredEvent = function(alias) {
        var handlers = listeners['assetRetired'];
        if (handlers != null) {
            for (var i = 0; i < handlers.length; i++) {
                handlers[i](alias);
            }
        }

        return 'OK';
    };

    /**
	* Called by the JAVA SDK when various state properties have changed.
    *
    * @returns string, 'OK'
	*/
    mraidview.fireChangeEvent = function(properties) {
        var handlers = listeners['change'];
        if (handlers != null) {
            for (var i = 0; i < handlers.length; i++) {
                handlers[i](properties);
            }
        }

        return 'OK';
    };

    /**
    * Called by the JAVA SDK when an error has occured.
    *
    * @returns string, 'OK'
    */
    mraidview.fireErrorEvent = function(message, action) {
        var handlers = listeners['error'];
        if (handlers != null) {
            for (var i = 0; i < handlers.length; i++) {
                handlers[i](message, action);
            }
        }

        return 'OK';
    };

    /**
     * Called by the JAVA SDK when the user shakes the device.
     *
     * @returns string, 'OK'
     */
    mraidview.fireShakeEvent = function() {
        var handlers = listeners['shake'];
        if (handlers != null) {
            for (var i = 0; i < handlers.length; i++) {
                handlers[i]();
            }
        }

        return 'OK';
    };

    /**
     *
     */
    mraidview.showAlert = function(message) {
        MRAIDUtilityControllerBridge.showAlert(message);
    };

    /*********************************************/
    /********** INTERNALLY USED METHODS **********/
    /*********************************************/

    /**
     * 
     */
    mraidview.zeroPad = function(number) {
        var text = '';
        if (number < 10) {
            text += '0';
        }
        text += number;
        return text;
    }

    /***************************************************************************/
    /********** LEVEL 0 (not part of spec, but required by public API **********/
    /***************************************************************************/

    /**
    *
    */
    mraidview.activate = function(event) {
        MRAIDUtilityControllerBridge.activate(event);
    };

    /**
    *
    */
    mraidview.addEventListener = function(event, listener) {
        var handlers = listeners[event];
        if (handlers == null) {
            // no handlers defined yet, set it up
            listeners[event] = [];
            handlers = listeners[event];
        }

        // see if the listener is already present
        for (var handler in handlers) {
            if (listener == handler) {
                // listener already present, nothing to do
                return;
            }
        }

        // not present yet, go ahead and add it
        handlers.push(listener);
    };

    /**
    *
    */
    mraidview.deactivate = function(event) {
        MRAIDUtilityControllerBridge.deactivate(event);
    };

    /**
    *
    */
    mraidview.removeEventListener = function(event, listener) {
        var handlers = listeners[event];
        if (handlers != null) {
            handlers.remove(listener);
        }
    };

    /*****************************/
    /********** LEVEL 1 **********/
    /*****************************/

    /**
     *
     */
    mraidview.close = function() {
        try {
            MRAIDDisplayControllerBridge.close();
        } catch(e) {
            mraidview.showAlert('close: ' + e);
        }
    };
    
    /**
     * 
     * 
     * 
     */
	mraidview.share = function( message/*:String*/ ) {		
		try {
			MRAIDUtilityControllerBridge.share( message );
		} catch( e ){
			mraidview.showAlert('share: ' + e); 
		}
	};

    /**
     *
     */
    mraidview.expand = function(dimensions, URL) {
        try {
            /*
			this.dimensions = dimensions;
            var d = mraidview.stringify( dimensions );
            var p = mraidview.stringify( mraidview.getExpandProperties() );
            MRAIDDisplayControllerBridge.expand(d, URL, p);
			*/
        } catch( e ) {
            mraidview.showAlert('executeNativeExpand: ' + e + ', dimensions = ' + dimensions + ', URL = ' + URL + ', expandProperties = ' + mraidview.getExpandProperties());
        }
    };
    
    /**
     *
     */
    mraidview.hide = function() {
        try {
            MRAIDDisplayControllerBridge.hide();
        } catch(e) {
            mraidview.showAlert('hide: ' + e);
        }
    };

    /**
     * 
     * 
     * 
     */
    mraidview.open = function(URL, controls) {
        // the navigation parameter is an array, break it into its parts
        var back = false;
        var forward = false;
        var refresh = false;
        if (controls == null) {
            back = true;
            forward = true;
            refresh = true;
        } else {
            for (var i = 0; i < controls.length; i++) {
                if ((controls[i] == 'none') && (i > 0)) {
                    // error
                    self.fireErrorEvent('none must be the only navigation element present.', 'open');
                    return;
                } else if (controls[i] == 'all') {
                    if (i > 0) {
                        // error
                        self.fireErrorEvent('none must be the only navigation element present.', 'open');
                        return;
                    }

                    // ok
                    back = true;
                    forward = true;
                    refresh = true;
                } else if (controls[i] == 'back') {
                    back = true;
                } else if (controls[i] == 'forward') {
                    forward = true;
                } else if (controls[i] == 'refresh') {
                    refresh = true;
                }
            }
        }

        try {
            MRAIDDisplayControllerBridge.open(URL, back, forward, refresh);
        } catch(e) {
            mraidview.showAlert('open: ' + e);
        }

    };

    /**
     *
     */
    mraidview.openMap = function(POI, fullscreen) {
        try {
            MRAIDDisplayControllerBridge.openMap(POI, fullscreen);
        } catch(e) {
            mraidview.showAlert('openMap: ' + e);
        }
    };

    /**
     *
     */
    mraidview.playAudio = function(URL, properties) {
        var autoPlay = false,
        controls = false,
        loop = false,
        position = false,
        startStyle = 'normal',
        stopStyle = 'normal';

        if (properties != null) {

            if ((typeof properties.autoplay != 'undefined') && (properties.autoplay != null)) {
                autoPlay = true;
            }

            if ((typeof properties.controls != 'undefined') && (properties.controls != null)) {
                controls = true;
            }

            if ((typeof properties.loop != 'undefined') && (properties.loop != null)) {
                loop = true;
            }

            if ((typeof properties.position != 'undefined') && (properties.position != null)) {
                position = true;
            }

            //TODO check valid values...           
            if ((typeof properties.startStyle != 'undefined') && (properties.startStyle != null)) {
                startStyle = properties.startStyle;
            }

            if ((typeof properties.stopStyle != 'undefined') && (properties.stopStyle != null)) {
                stopStyle = properties.stopStyle;
            }

            if (startStyle == 'normal') {
                position = true;
            }

            if (position) {
                autoPlay = true;
                controls = false;
                loop = false;
                stopStyle = 'exit';
            }

            if (loop) {
                stopStyle = 'normal';
                controls = true;
            }

            if (!autoPlay) {
                controls = true;
            }

            if (!controls) {
                stopStyle = 'exit';
            }
        }

        try {
            MRAIDDisplayControllerBridge.playAudio(URL, autoPlay, controls, loop, position, startStyle, stopStyle);
        } catch(e) {
            mraidview.showAlert('playAudio: ' + e);
        }
    };

    /**
     * 
     */
    mraidview.playVideo = function(URL, properties) {
        var audioMuted = false,
        autoPlay = false,
        controls = false,
        loop = false,
        position = [ - 1, -1, -1, -1],
        startStyle = 'normal',
        stopStyle = 'normal';
        if (properties != null) {

            if ((typeof properties.audio != 'undefined') && (properties.audio != null)) {
                audioMuted = true;
            }

            if ((typeof properties.autoplay != 'undefined') && (properties.autoplay != null)) {
                autoPlay = true;
            }

            if ((typeof properties.controls != 'undefined') && (properties.controls != null)) {
                controls = true;
            }

            if ((typeof properties.loop != 'undefined') && (properties.loop != null)) {
                loop = true;
            }

            if ((typeof properties.position != 'undefined') && (properties.position != null)) {
                inline = new Array(4);

                inline[0] = properties.position.top;
                inline[1] = properties.position.left;

                if ((typeof properties.width != 'undefined') && (properties.width != null)) {
                    inline[2] = properties.width;
                } else {
                    //TODO ERROR
                }

                if ((typeof properties.height != 'undefined') && (properties.height != null)) {
                    inline[3] = properties.height;
                } else {
                    //TODO ERROR
                }
            }

            if ((typeof properties.startStyle != 'undefined') && (properties.startStyle != null)) {
                startStyle = properties.startStyle;
            }

            if ((typeof properties.stopStyle != 'undefined') && (properties.stopStyle != null)) {
                stopStyle = properties.stopStyle;
            }

            if (loop) {
                stopStyle = 'normal';
                controls = true;
            }

            if (!autoPlay) controls = true;

            if (!controls) {
                stopStyle = 'exit';
            }

            if (position[0] == -1 || position[1] == -1) {
                startStyle = 'fullscreen';
            }
        }

        try {
            MRAIDDisplayControllerBridge.playVideo(URL, audioMuted, autoPlay, controls, loop, position, startStyle, stopStyle);
        } catch(e) {
            mraidview.showAlert('playVideo: ' + e);
        }

    };

    /**
     * 
     */
    mraidview.resize = function(width, height) {
        try {        
        	var p = mraidview.getResizeProperties();
            MRAIDDisplayControllerBridge.resize(width, height, p.offsetX, p.offsetY, p.customClosePosition, p.allowOffscreen);
        } catch(e) {
            mraidview.showAlert('resize: ' + e);
        }
    };


	mraidview.useCustomClose = function( flag/*:Boolean*/ ) {
		MRAIDDisplayControllerBridge.useCustomClose(flag);
	};

    mraidview.getExpandProperties = function() {
        return expandProperties;
    };

    /**
     *
     */
    mraidview.setExpandProperties = function(properties) {
        expandProperties = properties;
    };



    mraidview.getOrientationProperties = function() {
        return orientationProperties;
    };

    /**
     *
     */
    mraidview.setOrientationProperties = function(properties) {
        orientationProperties = properties;
    };
    
    
    mraidview.getResizeProperties = function() {
        return resizeProperties;
    };
    /**
     *
     */
    mraidview.setResizeProperties = function( properties ) {
        resizeProperties = properties;
    };    
    
    /**
     *
     */
    mraidview.show = function() {
        try {
            MRAIDDisplayControllerBridge.show();
        } catch(e) {
            mraidview.showAlert('show: ' + e);
        }
    };

    /*****************************/
    /********** LEVEL 2 **********/
    /*****************************/

    /**
     * 
     */
    mraidview.createEvent = function(date, title, body) {
        var msecs = (date.getTime() - date.getMilliseconds());

        try {
            MRAIDUtilityControllerBridge.createEvent(msecs.toString(), title, body);
        } catch(e) {
            mraidview.showAlert('createEvent: ' + e);
        }

    };

    mraidview.getSDKProfile = function( ) {
        var s = '';
        try {
            s = MRAIDUtilityControllerBridge.getSDKProfile( );
        } catch(e) {
            s = 'getSDKProfile' + e;
        }
        return s;
    }
    
    /**
     * 
     */
    mraidview.makeCall = function(phoneNumber) {
        try {
            MRAIDUtilityControllerBridge.makeCall(phoneNumber);
        } catch(e) {
            mraidview.showAlert('makeCall: ' + e);
        }
    };

    /**
     * 
     */
    mraidview.sendMail = function(recipient, subject, body) {
        try {
            MRAIDUtilityControllerBridge.sendMail(recipient, subject, body);
        } catch(e) {
            mraidview.showAlert('sendMail: ' + e);
        }
    };

    /**
     * 
     */
    mraidview.sendSMS = function(recipient, body) {
        try {
            MRAIDUtilityControllerBridge.sendSMS(recipient, body);
        } catch(e) {
            mraidview.showAlert('sendSMS: ' + e);
        }
    };

    /**
     * 
     */
    mraidview.setShakeProperties = function(properties) {
		
	};
	
	/**
	 * 
	 */
	mraidview.pauseVideoAd = function( ) {
		try {
            MRAIDDisplayControllerBridge.pauseVideoAd();
        } catch(e) {
            mraidview.showAlert('sendSMS: ' + e);
        }
	};
	mraidview.resumeVideoAd = function( ) {
		try {
            MRAIDDisplayControllerBridge.resumeVideoAd();
        } catch(e) {
            mraidview.showAlert('sendSMS: ' + e);
        }
		
	};
	
	/**
	 * 
	 */
	mraidview.getVideoAdPlayheadTime = function(){
		try {
			return MRAIDUtilityControllerBridge.getVideoAdPlayheadTime(); 
		} catch( e ) {
			return 0;
		}
	};
	mraidview.getVideoAdDuration = function(){
		try {
			return MRAIDUtilityControllerBridge.getVideoAdDuration();
		} catch( e ) {
			return 0;
		}
	};
	
	
	
    /*****************************/
    /********** LEVEL 3 **********/
    /*****************************/

    /**
     * 
     */
    mraidview.addAsset = function(URL, alias) {
		// TODO
	};
    /**
     *
     */
    mraidview.request = function(URI, display) {
		// TODO
		console.log('mraidview.request ' + URI+', '+display);
		mraidview.executeNativeCall('request', 'uri', URI, 'display', display);
	};
    /**
     * 
     */
    mraidview.removeAsset = function(alias) {
    	// TODO
    };

    mraidview.stringify = function(args) {
        return '';
		/*
		if (typeof JSON === 'undefined') {
            var s = '';
            var len = args.length;
            var i;
            if (typeof len == 'undefined') {
                return mraidview.stringifyArg(args);
            }
            for (i = 0; i < args.length; i++) {
                if (i > 0) {
                    s = s + ',';
                }
                s = s + mraidview.stringifyArg(args[i]);
            }
            s = s + ']';
            return s;
        } else {
            return JSON.stringify(args);
        }
		*/
    };

    mraidview.stringifyArg = function( arg ) {
		return '';
		/*
        var s, type, start, name, nameType, a;
        type = typeof arg;
        s = '';
        if ((type === 'number') || (type === 'boolean')) {
            s = s + args;
        } else if (arg instanceof Array) {
            s = s + '[' + arg + ']';
        } else if (arg instanceof Object) {
            start = true;
            s = s + '{';
            for (name in arg) {
                if (arg[name] !== null) {
                    if (!start) {
                        s = s + ',';
                    }
                    s = s + '"' + name + '":';
                    nameType = typeof arg[name];
                    if ((nameType === 'number') || (nameType === 'boolean')) {
                        s = s + arg[name];
                    } else if ((typeof arg[name]) === 'function') {
                        // don't copy the functions
                        s = s + '""';
                    } else if (arg[name] instanceof Object) {
                        s = s + this.stringify(args[i][name]);
                    } else {
                        s = s + '"' + arg[name] + '"';
                    }
                    start = false;
                }
            }
            s = s + '}';
        } else {
            a = arg.replace(/\\/g, '\\\\');
            a = a.replace(/"/g, '\\"');
            s = s + '"' + a + '"';
        }
        mraidview.showAlert('json:' + s);
        return s;
		*/
    };
  	
  	mraidview.executeNativeCall = function(command) {
    	// build iOS command
    	var bridgeCall = 'mraid://' + command;
    	var value;
    	var firstArg = true;
    	for (var i = 1; i < arguments.length; i += 2) {
      		value = arguments[i + 1];
      		if (value == null) {
        		// no value, ignore the property
        		continue;
      		}
      		// add the correct separator to the name/value pairs
      		if (firstArg) {
        		bridgeCall += '?';
        		firstArg = false;
      		} else {
        		bridgeCall += '&';
      		}
      		bridgeCall += arguments[i] + '=' + escape(value);
    	}
    	
    	console.log('executeNativeCall nativeCallInFlight='+nativeCallInFlight+', bridgeCall='+bridgeCall);
    	// add call to queue
    	if (nativeCallInFlight) {
      		// call pending, queue up request
      		nativeCallQueue.push(bridgeCall);
    	} else {
      		// no call currently in process, execute it directly
      		nativeCallInFlight = true;
      		window.location = bridgeCall;
    	}
  	};

	var mraid = window.mraid = {};
	var ormma = window.ormma = {}; 
	
	/**
	 * the current state of the ad container, returning whether the ad container is in its default, 
	 * fixed position or is in an expanded or resized, larger position, or hidden.
	 * 
	 * 
	 * loading
	 * the container is not yet ready for interactions with the MRAID implementation
	 * 
	 * default
	 * the initial position and size of the ad container as placed by the application and SDK
	 * 
	 * expanded
	 * the ad container has expanded to cover the application content at the top of the view hierarchy
	 * 
	 * resized
	 * the ad container has changed size via MRAID 2.0¡¯s resize() method
	 * 
	 * hidden
	 * the state an interstitial ad transitions to when closed. Where supported, the state a banner ad transitions to when closed
	 */
	var STATES = ormma.STATES = mraid.STATES = {
		UNKNOWN : 'unknown',
		LOADING : 'loading',
		DEFAULT : 'default',
		RESIZED : 'resized',
		EXPANDED : 'expanded',
		HIDDEN : 'hidden'
	};
	
	/**
	 * ------------- headingChange -------------
	 * This event is thrown when the devices compass direction changes.
	 * parameters:
	 * heading (Number) : compass heading in degrees or -1 for unknown
	 * triggered by: a change in the device heading after the compass has been activated by registering a 'heading' event listener.
	 * level: 2
	 * ------------- keyboardChange -------------
	 * This event is thrown when the software keyboard is opened or closed for text entry in an ad.
	 * parameters:
	 * open (Boolean) : true if keyboard is open, false if keyboard is not open
	 * triggered by: a change in the state of the virtual keyboard after registering a 'keyboard' event listener.
	 * level: 2
	 * ------------- response -------------
	 * This event is thrown when a request action with a display type of 'proxy' returns a response.
	 * parameters:
	 * url (String) : the URL of the original request action
	 * response (String) : the full body of the response
	 * triggered by: a request() method call returning.
	 * level: 2
	 * ------------- xxx -------------
	 * 
	 * ------------- xxx -------------
	 * 
	 * 
	 */
	var EVENTS = ormma.EVENTS = mraid.EVENTS = {
		READY : 'ready',
		ASSETREADY : 'assetReady', 
		ASSETREMOVED : 'assetRemoved', 
		ASSETRETIRED : 'assetRetired', 
		ERROR : 'error',
		INFO : 'info', 
		HEADINGCHANGE : 'headingChange', 
		KEYBOARDCHANGE : 'keyboardChange', 
		LOCATIONCHANGE : 'locationChange', 
		NETWORKCHANGE : 'networkChange', 
		ORIENTATIONCHANGE : 'orientationChange',
		RESPONSE : 'response',
		SCREENCHANGE : 'screenChange',
		SHAKE : 'shake',
		SIZECHANGE : 'sizeChange',
		STATECHANGE : 'stateChange',
		TILTCHANGE : 'tiltChange',
		VIEWABLECHANGE : 'viewableChange',
		XADSDKVIEWABLECHANGE : 'xadsdkviewableChange'
	};

	var CONTROLS = mraid.CONTROLS = {
		BACK : 'back',
		FORWARD : 'forward',
		REFRESH : 'refresh',
		ALL : 'all'
	};

	var FEATURES = mraid.FEATURES = {
		LEVEL1 : 'level-1',
		LEVEL2 : 'level-2',
		LEVEL3 : 'level-3',
		SCREEN : 'screen',
		ORIENTATION : 'orientation',
		HEADING : 'heading',
		LOCATION : 'location',
		SHAKE : 'shake',
		TILT : 'tilt',
		NETWORK : 'network',
		SMS : 'sms',
		PHONE : 'phone',
		EMAIL : 'email',
		CALENDAR : 'calendar',
		CAMERA : 'camera',
		AUDIO : 'audio',
		VIDEO : 'video',
		MAP : 'map'
	};

	var NETWORK = mraid.NETWORK = {
		OFFLINE : 'offline',
		WIFI : 'wifi',
		CELL : 'cell',
		UNKNOWN : 'unknown'
	};
	var state = STATES.UNKNOWN;

	var size = {
		width : 0,
		height : 0
	};

	var defaultPosition = {
		x : 0,
		y : 0,
		width : 0,
		height : 0
	};

	var maxSize = {
		width : 0,
		height : 0
	};

	var supports = {
		'level-1' : true,
		'level-2' : true,
		'level-3' : true,
		'screen' : true,
		'orientation' : true,
		'heading' : true,
		'location' : true,
		'shake' : true,
		'tilt' : true,
		'network' : true,
		'sms' : true,
		'phone' : true,
		'email' : true,
		'calendar' : true,
		'camera' : true,
		'audio' : true,
		'video' : true,
		'map' : true
	};

	var viewable = true;

	var xadsdkviewable = true;

	var heading = -1;

	var keyboardState = false;

	var deviceProperties = {};

	var location = null;

	var network = NETWORK.UNKNOWN;

	var orientation = -1;

	var screenSize = null;

	var shakeProperties = null;

	var tilt = null;

	var assets = {};

	var cacheRemaining = -1;

	var intervalID = null;
	var readyTimeout = 10000;
	var readyDuration = 0;

	var dimensionValidators = {
		x : function(value) {
			return !isNaN(value) && value >= 0;
		},
		y : function(value) {
			return !isNaN(value) && value >= 0;
		},
		width : function(value) {
			return !isNaN(value) && value >= 0 && value <= screenSize.width;
		},
		height : function(value) {
			return !isNaN(value) && value >= 0 && value <= screenSize.height;
		}
	};
	
	/**
	 * 
	 * 
	 * 
	 */
	var expandPropertyValidators = {
		width : function(value) {
			return !isNaN(value) && value >= 0 && value <= screenSize.width;
		},
		height : function(value) {
			return !isNaN(value) && value >= 0 && value <= screenSize.height;
		},
		useBackground : function(value) {
			return (value === true || value === false);
		},
		backgroundColor : function(value) {
			return (typeof value == 'string' && value.substr(0, 1) == '#' && !isNaN(parseInt(value.substr(1), 16)));
		},
		backgroundOpacity : function(value) {
			return !isNaN(value) && value >= 0 && value <= 1;
		},
		lockOrientation : function(value) {
			return (value === true || value === false);
		}
	};
	
	/**
	 * 
	 * 
	 * 
	 */
	var orientationPropertyValidators = {
		allowOrientationChange : function( value ) {
			return (value === true || value === false);
		},
		forceOrientation : function( value ) {
			return (typeof value == 'string' && (value==='portrait' || value==='landscape' || value==='none') );
		}
	};
	
	var shakePropertyValidators = {
		intensity : function(value) {
			return !isNaN(value);
		},
		interval : function(value) {
			return !isNaN(value);
		}
	};

	var changeHandlers = {
		state : function(val) {
			if (state == STATES.UNKNOWN) {
				intervalID = window.setInterval(window.mraid.signalReady, 20);
				broadcastEvent(EVENTS.INFO, 'controller initialized, attempting callback');
				console.log('controller initialized, attempting callback');
			}
			broadcastEvent(EVENTS.INFO, 'setting state to ' + stringify(val));
			state = val;
			broadcastEvent(EVENTS.STATECHANGE, state);			
		},
		size : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting size to ' + stringify(val));
			size = val;
			broadcastEvent(EVENTS.SIZECHANGE, size.width, size.height);
		},
		defaultPosition : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting default position to ' + stringify(val));
			defaultPosition = val;
		},
		maxSize : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting maxSize to ' + stringify(val));
			maxSize = val;
		},
		expandProperties : function(val) {
			broadcastEvent(EVENTS.INFO, 'merging expandProperties with ' + stringify(val));
			for ( var i in val) {
				expandProperties[i] = val[i];
			}
		},
		orientationProperties : function(val) {
			broadcastEvent(EVENTS.INFO, 'merging orientationProperties with ' + stringify(val));
			for ( var i in val) {
				orientationProperties[i] = val[i];
			}
		},
		supports : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting supports to ' + stringify(val));
			supports = {};
			for ( var key in FEATURES) {
				supports[FEATURES[key]] = contains(FEATURES[key], val);
			}
		},
		heading : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting heading to ' + stringify(val));
			heading = val;
			broadcastEvent(EVENTS.HEADINGCHANGE, heading);
		},
		keyboardState : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting keyboardState to '+ stringify(val));
			keyboardState = val;
			broadcastEvent(EVENTS.KEYBOARDCHANGE, keyboardState);
		},
		deviceProperties : function( val ) {
			deviceProperties = val;
		},
		location : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting location to ' + stringify(val));
			location = val;
			broadcastEvent(EVENTS.LOCATIONCHANGE, location.lat, location.lon, location.acc);
		},
		network : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting network to ' + stringify(val));
			network = val;
			broadcastEvent(EVENTS.NETWORKCHANGE,(network != NETWORK.OFFLINE && network != NETWORK.UNKNOWN),network);
		},
		orientation : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting orientation to '+ stringify(val));
			orientation = val;
			broadcastEvent(EVENTS.ORIENTATIONCHANGE, orientation);
		},
		screenSize : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting screenSize to ' + stringify(val));
			screenSize = val;
			broadcastEvent(EVENTS.SCREENCHANGE, screenSize.width,screenSize.height);
		},
		shakeProperties : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting shakeProperties to '+ stringify(val));
			shakeProperties = val;
		},
		tilt : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting tilt to ' + stringify(val));
			tilt = val;
			broadcastEvent(EVENTS.TILTCHANGE, tilt.x, tilt.y, tilt.z);
		},
		cacheRemaining : function(val) {
			broadcastEvent(EVENTS.INFO, 'setting cacheRemaining to '+ stringify(val));
			cacheRemaining = val;
		},
		viewable : function( val ) {
			viewable = (val === 'true') || ( val === '1' ) || ( val === 'yes' );
			broadcastEvent(EVENTS.VIEWABLECHANGE, viewable);		
		},
		xadsdkviewable: function( val ) {
			xadsdkviewable = (val === 'true') || ( val === '1' ) || ( val === 'yes' );
			broadcastEvent(EVENTS.XADSDKVIEWABLECHANGE, xadsdkviewable);		
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
				if (this.count == 1)
					mraidview.activate(event);
			}
		};
		this.remove = function(func) {
			var id = String(func);
			if (listeners[id]) {
				listeners[id] = null;
				delete listeners[id];
				this.count--;
				if (this.count == 0)
					mraidview.deactivate(event);
				return true;
			} else {
				return false;
			}
		};
		this.removeAll = function() {
			for ( var id in listeners)
				this.remove(listeners[id]);
		};
		this.broadcast = function(args) {
			for ( var id in listeners)
				listeners[id].apply({}, args);
		};
		this.toString = function() {
			var out = [ event, ':' ];
			for ( var id in listeners)
				out.push('|', id, '|');
			return out.join('');
		};
	};

	// PRIVATE METHODS
	// ////////////////////////////////////////////////////////////

	mraidview.addEventListener('change', function(properties) {
		for ( var property in properties) {
			var handler = changeHandlers[property];
			handler(properties[property]);
		}
	});

	mraidview.addEventListener('shake', function() {
		broadcastEvent(EVENTS.SHAKE);
	});

	mraidview.addEventListener('error', function(message, action) {
		broadcastEvent(EVENTS.ERROR, message, action);
	});

	mraidview.addEventListener('response', function(uri, response) {
		broadcastEvent(EVENTS.RESPONSE, uri, response);
	});

	mraidview.addEventListener('assetReady', function(alias, URL) {
		assets[alias] = URL;
		broadcastEvent(EVENTS.ASSETREADY, alias);
	});

	mraidview.addEventListener('assetRemoved', function(alias) {
		assets[alias] = null;
		delete assets[alias];
		broadcastEvent(EVENTS.ASSETREMOVED, alias);
	});

	mraidview.addEventListener('assetRetired', function(alias) {
		assets[alias] = null;
		delete assets[alias];
		broadcastEvent(EVENTS.ASSETRETIRED, alias);
	});

	var clone = function(obj) {
		var f = function() {
		};
		f.prototype = obj;
		return new f();
	};

	var stringify = function(obj) {
		return '';
		/*
		if (typeof obj == 'object') {
			if (obj.push) {
				var out = [];
				for ( var p in obj) {
					out.push(obj[p]);
				}
				return '[' + out.join(',') + ']';
			} else {
				var out = [];
				for ( var p in obj) {
					out.push('\'' + p + '\':' + obj[p]);
				}
				return '{' + out.join(',') + '}';
			}
		} else {
			return String(obj);
		}
		*/
	};

	var valid = function(obj, validators, action, full) {
		if (full) {
			if (obj === undefined) {
				broadcastEvent(EVENTS.ERROR, 'Required object missing.', action);
				return false;
			} else {
				for ( var i in validators) {
					if (obj[i] === undefined) {
						broadcastEvent(EVENTS.ERROR,'Object missing required property ' + i, action);
						return false;
					}
				}
			}
		}
		for ( var i in obj) {
			if (!validators[i]) {
				broadcastEvent(EVENTS.ERROR, 'Invalid property specified - ' + i + '.', action);
				return false;
			} else if (!validators[i](obj[i])) {
				broadcastEvent(EVENTS.ERROR, 'Value of property ' + i + '<'+ obj[i] + '>' + ' is not valid type.', action);
				return false;
			}
		}
		return true;
	};

	var contains = function(value, array) {
		for ( var i in array) {
			if (array[i] == value) {
				return true;
			}
		}
		return false;
	};

	var broadcastEvent = function() {
		var args = new Array(arguments.length);
		for ( var i = 0; i < arguments.length; i++) {
			args[i] = arguments[i];
		}
		var event = args.shift();
		try {
			if (listeners[event]){
				listeners[event].broadcast(args);
			}
			console.log('broadcastEvent:' + event + ':args:' + args);
		} catch (e) {
			//
		}
	}

	var trim = function(s) {
		var l = 0;
		var r = s.length - 1;
		while (l < s.length && s[l] == ' ') {
			l++;
		}
		while (r > l && s[r] == ' ') {
			r -= 1;
		}
		return s.substring(l, r + 1);
	}

	// LEVEL 1
	// ////////////////////////////////////////////////////////////////////
	function mraidReadyEvent() {
		/* TODO */
	}
	function handleStateChangeEvent() {
		/* TODO */
	}

	mraid.signalReady = function() {
		/*
		broadcastEvent(EVENTS.INFO, 'setting state to '+ stringify(STATES.DEFAULT));
		state = STATES.DEFAULT;
		broadcastEvent(EVENTS.STATECHANGE, state);
		mraid.addEventListener('stateChange', handleStateChangeEvent);
		broadcastEvent(EVENTS.INFO, 'ready eventListener triggered');
		broadcastEvent(EVENTS.READY, 'mraid ready event triggered');
		
		//broadcastEvent(mraid.EVENTS.READY, '..........................mraid ready event triggered');
		
		window.clearInterval(intervalID);
		*/
		
		state = STATES.DEFAULT;
		mraid.addEventListener('stateChange', handleStateChangeEvent);
		
		window.clearInterval(intervalID);
        broadcastEvent(EVENTS.INFO, 'ready eventListener triggered');
        broadcastEvent(mraid.EVENTS.READY, 'mraid ready event triggered');
		
		try {
			ORMMAReady(); 
			mraid.addEventListener('ready', mraidReadyEvent);
			broadcastEvent(EVENTS.INFO, 'MRAID callback invoked');
		} catch (e) {
			// ignore errors, will try again soon and then timeout
			console.log('ignore errors, will try again soon and then timeout' + e);
		}
	};

	mraid.addEventListener = function(event, listener) {
		if (!event || !listener) {
			broadcastEvent(EVENTS.ERROR, 'Both event and listener are required.', 'addEventListener');
		} else if (!contains(event, EVENTS)) {
			broadcastEvent(EVENTS.ERROR, 'Unknown event: ' + event, 'addEventListener');
		} else {
			if (!listeners[event]) {
				listeners[event] = new EventListeners(event);
			}
			listeners[event].add(listener);
		}
	};

	mraid.close = function() {
		mraidview.close();
	};
	
	
	mraid.share = function( message/*:String*/ ) {
		mraidview.share( message );
	};
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @parameter URL(optional): 
	 * 					The URL for the document to be displayed in a new overlay view. If null, the body of the current ad will be used in the current webview. 
	 */
	mraid.expand = function( URL ) {
		var p = mraid.getExpandProperties();;
		var pos = mraid.getDefaultPosition();		
		
		var d = {x:pos.x, y:pos.y, width:p.width, height:p.height};
		broadcastEvent(EVENTS.INFO, 'expanding to ' + stringify(d)+', URL='+URL);
		/*
		if (typeof dimensions == 'undefined') {
			var pos = mraid.getDefaultPosition();
			var size = mraid.getSize();
			dimensions = {
				x : pos.x,
				y : pos.y,
				width : size.width,
				height : 250
			};
		}
		*/
		broadcastEvent(EVENTS.INFO, 'expanding to new: ' + stringify(d));
		
		if (valid(d, dimensionValidators, 'expand', true) && mraid.getState() != STATES.EXPANDED) {
			mraidview.expand(d, URL);
			console.log('state:' + mraid.getState());
		}
	};
	
	/**
	 * get the location and size of the default ad view
	 */
	mraid.getDefaultPosition = function() {
		return clone(defaultPosition);
	};
	mraid.getCurrentPosition = function() {
		return mraid.getDefaultPosition();
	};
	mraid.getExpandProperties = function() {
		var p = clone( mraidview.getExpandProperties() );
		var s = clone( mraid.getScreenSize() );
		
		var obj = {	x : p.x, 
					y : p.y,
					width : p.width,
					height : p.height,
					useCustomClose : p.useCustomClose,
    				isModal:p.isModal};
		
		if( p['width'] <= 0 )	obj['width'] = s['width'];
		if( p['height'] <= 0 )	obj['height'] = s['height'];
		
		return obj;
	};
	
	mraid.getOrientationProperties = function() {
		return clone(mraidview.getOrientationProperties());
	};	

	mraid.getMaxSize = function() {
		return clone(maxSize);
	};
	
	/**
	 * get the current size of the ad
	 */
	mraid.getSize = function() {
		return clone(size);
	};

	mraid.getState = function() {
		return state;
	};
	
	mraid.getDeviceProperties = function() {
		return deviceProperties;
	};	
	
	/**
	 * 
	 */
	mraid.isViewable = function() {
		return viewable;
	};
	
	/**
	 * the placementType should be one of : 'inline', 'interstitial', 'unknown'
	 */
	mraid.getPlacementType = function() {
            return 'interstitial';
	};
    
    mraid.getVersion = function () {
	   	return 'yd_mraid 1.0';
   	};   
	mraid.hide = function() {
		if (state == STATES.HIDDEN) {
			broadcastEvent(EVENTS.ERROR, 'Ad is currently hidden.', 'hide');
		} else {
			mraidview.hide();
		}
	};
	
	/**
	 * The open method will display an embedded browser window in the application that loads an external URL. 
	 * On device platforms that do not allow an embedded browser, the open method invokes the native browser with the external URL.
	 * 
	 * 
	 */
	mraid.open = function(URL, controls) {
		if (!URL) {
			broadcastEvent(EVENTS.ERROR, 'URL is required.', 'open');
		} else {
			mraidview.open(URL, controls);
		}
	};

	mraid.openMap = function(POI, fullscreen) {
		/*
		if (!POI) {
			broadcastEvent(EVENTS.ERROR, 'POI is required.', 'openMap');
		} else {
			mraidview.openMap(POI, fullscreen);
		}
		*/
	};

	mraid.removeEventListener = function(event, listener) {
		if (!event) {
			broadcastEvent(EVENTS.ERROR, 'Must specify an event.',
					'removeEventListener');
		} else {
			if (listener
					&& (!listeners[event] || !listeners[event].remove(listener))) {
				broadcastEvent(EVENTS.ERROR,
						'Listener not currently registered for event',
						'removeEventListener');
				return;
			} else if (listeners[event]) {
				listeners[event].removeAll();
			}

			if (listeners[event] && listeners[event].count == 0) {
				listeners[event] = null;
				delete listeners[event];
			}
		}
	};

	mraid.resize = function(width, height) {
		var w;
		var h;
		if (width == null && height == null ) {
			var p = mraid.getResizeProperties();
			w = p.width;
			h = p.height;
			//mraidview.resize(p.width, p.height);
		} else {
			w = width;
			h = height;		
			/*
			if (width == null || height == null || isNaN(width) || isNaN(height)|| width < 0 || height < 0) {
				broadcastEvent(EVENTS.ERROR, 'Requested size must be numeric values between 0 and maxSize.', 'resize');
			} else if (width > maxSize.width || height > maxSize.height) {
				broadcastEvent(EVENTS.ERROR, 'Request (' + width + ' x ' + height + ') exceeds maximum allowable size of (' + maxSize.width + ' x ' + maxSize.height + ')', 'resize');
			} else if (width == size.width && height == size.height) {
				//broadcastEvent(EVENTS.ERROR, 'Requested size equals current size.', 'resize');
			} else {
				mraidview.resize(width, height);
			}
			*/
		}
		
		//alert('mraid.resize w='+w+', h='+h+', maxW='+maxSize.width+', maxH='+maxSize.height);
		
		// 
		if( mraid.isValidResizeProperties(w,h) ) {
			mraidview.resize(w, h);
		}
	};
	mraid.isValidResizeProperties = function(width, height) { 
		var b = true;
		if (width == null || height == null || isNaN(width) || isNaN(height)|| width < 0 || height < 0) {
			b = false;
			broadcastEvent(EVENTS.ERROR, 'Requested size must be numeric values between 0 and maxSize.', 'resize');
		} else if (width > maxSize.width || height > maxSize.height) {
			b = false;
			broadcastEvent(EVENTS.ERROR, 'Request (' + width + ' x ' + height + ') exceeds maximum allowable size of (' + maxSize.width + ' x ' + maxSize.height + ')', 'resize');
		} else if (width == size.width && height == size.height) {
			//broadcastEvent(EVENTS.ERROR, 'Requested size equals current size.', 'resize');
		}
		return b;
	}
	
	mraid.setExpandProperties = function(properties) {
		//if (valid(properties, expandPropertyValidators, 'setExpandProperties')) {
			mraidview.setExpandProperties(properties);
		//}
	};
	mraid.setOrientationProperties = function(properties) {
		if (valid(properties, orientationPropertyValidators, 'setOrientationProperties')) {
			mraidview.setOrientationProperties(properties);
		}
	};
	
	
	var isMraidResizePropertiesUpdated = false;
	/**
	 * 
	 * 
	 */
	mraid.setResizeProperties = function(properties) {
		//if (valid(properties, resizePropertiesValidators, 'setResizeProperties')) {
			isMraidResizePropertiesUpdated = true;
			mraidview.setResizeProperties(properties);
		//}
	};	
	mraid.getResizeProperties = function() {
		var o = clone(mraidview.getResizeProperties());
		if( !isMraidResizePropertiesUpdated ) {
			var s = mraid.getSize();
			o['width'] = s['width'];
			o['height'] = s['height'];
		}
		return o;
	};
	
	
	
	
	
	mraid.show = function() {
		if (state != STATES.HIDDEN) {
			broadcastEvent(EVENTS.ERROR, 'Ad is currently visible.', 'show');
		} else {
			mraidview.show();
		}
	};

	mraid.playAudio = function(URL, properties) {
		if (!supports[FEATURES.AUDIO]) {
			broadcastEvent(EVENTS.ERROR, 'Method not supported by this client.', 'playAudio');
		} else if (!URL || typeof URL != 'string') {
			broadcastEvent(EVENTS.ERROR, 'Request must specify a URL', 'playAudio');
		} else {
			mraidview.playAudio(URL, properties);
		}
	};

	mraid.playVideo = function(URL, properties) {
		if (!supports[FEATURES.VIDEO]) {
			broadcastEvent(EVENTS.ERROR, 'Method not supported by this client.', 'playVideo');
		} else if (!URL || typeof URL != 'string') {
			broadcastEvent(EVENTS.ERROR, 'Request must specify a URL', 'playVideo');
		} else {
			mraidview.playVideo(URL, properties);
		}
	};

	// LEVEL 2
	// ////////////////////////////////////////////////////////////////////

	mraid.createEvent = function(date, title, body) {		
		if (!supports[FEATURES.CALENDAR]) {
			broadcastEvent(EVENTS.ERROR, 'Method not supported by this client.', 'createEvent');
		} else if (!date || typeof date != 'object' || !date.getDate) {
			broadcastEvent(EVENTS.ERROR, 'Valid date required.', 'createEvent');
		} else if (!title || typeof title != 'string'|| trim(title).length == 0) {
			broadcastEvent(EVENTS.ERROR, 'Valid title required.', 'createEvent');
		} else if (!body || typeof body != 'string' || trim(body).length == 0) {
			broadcastEvent(EVENTS.ERROR, 'Valid body required.', 'createEvent');
		} else {
			mraidview.createEvent(date, title, body);
		}
	};
	
	/**
	 * 
	 * 
	 */
    mraid.getSDKProfile = function( ) {
    	return mraidview.getSDKProfile( );
    }
    
	mraid.getHeading = function() {
		if (!supports[FEATURES.HEADING]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.', 'getHeading');
		}
		return heading;
	};

	mraid.getKeyboardState = function() {
		if (!supports[FEATURES.LEVEL2]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.', 'getKeyboardState');
		}
		return keyboardState;
	}

	mraid.getLocation = function() {
		if (!supports[FEATURES.LOCATION]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.', 'getLocation');
		}
		return (null == location) ? null : clone(location);
	};
	
	/**
	 * value			description
	 * --------------------------------------------------------------------
	 * offline		 	no network connection
	 * wifi	 			network using a wifi antennae
	 * cell	 			network using a cellular antennae (such as 3G)
	 * unknown	 		network connection in unknown state
	 */
	mraid.getNetwork = function() {
		if (!supports[FEATURES.NETWORK]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.', 'getNetwork');
		}
		return network;
	};

	mraid.getOrientation = function() {
		if (!supports[FEATURES.ORIENTATION]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.', 'getOrientation');
		}
		return orientation;
	};

	mraid.getScreenSize = function() {
		if (!supports[FEATURES.SCREEN]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.', 'getScreenSize');
		} else {
			return (null == screenSize) ? null : clone(screenSize);
		}
	};

	mraid.getShakeProperties = function() {
		if (!supports[FEATURES.SHAKE]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.',
					'getShakeProperties');
		} else {
			return (null == shakeProperties) ? null : clone(shakeProperties);
		}
	};

	mraid.getTilt = function() {
		if (!supports[FEATURES.TILT]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.', 'getTilt');
		} else {
			return (null == tilt) ? null : clone(tilt);
		}
	};

	mraid.makeCall = function(number) {
	
		if (!supports[FEATURES.PHONE]) {
			broadcastEvent(EVENTS.ERROR,'Method not supported by this client.', 'makeCall');
		} else if (!number || typeof number != 'string'
				|| trim(number).length == 0) {
			broadcastEvent(EVENTS.ERROR,
					'Request must provide a number to call.', 'makeCall');
		} else {
			mraidview.makeCall(number);
		}
	};

	mraid.sendMail = function(recipient, subject, body) {
		if (!supports[FEATURES.EMAIL]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.', 'sendMail');
		} else if (!recipient || typeof recipient != 'string'
				|| trim(recipient).length == 0) {
			broadcastEvent(EVENTS.ERROR, 'Request must specify a recipient.',
					'sendMail');
		} else {
			mraidview.sendMail(recipient, subject, body);
		}
	};

	mraid.sendSMS = function(recipient, body) {
		if (!supports[FEATURES.SMS]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.', 'sendSMS');
		} else if (!recipient || typeof recipient != 'string'
				|| trim(recipient).length == 0) {
			broadcastEvent(EVENTS.ERROR, 'Request must specify a recipient.',
					'sendSMS');
		} else {
			mraidview.sendSMS(recipient, body);
		}
	};

	mraid.setShakeProperties = function(properties) {
		if (!supports[FEATURES.SHAKE]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.',
					'setShakeProperties');
		} else if (valid(properties, shakePropertyValidators,
				'setShakeProperties')) {
			mraidview.setShakeProperties(properties);
		}
	};


	mraid.supports = function(feature) {
		if (supports[feature]) {
			return true;
		} else {
			return false;
		}
	};

	// LEVEL 3
	// ////////////////////////////////////////////////////////////////////

	mraid.addAsset = function(URL, alias) {
		if (!URL || !alias || typeof URL != 'string'
				|| typeof alias != 'string') {
			broadcastEvent(EVENTS.ERROR, 'URL and alias are required.',
					'addAsset');
		} else if (supports[FEATURES.LEVEL3]) {
			mraidview.addAsset(URL, alias);
		} else if (URL.indexOf('mraid://') == 0) {
			broadcastEvent(EVENTS.ERROR,
					'Native device assets not supported by this client.',
					'addAsset');
		} else {
			assets[alias] = URL;
			broadcastEvent(EVENTS.ASSETREADY, alias);
		}
	};

	mraid.addAssets = function(assets) {
		for ( var alias in assets) {
			mraid.addAsset(assets[alias], alias);
		}
	};

	mraid.getAssetURL = function(alias) {
		if (!assets[alias]) {
			broadcastEvent(EVENTS.ERROR, 'Alias unknown.', 'getAssetURL');
		}
		return assets[alias];
	};

	mraid.getCacheRemaining = function() {
		if (!supports[FEATURES.LEVEL3]) {
			broadcastEvent(EVENTS.ERROR,
					'Method not supported by this client.', 'getCacheRemaining');
		}
		return cacheRemaining;
	};
	
	/**
	 * The method executes asynchronously, but returns a Boolean value of false to facilitate use in anchor tags. 
	 * There is also an option explicitly for metrics tracking that will cache requests offline and execute them 
	 * whenever the device reconnects. The display parameter supports the following values :
	 * ignore	 the response is ignored
	 * proxy	 the response is cached if the device is off-line and proxied when connectivity returns
	 * 
	 * 
	 * 
	 */
	mraid.request = function(uri, display) {
		console.log('mraid.request support_level3=' + supports[FEATURES.LEVEL3]+', uri='+uri+', display='+display);
		
		mraidview.request(uri, display);
		/*
		if (!supports[FEATURES.LEVEL3]) {
			broadcastEvent(EVENTS.ERROR, 'Method not supported by this client.', 'request');
		} else if (!uri || typeof uri != 'string') {
			broadcastEvent(EVENTS.ERROR, 'URI is required.', 'request');
		} else {
			console.log('mraid.request ' + uri+', '+display);
			mraidview.request(uri, display);
		}
		*/
	};

	mraid.removeAllAssets = function() {
		for ( var alias in assets) {
			mraid.removeAsset(alias);
		}
	};

	mraid.removeAsset = function(alias) {
		if (!alias || typeof alias != 'string') {
			broadcastEvent(EVENTS.ERROR, 'Alias is required.', 'removeAsset');
		} else if (!assets[alias]) {
			broadcastEvent(EVENTS.ERROR, 'Alias unknown.', 'removeAsset');
		} else if (supports[FEATURES.LEVEL3]) {
			mraidview.removeAsset(alias);
		} else {
			assets[alias] = null;
			delete assets[alias];
			broadcastEvent(EVENTS.ASSETREMOVED, alias);
		}
	};
	
	/**
	 * allows the ad designer to replace the default close graphic. 
	 * True, stop showing the default close graphic and rely on ad creative's custom close indicator; 
	 * false (default), container will display the default close graphic
	 */
	mraid.useCustomClose = function( flag/*:Boolean*/ ) {
	    var p = mraidview.getExpandProperties();
    	p['useCustomClose'] = flag;
    	mraidview.setExpandProperties( p );
	};
	
	mraid.pauseVideoAd = function( ) {
		mraidview.pauseVideoAd( );
	};
	mraid.resumeVideoAd = function( ) {
		mraidview.resumeVideoAd( );
	};
	
	mraid.getVideoAdPlayheadTime = function(){
		return mraidview.getVideoAdPlayheadTime();
	};
	mraid.getVideoAdDuration = function(){
		return mraidview.getVideoAdDuration();
	};


	
	/**
	 *
	 *
	 */
	mraid.setVideoDisplayProperties = function( offsetX, offsetY, drivingAdWidth, drivingAdHeight ) {
		mraidview.setVideoDisplayProperties( offsetX, offsetY, drivingAdWidth, drivingAdHeight );
	};	
	/**
	 * 
	 * 
	 * 
	 */
	mraid.detachBridgeInterface = function() {	};
        
	ormma.addEventListener = mraid.addEventListener;
	ormma.close = mraid.close;
	ormma.expand = mraid.expand;
	ormma.getExpandProperties = mraid.getExpandProperties;
	ormma.getOrientationProperties = mraid.getOrientationProperties;
	ormma.getState = mraid.getState;
	ormma.open = mraid.open;
	ormma.removeEventListener = mraid.removeEventListener; 
	ormma.setExpandProperties = mraid.setExpandProperties;
	ormma.setOrientationProperties = mraid.setOrientationProperties;
	ormma.useCustomClose = mraid.useCustomClose;

	ormma.show = mraid.show;
	ormma.error = mraid.error;
	ormma.stateChange = mraid.stateChange;
	ormma.ready = mraid.ready;
	ormma.viewableChange = mraid.viewableChange;

	ormma.getDefaultPosition = mraid.getDefaultPosition;
	ormma.getMaxSize = mraid.getMaxSize;
	ormma.getSize = mraid.getSize;
	ormma.hide = mraid.hide;
	ormma.resize = mraid.resize;
	
	ormma.pauseVideoAd = mraid.pauseVideoAd;
	ormma.resumeVideoAd = mraid.resumeVideoAd;

})();