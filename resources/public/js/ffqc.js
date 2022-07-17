let plyr = {pn: "Creature", maxsk: 10, sk: 10, maxst: 20, st: 20, maxlk: 10, lk: 10, tuff: true, dedly: true};
let nmy = [
	{pn: "Enemy #1", sk: 6, st: 6}
];

update_page()

function update_page() {
	$('#pn').val(plyr.pn);
	$('#sk').val(plyr.sk);
	$('#st').val(plyr.st);
	$('#lk').val(plyr.lk);
	$('#tough').prop('checked', plyr.tuff);
	
	$('#deadly').prop('checked', plyr.dedly);
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

$('#reroll').on('click', function () { roll_new_char() });

function roll_new_char() {
	plyr.maxsk = 6 + Math.ceil(Math.random() * 6);
	plyr.sk = plyr.maxsk;
	plyr.maxst = 12 + Math.ceil(Math.random() * 6) + Math.ceil(Math.random() * 6);
	plyr.st = plyr.maxst;
	plyr.maxlk = 6 + Math.ceil(Math.random() * 6);
	plyr.lk = plyr.maxlk;
	update_page();
}

$('#testluck').on('click', function () {
	let lk = test_luck();
	window.alert("Rolled " + lk.roll[0] + "+" + lk.roll[1] + " vs " + plyr.lk + "\n" + (lk.lucky ? "Lucky!" : "Un-Lucky!"));
	update_page();
});

function test_luck() {
	let roll = [d6(), d6()];
	plyr.lk -= 1;
	return {roll: roll, lucky: (roll[0] + roll[1] <= plyr.lk + 1)}
}

$('#pn').on('input', function() {plyr.pn = $(this).val()} );
$('#sk').on('input', function() {plyr.sk = parseInt($(this).val())} );
$('#st').on('input', function() {plyr.st = parseInt($(this).val())} );
$('#lk').on('input', function() {plyr.lk = parseInt($(this).val())} );
$('#tough').on('change', function() {plyr.tuff = true == $(this).prop('checked')});
$('#deadly').on('change', function() {plyr.dedly = true == $(this).prop('checked')});

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
	$('#results').empty();
	do_fight();
	update_page();
});

function do_fight() {
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

		if (round.plyr.roll[0] == round.plyr.roll[1] && round.plyr.dedly) {
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

		if (round.nmy[tgtid].manic && round.plyr.tot > round.nmy[tgtid].tot) { round.nmy[tgtid].sk = 9 } // Hard-coded for Manic Beast
		$.each( round.nmy, idx => {
			if (round.nmy[idx].manic && (round.plyr.tot <= round.nmy[idx].tot)) { round.nmy[tgtid].sk = 7 } // Hard-coded for Manic Beast
		});
		if (killid > -1) { round.nmy.splice(killid,1)} 
		round.plyr.wnd = false;
		round.nmy.forEach( n => n.wnd = false);
	} while ( plyr.st > 0 && round.nmy.length > 0) //nmy.reduce( (p,c) => p+=c.st,0) > 0)

	$(`<div>Combat Complete in  ${cb.length}  Rounds</div>`).attr({class: "h5"}).appendTo('#results')
	$(`<div><small><b>${plyr.pn} SK:${plyr.sk}/${plyr.maxsk} ST:${plyr.st}/${plyr.maxst} LK:${plyr.lk}/${plyr.maxlk}</b></small></div>`).appendTo('#results')
	cb.forEach( cbr => {
		let cbrnmyres = ''
		cbr.nmy.forEach( cbrnmy => cbrnmyres += cbres(cbrnmy) )
		$(`<div><div class="col">${cbres(cbr.plyr)}</div><div class="col">${cbrnmyres}</div></div>`).attr({class: "row border"}).appendTo('#results')
	});
	return cb;
}

function cbres( data ) {
	return (`<div>${data.pn}: SK=<b>${data.tot}</b> <span class="small">(${data.sk}+${data.roll[0]}+${data.roll[1]})</span> ST=<span class="${data.wnd ? "text-danger" : ""}">${data.st}</span></div>`);
}

