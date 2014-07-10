var fireOnThis = arguments[0];

setInterval(function(){hover()}, 1);

function hover() {
	if(document.createEvent){
		var evObj = document.createEvent('MouseEvents');
		evObj.initEvent( 'mouseover', true, true ); 
		fireOnThis.dispatchEvent(evObj);
	}
	else if( document.createEventObject ) {
		var evObj = document.createEventObject();
	    fireOnThis.fireEvent( 'onmouseover', evObj );
	}	
}