Event.observe(window, 'load', function(event) {
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
  sh_highlightDocument();
});