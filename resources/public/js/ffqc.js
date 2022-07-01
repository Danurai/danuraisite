let plyr = {pn: "Player1", sk: 10, st: 20, lk: 10, tuff: true};
let nmy = [
	{pn: "Enemy1", sk: 6, st: 6}
];

udpate_page()

function udpate_page() {
	$('#pn').val(plyr.pn);
	$('#sk').val(plyr.sk);
	$('#st').val(plyr.st);
	$('#lk').val(plyr.lk);
	$('#enemyinput').empty();
	
	$.each( nmy, id => {
		let ei = $('<div class="d-flex justify-content-left mb-2"></div>');
		ei.append('<div class="me-2 my-auto">Name</div>');
		$('<input>').attr({
			class: "form-control me-2",
			value: nmy[id].pn
		}).appendTo(ei);
		ei.append('<div class="me-2 my-auto">Skill</div>');
		$('<input>').attr({
			class: "form-control me-2",
			value: nmy[id].sk,
			list: "datalist1"
		}).appendTo(ei);
		ei.append('<div class="me-2 my-auto">Stamina</div>');
		$('<input>').attr({
			class: "form-control me-2",
			value: nmy[id].st,
			list: "datalist2"
		}).appendTo(ei);
		$('#enemyinput').append(ei)
	})

}

$('#pn').on('input', function() {plyr.pn = $(this).val()} );
$('#sk').on('input', function() {plyr.sk = $(this).val()} );
$('#st').on('input', function() {plyr.st = $(this).val()} );
$('#lk').on('input', function() {plyr.lk = $(this).val()} );

/*
$('#add').on('click', function() {
	n = nmy.length;
	nx = {pn: "Enemy", sk: 6, st: 6};
	nmy.push(nx);
	udpate_page();
});
*/

$('#run').on('click', function () {
	// 1st iteration 1:1
	let cb = [];
	let round = {};
	do  {
		round = {plyr: $.extend({}, plyr), nmy: $.extend( {}, nmy[0])};
		round.plyr.roll = [Math.ceil(Math.random() * 6), Math.ceil(Math.random() * 6)];
		round.nmy.roll = [Math.ceil(Math.random() * 6), Math.ceil(Math.random() * 6)];
		round.plyr.tot = round.plyr.sk + round.plyr.roll[0] + round.plyr.roll[1];
		round.nmy.tot = round.nmy.sk + round.nmy.roll[0] + round.nmy.roll[1];
		if (round.plyr.tot > round.nmy.tot) {
			nmy[0].st = nmy[0].st - 2;
			round.nmy.st = round.nmy.st - 2;
			round.nmy.wnd = true;
		} else if (round.plyr.tot < round.nmy.tot) {
			plyr.st = plyr.st - (plyr.tuff ? 1 : 2);
			round.plyr.st = round.plyr.st - (plyr.tuff ? 1 : 2);
			round.plyr.wnd = true;
		} 
		cb.push(round);
	} while ( plyr.st > 0 && nmy.reduce( (p,c) => p+=c.st,0) > 0)
	$('#results').html(JSON.stringify(cb));
	$('#results').empty();
	$(`<div>Combat Complete in  ${cb.length}  Rounds</div>`).attr({class: "h5"}).appendTo('#results')
	cb.forEach( cbr =>
		$(`<div>${cbres(cbr.plyr)}${cbres(cbr.nmy)}</div>`).attr({class: "row"}).appendTo('#results')
	);
	udpate_page();
})

function cbres( data ) {
	return (`<div class="col-4">${data.pn}: SK=${data.sk} Roll=${data.roll[0]+data.roll[1]} (${data.roll[0]}+${data.roll[0]}) ST=<span class="${data.wnd ? "text-danger" : ""}">${data.st}</span></div>`);
}