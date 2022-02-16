let _compare = [];
let dt;

$.getJSON("/colours/api/json", function (data) {
	dt = $('#colourtable').DataTable({
		searching: true,
		pageLength: 50,
		dom: '<"d-flex justify-content-between"l<"#brandfilter.dataTables_length"><"#rangefilter.dataTables_length">f>tip',	//https://datatables.net/examples/basic_init/dom.html
		data: data,
		columns: [
			{ title: "Code",  data: "code" },
			{ title: "Brand", data: "brand" },
			{ title: "Range", data: "range" },
			{ title: "Name",  data: "name", render: function (data, type, row, meta) {return get_name(row);} },
			{ title: "Hex" , data: "hex", width: '20%', className: "text-center" },
			{ title: "Colour" , data: "hex", width: '20%', className: "hexcode text-center"}
		],
		'rowCallback': function( row, data, index) {
			$(row).find('td:eq(4)').text(data.hex + (data.hex2 != null ? ' - ' + data.hex2 : ''));
			$(row).find('td:eq(5)').text('');
			$(row).find('td:eq(5)').css('background', get_background( data ));
			$(row).find('td:eq(5)').css('color', fontcolour(data.hex));
		}
	});
	$brandselect = $('<select id="brandselect"></select>');
	$brandselect.append('<option>(All)</option>');
	data.map( d => d.brand )
		.filter( (v, i, a) => a.indexOf(v) == i).sort()
		.forEach( b => {
			$brandselect.append('<option>' + b + '</option>')
		} );
	$rangeselect = $('<select id="rangeselect"></select>');
	$rangeselect.append('<option>(All)</option>');
	data.map( d => d.range )
		.filter( (v, i, a) => a.indexOf(v) == i).sort()
		.forEach( b => {
			$rangeselect.append('<option>' + b + '</option>')
		} );
	$('#brandfilter').append($('<label></label>').text("Brand ").append($brandselect));
	$('#rangefilter').append($('<label></label>').text("Range ").append($rangeselect));
	filter_table();
	compare_row();
	//$brandselect.selectpicker();
});


function filter_table () {
	let bs = $('#brandselect').val();
	let rs = $('#rangeselect').val();
	if (bs != "(All)") { dt.columns(1).search(bs); } else { dt.columns(1).search(''); } 
	if (rs != "(All)") { dt.columns(2).search(rs); } else { dt.columns(2).search(''); }
	dt.draw();
}

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
