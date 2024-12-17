(function(){
  'use strict';

  function IframeDialog(width, height) {
    var iframe = this.iframe = document.createElement('iframe');
    iframe.setAttribute('width', '100%');
    iframe.setAttribute('height', '100%');
    iframe.setAttribute('frameborder', '0');
    iframe.setAttribute('allowfullscreen', true);
    document.body.appendChild(this.iframe);

    var dialog = this.dialog = WE_API.Common.createDialog();
    dialog.setSize(width + 24, height + 118);
    dialog.setContent(iframe);

    var closeLabel = WE_API.Common.getDisplayLanguage() === 'DE' ? 'Schließen' : 'Close';
    dialog.addButton(closeLabel, function(){ dialog.hide(); });

    this.hide = dialog.hide;

    this.open = function(title, url) {
      dialog.setTitle(title);
      iframe.src = url;
      dialog.show();
    };
  }

  var youtubeDialog = new IframeDialog(560, 315);

  window.openYoutubePreview = function(title, videoId) {
    youtubeDialog.open(title, 'https://www.youtube.com/embed/' + videoId);
  };

})();