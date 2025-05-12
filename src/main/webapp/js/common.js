//Config

let managerReload=900000; //15 mins  //5000;  // 1800000 == 30 mins
let retryInitialization=30000;
let reloadEntireFrameDelay= 14400000;   //should be 14400000 in live.
let doTopFrameReload= false;
let maxLogCharacters= 10000;
let frameWidth= 0;
let delayBeforeCheckingRHS= 1500;
let cachingDuration= 300000;
let loaded= true;
let retry_initialization_delay= 300000; // == 5 mins. Used in rhs.html in case initialization stalls.

let BASE_WEB_URL= "/FileServlet?uri=";

let logOn= false;
let logOff= false;


// Find out the display ID passed to the top frame, please note this is not robust.
// It just takes the value of the first parameter passed!


function getDisplayId() {
    return getParameter("display");
}

function getParameterForWindow(theWindow, param) {
	let urlquery=theWindow.location.href.split("?");
	if(urlquery == null || urlquery.length < 1) {
		return "undefined";
	}
	let pairs=urlquery[1].split("&");
	for(let i= 0; i < pairs.length; i++) {
	    let namevalue=pairs[i].split("=");
        name=namevalue[0];
	    let value=namevalue[1];
	    if(name == param) {
	        return value;
	    }
	}
	return null;
}

 function getParameter(param) {
    return getParameterForWindow(window.top,param);
 }

// Determine if logging is activated, ie the log window is ready and logging has not been explicitly turned off by the logging parameter
function isLogActive() {
    if(logOff) return false;
    if(logOn) return true;
    if(window.top.lhs && window.top.lhs.log && window.top.lhs.log.document &&  window.top.lhs.log.document.body &&  window.top.lhs.log.document.body.all['log'] && window.top.lhs.log.document.body.all['end'] ) {
        if(getParameter("logging") == "true") {
            logOn= true;
            return true;
        } else {
            logOff= true;
            return false;
        }
    }
    return false;
}

// Logs a message to wherever we're currently logging to.

function getCallingSourceFile() {
    try {
        throw new Error();
    } catch (e) {
        const stackLines = e.stack.split('\n');

        // The stack trace format is browser-dependent.
        // We skip the first line (message), then get the second meaningful call
        for (let i = 1; i < stackLines.length; i++) {
            const line = stackLines[i].trim();

            if (!line.includes("log") && !line.includes("error") && !line.includes("fail")) {
                // Example formats:
                // Chrome: at funcName (http://localhost:8080/js/myFile.js:10:15)
                // Firefox: funcName@http://localhost:8080/js/myFile.js:10:15
                const match = line.match(/(?:at\s+)?(?:.*\()?(.+\/([^/]+\.js)):\d+:\d+\)?$/);
                if (match && match[2]) {
                    return match[2]; // Just the filename
                }
            }
        }
    }
    return "unknown.js";
}

function log(message)
{
	const sourceFile = getCallingSourceFile();
	const fullMessage = `[${sourceFile}] ${message}`;
    console.log(fullMessage);
    window.status=fullMessage;
    if(isLogActive()) {
		let logEl= window.top.lhs.log.document.body.all['log'];
		logEl.innerHTML += "<br/><b>" + self.document.title+"</b> : "+(fullMessage);
		window.status=fullMessage;
		//Opera compatability issue.
		if(!is_opera) {
		    window.top.lhs.log.document.body.all['end'].scrollIntoView();
		}
		//Prune the log if larger than 5000 characters
		if(logEl.innerHTML.length > maxLogCharacters) {
		    //Knock off 1000 characters
		    logEl.innerHTML= logEl.innerHTML.substring(1000);
		}
    }

}

function error(message)
{
	const sourceFile = getCallingSourceFile();
	window.status = `[${sourceFile}] ${message}`;
	log("ERROR: " + message);
}

function fail(message)
{
	const sourceFile = getCallingSourceFile();
	top.document.clear();
	top.document.write("<h1 style='color:red'>[" + sourceFile + "] " + message + "<h1>");
}

