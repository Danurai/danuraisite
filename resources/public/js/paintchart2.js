let _compare = [];
let dt;
let data;

$.getJSON("/colours/api/json", function (d) {
	data = add_hsl( d );
	dt = $('#colourtable').DataTable({
		searching: true,
		pageLength: 100,
		dom: '<"d-flex"<"#filterbtn.mx-2"><"#filters.flex-fill d-flex mx-2">f>tip',	//https://datatables.net/examples/basic_init/dom.html
		data: data,
		columns: [
			{ title: "Code",  data: "code" },
			{ title: "Brand", data: "brand" },
			{ title: "Range", data: "range" },
			{ title: "Name",  data: "name", render: function (data, type, row, meta) {return get_name(row);} },
			{ title: "Hex" , data: "hex", className: "text-center"}, //render: function(data, type, row, meta) {return row.hex.toUpperCase(); }, width: '20%', className: "text-center" },
			{ title: "Colour" , data: "hex", width: '20%', className: "hexcode text-center"},
			{ title: "Hue" , data: "h", className: "text-center"}
		],
		'rowCallback': function( row, data, index) {
			$(row).find('td:eq(5)').text('');
			$(row).find('td:eq(5)').css('background', get_background( data ));
			$(row).find('td:eq(5)').css('color', fontcolour(data.hex));
		}
	});
	$('#filterbtn').append('<button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#filterModal">Filter</button>');
	set_filters(data);
	compare_row();
});

function set_filters ( data ) {
	let brandnames = data.map( d => d.brand ).filter( (v, i, a) => a.indexOf(v) == i).sort();
	let rangenames = data.map( d => d.range ).filter( (v, i, a) => a.indexOf(v) == i).sort();

	$('#brandmulti').empty();
	brandnames.forEach( b => {
		$('#brandmulti').append('<option value="' + b + '">' + b + '</option>');
	})
	$('#rangemulti').empty();
	rangenames.forEach( r => {
		$('#rangemulti').append('<option value="' + r + '">' + r + '</option>');
	})
	$.each(['Citadel', 'Pro Acryl'], function (i, b) {
		$('#brandmulti').find('option[value="' + b + '"]').prop("selected", true);
	});
	$.each(['Base', 'Layer'], function (i, r) {
		$('#rangemulti').find('option[value="' + r + '"]').prop("selected", true);
	});
	filter_table();
}

$('#filterModal').on('change', 'select', function () {
	filter_table();
});

function filter_table () {
	let bs = $('#brandmulti').val().map( d => "^" + d + "$").join("|");
	let rs = $('#rangemulti').val().map( d => "^" + d + "$").join("|");
	$('#filters').empty();
	$('#brandmulti').val().forEach( b => $('#filters').append('<div class="filter-label rounded bg-light my-auto me-2" data-brand="'  + b + '">' + b + '<i class="fas fa-times-circle text-secondary ms-1" /></label>'))
	$('#rangemulti').val().forEach( r => $('#filters').append('<div class="filter-label rounded bg-light my-auto me-2" data-range="'  + r + '">' + r + '<i class="fas fa-times-circle text-secondary ms-1" /></label>'))
	dt.columns(1).search(bs, true, false)
	dt.columns(2).search(rs, true, false)
	dt.draw();
}

$('body').on('click', '.filter-label', function () {
	let brand = $(this).data('brand');
	let range = $(this).data('range');
	$('#brandmulti').find('option[value="' + brand + '"]').prop("selected", false);
	$('#rangemulti').find('option[value="' + range + '"]').prop("selected", false);
	filter_table()
} );

function get_background (data) {
	if (typeof data !== 'undefined') {
		if (data.metallic) {
			if (data.hex1 != null) {
				return 'radial-gradient(ellipse, ' + data.hex + ', ' + data.hex1 + ' 50%, ' + data.hex2 + ' 100%)' 
			} else {
				return 'radial-gradient(ellipse, ' + data.hex + ', ' + data.hex2 + ')' 
			}
		} else if (data.transparent) {
			return 'linear-gradient(to right, ' + data.hex + ', ' + data.hex2 + ")"
		} else {
			return data.hex
		}
	}
}

function get_name (data) {
	if (typeof data !== 'undefined') {
		if (data.oldgw != null) {
			return data.name + ' (' + data.oldgw + ')';
		} else {
			return data.name;
		}
	}
}

function compare_row () {
	let $comp = $('#comparison');
	$('#matches').empty();
	$comp.empty();
	if (_compare.length == 0) {
		$comp.append('<div class="h4 w-100 text-center muted">Click on Colours to compare them here</h4>')
	}
	_compare.forEach( d => $comp.append( compare_ele(d) ) );
}

function compare_ele ( d ) {
	let $div = $('<div>');
	$div.addClass("colourbox");
	$div.css('background', get_background( d ));
	$div.css('color', fontcolour(d.hex));
	$div.append('<div>' + d.code + '</div>');
	$div.append('<div class="small">' + d.brand + ': ' + d.range + '</div>');
	$div.append('<span class="removecolour" data-code="' + d.code + '"><i class="fas fa-times-circle"></span>')
	$div.append('<h5>' + get_name(d) + '</h5>');
	$div.append('<div>' + d.hex +  (d.hex2 != null ? ' - ' + d.hex2 : '') + ' / ' + '<a href="#" data-hue="' + d.h + '">HSL:</a> ' + d.h + '-' + d.s + '-' + d.l + '</div>');
	return $div;
}

$('#comparison').on('click', 'a', function (e) {
	e.preventDefault();
	let hue = $(this).data('hue');
	let match = data.filter( c => (hue - 5) < c.h && c.h < (hue + 5) && c.range != 'Air' && c.range != 'Spray').sort( (a, b) => a.l > b.l ? 1 : 0);
	console.log(match);
	$('#matches').empty();
	$.each(match, function (i, c) {
		$('#matches').append(compare_ele( c ));
	});
})

$('body').on('change', 'select', function () {
	let sl =  ($(this)[0].id)
	if (sl == "brandselect" || sl == "rangeselect")  {
		filter_table();
	}
});

$('#colourtable').on('click', 'tbody tr', function () {
	_compare.push( dt.row(this).data() );
	compare_row();
});


$('#comparison').on('click', '.removecolour', function () {
	let code = $(this).data('code');
	_compare = _compare.filter( d => d.code != code );
	compare_row();
});



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


// HSL CODE //

function add_hsl(d) {
	// update data to include HSL values based on hex
		return d.map(c => $.extend(hsl_codes(c.hex),c));
	}
	
	function hexToRgb (hex) {
		var result = /^#([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
		if (result) {
			return [parseInt(result[1], 16), parseInt(result[2], 16), parseInt(result[3], 16)];
		} else {
			return [255,255,255]
		}
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
	