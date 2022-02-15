let _compare = [];
let dt;

$.getJSON("/colours/api/json", function (data) {
	dt = $('#colourtable').DataTable({
		searching: true,
		pageLength: 25,
		data: data,
		columns: [
			{ title: "Code",  data: "code" },
			{ title: "Brand", data: "brand" },
			{ title: "Range", data: "range" },
			{ title: "Name",  data: "name" },
			{ title: "Hex" , data: "hex", className: "text-center" },
			{ title: "Colour" , data: "hex", width: '20%', className: "hexcode text-center"}
		],
		'rowCallback': function( row, data, index) {
			$(row).find('td:eq(4)').text(data.hex + (data.hex2 != null ? ' - ' + data.hex2 : ''));
			$(row).find('td:eq(5)').text('');
			$(row).find('td:eq(5)').css('background', get_background( data ));
			$(row).find('td:eq(5)').css('color', fontcolour(data.hex));
		}
	});
});

function get_background (data) {
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

$('#colourtable').on('click', 'tr', function () {
	_compare.push( dt.row(this).data() );
	compare_row();
});

function compare_row () {
	let $comp = $('#comparison');
	$comp.empty();
	_compare.forEach( d => {
		let $div = $('<div>');
		$div.addClass("colourbox");
		$div.css('background', get_background( d ));
		$div.css('color', fontcolour(d.hex));
		$div.append('<div>' + d.code + '</div>');
		$div.append('<div class="small">' + d.brand + ': ' + d.range + '</div>');
		$div.append('<span class="removecolour" data-code="' + d.code + '"><i class="fas fa-times-circle"></span>')
		$div.append('<h5>' + d.name + '</h5>');
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
