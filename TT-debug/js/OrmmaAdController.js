/*
 * Anonymous function to encapsulate the OrmmaAdController methods
 */


const ORMMA_STATE_UNKNOWN  = "unknown";
const ORMMA_STATE_HIDDEN   = "hidden";
const ORMMA_STATE_DEFAULT  = "default";
const ORMMA_STATE_EXPANDED = "expanded";
const ORMMA_STATE_RESIZED = "resized";

const ORMMA_EVENT_ERROR = "error";
const ORMMA_EVENT_HEADING_CHANGE = "headingChange";
const ORMMA_EVENT_KEYBOARD_CHANGE = "keyboardChange";
const ORMMA_EVENT_LOCATION_CHANGE = "locationChange";
const ORMMA_EVENT_NETWORK_CHANGE = "networkChange";
const ORMMA_EVENT_ORIENTATION_CHANGE = "orientationChange";
const ORMMA_EVENT_READY = "ready";
const ORMMA_EVENT_RESPONSE = "response";
const ORMMA_EVENT_SCREEN_CHANGE = "screenChange";
const ORMMA_EVENT_SHAKE = "shake";
const ORMMA_EVENT_SIZE_CHANGE = "sizeChange";
const ORMMA_EVENT_STATE_CHANGE = "stateChange";
const ORMMA_EVENT_TILT_CHANGE = "tiltChange";


