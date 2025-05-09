// Document scrolling classes and functions
//
// author: Rakesh Lakhani & Neil Ellis

var thisDisplayDocument = null; //The main rotating part of a given display document.
var frameVisible= true; //This is turned off by the rotation process in rotator.js

/*
 * ScrollingDocument class
 *
 * This is a document which potentially contains multiple pages which are scolled through
 * by a controller.
 *
 * author: Rakesh Lakhani & Neil Ellis
 */

function ScrollingDocument(_scroller, _displayArea, _table)
{
    this.table= _table;
    this.scroller= _scroller;
    this.displayArea= _displayArea;

    this.finished = false;
    this.pageCount = 1;
    this.currentPage = 1;
    this.pages = new Array(0);

    this.calculatePages = ScrollingDocument_calculatePages;
    this.move = ScrollingDocument_move;
    this.final = ScrollingDocument_final;
    this.start = ScrollingDocument_start;
    this.reset= ScrollingDocument_reset;
    this.cycle= ScrollingDocument_cycle;

    this.calculatePages();
}


function ScrollingDocument_cycle() {
    if(frameVisible == false) {
        log("Frame not visible so cycle() not active.");
        return;
    }

    if(this.finished) {
        this.reset();
    } else {
        this.move();
    }
}

/*
 * This method is useful if the document is to be cached, it basically resets the paging to the beginning.
 */
function ScrollingDocument_reset() {
    this.currentPage=1;
    this.finished= false;
    this.pages = new Array(0);
    this.calculatePages();
    this.move();
}

/*
 * The ScrollingDocument precalculates the dimensions of each page and the number of them.
 * These are then stored ready to be displayed sequentially.
 */

function ScrollingDocument_calculatePages()
{

    // The convoluted logic is due to the fact that we cannot correctly
    // calcultate the bottom of the elements but we can work out the top. So
    // we look ahead to the first element of the next screen to find it's
    // top when we need the bottom of this page!!!

    var allRows = this.table.tBodies[0].rows;
    //log("Calculating page information from document.");

    var border= 2;
    var scrollByAmount= this.scroller.offsetHeight;
    var pageCount = 0;
    var currentLine = 0;
    var finished = false;

    log("ScrollByAmount:" + scrollByAmount);
    while(currentLine < allRows.length && !finished) {
        log("Current line "+currentLine);
        // Remember the top line
        var oldPosition = currentLine;
        // calculate the maximum visible offset
        var maxCanScroll = allRows[currentLine].offsetTop + scrollByAmount;
        var cropSize;
        log("MaxCanScroll:" + maxCanScroll);

        //While there is a next row and the top of the next row is within our scrollLimit go to the next row
        while(currentLine+1 < allRows.length && allRows[currentLine+1].offsetTop <= maxCanScroll)
        {
            log("Line: " + currentLine + " top " + allRows[currentLine+1].offsetTop );
            currentLine++;
        }
        if(currentLine == allRows.length-1)
        {
            // we are at the last row, check if it fits.
            if(this.displayArea.offsetHeight <= maxCanScroll)
            {
                cropSize = scrollByAmount;
                finished = true;
                log("1. cropSize=" + cropSize);
            }
            else
            {
                cropSize = allRows[currentLine].offsetTop - allRows[oldPosition].offsetTop+border;
                log("2. cropSize=" + cropSize);
            }
        }
        else
        {
            cropSize = allRows[currentLine].offsetTop - allRows[oldPosition].offsetTop+border;
                log("3. cropSize=" + cropSize);
            // If the crop size is 2 (ie the border) then the cell is too big for the screen
            // so just crop the name
            if (cropSize == 2) cropSize = scrollByAmount;
                log("4. cropSize=" + cropSize);

        }
      if(currentLine == oldPosition)
        {
            currentLine++;
        }
            //log("Adding new page: " + -allRows[currentLine].offsetTop);
            this.pages.push(new Page(pageCount, - allRows[oldPosition].offsetTop, cropSize));
            pageCount++;
    }
}


/*
 * Move the document to the next page.
 *
 */

