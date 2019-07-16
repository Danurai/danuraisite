var _cards;
var _filter = {};
var _order = "";
var _local = true;

if (_local) {
  $.getJSON("/api/data/carddatabase", function (data) { 
    var d = data.hits.hits.map(s => s._source);
    _cards = TAFFY(d.map(c => $.extend(c, {"category_en": c.category.en, "set_number": c.set[0].number, "class_en": (typeof c.class != 'undefined' ? c.class.en : "")})));
    write_table();      
  })
}

$('body').on('mouseover','tr', function () {
  if (!_local) {
    warhammerCardTooltip.init({
      findCardLinks: () => $(this).find('.card-tooltip')
    });    
  }
});

$('#filter').on('input', function () {
  var flt = $(this).val();
  if (flt != "") {
    _filter = {"name":{"likenocase":flt}};
  } else {
    _filter = {};
  }
  write_table();
});

function write_table () {
  $('#tblbody').empty();
  _cards(_filter).order(_order).each(function (c) {
    $('#tblbody').append(row (c));
  });
}
function row (src) {
  return "<tr>"
    + '<td>' + src.id + '</td>'
    + '<td>' + src.collectorInfo + '</td>'
    + '<td><span class="card-tooltip">' + src.name + '</span></td>'
    + '<td>' + src.category.en + '</td>'
    + '<td>' + src.alliance + '</td>'
    + '<td>' + (typeof src.class !== 'undefined' ? src.class.en : '') + '</td>'
    + '<td>' + (corners (src.category.en,src.corners)) + '</td>'
    + '<td>' + src.set[0].name + '</td>'
    + '</tr>';
}
function corners (cat, crn)  {
  var outp='';
  $.each(crn, function (id, c) {
    if (cat == "Champion" && !_local) {
      outp += '<span class="mr-1">'
        + '<img class="corner" src="https://assets.warhammerchampions.com/card-database/icons/quest_' 
          + c.value.toLowerCase() 
          + (typeof c.qualifier !== 'undefined' ? '_' + c.qualifier.toLowerCase() : '') 
          + '.png"></span>';
    } else {
      outp += '<span class="mr-1">' + c.value[0] + '</span>';
    }
  });
  return outp;
}

// requires a flattened hierarchy and mapping to this.cellIndex 
$('th.sortable').on('click',function() {
  var f = ["id","collectorInfo","name","category_en","alliance","class_en","corners","set_number"][this.cellIndex];
  
  
  if (_order == f + " asec") {
    _order = f + " desc";
  } else {
    _order = f + " asec";
  }
  $(this).closest('tr').find('.caret').remove();
  $(this).append('<span class="caret"><i class="fas fa-caret-' + (_order.slice(-4) == "asec" ? "up" : "down") + '"></span>');
    write_table();
        
  });