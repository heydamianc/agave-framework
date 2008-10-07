Event.observe(window, 'load', function(event) {
	var maxHeight = 0;
	$$('div.equalize').each(function(column) {
		if (column.getHeight() > maxHeight) {
			maxHeight = column.getHeight();
		}
	});
	$$('div.equalize').each(function(column) {
		column.setStyle({height: maxHeight + 'px'}); 
	});
	
	if ($('contents')) {
		Event.observe($('contents'), 'focus', function(event) {
		  if (!window.erased) {
		    $('contents').value = '';
		    window.erased = true;
		  }
		});
		Event.observe($('contents'), 'blur', function(event) {
		  if ($('contents').value == '') {
		    $('contents').value = 'Enter snippet contents...';
		    window.erased = false;
		  }
		});
	}
});