$('body')
  .on('mouseover','.cardlink',function () {
    var uri = $(this).data("image_url");
    if (typeof uri == 'undefined') {
      uri = 'https://netrunnerdb.com/card_image/' + $(this).data("code") + '.png';
    }
    $(this).popover({
      trigger: 'hover',
      placement: 'auto',
      html: true,
      content: '<img class="img-fluid" src="' + uri + '" />'
    }).popover('show');
  });