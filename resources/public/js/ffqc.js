let plyr = {pn: "Creature", sk: 10, st: 20, lk: 10, tuff: true};
let nmy = [
	{pn: "Enemy #1", sk: 6, st: 6}
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

$('#reroll').on('click', function () {
	plyr.sk = 6 + Math.ceil(Math.random() * 6);
	plyr.st = 12 + Math.ceil(Math.random() * 6) + Math.ceil(Math.random() * 6);
	plyr.lk = 6 + Math.ceil(Math.random() * 6);
	update_page();
})

$('#testluck').on('click', function () {
	let roll = [Math.ceil(Math.random() * 6), Math.ceil(Math.random() * 6)];
	window.alert("Rolled " + roll[0] + "+" + roll[1] + " vs " + plyr.lk + "\n" + (roll[0] + roll[1] > plyr.lk ? "Un-lucky" : "Lucky!"));
	plyr.lk -= 1;
	update_page();
})

$('#pn').on('input', function() {plyr.pn = $(this).val()} );
$('#sk').on('input', function() {plyr.sk = parseInt($(this).val())} );
$('#st').on('input', function() {plyr.st = parseInt($(this).val())} );
$('#lk').on('input', function() {plyr.lk = parseInt($(this).val())} );

$('#enemyinput').on('input', 'input', function () {
	let val = isNaN(parseInt($(this).val())) ? $(this).val() : parseInt($(this).val())
	nmy[$(this).data('id')][$(this).data('stat')] = val;
});

$('#add').on('click', function() {
	n = nmy.length;
	nx = {pn: `Enemy #${n}`, sk: 6, st: 6};
	nmy.push(nx);
	update_page();
});


$('#run').on('click', function () {
	// 1st iteration 1:1
	let cb = [];
	let round = {};
	let killid;
	
	round = {plyr: $.extend({}, plyr), nmy: JSON.parse(JSON.stringify(nmy))};
	do  {
		//round = {plyr: $.extend({}, plyr), nmy: JSON.parse(JSON.stringify(round.nmy))};
		killid = -1;

		round.plyr.roll = [Math.ceil(Math.random() * 6), Math.ceil(Math.random() * 6)];		
		round.plyr.tot = round.plyr.sk + round.plyr.roll[0] + round.plyr.roll[1];		

		$.each( round.nmy, idx => {
			round.nmy[idx].roll = [Math.ceil(Math.random() * 6), Math.ceil(Math.random() * 6)]
			round.nmy[idx].tot = round.nmy[idx].sk + round.nmy[idx].roll[0] + round.nmy[idx].roll[1]
		});

		// check focus nmy max skill, min stam
		let tgtid = 0;
		$.each(round.nmy, idx => {
			if (round.nmy[idx].sk > round.nmy[tgtid].sk) {
				tgtid = idx
			} else if (round.nmy[idx].sk == round.nmy[tgtid].sk && round.nmy[idx].st < round.nmy[tgtid].st) {
				tgtid = idx
			}
		});
		if (round.plyr.roll[0] == round.plyr.roll[1]) {
			round.nmy[tgtid].st = 0;
			round.nmy[tgtid].wnd = true;
			killid = tgtid;
		} else if (round.plyr.tot > round.nmy[tgtid].tot) {
			round.nmy[tgtid].st = Math.max(0, round.nmy[tgtid].st - 2);
			round.nmy[tgtid].wnd = true;
			if (round.nmy[tgtid].st <= 0) { killid = tgtid }
		}

		$.each( round.nmy, idx => {
			if (round.plyr.tot < round.nmy[idx].tot) {
				plyr.st = plyr.st - (plyr.tuff ? 1 : 2);
				round.plyr.st = round.plyr.st - (plyr.tuff ? 1 : 2);
				round.plyr.wnd = true;
			} 
		});
		cb.push(JSON.parse(JSON.stringify(round)));
		if (killid > -1) { round.nmy.splice(killid,1)} 
		round.plyr.wnd = false;
		round.nmy.forEach( n => n.wnd = false);
	} while ( plyr.st > 0 && round.nmy.length > 0) //nmy.reduce( (p,c) => p+=c.st,0) > 0)

	$('#results').empty();
	$(`<div>Combat Complete in  ${cb.length}  Rounds</div>`).attr({class: "h5"}).appendTo('#results')
	cb.forEach( cbr => {
		let cbrnmyres = ''
		cbr.nmy.forEach( cbrnmy => cbrnmyres += cbres(cbrnmy) )
		$(`<div><div class="col">${cbres(cbr.plyr)}</div><div class="col">${cbrnmyres}</div></div>`).attr({class: "row border"}).appendTo('#results')
	});
	update_page();
})

function cbres( data ) {
	return (`<div>${data.pn}: SK=<b>${data.tot}</b> <span class="small">(${data.sk}+${data.roll[0]}+${data.roll[1]})</span> ST=<span class="${data.wnd ? "text-danger" : ""}">${data.st}</span></div>`);
}

$('#rosters').on('click','li',function () {
	nmy = JSON.parse(JSON.stringify($(this).data('roster')));
	$('#results').empty();
	update_page();
})