function initializeDisplay()
{
    checkBrowser();

    let widthStr= getParameter("frameWidth");
    if(widthStr != null) {
        frameWidth= parseInt(widthStr);
    }
    top.mainFrameset.cols=frameWidth+"%,"+(100-frameWidth)+"%";

    let reload= getParameter("managerReloadMinutes");
    if(reload != null) {
        managerReload= parseInt(reload)*60*1000;
     }
    top.document.title= getDisplayId() + ' - XHIBIT - Public Displays';

    displayCopyright();
    displayConfiguration();
}


function displayConfiguration()
{
    log("-----------------------------------------------------------------------------------");
    log("-----------------------------------------------------------------------------------");
    log("--------------------------- Configuration -----------------------------------------");
    log("-----------------------------------------------------------------------------------");
    log("-- Client side caching of lists is set to a maximum of "+cachingDuration/1000+" seconds.");
    log("-- Manager reload is set at " + managerReload/1000 + " seconds.");
    log("-- Retrying of initialization is set at " + retryInitialization/1000 + " seconds.");
    if(doTopFrameReload)
    {
        log("-- The top frame will be reloaded after " + reloadEntireFrameDelay/1000 + " seconds.");
    }
    log("-- The log will be truncated to " + maxLogCharacters + " characters.");
    log("-- The LHS frame width (frameWidth) is set to "+frameWidth);
    log("-- The RHS will be checked to make sure it loaded okay after "+delayBeforeCheckingRHS/1000+" seconds.");
    log("-- The base web url is '"+BASE_WEB_URL+"'.");
    log("-- display="+ getParameter('display')+" (reqired)");
    log("-- ignoreBrowser="+ getParameter('ignoreBrowser')+" (defaults to false)");
    log("-- ignoreOS="+ getParameter('ignoreOS')+" (defaults to false)");
    log("-- logging="+ getParameter('logging')+" (defaults to false)");
    log("-----------------------------------------------------------------------------------");
    log("-----------------------------------------------------------------------------------");
    log("");
    log("");
}

function displayCopyright() {
    log("-----------------------------------------------------------------------------------");
    log("-----------------------------------------------------------------------------------");
    log("----------------------------- Public Displays  ------------------------------------");
    log("-----------------------------------------------------------------------------------");
    log("--------------------- (c) 2004 Electronic Data Systems ----------------------------");
    log("-----------------------------------------------------------------------------------");
    log("-----------------------------------------------------------------------------------");
}


//
// JavaScript Browser Sniffer
// This has copied over only the required functions from browser_sniffer.js
//
// convert all characters to lowercase to simplify testing
let agt=navigator.userAgent.toLowerCase();
let appVer = navigator.appVersion.toLowerCase();

// *** BROWSER VERSION ***

let is_minor = parseFloat(appVer);
let is_major = parseInt(is_minor);

let is_opera = (agt.indexOf("opera") != -1);
let is_opera2 = (agt.indexOf("opera 2") != -1 || agt.indexOf("opera/2") != -1);
let is_opera3 = (agt.indexOf("opera 3") != -1 || agt.indexOf("opera/3") != -1);
let is_opera4 = (agt.indexOf("opera 4") != -1 || agt.indexOf("opera/4") != -1);
let is_opera5 = (agt.indexOf("opera 5") != -1 || agt.indexOf("opera/5") != -1);
let is_opera6 = (agt.indexOf("opera 6") != -1 || agt.indexOf("opera/6") != -1); // new 020128- abk
let is_opera7 = (agt.indexOf("opera 7") != -1 || agt.indexOf("opera/7") != -1); // new 021205- dmr
let is_opera5up = (is_opera && !is_opera2 && !is_opera3 && !is_opera4);
let is_opera6up = (is_opera && !is_opera2 && !is_opera3 && !is_opera4 && !is_opera5); // new020128
let is_opera7up = (is_opera && !is_opera2 && !is_opera3 && !is_opera4 && !is_opera5 && !is_opera6); // new021205 -- dmr

// Note: On IE, start of appVersion return 3 or 4
// which supposedly is the version of Netscape it is compatible with.
// So we look for the real version further on in the string