(function() {

    /**
     * The main ad controller object
     */
    window.Ormma = {

        /**
         * The object that holds all types of OrmmaAdController events and associated listeners
         */
        events : [],

        /**
         * Holds the current dimension values
         */
        dimensions : {},

        /**
         * Holds the current property values
         */
        expandProperties :  {
                        "use-background":false,
                        "background-color" : "#000000",
                        "background-opacity" : 1.0,
                        "is-modal" : true},


                shakeProperties : {
                        "interval" : "10",
                        "intensity" : "20"
                },
                
                resizeProperties : { 
                        transition : ORMMA_STATE_UNKNOWN 
                },
                        
                state : ORMMA_STATE_DEFAULT,    
                lastState : ORMMA_STATE_DEFAULT,        

/**
         * Use this method to subscribe a specific handler method to a specific
         * event. In this way, multiple listeners can subscribe to a specific event, 
         * and a single listener can handle multiple events. The events are:
         *   
         * <table>
         *   <tr><td>ready</td><td>report initialize complete</td></tr>
         *   <tr><td>network</td><td>report network connectivity changes</td></tr>
         *   <tr><td>keyboard</td><td>report soft keyboard changes</td></tr>
         *   <tr><td>orientation</td><td>report orientation changes</td></tr>
         *   <tr><td>heading</td><td>report heading changes</td></tr>
         *   <tr><td>location</td><td>report location changes</td></tr>
         *   <tr><td>rotation</td><td>report rotation changes</td></tr>
         *   <tr><td>shake</td><td>report device being shaken</td></tr>
         *   <tr><td>state</td><td>report state changes</td></tr>
         *   <tr><td>tilt</td><td>report tilt changes</td></tr>
         * </table>
         *
         * <br/>#side effects: registering listeners for device features may power 
         *                     up sensors in the device that will reduce the device
         *                     battery life. 
         * <br/>#ORMMA Level: 1 
         *
         * @param {String} event name of event to listen for
         * @param {Function} listener function name / anonymous function to execute 
         */
        addEventListener : function( evt, listener ) {
                window.addEventListener( evt, listener, false );        
                if (evt == ORMMA_EVENT_LOCATION_CHANGE){
                _startLocationListener();
        }

                if (evt == ORMMA_EVENT_TILT_CHANGE){
                _startTiltListener();
        }

                if (evt == ORMMA_EVENT_SHAKE){
                _startShakeListener();
        }        

                if (evt == ORMMA_EVENT_ORIENTATION_CHANGE){
                _startOrientationListener();
        }        

                if (evt == ORMMA_EVENT_NETWORK_CHANGE){
                _startNetworkListener();
        }        

                if (evt == ORMMA_EVENT_HEADING_CHANGE){
                _startHeadingListener();
        }        

        
        },
        
        removeEventListener : function( evt, listener ) {
                // notify the native API that the appropriate sensor should be 
                // brough on-line
                // now remove the actual listener
                if (evt == ORMMA_EVENT_LOCATION_CHANGE){
                _stopLocationListener();
        }
                if (evt == ORMMA_EVENT_TILT_CHANGE){
                _stopTiltListener();
        }
        if (evt == ORMMA_EVENT_SHAKE){
                _stopShakeListener();
        }                
                if (evt == ORMMA_EVENT_ORIENTATION_CHANGE){
                _stopOrientationListener();
        }        
                if (evt == ORMMA_EVENT_NETWORK_CHANGE){
                _stopNetworkListener();
        }        
                if (evt == ORMMA_EVENT_HEADING_CHANGE){
                _stopHeadingListener();
        }        
                window.removeEventListener( evt, listener, false );
        },
        
        



                expand : function (dimensions, URL){

                        this.dimensions = dimensions;
                        _expand(dimensions, URL, this.expandProperties);
                        var data = { dimensions : dimensions,
                                         properties : this.expandProperties };
            fireEvent('sizeChange', data);
            fireEvent('stateChange', ORMMA_STATE_HIDDEN);                       
                },

                unexpand : function (){
            fireEvent('stateChange', ORMMA_STATE_DEFAULT);                      
                },


        /**
         * resize resizes the display window
         * @param {Object} dimensions The new dimension values of the window
         * @returns nothing
         */
        resize : function (width, height) {

//            this.dimensions = dimensions;

            _resize(width, height);

            var data = { dimensions : {width : width, height: height},
                                         properties : this.expandProperties };
            fireEvent(ORMMA_EVENT_SIZE_CHANGE, data);
            fireEvent(ORMMA_EVENT_STATE_CHANGE, ORMMA_STATE_RESIZED);
        },





        /**
         * reset the window size to the original state
         * @param {Function} listener The listener function
         * @returns nothing
         */
        close : function () {

            _close();
            fireEvent(ORMMA_EVENT_STATE_CHANGE, ORMMA_STATE_DEFAULT);
        },

        open : function (URL, controls) {
            _open(URL, controls);
            fireEvent(ORMMA_EVENT_STATE_CHANGE, ORMMA_STATE_DEFAULT);
        },


        
        /**
         * Use this method to get the available size of the local cache.
         * @param none
         * @returns available size of local cache
         */
                cacheRemaining : function() {
                                return _cacheRemaining();
                },

        /**
         * Use this method to hide the web viewer.
         * @param none
         * @returns nothing
         */
                hide : function() {
                        _hide();
                        fireEvent(ORMMA_EVENT_STATE_CHANGE, ORMMA_STATE_HIDDEN);
                },

                show : function() {
                        _show();
                        fireEvent(ORMMA_EVENT_STATE_CHANGE, this.lastState);
                },
        /**
         * Use this method to get the current state of the web viewer. 
         * @param none
         * @returns boolean reflecting visible state
         */             
                getState : function() {
                        return this.state;
                },
                
                setState : function(state) {
                        this.state = state;
                },


                getHeading: function() {
                        return _getHeading();
                },

                getLocation: function() {
                        return _getLocation();
                },

                getNetwork: function() {
                        return _getNetwork();
                },

                getTilt: function() {
                        return _getTilt();
                },

                gotTiltChange: function(change){
                        fireEvent(ORMMA_EVENT_TILT_CHANGE, change);
                },

                gotShake: function(change){
                        fireEvent(ORMMA_EVENT_SHAKE, change);
                },

                gotOrientationChange: function(change){
                        fireEvent(ORMMA_EVENT_ORIENTATION_CHANGE, change);
                },

                gotNetworkChange: function(change){
                        fireEvent(ORMMA_EVENT_NETWORK_CHANGE, change);
                },

                getOrientation: function() {
                        return _getOrientation();
                },

                getResizeDimensions: function() {
                        return dimensions;
                },

                getExpandProperties: function() {
                        return this.expandProperties;
                },

                getScreenSize: function() {
                        return _getScreenSize();
                },

                getSize: function() {
                        return _getSize();
                },

                getShakeProperties: function() {
                        return _getShakeProperties();
                },


                getMaxSize: function() {
                        return _getMaxSize();
                },

                
                locationChanged: function(loc){
                //      location = eval('(' + loc +')');
                //      alert(loc);
                        fireEvent(ORMMA_EVENT_LOCATION_CHANGE, loc);
                },
                
                gotHeadingChange: function(heading){
                        fireEvent(ORMMA_EVENT_HEADING_CHANGE, heading);
                },

                supports: function(feature) {
                        return _supports(feature);
                
                },

                getResizeProperties: function() {
                        return this.resizeProperties;
                
                },

                setResizeProperties: function(properties) {
                        this.resizeProperties = properties;
                
                },
                getExpandProperties: function() {
                        return this.expandProperties;
                
                },

                setExpandProperties: function(properties) {
                        this.expandProperties = properties;
                
                },
                
                ready: function(){
                        fireEvent(ORMMA_EVENT_READY);
                },
                
                fireError: function(action, message){
                        var data = { message : message,
                                         action : action };
                        fireEvent(ORMMA_EVENT_ERROR, data);
                },
                
                sendSMS: function(recipient, body){
                        _sendSMS(recipient, body);
                },
                
                sendMail: function(recipient, subject, body){
                        _sendMail(recipient, subject, body);
                },
                
                makeCall: function(number){
                        _makeCall(number);
                },
                
                createEvent: function(date, title, body){
                        _createEvent(date, title, body);
                }
                
                
                
                
                
                


        };
    /**
     * The private methods
     */


   
    /**
     * fireEvent fires an event
     * @private
     * @param {String} event The event name
     * @param {Object} additional information about the event
     * @returns nothing
     */
    function fireEvent (name, data) {
        
        var event;
                
                if (name == ORMMA_EVENT_STATE_CHANGE) {
                        Ormma.lastState = Ormma.state;
                        Ormma.state = data;
                }
                                
                
                try {
                if ( data == null ) {
                        event = document.createEvent( "Event" );
                        event.initEvent( name, true, true );
                }
                else {
                        event = document.createEvent( "MessageEvent" );
                        if ((typeof data) == 'object'){
                                event.initMessageEvent( name, true, true, JSON.stringify(data), null, 0, null, null );
                        }
                        else {
                                event.initMessageEvent( name, true, true, data, null, 0, null, null );
                        }                       
                }
                } catch ( e ) {
                        alert( "sendOrmmaEvent: " + e );
                }
                window.dispatchEvent( event ); 
    }

    /* implementations of public methods for specific vendors */

    function _expand(dimensions, URL, properties) {
        ORMMADisplayControllerBridge.expand(JSON.stringify(dimensions), URL, JSON.stringify(properties));
    }

    function _open(URL, controls) {
        ORMMADisplayControllerBridge.open(URL);
    }


    function _resize (width, height) {
        ORMMADisplayControllerBridge.resize(width, height);
    }

    function _close () {
        ORMMADisplayControllerBridge.close();
    }

        function _cacheRemaining() {
                return ORMMAAssetsControllerBridge.cacheRemaining();
        }

        function _hide() {
                ORMMADisplayControllerBridge.hide();
        }

        function _show() {
                ORMMADisplayControllerBridge.show();
        }


        function _addAsset(alias, uri) {
                ORMMAAssetsControllerBridge.addAsset(alias, uri);
        }

        function _removeAsset(alias) {
                ORMMAAssetsControllerBridge.removeAsset(alias);
        }

        function _getHeading() {
                return ORMMASensorControllerBridge.getHeading();
        }

        function _getLocation() {
                return eval('('+ORMMALocationControllerBridge.getLocation()+')');
        }

        function _getNetwork() {
                return ORMMANetworkControllerBridge.getNetwork();
        }

        function _getTilt() {
                return eval('('+ORMMASensorControllerBridge.getTilt()+")");
        }


        function _getOrientation() {
                return ORMMADisplayControllerBridge.getOrientation();
        }

        
        function _startLocationListener(){
                ORMMALocationControllerBridge.startLocationListener();
        }

        function _stopLocationListener(){
                ORMMALocationControllerBridge.stopLocationListener();
        }

        function _startTiltListener(){
                ORMMASensorControllerBridge.startTiltListener();
        }

        function _stopTiltListener(){
                ORMMASensorControllerBridge.stopTiltListener();
        }
        
        function _startHeadingListener(){
                ORMMASensorControllerBridge.startHeadingListener();
        }

        function _stopHeadingListener(){
                ORMMASensorControllerBridge.stopHeadingListener();
        }

        function _startShakeListener(){
                ORMMASensorControllerBridge.startShakeListener();
        }

        function _stopShakeListener(){
                ORMMASensorControllerBridge.stopShakeListener();
        }

        function _startOrientationListener(){
                ORMMADisplayControllerBridge.startOrientationListener();
        }

        function _stopOrientationListener(){
                ORMMADisplayControllerBridge.stopOrientationListener();
        }

        function _startNetworkListener(){
                ORMMANetworkControllerBridge.startNetworkListener();
        }

        function _stopNetworkListener(){
                ORMMANetworkControllerBridge.stopNetworkListener();
        }


        function _getScreenSize() {
                return eval('('+ORMMADisplayControllerBridge.getScreenSize()+')');
        }
        
        function _getMaxSize(){
                return eval('('+ORMMADisplayControllerBridge.getMaxSize()+')');
        }

        function _getSize(){
                return eval('('+ORMMADisplayControllerBridge.getSize()+')');
        }

        function _supports(feature){
                return ORMMAUtilityControllerBridge.supports(feature);
        }

        function _sendSMS(recipient, body){
                ORMMAUtilityControllerBridge.sendSMS(recipient, body);
        }
                
        function _sendMail(recipient, subject, body){
                alert("sendMail");
                ORMMAUtilityControllerBridge.sendMail(recipient, subject, body);
        } 
                
        function _makeCall(number){
                ORMMAUtilityControllerBridge.makeCall(number);
        }
                
        function _createEvent(date, title, body){
                var msecs=(date.getTime()-date.getMilliseconds())/1000;
                alert(msecs.toString());
                ORMMAUtilityControllerBridge.createEvent(msecs.toString(), title, body);
        }


})();
//      alert("injected");
                ORMMAUtilityControllerBridge.ready();