$('#rosters').on('click','li',function () {
	nmy = JSON.parse(JSON.stringify($(this).data('roster')));
	$('#results').empty();
	update_page();
})


// CoH
$('#coh').on('click', evt =>  {
	CoH();
}); 
function CoH () {
	$('#results').empty()
	roll_new_char();
	let cb, lk, kill;
	
	$('#results').append(`<br /><div><b>${plyr.pn}: SK:${plyr.sk} ST:${plyr.st} LK:${plyr.lk}</b></div><br />`);

	rolls = Array(3).fill().map( () => d6());
	if (rolls[0]<4 || rolls[1]<3 || rolls[2]<1 ) {
		$('#results').append('<div>Straight to Hobbit fight.</div>')
	} else {
		
		cb = show_fight( [{pn: 'Clawbeast', sk: 9, st: 14}] )
		if(plyr.st <= 0) {
			$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
			return null;
		}
		
		if (d6() < 5) {
			plyr.st = Math.min(plyr.maxst, plyr.st+2);
		}

		if (d6() < 4) {
			$('#results').append('<div>Killed by Dark Elves</div>');
			return null;
		} else {
			$('#results').append('<div>Return to staring point</div>');
		}
	}	
	
	cb = show_fight( [{pn: 'Hobbit', sk: 5, st: 6}] )
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return nil;
	}

	kill = true;
	lk = test_luck(); 
	if (cb.length < 4 && lk || d6() > 4) {
		kill = false;
		$('#results').append('<div>Killed Hobbit and Lucky</div>')
	} else {
		kill = true;
	}
	if (kill) {
		$('#results').append('<div>Killed by Mage</div>')
		return null;
	}

	cb = show_fight( [{pn: 'Knight', sk: 8, st: 9}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}
	
	$('#results').append('<div>Restore Stamina</div>');
	plyr.st = plyr.maxst;

	if (d6()<3 ) {
		$('#results').append('<div>attacked by bat, -1 Skill.</div>')
		plyr.sk -= 1;
	}
	if(d6()>3) {
		$('#results').append('<div>Bitten by Flesh Feeder, -2 Stamina.</div>');
		plyr.st -= 2;
	}

	cb = show_fight( [{pn: 'Flesh Feeder #1', sk: 6, st: 6},{pn: 'Flesh Feeder #2', sk: 6, st: 7},{pn: 'Flesh Feeder #3', sk: 6, st: 6}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}

	$('#results').append('<div>Found Vapour of Reason, +2 Luck</div>');
	plyr.lk = Math.min(plyr.maxlk, plyr.lk+2);

	cb = show_fight( [{pn: 'Strongarm', sk: 7, st: 8}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}
	cb = show_fight( [{pn: 'Warrior', sk: 7, st: 7},{pn: 'Thief', sk: 8, st: 6}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}

	lk = test_luck();
	if (lk.lucky) {
		$('#results').append('<div>Crossed the Bilgewater Safely</div>')
	} else {
		$('#results').append('<div>Fell into the Bilgewater & died.</div>');
		return null;
	}
	
	$('#results').append('<div>Grabbed the Crystal Club. Gain 1 luck.</div>')
	plyr.lk = Math.min(plyr.maxlk, plyr.lk+1);

	cb = show_fight( [{pn: 'Warrior', sk: 8, st: 9}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}

	cb = show_fight( [{pn: 'Fighter', sk: 7, st: 8}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}

	$('#results').append('<div>Found Vapour of Tongues, +1 Luck</div>');
	plyr.lk = Math.min(plyr.maxlk, plyr.lk+1);

	$('#results').append('<div>Meet Rhino Man #62</div>');

	cb = show_fight( [{pn: 'Blood Orc #1', sk: 7, st: 7},{pn: 'Blood Orc #2', sk: 8, st: 7}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}

	$('#results').append('<div>Gained Ring of Devotion</div>');

	lk = test_luck();
	if (lk.lucky) {
		$('#results').append('<div>Crossed the Bilgewater Safely</div>')
	} else {
		$('#results').append('<div>Fell into the Bilgewater & died.</div>');
		return null;
	}

	$('#results').append('<div>Gained Parchment #193</div>');

	cb = show_fight( [{pn: 'Manic Beast', sk: 7, st: 8, manic: true}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}

	$('#results').append('<div>Drink potion of strength, restore Stamina</div>');
	plyr.st = plyr.maxst;

	
	$('#results').append('<div>Defeat Darramous and Escape! (442)</div>');

	$('#results').append('<div>Visit witches and rest, +8 Stamina, +2 Luck</div>')
	plyr.st = Math.min(plyr.maxst, plyr.st + 8)
	plyr.st = Math.min(plyr.maxlk, plyr.lk + 2)

	cb = show_fight( [{pn: 'Fighter', sk: 7, st: 8}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}

	$('#results').append('<div>Meet Grog, Visit Rosina. +2 Luck, +4 Stamina</div>')
	plyr.st = Math.min(plyr.maxst, plyr.st + 4)
	plyr.st = Math.min(plyr.maxlk, plyr.lk + 2)

	$('#results').append('<div>Get Sculliweed Root</div>')

	cb = show_fight( [{pn: 'Toadman', sk: 9, st: 9}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}

	$('#results').append('<div>Visit witches and rest, Restore Stamina and Luck, gain Ringof truth</div>')
	plyr.st = plyr.maxst
	plyr.lk = plyr.maxlk

	if (d6()+d6() <= plyr.sk) {
		$('#results').append('<div>Ride Ophidataur to Dara Weaseltongue</div>')
	} else {
		$('#results').append('<div>Fail to catch Ophidataur and die in Knot Oak Wood</div>')
		return null;
	}

	cb = show_fight( [{pn: 'Brigand #1', sk: 8, st: 9},{pn: 'Brigand #2', sk: 8, st: 7}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}

	let findtrap = false;
	if (test_luck()) {
		findtrap = true;
	} else if (test_luck()) {
		findtrap=true;
	}

	if (!findtrap) {
		$('#results').append('<div>Caught in trap and died</div>')
		return null
	}

	cb = show_fight( [{pn: 'Goblin #1', sk: 6, st: 5},{pn: 'Goblin #2', sk: 5, st: 5}])
	if(plyr.st <= 0) {
		$('#results').append(`<div>Killed by ${nmy.map(c=>c.pn).join(",")}.</div>`)
		return null;
	}

	$('#results').append('<div>Got onto Gelleykeep! (312)</div>')
	


	$('#results').append(`<br /><div><b>${plyr.pn}: SK:${plyr.sk}<small>/${plyr.maxsk}</small> ST:${plyr.st}<small>/${plyr.maxst}</small> LK:${plyr.lk}<small>/${plyr.maxlk}</small></b></div><br />`);
	update_page();
}

function d6() { return Math.floor( Math.random() * 6 ) + 1}
function show_fight( enemies ) {
	nmy = enemies; 
	$('#results').append(`<div><b>Fight ${ nmy.map( e => e.pn).join(',') }</b></div>`)
	cb = do_fight();
	return cb;
}
// TRANSLATION

$('#lang').val("Whpod jstv rbsum yusl vmbfr?")

$('#trans').on('click',function() {
	let t = $('#lang').val();
	t = t.replace(/\s/g, '');
	s = t.split(/[aeiou]/gi);
	st = s.map( w => { return translate(w); });
	r = st.join(' ');
	alert(r);
})

function translate (w) {
	r = w//.replace('b','a')
			.replace(/f/gi, 'e')
			.replace('j', 'i')
			.replace('p', 'o')
			.replace('v', 'u');
	//$.getJSON('https://api.dictionaryapi.dev/api/v2/entries/en/' + w, function(data) {
	//	return w;
	//}).fail( function() {
	//	return "false"
	//});
	//console.log (r);
	return r;
}

//ab ef ij op uv