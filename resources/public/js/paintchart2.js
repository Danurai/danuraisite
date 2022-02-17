let _compare = [];
let dt;

$.getJSON("/colours/api/json", function (data) {
	dt = $('#colourtable').DataTable({
		searching: true,
		pageLength: 50,
		dom: '<"d-flex"l<"#filterbtn.mx-2"><"#filters.flex-fill d-flex mx-2">f>tip',	//https://datatables.net/examples/basic_init/dom.html
		data: data,
		columns: [
			{ title: "Code",  data: "code" },
			{ title: "Brand", data: "brand" },
			{ title: "Range", data: "range" },
			{ title: "Name",  data: "name", render: function (data, type, row, meta) {return get_name(row);} },
			{ title: "Hex" , data: "hex", className: "text-center"}, //render: function(data, type, row, meta) {return row.hex.toUpperCase(); }, width: '20%', className: "text-center" },
			{ title: "Colour" , data: "hex", width: '20%', className: "hexcode text-center"}
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
	$.each(['Citadel', 'Pro Acryl', 'Molotow'], function (i, b) {
		$('#brandmulti').find('option[value="' + b + '"]').prop("selected", true);
	});
	$.each(['Classic', 'Base', 'Base 1', 'Base 3', 'Metallic', 'Layer'], function (i, r) {
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
	$comp.empty();
	if (_compare.length == 0) {
		$comp.append('<div class="h4 w-100 text-center muted">Click on Colours to compare them here</h4>')
	}
	_compare.forEach( d => {
		let $div = $('<div>');
		$div.addClass("colourbox");
		$div.css('background', get_background( d ));
		$div.css('color', fontcolour(d.hex));
		$div.append('<div>' + d.code + '</div>');
		$div.append('<div class="small">' + d.brand + ': ' + d.range + '</div>');
		$div.append('<span class="removecolour" data-code="' + d.code + '"><i class="fas fa-times-circle"></span>')
		$div.append('<h5>' + get_name(d) + '</h5>');
		$div.append('<div>' + d.hex +  (d.hex2 != null ? ' - ' + d.hex2 : '') + '</div>');
		$comp.append($div);
	});
}

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

function hexToRgb (hex) {
  var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  return [parseInt(result[1], 16), parseInt(result[2], 16), parseInt(result[3], 16)];
}