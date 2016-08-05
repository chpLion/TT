/*  Copyright (c) 2011 The ORMMA.org project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */


(function() {

   var ormmaview = window.ormmaview = {};

 
 
   /****************************************************/
   /********** PROPERTIES OF THE ORMMA BRIDGE **********/
   /****************************************************/
 
   /** Expand Properties */
   var expandProperties = {
        useBackground:false,
        backgroundColor:'#ffffff',
        backgroundOpacity:1.0,
        lockOrientation:false
    };
 
 
   /** The set of listeners for ORMMA Native Bridge Events */
   var listeners = { };
 
   /** Holds the current dimension values */
   dimensions : {};
        
   /** A Queue of Calls to the Native SDK that still need execution */
   var nativeCallQueue = [ ];
 
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
    * @returns string, "OK"
    */
   ormmaview.fireAssetReadyEvent = function( alias, URL ) {
      var handlers = listeners["assetReady"];
      if ( handlers != null ) {
         for ( var i = 0; i < handlers.length; i++ ) {
            handlers[i]( alias, URL );
         }
      }
 
      return "OK";
   };
 
 
   /**
    * Called by the JAVA SDK when an asset has been removed from the
	* cache at the request of the creative.
    *
    * @returns string, "OK"
    */
   ormmaview.fireAssetRemovedEvent = function( alias ) {
      var handlers = listeners["assetRemoved"];
      if ( handlers != null ) {
         for ( var i = 0; i < handlers.length; i++ ) {
            handlers[i]( alias );
         }
      }
 
      return "OK";
   };
 
 
   /**
    * Called by the JAVA SDK when an asset has been automatically
	* removed from the cache for reasons outside the control of the creative.
    *
    * @returns string, "OK"
    */
   ormmaview.fireAssetRetiredEvent = function( alias ) {
      var handlers = listeners["assetRetired"];
      if ( handlers != null ) {
         for ( var i = 0; i < handlers.length; i++ ) {
            handlers[i]( alias );
         }
      }
 
      return "OK";
   };
 
 
   /**
	* Called by the JAVA SDK when various state properties have changed.
    *
    * @returns string, "OK"
	*/
   ormmaview.fireChangeEvent = function( properties ) {
      var handlers = listeners["change"];
      if ( handlers != null ) {
         for ( var i = 0; i < handlers.length; i++ ) {
		    handlers[i]( properties );
         }
      }
 
      return "OK";
   };
 
 
   /**
    * Called by the JAVA SDK when an error has occured.
    *
    * @returns string, "OK"
    */
   ormmaview.fireErrorEvent = function( message, action ) {
      var handlers = listeners["error"];
      if ( handlers != null ) {
         for ( var i = 0; i < handlers.length; i++ ) {
            handlers[i]( message, action );
         }
      }
 
      return "OK";
   };
 
 
   /**
    * Called by the JAVA SDK when the user shakes the device.
    *
    * @returns string, "OK"
    */
   ormmaview.fireShakeEvent = function() {
      var handlers = listeners["shake"];
      if ( handlers != null ) {
         for ( var i = 0; i < handlers.length; i++ ) {
            handlers[i]();
         }
      }
 
      return "OK";
   };
 
 
   
 
 
   /**
    *
    */
   ormmaview.showAlert = function( message ) {
      ORMMAUtilityControllerBridge.showAlert( message );
   };
 
 
   /*********************************************/
   /********** INTERNALLY USED METHODS **********/
   /*********************************************/
 
 
   /**
    *
    */
   ormmaview.zeroPad = function( number ) {
      var text = "";
      if ( number < 10 ) {
         text += "0";
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
   ormmaview.activate = function( event ) {
   			alert( ORMMAUtilityControllerBridge+", "+event );
   		 ORMMAUtilityControllerBridge.activate(event);
   };

 
   /**
    *
    */
   ormmaview.addEventListener = function( event, listener ) {
      var handlers = listeners[event];
	  if ( handlers == null ) {
		 // no handlers defined yet, set it up
         listeners[event] = [];
         handlers = listeners[event];
      }
 
      // see if the listener is already present
	  for ( var handler in handlers ) {
	     if ( listener == handler ) {
		    // listener already present, nothing to do
			return;
		}
	  }
 
      // not present yet, go ahead and add it
      handlers.push( listener );
   };


   /**
    *
    */
   ormmaview.deactivate = function( event ) {
      ORMMAUtilityControllerBridge.deactivate(event);
   };

 
   /**
    *
    */
   ormmaview.removeEventListener = function( event, listener ) {
	  var handlers = listeners[event];
	  if ( handlers != null ) {
         handlers.remove( listener );
	  }
   };
 

 
   /*****************************/
   /********** LEVEL 1 **********/
   /*****************************/

   /**
    *
    */
   ormmaview.close = function() {
   try {
   	  ORMMADisplayControllerBridge.close();
	  } catch ( e ) {
	     ormmaview.showAlert( "close: " + e );
	  }
   };
 
 
   /**
    *
    */
   ormmaview.expand = function( dimensions, URL ) {
	  try {
		 this.dimensions = dimensions;
		 ORMMADisplayControllerBridge.expand(ormmaview.stringify(dimensions), URL, ormmaview.stringify(expandProperties));
	  } catch ( e ) {
	     ormmaview.showAlert( "executeNativeExpand: " + e + ", dimensions = " + dimensions  + ", URL = " + URL + ", expandProperties = " + expandProperties);
	  }
   };

 
   /**
    *
    */
   ormmaview.hide = function() {
   try {
	  ORMMADisplayControllerBridge.hide();
	  } catch ( e ) {
	     ormmaview.showAlert( "hide: " + e );
	  }
   };

 
   /**
    *
    */
   ormmaview.open = function( URL, controls ) {
	  // the navigation parameter is an array, break it into its parts
	  var back = false;
	  var forward = false;
	  var refresh = false;
	  if ( controls == null ) {
		 back = true;
		 forward = true;
		 refresh = true;
	  }
	  else {
		 for ( var i = 0; i < controls.length; i++ ) {
			if ( ( controls[i] == "none" ) && ( i > 0 ) ) {
			   // error
			   self.fireErrorEvent( "none must be the only navigation element present.", "open" );
			   return;
			}
			else if ( controls[i] == "all" ) {
			   if ( i > 0 ) {
				   // error
				   self.fireErrorEvent( "none must be the only navigation element present.", "open" );
				   return;
				}
				
				// ok
				back = true;
				forward = true;
				refresh = true;
			}
			else if ( controls[i] == "back" ) {
				back = true;
			}
			else if ( controls[i] == "forward" ) {
				forward = true;
			}
			else if ( controls[i] == "refresh" ) {
				refresh = true;
			}
	     }
	  }
	
	 try{
	  ORMMADisplayControllerBridge.open(URL, back, forward, refresh);
   		} catch ( e ) {
	     ormmaview.showAlert( "open: " + e );
	  }
   
   };
   
   /**
   *
   */
  ormmaview.openMap = function( POI, fullscreen ) {
      try{
    	  ORMMADisplayControllerBridge.openMap(POI, fullscreen);
      } catch ( e ) {
	     ormmaview.showAlert( "openMap: " + e );
	  }
  };

   
  /**
  *
  */
  ormmaview.playAudio = function( URL, properties ) {
	  
	  alert('playAudio');
	
	var autoPlay = false, controls = false, loop = false, position = false, 
	    startStyle = 'normal', stopStyle = 'normal';
	 
    if ( properties != null ) {
        
        if ( ( typeof properties.autoplay != "undefined" ) && ( properties.autoplay != null ) ) {
            autoPlay = true;
        }
       
        if ( ( typeof properties.controls != "undefined" ) && ( properties.controls != null ) ) {
        	controls = true;
        }
        
        if ( ( typeof properties.loop != "undefined" ) && ( properties.loop != null ) ) {
        	loop = true;
        }
        
        if ( ( typeof properties.position != "undefined" ) && ( properties.position != null ) ) {
        	position = true;
        }
        
        //TODO check valid values...           
        
        if ( ( typeof properties.startStyle != "undefined" ) && ( properties.startStyle != null ) ) {
             startStyle = properties.startStyle;
        }
        
        if ( ( typeof properties.stopStyle != "undefined" ) && ( properties.stopStyle != null ) ) {
            stopStyle = properties.stopStyle;
        }  
        
        if(startStyle =='normal') {
        	position = true;
        }
        
 		 if(position) {
       		autoPlay = true;
       		controls = false;
       		loop = false;
       		stopStyle = 'exit';
       	}

       	if(loop) {
           stopStyle = 'normal'; 
           controls = true;
        }
        
        if(!autoPlay) {
        	controls = true;
        }
               	
       	if (!controls) {
			stopStyle = 'exit';
       }
    }  
    
    try{
  	  ORMMADisplayControllerBridge.playAudio(URL, autoPlay, controls, loop, position, startStyle, stopStyle);
    } 
    catch ( e ) {
	     ormmaview.showAlert( "playAudio: " + e );
	}     
 };
 
 
  /**
   *
   */
  ormmaview.playVideo = function( URL, properties ) {
	 var audioMuted = false, autoPlay = false, controls = false, loop = false, position = [-1, -1, -1, -1], 
	    startStyle = 'normal', stopStyle = 'normal';
     if ( properties != null ) {
         
         if ( ( typeof properties.audio != "undefined" ) && ( properties.audio != null ) ) {
             audioMuted = true;
         }
         
         if ( ( typeof properties.autoplay != "undefined" ) && ( properties.autoplay != null ) ) {
             autoPlay = true;
         }
        
         if ( ( typeof properties.controls != "undefined" ) && ( properties.controls != null ) ) {
         	controls = true;
         }
         
         if ( ( typeof properties.loop != "undefined" ) && ( properties.loop != null ) ) {
         	loop = true;
         }
         
         if ( ( typeof properties.position != "undefined" ) && ( properties.position != null ) ) {
        	 inline = new Array(4);
        	 
        	 inline[0] = properties.position.top;
        	 inline[1] = properties.position.left;
        	 
             if ( ( typeof properties.width != "undefined" ) && ( properties.width != null ) ) {
            	 inline[2] =  properties.width;
             }
             else{
                 //TODO ERROR
             }
             
             if ( ( typeof properties.height != "undefined" ) && ( properties.height != null ) ) {
            	 inline[3] =  properties.height;
             }
             else{
                 //TODO ERROR
             }
         }
       

         if ( ( typeof properties.startStyle != "undefined" ) && ( properties.startStyle != null ) ) {
             startStyle = properties.startStyle;
         }
        
         if ( ( typeof properties.stopStyle != "undefined" ) && ( properties.stopStyle != null ) ) {
            stopStyle = properties.stopStyle;
         }  
         
		if (loop) {
			stopStyle = 'normal';
			controls = true;
		}

	    if (!autoPlay)
	        controls = true;
		        
	  	if (!controls) {
			stopStyle = 'exit';
		} 
		
		if(position[0]== -1 || position[1] == -1)   {
			startStyle = "fullscreen";
		}      
     }    
     
     try{
     	  ORMMADisplayControllerBridge.playVideo(URL, audioMuted, autoPlay, controls, loop, position, startStyle, stopStyle);
       } 
       catch ( e ) {
   	     ormmaview.showAlert( "playVideo: " + e );
   	}     

  };

   
   
 
   /**
    *
    */
   ormmaview.resize = function( width, height ) {
   try {
	  ORMMADisplayControllerBridge.resize(width, height);
	  } catch ( e ) {
	     ormmaview.showAlert( "resize: " + e );
	  }
   };

   
   ormmaview.getExpandProperties = function(){
	   return expandProperties;
   }
   
 
   /**
    *
    */
   ormmaview.setExpandProperties = function( properties ) {
	  expandProperties = properties;
   };

 
   /**
    *
    */
   ormmaview.show = function() {
   try{
	  ORMMADisplayControllerBridge.show();
	  } catch ( e ) {
	     ormmaview.showAlert( "show: " + e );
	  }
   };
 
 
 
   /*****************************/
   /********** LEVEL 2 **********/
   /*****************************/

   /**
    *
    */
   ormmaview.createEvent = function( date, title, body ) {
      	var msecs=(date.getTime()-date.getMilliseconds());

		try {		
		ORMMAUtilityControllerBridge.createEvent(msecs.toString(), title, body);
		} catch ( e ) {
	     ormmaview.showAlert( "createEvent: " + e );
	  }
		
   };
 
   /**
    *
    */
   ormmaview.makeCall = function( phoneNumber ) {
   try {
	  ORMMAUtilityControllerBridge.makeCall(phoneNumber);
	  } catch ( e ) {
	     ormmaview.showAlert( "makeCall: " + e );
	  }
   };
 
 
   /**
    *
    */
   ormmaview.sendMail = function( recipient, subject, body ) {
   try {
	  ORMMAUtilityControllerBridge.sendMail(recipient, subject, body);
	  } catch ( e ) {
	     ormmaview.showAlert( "sendMail: " + e );
	  }
   };
 

   /**
    *
    */
   ormmaview.sendSMS = function( recipient, body ) {
   try {
	  ORMMAUtilityControllerBridge.sendSMS(recipient, body);
	  } catch ( e ) {
	     ormmaview.showAlert( "sendSMS: " + e );
	  }
   };
 
   /**
    *
    */
   ormmaview.setShakeProperties = function( properties ) {
   };
 
 
 
   /*****************************/
   /********** LEVEL 3 **********/
   /*****************************/

   /**
    *
    */
   ormmaview.addAsset = function( URL, alias ) {
	 
   };
   /**
    *
    */
   ormmaview.request = function( URI, display ) {
	  
   }; 
   /**
    *
    */
   ormmaview.removeAsset = function( alias ) {
   };
   
   
   ormmaview.stringify = function(args) {
    if (typeof JSON === "undefined") {
        var s = "";
        var len = args.length;
        var i;
        if (typeof len == "undefined"){
        	return ormmaview.stringifyArg(args);
        }
        for (i = 0; i < args.length; i++) {
            if (i > 0) {
                s = s + ",";
            }
            s = s + ormmaview.stringifyArg(args[i]);
        }
        s = s + "]";
        return s;
    } else {
        return JSON.stringify(args);
    }
};

	ormmaview.stringifyArg = function(arg) {
        var s, type, start, name, nameType, a;
            type = typeof arg;
            s = "";
            if ((type === "number") || (type === "boolean")) {
                s = s + args;
            } else if (arg instanceof Array) {
                s = s + "[" + arg + "]";
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
                        if ((nameType === "number") || (nameType === "boolean")) {
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
        ormmaview.showAlert("json:"+ s);
		return s;
	}
	
	alert( "ORMMAUtilityControllerBridge="+ORMMAUtilityControllerBridge );
	ORMMAUtilityControllerBridge.ready();
   
   })();