function ScrollingDocument_move()
{
    var page = this.pages[this.currentPage-1];
    // Scroll the current line into view
    this.displayArea.style.pixelTop = page.scrollPosition;
    log("Scroll position: "+ page.scrollPosition);
    if(page.cropSize == 2) {
        log("Display glitch occured, window height was to be set at 2, have ignored this (see PR55819).");
    } else {
        this.scroller.style.clip = 'rect(0 ' + this.scroller.offsetWidth + ' ' + page.cropSize + ' 0)';
    }
    log(this.scroller.style.clip);
    
    //Change the Page x of Y text.
    pageInfo.innerText = document.forms[0].pageTitleText.value + " " + this.currentPage + " " + document.forms[0].ofTitleText.value + " " + this.pages.length;
    
    
    if(this.currentPage+1 > this.pages.length)
    {
        log("Finished.");
        this.final();
    }
    else
    {
        log("Moving.");
        this.currentPage++;
    }
}

/*
 * Mark the fact we have reached the end of the paged document.
 *
 */

function ScrollingDocument_final()
{
    this.finished = true;
}


/*
 * Start paging.
 * @deprecated
 */
function ScrollingDocument_start()
{
    this.move();
}

// end of ScrollingDocument class




/*
 * Page class
 *
 * This encapsulates the dimensions of a page.
 *
 * author: Neil Ellis
 */



function Page(number, scrollPosition, cropSize)
{

    this.number = number;
    this.scrollPosition = scrollPosition;
    this.cropSize = cropSize;
}

// end of Page class.



/*
     General functions that could probably be made more OO than procedural at some point!
*/


/*
 * To keep the HTML code clean we place the div tags used for paging the results in the HTML after it has loaded.
 *
 */
function insertDivsOrig()
{
    var divHTML = '<div id="outerdiv" class="results-outer-div"><div id="resultsHeaderDiv" class="results-header-div"/><div id="scroller" class="outerScroller"><div id="displayArea" class="scrollArea">';
    divHTML += resultTable.outerHTML;
    divHTML += '</div></div></div>';
    resultTable.outerHTML = divHTML;
}

function insertDivs() {
    if (typeof resultTable === "undefined" || !resultTable) {
        log("insertDivs: resultTable is undefined or not present.");
        return;
    }

    // Wrap existing table in structured divs for scrolling
    const outerDiv = document.createElement('div');
    outerDiv.id = "outerdiv";
    outerDiv.className = "results-outer-div";

    const headerDiv = document.createElement('div');
    headerDiv.id = "resultsHeaderDiv";
    headerDiv.className = "results-header-div";

    const scrollerDiv = document.createElement('div');
    scrollerDiv.id = "scroller";
    scrollerDiv.className = "outerScroller";

    const displayAreaDiv = document.createElement('div');
    displayAreaDiv.id = "displayArea";
    displayAreaDiv.className = "scrollArea";

    // Move the resultTable into displayArea
    resultTable.parentNode.insertBefore(outerDiv, resultTable);
    displayAreaDiv.appendChild(resultTable);
    scrollerDiv.appendChild(displayAreaDiv);
    outerDiv.appendChild(headerDiv);
    outerDiv.appendChild(scrollerDiv);
}


/*
 * This function takes the columns from two seperate tables
 * (the header table and the result table) and aligns
 * them with each other.
 */
function alignHeadersOrig()
{
    var resultColumns = resultTable.tBodies[0].rows[0].cells;
    // Clone the result table (not the nested rows)
    var headerTable= resultTable.cloneNode(false);
    headerTable.id="headerTable"; // give it an id
    headerTable.className="results-header";
    resultsHeaderDiv.insertBefore(headerTable, null);
    //Now create a <thead/> element.
    thead= headerTable.createTHead();
    thead.insertBefore(resultTable.rows[0].cloneNode(true),null);
    var resultHeaderColumns = thead.rows[0].cells;
    for(var i = 0; i < resultColumns.length; i++)
    {

        var resultWidth = resultColumns[i].clientWidth-resultTable.cellPadding*2;
        resultHeaderColumns[i].width = resultWidth;

    }
}

