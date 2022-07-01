let plyr = {pn: "Player1", sk: 10, st: 20, lk: 10, tuff: true};
let nmy = [
	{pn: "Enemy1", sk: 6, st: 6}
];

update_page()

function update_page() {
	$('#pn').val(plyr.pn);
	$('#sk').val(plyr.sk);
	$('#st').val(plyr.st);
	$('#lk').val(plyr.lk);
	$('#enemyinput').empty();
	
	$.each( nmy, id => {
		let ei = $('<div class="d-flex justify-content-left mb-2"></div>');
		let pn = $('<div>').attr({class:"me-2"}).appendTo(ei);
		let sk = $('<div>').attr({class:"me-2"}).appendTo(ei);
		let st = $('<div>').attr({class:"me-2"}).appendTo(ei);
		$('<div class="me-2 my-auto">Name</div>').appendTo(pn);
		$('<input>').attr({
			class: "form-control me-2",
			"data-stat": "pn",
			"data-id": id,
			value: nmy[id].pn
		}).appendTo(pn);
		$(`<div>Skill</div>`).attr({class: "me-2 my-auto"}).appendTo(sk);
		$('<input>').attr({
			class: "form-control me-2",
			value: nmy[id].sk,
			"data-stat": "sk",
			"data-id": id,
			list: "datalist1"
		}).appendTo(sk);
		$('<div class="me-2 my-auto">Stamina</div>').appendTo(st);
		$('<input>').attr({
			class: "form-control",
			value: nmy[id].st,
			"data-stat": "st",
			"data-id": id,
			list: "datalist2"
		}).appendTo(st);
		$('#enemyinput').append(ei)
	})

}

$('#pn').on('input', function() {plyr.pn = $(this).val()} );
$('#sk').on('input', function() {plyr.sk = parseInt($(this).val())} );
$('#st').on('input', function() {plyr.st = parseInt($(this).val())} );
$('#lk').on('input', function() {plyr.lk = parseInt($(this).val())} );

$('#enemyinput').on('input', 'input', function () {
	nmy[$(this).data('id')][$(this).data('stat')] = $(this).val();
});

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
	update_page();
})

function cbres( data ) {
	return (`<div class="col">${data.pn}: SK=${data.sk} + ${data.roll[0]},${data.roll[1]} Total=${data.tot} ST=<span class="${data.wnd ? "text-danger" : ""}">${data.st}</span></div>`);
}

$('#rosters').on('click','li',function () {
	nmy = JSON.parse(JSON.stringify($(this).data('roster')));
	$('#results').empty();
	update_page();
})