
var compare = []
var outp = '';
var _filter = {};
var _sort = '';
var _data = [];
var ranges;



$.getJSON("/colours/paintlist", function (d) {
	d=add_hsl(d);
	_data = TAFFY(d.map(c => (typeof c.oldgw == 'undefined' ? $.extend({oldgw: ""},c) : c)));
	ranges = d.map(c => c.range).filter(function (value, index, self) {return self.indexOf(value) === index});
	$('#selectrange').html(ranges.sort().map(c => '<option>' + c + '</option>'));
	$('#selectrange').selectpicker('refesh');
	write_table();
});

function write_table () {
  $('#colours').empty();
  _data(_filter).order(_sort).each(function(v) {
    outp = '<tr>'
      + '<td>' + v.name + '</td>'
      + '<td>' + v.range + '</td>'
      + '<td>' + v.oldgw.replace(/\#([a-f\d]{6})/i,'<a href="hsl?q=$1" target="_blank">$&</a>') + '</td>'
      + '<td><a href="hsl?q=' + v.hex.slice(1) + '">' + v.hex + '</a></td>'
      + '<td>' + v.h + '</td>'
      + '<td>' + v.s + '</td>'
      + '<td>' + v.l + '</td>'
      + '<td class="sample" data-name="' + v.name + '" data-range="' + v.range + '" style = "background-color: ' + v.hex + '; width: 20%;">&nbsp;</td>'
      + '</tr>';
    $('#colours').append(outp);
  });
}

function swatch (c) {
  return '<div class="col-sm-2 px-0">'
    + '<div class="text-center">' + c.name + '<br>' + c.range + '</div>'
    + '<div class="text-center sample"  data-name="' + c.name + '" data-range="' + c.range + '" style="background-color: ' + c.hex + '; color: ' + fontcolour(c.hex) + ';">' 
    + '<div>' + c.hex + '</div>'
    + '<div>h: ' + c.h + ' s: ' + Math.round(c.s) + '% l: ' + Math.round(c.l) + '%</div>'
    + '<div>' + (c.oldgw == "" ? '&nbsp;' : c.oldgw) + '</div>'
    + '</div>' 
    + '</div>';
}

function fontcolour (hex) {
  var weights = [0.299, 0.587, 0.114]
  if (0.5 > hexToRgb(hex)
              .map(function(c, index) {return c * weights[index];})
              .reduce(function(total, v) { return total + v }) 
              / 255) {
      return "white" 
  } else { 
    return "black" 
  }
}
              
function write_comparison () {
  $('#comparison').empty();
  $.each(compare, function (k, v) {
    $('#comparison').append( swatch (_data({"name": v.name, "range": v.range}).first() ) );
  });
}

$('#colours').on('click','.sample', function () {
  var c = _data({name: $(this).data('name'), range: $(this).data('range')}).first();
  if (c.hex != "") { 
    compare.push(c);
    write_comparison();
  }
});
$('#comparison').on('click','.sample', function () {
  var q = {name: $(this).data('name'), range: $(this).data('range')};
  compare.splice(compare.findIndex(x => x.name==q.name && x.range == q.range),1);
  write_comparison();
});

// Sort and Filter

$('#selectrange').on('changed.bs.select', function (e) {
  var rngflt = $('#selectrange').selectpicker('val');
  if (rngflt.length == 0) {
    delete _filter.range;
  } else {
    _filter.range = rngflt;
  }
  write_table();
});

$('#filter').on('input', function () {
  if ($(this).val() == "") {
    delete _filter.name;
  } else {
    _filter.name = {"likenocase" : $(this).val()};
  }
  write_table();
}); 

$('.sortable').on('click', function () {
  var fld = ["name","range","oldgw","hex","h","s","l"][this.cellIndex];
  var dir = "asec";
  if (_sort.slice(0,fld.length) == fld ) {
    dir = (_sort.slice(-4) == "asec" ? "desc" : "asec")
  }
  _sort = fld + ' ' + dir;
  write_table();
  
  // visual clues
  $(this).closest('tr').remove('.caret');
  
  
})



// HSL CODE //

function add_hsl(d) {
// update data to include HSL values based on hex
  return d.map(c => $.extend(hsl_codes(c.hex),c));
}

function hexToRgb (hex) {
  var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  return [parseInt(result[1], 16), parseInt(result[2], 16), parseInt(result[3], 16)];
}

function hsl_codes(hex) {
  if (hex != "") {
    var rgb = hexToRgb(hex);
    return RGBToHSL(rgb[0],rgb[1],rgb[2]);
  } else {
    return {h: 0, s: 1, l: 0};
  }
}  

function RGBToHSL(r,g,b) {
  // Make r, g, and b fractions of 1
  r /= 255;
  g /= 255;
  b /= 255;

  // Find greatest and smallest channel values
  let cmin = Math.min(r,g,b),
      cmax = Math.max(r,g,b),
      delta = cmax - cmin,
      h = 0,
      s = 0,
      l = 0;
  // Calculate hue
  // No difference
  if (delta == 0)
    h = 0;
  // Red is max
  else if (cmax == r)
    h = ((g - b) / delta) % 6;
  // Green is max
  else if (cmax == g)
    h = (b - r) / delta + 2;
  // Blue is max
  else
    h = (r - g) / delta + 4;

  h = Math.round(h * 60);
    
  // Make negative hues positive behind 360°
  if (h < 0)
      h += 360;
  // Calculate lightness
  l = (cmax + cmin) / 2;

  // Calculate saturation
  s = delta == 0 ? 0 : delta / (1 - Math.abs(2 * l - 1));
    
  // Multiply l and s by 100
  s = +(s * 100).toFixed(1);
  l = +(l * 100).toFixed(1);

  return {h: h, s: s, l: l};
}