function alignHeaders() {
    // Get the original table and first row of data (assumes it's the header)
    const originalHeaderRow = resultTable.tHead ? resultTable.tHead.rows[0] : resultTable.rows[0];

    if (!originalHeaderRow || originalHeaderRow.cells.length === 0) {
        log("alignHeaders: No header row found in resultTable.");
        return;
    }

    // Create a new header table
    const headerTable = document.createElement('table');
    headerTable.id = "headerTable";
    headerTable.className = "results-header";
    headerTable.style.tableLayout = "fixed";
    headerTable.style.width = resultTable.offsetWidth + "px";  // Force match total width

    const thead = headerTable.createTHead();
    const newHeaderRow = thead.insertRow();

    // Copy each cell and preserve dimensions
    for (let i = 0; i < originalHeaderRow.cells.length; i++) {
        const originalCell = originalHeaderRow.cells[i];
        const newCell = originalCell.cloneNode(true);

        const computedWidth = originalCell.offsetWidth;
        newCell.style.width = computedWidth + "px";
        newHeaderRow.appendChild(newCell);
    }

    // Add the new header table to the resultsHeaderDiv
    resultsHeaderDiv.innerHTML = "";  // Clear any existing
    resultsHeaderDiv.appendChild(headerTable);
}



/*
 *Attaches a stylesheet to the page which relates to the display type we are using "(eg 42in plasma)" and
 * an optional stylesheet which the user supplies as a stylesheet= parameter
 */
 function attachStylesheets() {
     function addStylesheet(href) {
         if (typeof document.createStyleSheet === "function") {
             // For old versions of Internet Explorer
             document.createStyleSheet(href);
         } else {
             // For modern browsers
             var link = document.createElement("link");
             link.rel = "stylesheet";
             link.type = "text/css";
             link.href = href;
             document.head.appendChild(link);
         }
     }

     var displayType = trimToNull(getParameterForWindow(self, "displayType"));
     if (displayType != null) {
         log("Display Stylesheet " + displayType);
         addStylesheet("css/" + displayType + ".css");
     } else {
         log("Display Stylesheet not found.");
     }

     var additionalStylesheet = trimToNull(getParameter("stylesheet"));
     if (additionalStylesheet != null) {
         addStylesheet("css/" + additionalStylesheet + ".css");
         log("Additional Stylesheet " + additionalStylesheet + " added.");
     } else {
         log("Additional Stylesheet not found.");
     }
 }


/*
 * Set up a document ready for paging. That means it will be shown one sectiom at a time rather than scrollable.
*/
function initializePagedDocument()
{
        var controllerFrame;

        log("Initializing paged display document.");

        controllerFrame = getFrameElement('lhs.controller');

        try {
            if(controllerFrame.contentWindow.rotator != null &&
               controllerFrame.contentWindow.rotator.documentNumber != null &&
               controllerFrame.contentWindow.rotator.documentList != null)
            {
                listText.innerText=document.forms[0].listTitleText.value + " ";
                ofText.innerText=" "+ document.forms[0].ofTitleText.value + " ";
				
				if (window.top &&
			        window.top.lhs &&
			        window.top.lhs.controller &&
			        window.top.lhs.controller.rotator &&
			        window.top.lhs.controller.rotator.documentNumber != null &&
			        window.top.lhs.controller.rotator.documentList != null) {

			        listInfoDoc.innerText = window.top.lhs.controller.rotator.documentNumber + 1;
			        listInfoDocTotal.innerText = window.top.lhs.controller.rotator.documentList.length;
			    } else {
			        log("Rotator or frame hierarchy not fully initialized.");
			    }
            }
        }
        catch (e)
        {
            error(e.message);
        }

        if(typeof(resultTable) != "undefined") {
            insertDivs();
            alignHeaders();
            headerTable.style.visibility="visible";
            thisDisplayDocument = new ScrollingDocument(scroller,displayArea,resultTable);
            thisDisplayDocument.start();
            log("Display document initialized.");
            resultTable.style.visibility="visible";
        }
}


/*
 * This method will be called to initialize a document that will not be displayed page by page.
 *
 */
function initializeUnpagedDocument()
{
        log("Initializing unpaged display document.");

        if(typeof(document.all.resultTable) != "undefined") {
             document.all.resultTable.style.visibility= "visible";
             log("Table now made visible.");
        }
}


/*
 * This function decides whether or not we should page the document 'screen' style or
 * display it in a browser style scrollable form. It returns true for the paged style.
 */
function isDisplayPaged() {
    return (self != top);
}

function initialise() {
        try {
            if(isDisplayPaged()) {
                initializePagedDocument();
            } else {
                initializeUnpagedDocument();
            }
        }
        catch (e)
        {
            error(e.message);
        }
}
/*
 * As soon as the document is loaded we need to initialize the script.
 * We initialize paged documents differenty.
 */
window.onload= initialise;

// We attach the stylesheets before the rest of the document can be loaded.

attachStylesheets();
