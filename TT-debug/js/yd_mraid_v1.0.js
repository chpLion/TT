/**
 * 
 * Mobile Rich-media Ad Interface Definitions (MRAID) v.1.0
 * 
 * The Interactive Advertising Bureau("IAB"), its members and other significant contributors 
 * joined together to create this document, a standard interface specification for mobile rich 
 * media ads. The goal of the Mobile Rich-media Ad Interface Definition (MRAID) project is to 
 * address known interoperability issues between publisher mobile applications, different ad 
 * servers and different rich media platforms.
 * 
 */
function() {
    window.mraid = new function() {
        this.getVersion = function() {
            return "1.0"
        };
		
        this.expandProperties = {
            width: 0,
            height: 0,
            useCustomClose: !1
        };
        this.state = "loading";
        this.placementType = null;
        this.listeners = {};
        this.viewable = !0;
        
        
        this.FEATURES = {
        	SMS         	:'sms',
        	PHONE       	:'phone',
        	EMAIL       	:'email',
        	CALENDAR    	:'calendar',
        	STOREPICTURE	:'storePicture',
			INLINEVIDEO     :'inlineVideo'
    	}; 	
    	this.EVENTS = {
        	INFO                :'info',
        	ORIENTATIONCHANGE   :'orientationChange',
			READY               :'ready',
        	ERROR               :'error',
        	STATECHANGE         :'stateChange',
			VIEWABLECHANGE      :'viewableChange',
			CALENDAREVENTADDED  :'calendarEventAdded',
			PICTUREADDED        :'pictureAdded',
        	SIZECHANGE          :'sizeChange'
    	}; 
        
        
        
        
        
        
        
		/**
		 * 
		 * 
		 * @parameter a 
		 *				the state of SDK, should be one of : "loading","default","expanded","hidden".
		 * @parameter b 
		 *				viewable true: container is on-screen and viewable by the user; false: container is off-screen and not viewable.
		 * @parameter c
		 *				For efficiency, ad designers sometimes flight a single piece of creative in both banner and interstitial placements. 
		 *				So that the creative can be aware of its placement, and therefore potentially behave differently, each ad container 
		 *				has a placement type determining whether the ad is being displayed inline with content (i.e. a banner) or as an interstitial 
		 *				overlaid content (e.g. during a content transition). The SDK returns the value of the placement to creative so that creative 
		 *				can behave differently as necessary. The SDK does not determine whether a banner is an expandable (the creative does) and 
		 *				thus does not return a separate type for expandable. the placementType should be one of : "inline", "interstitial", "unknown"
		 *				inline 		 --- the ad placement is inline with content (i.e. a banner) in the display
		 *				interstitial --- the ad placement is over laid on top of content
		 * @parameter d
		 *				width : integer width of creative in pixels, default is full screen width	
		 * @parameter e
		 *				height : integer height of creative in pixels, default is full screen height. 
		 *				Note that when getting the expand properties before setting them, the values for 
		 *				width and height will reflect the actual values of the screen. This will allow ad 
		 *				designers who want to use application or device values to adjust as necessary.
		 * @parameter g
		 *				useCustomClose : boolean true, SDK will stop showing default close graphic and rely 
		 * 				on ad creative custom close indicator; false (default), SDK will display the default 
		 *				close graphic. This property has exactly the same function as the useCustomClose method (described below), 
		 *				and is provided as a convenience for creators of expandable ads.
		 * @parameter h
		 *				isModal : boolean true, the SDK is providing a modal container for the expanded ad; 
		 *				false, the SDK is not providing a modal container for the expanded ad; this property 
		 *				is read-only and cannot be set by the ad designer
		 */
        this._Update = function(a, b, c, d, e, g, h) {
            var f = this.state,
            i = b != this.viewable;
            this.state = a;
            this.viewable = b;
            this.placementType = c;
            this.expandProperties = {
                width: d,
                height: e,
                useCustomClose: g,
                isModal: h
            };
            a != f && ("loading" == f && this.dispatchEvent("ready"), this.dispatchEvent("stateChange", a));
            i && this.dispatchEvent("viewableChange", b)
        };
        this._log = function(a) {
            try {
                YD_LOGGER.log(a)
            } catch(b) {
                void 0 !== window.console && window.console.log(a + ", error:" + b)
            }
        };
        this._debug = function(a) {
            try {
                YD_LOGGER.debug(a)
            } catch(b) {
                void 0 !== window.console && window.console.log(a + ", error:" + b)
            }
        };
        this.addEventListener = function(a, b) {
            void 0 === this.listeners[a] && (this.listeners[a] = []);
            this.listeners[a].push(b)
        };
        this.removeEventListener = function(a, b) {
            if (this.listeners[a] instanceof Array) {
                var c = this.listeners[a], d, e;
                if (null !== c) {
                    e = c.length;
                    for (d = 0; d < e; d++) 
						if (c[d] === b) {
							c.splice(d, 1);
							break
						}
                }
            }
        };
        this.dispatchEvent = function(a) {
            if (void 0 !== a && this.listeners[a] instanceof Array) {
                var b = this.listeners[a], c, d;
                d = b.length;
                for (c = 0; c < d; c++) 
					try {
						b[c] && b[c].apply(this, Array.prototype.slice.call(arguments, 1))
					} catch(e) {
						this._debug("Exception in dispatchEvent:event=" + a + ", exception=" + e.toString())
					}
            }
        };
        this.onError = function(a, b) {
            this._debug("onError: message=" + a + ", action=" + b)
        };
        this.open = function(a) {
            try {
                YD_MRAID_RENDERER.open(a.toString())
            } catch(b) {
                this._debug("open, error:" + b)
            }
        };
        this.close = function() {
            try {
                YD_MRAID_RENDERER.close()
            } catch(a) {
                this._debug("close, error:" + a)
            }
        };
		/**
		 * 
		 * @parameter a(optional): 
		 *				The URL for the document to be displayed in a new overlay view. If null, the body of the current ad will be used in the current webview. 
		 */
        this.expand = function(a) {
            var b = this.expandProperties.width + ":" + this.expandProperties.height + ":" + this.expandProperties.useCustomClose;
            if (void 0 === a) 
				try {
					YD_MRAID_RENDERER.expand(b)
				} catch(c) {
					this._debug("expand, error:" + c)
				} 
			else 
				try {
					YD_MRAID_RENDERER.expand(a.toString(), b)
				} catch(d) {
					this._debug("expand(" + a + "), error:" + d)
				}
        };
		/**
		 * 
		 * @parameter a
		 *				true 	ad creative supplies its own designs for the close area
		 *				false	SDK default image should be displayed for the close area	
		 */
        this.useCustomClose = function(a) {
            a = !!a;
            if (a != this.expandProperties.useCustomClose) {
                this.expandProperties.useCustomClose = a;
                try {
                    YD_MRAID_RENDERER.useCustomClose(a ? "true": "false")
                } catch(b) {
                    this._debug("useCustomClose(" + a + "), error:" + b)
                }
            }
        };
        this.setExpandProperties = function(a) {
            this.useCustomClose( !! a.useCustomClose);
            this.expandProperties.width = "undefined" === typeof a || "undefined" === typeof a.width ? 0 : Math.round(a.width);
            this.expandProperties.height = "undefined" === typeof a || "undefined" === typeof a.height ? 0 : Math.round(a.height)
        };
        this.detachBridgeInterface = function() {
            this.open = this.close = this.expand = this.useCustomClose = this.setExpandProperties = function() {};
            this._log = this._debug = function() {}
        };
        this.getExpandProperties = function() {
            return this.expandProperties
        };
        this.isViewable = function() {
            return this.viewable
        };
        this.getState = function() {
            return this.state
        };
        this.getPlacementType = function() {
            return this.placementType
        };
        
        
        
        // ---------------------------------------- for 2.0
        /**
         * 
         */
		this.createCalendarEvent = function(params) {
			if (!supports[FEATURES.CALENDAR]) {
            	broadcastEvent(EVENTS.ERROR, 'Method not supported by this client.', 'createCalendarEvent');
        	} else if (!params || typeof date != 'object') {
            	broadcastEvent(EVENTS.ERROR, 'Valid params required.', 'createCalendarEvent');
        	} else {
            	YD_MRAID_RENDERER.createCalendarEvent(params);
        	}
    	};
    	
		this.getCurrentPosition = function() {
        	return clone(currentPosition); //@TODO
    	};
        
    	mraid.getDefaultPosition = function() {
        	return clone(defaultPosition);
    	};
		mraid.getResizeProperties = function() {
        	return clone(resizeProperties);
    	};
    	this.setResizeProperties = function(properties) {
        	if (valid(properties, resizePropertyValidators, 'setResizeProperties')) {
            	YD_MRAID_RENDERER.setResizeProperties(properties);
        	}
    	};
        this.getCurrentPosition = function() {
        	return clone(currentPosition); //@TODO
    	};  
    	this.getMaxSize = function() {
        	return clone(maxSize);
    	};
        this.getScreenSize = function() {
        	return clone(screenSize);
    	};
        
        this.playVideo = function(url) {
        	if (supports[FEATURES.INLINEVIDEO]) {
            	broadcastEvent(EVENTS.INFO, 'Inline video is available. playVideo will use native player.');
        	} 
			if (!url || typeof url != 'string') {
            	broadcastEvent(EVENTS.ERROR, 'Valid url required.', 'playVideo');
        	} else {
            	YD_MRAID_RENDERER.playVideo(url);
        	}
        };
        this.supports = function(feature) {
        	if (supports[feature]) {
            	return true;
        	} else {
            	return false;
        	}
    	};
        
        this.storePicture = function(url) {
                if (!supports['storePicture']) {
                        //broadcastEvent(EVENTS.ERROR, 'Method not supported by this client.', 'storePicture');
                        
                } else if (!url || typeof url !== 'string') {
                        
                        broadcastEvent(EVENTS.PICTUREADDED, false);
                } else {
                        mraidview.storePicture(url);
                }
        };
        
        
        
        
        
        this.addEventListener("error", this.onError)
    }
}