let iePos  = appVer.indexOf('msie');
if (iePos !=-1) {
   is_minor = parseFloat(appVer.substring(iePos+5,appVer.indexOf(';',iePos)))
   is_major = parseInt(is_minor);
}

let is_ie   = ((iePos!=-1) && (!is_opera) /*&& (!is_khtml)*/);
let is_ie6   = (is_ie && is_major == 6);
let is_ie6up = (is_ie && is_minor >= 6);

// *** PLATFORM ***
let is_win   = ( (agt.indexOf("win")!=-1) || (agt.indexOf("16bit")!=-1) );
// NOTE: On Opera 3.0, the userAgent string includes "Windows 95/NT4" on all
//        Win32, so you can't distinguish between Win95 and WinNT.
let is_win95 = ((agt.indexOf("win95")!=-1) || (agt.indexOf("windows 95")!=-1));

// is this a 16 bit compiled version?
let is_win16 = ((agt.indexOf("win16")!=-1) ||
           (agt.indexOf("16bit")!=-1) || (agt.indexOf("windows 3.1")!=-1) ||
           (agt.indexOf("windows 16-bit")!=-1) );

let is_win31 = ((agt.indexOf("windows 3.1")!=-1) || (agt.indexOf("win16")!=-1) ||
                (agt.indexOf("windows 16-bit")!=-1));

let is_winme = (agt.indexOf("win 9x 4.90")!=-1);    // new 020128 - abk
let is_win2k = ((agt.indexOf("windows nt 5.0")!=-1) || (agt.indexOf("windows 2000")!=-1)); // 020214 - dmr
let is_winxp = ((agt.indexOf("windows nt 5.1")!=-1) || (agt.indexOf("windows xp")!=-1)); // 020214 - dmr

// NOTE: Reliable detection of Win98 may not be possible. It appears that:
//       - On Nav 4.x and before you'll get plain "Windows" in userAgent.
//       - On Mercury client, the 32-bit version will return "Win98", but
//         the 16-bit version running on Win98 will still return "Win95".
let is_win98 = ((agt.indexOf("win98")!=-1) || (agt.indexOf("windows 98")!=-1));
let is_winnt = ((agt.indexOf("winnt")!=-1) || (agt.indexOf("windows nt")!=-1));
let is_win32 = (is_win95 || is_winnt || is_win98 ||
                ((is_major >= 4) && (navigator.platform == "Win32")) ||
                (agt.indexOf("win32")!=-1) || (agt.indexOf("32bit")!=-1));


//
// End of browser_sniffer.js
//

function checkBrowser() {

   if(!is_ie6up && !is_opera7up && getParameter('ignoreBrowser') != "true") {
        fail("Only IE 6.0+ and Opera 7.0+ browsers are supported by this application; your browser was detected as '"+agt+"'. Set the parameter ignoreBrowser=true to overide.");
   }

   if(!is_winxp && !is_win2k && getParameter('ignoreOS') != "true") {
        fail("Application only tested and guaranteed on Windows XP / Windows 2000. Set the parameter ignoreOS=true to overide.");
   }

}

//
// String utility, trims text to null
//

function trimToNull(text) {
    if(text != null) {
        let trimed = text.replace( /^\s+/g, "" );  // NOSONAR
        trimed = trimed.replace( /\s+$/g, "" ); // NOSONAR
        if(trimed != "") {
            return trimed;
        }
    }
    return null;
}

//
// Method to find the correct frame from the name.
//

function getFrameElement(framePath){

  var frameItems;
  var frameElement = null;

  try {
    frameItems = framePath.split(".");

    if (frameItems.length > 0){
      frameElement = window.top[frameItems[0]];
    }

    if (frameItems.length > 1){
      for(index=1; index < frameItems.length; index++){
        frameElement = frameElement.contentDocument.getElementById(frameItems[index]);
      }
    }
  } catch(e){
    log("Error in common.getFrameElement()");
    log("framePath: " + framePath);
    frameElement = null;
  }

  log("getFrameElement: " + framePath);

  return frameElement;

}
