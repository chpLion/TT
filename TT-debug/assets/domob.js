// Domob JavaScript Interface
var domobBridge = window.domobBridge = {};

/**
 * 一次对多个资源发起缓存请求
 */
domobBridge.addAssets = function(imgs) {
	var call = 'domob://addAssets';
	var isFirstArgument = true;

	for (img in imgs) {
		var alias = img;
		var url = imgs[img];

		if (url === null) {
			continue;
		}

		if (isFirstArgument) {
			call += '?';
			isFirstArgument = false;
		} else {
			call += '&';
		}

		call += alias + '=' + encodeURIComponent(url);
	}

	location.href = call;
}
/**
 * 对一个资源发起一次缓存请求
 */
domobBridge.addAsset = function(alias, URL) {
	var img = {
		alias : URL
	};

	domobBridge.addAssets(img);
};

domobBridge.assetReady = function(alias, path) {
	var img = document.getElementById(alias);
	img.setAttribute("src", "file://" + path);
};

domobBridge.getSDKVersion = function() {
	return SDK_VERSION;
}

domobBridge.getDevice = function() {
	return DEVICE;
}

domobBridge.getOS = function() {
	return OS;
}

domobBridge.getOSVersion = function() {
	return OS_VERSION;
}

domobBridge.getCarrier = function() {
	return CARRIER;
}

domobBridge.getNetwork = function() {
	return NETWORK;
}