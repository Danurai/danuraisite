var decks = {};
var players = {};
var regions = {};
var handsize = 5;
var regCount;	//Corp Only
var playerids = ['corp','run'];

$(document).ready(function ()	{
	// Alt: get published decklist from netrunnerdb https://netrunnerdb.com/api/2.0/public/decklist/29088
	// data.name, data.cards = {"{id}":{number}} !Includes ID
	var htmlout = '';
	var _cards = TAFFY(nrdb_cards.data);
	var imgurl = nrdb_cards.imageUrlTemplate;
	regCount = 0;
	
	// Code. Agenda\Asset\deck \ Upgrade \ Ice. Rezzed. 	
	// Region = ["{code}",]
	// Region = ["code":"{code}"]
	regions['corp'] = {"scored":[],"corphand":[],"hq":[],"archives":[],"randd":[]};
	regions['run'] = {"stolen":[],"runhand":[],"resource":[],"hardware":[],"program":[]};
		
// Initialisation
	$.each(playerids,function(id,faction)	{
		$.each(_decks[faction], function (idx,strdeck)	{
			htmlout += '<li data-deckidx="' + idx + '"><a role="menuitem">' + strdeck.match(/(.+)/g)[0] + '</a></li>';
		});
	// Pre-populate list of decks :: Alt: get published decklist from netrunnerdb\thronesdb etc https://netrunnerdb.com/api/2.0/public/decklist/29088
		$('#' + faction + 'decks').html(htmlout);
	// Load Initial Deck
		$('#' + faction + 'dl').html(_decks[faction][0]);
	// Create new Player and decks
		players[faction] = new anrPlayer(faction);
		loadDeck(faction);
		updateInfo(faction);
		updateRegion(faction);
	});
	
	function loadDeck(faction)	{
		var decklist = $('#' + faction + 'dl').val();
		var deckinfo = parseDeck(decklist);
		decks[faction] = new anrDeck(deckinfo.data);
		
		decks[faction].setMeta("title",deckinfo.title);
		decks[faction].setMeta("idcode",deckinfo.idcode);
		decks[faction].setMeta("idname",deckinfo.idname);
		decks[faction].setMeta("faction",faction);
		resetDeck(decks[faction]);
	}	
	function resetDeck(deck)	{
		// Shuffle, draw 5, clear & render
		var faction = deck.getMeta('faction');
		deck.resetCards();
		players[faction].reset();
		// Clear Regions
		if (faction == 'corp')	{
			regions['corp'] = {"scored":[],"corphand":[],"hq":[],"archives":[],"randd":[]};
			regions['run'].stolen = [];
			regCount = 0;
		}
		if (faction == 'run')	{
			regions['run'] = {"stolen":[],"runhand":[],"resource":[],"hardware":[],"program":[],"trash":[]};
		}
		//Draw 5
		for (var n=0; n<5; n++)	{drawCard(deck);}
		$.each(players, function (key,data)	{
			updateInfo(key);
			updateRegion(key);
		});
	}
	function drawCard(deck, idx=0)	{
		var faction = deck.getMeta('faction');
		var code = deck.draw(idx);
		var isRoot = false;
		if (code != "00000")	{
			isRoot = _cards({"code":code,}).first().type_code != 'ice';
			regions[faction][faction + 'hand'].push({"code":code,"counters":0,"root":isRoot,"rez":faction=='run'});
			updateRegion(faction);
			updateChooseList(faction);
		}
	}

	
	
		
// Listeners

// Load Decks
	// Select Deck
	$('.dropdown-deck').on('click','li',function()	{
		var idx = $(this).data('deckidx');
		var faction = $(this).closest('ul').attr('for');
		$('#' + faction + 'dl').html(_decks[faction][idx]);
		loadDeck(faction)
	});
	$('.btn-load').on('click',function () {
		loadDeck($(this).attr('for'));
	});
	
	$('.btn-draw').on('click',function() {
		var faction = $(this).closest('div').attr('for');
		var deck = decks[faction];
		if ($(this).attr('val') == 0) {
			resetDeck(deck);
		} else {
			var n = $(this).attr('val') == "all" ? deck.cardsInDeck() : $(this).attr('val');
			for (var i=0; i<n; i++) {drawCard(deck)};
		}
	});
	
	$(document).on('click','li.card-picker',function () {
		var faction = decks[$(this).closest('ul').attr('for')];
		drawCard(faction, $(this).data('index'));
	});
	
// Resources
	$(document)
		.on('click','.btn-cred',function()	{
			var faction = $(this).closest('.btn-group').attr('for');
			var value = parseInt($(this).attr('val'),10);
			players[faction].addCreds(value);
			updateInfo(faction);
		})
		.on('click','.btn-score',function()	{
			var faction = $(this).closest('.btn-group').attr('for');
			var value = parseInt($(this).attr('val'),10);
			players[faction].addScore(value);
			updateInfo(faction);
		})
		.on('click','.btn-mu',function()	{
			var faction = $(this).closest('.btn-group').attr('for');
			var value = parseInt($(this).attr('val'),10);
			players[faction].addMU(value);
			updateInfo(faction);
		})
		.on('click','.btn-access',function(ev)	{
			var crdlist = [];
			var accessdeck;
			var crd;
			var src = '';
			var accessSrc;
			var tgt = 'stolen';
			var idx;
			
			accessSrc = $(this).data('tgt');
			
			if (accessSrc == "hand")	{
				$.each(regions['corp']['corphand'],function(idx,item)	{
					crdlist.push(item.code);
				})
				accessdeck = new anrDeck(crdlist);
				accessdeck.resetCards();	//shuffles
				src = 'corphand';
			} else {
				accessdeck = decks['corp'];
			}
			crds = accessdeck.getDeck();
			var outp = "<div>Access:</div>"
				+ '<div class="btn-group-vertical">'
			$.each(crds,function(n,code)	{
				crd = _cards({"code":code}).first();
				if (accessSrc == "hand") {
					$.each(regions['corp']['corphand'],function (i,handcrd)	{
						if (handcrd.code == crd.code) {
							idx = i;
						}
					});
				}
				outp += menuButton(src,idx,tgt,'corp',crd.title);
			})
			outp += '</div>';
			$('#popupmenu').html(outp);
			$('#popupmenu').css({"left":ev.pageX,"top":ev.pageY});
			$('#popupmenu').toggle();
		})
		;
// Card info
	$(document)
		.on('click','.card-info',function(ev) {
			var outp = '';
			var card;
			var code = $(this).data('code');
			
			if (typeof code !== 'undefined') {
				card = _cards({"code":code}).first();
				outp += '<img class="card-popup" src="' + imgurl.replace("{code}",code) + '"></img>';
			}
			$('#popupmenu').html(outp);
			$('#popupmenu').css({"left":Math.max(ev.pageX - 150,0),"top":ev.pageY - 20});
			$('#popupmenu').toggle();
		})
		.on('click','.card-popup',function () {
			$('#popupmenu').css("display","none");
		});
	
		
// Create Menu
	$(document)
		.on('click','.card-deck', function(ev)	{
			var outp = '';
			var src  = $(this).closest('div.region').attr('id');
			var idx = $(this).data('idx');
			var faction = $(this).closest('div.region').attr('for');
			
			// Move To: Regions
			outp = '<div class="small"><b>Move To:</b></div>'
				+ '<div class="btn-group-vertical btn-group-sm" style="padding: 5px;">';
			$.each(regions[faction],function(tgt,crds)	{
				outp += menuButton(src,idx,tgt,faction,$('#' + tgt).attr('name') + (tgt == src ? ' >>' : ''));
			});
			// New Region
			if (faction == 'corp')	{
				outp += '<span class="btn-separator"></span>';
				outp += menuButton(src,idx,"new",faction,"New Region");
			}
			// Deck
			outp += menuButton(src,idx,"deck",faction,"Deck & Shuffle");
			// Rez Card
			outp += (faction == 'corp' ? menuButton(src,idx,"actRez",faction,'<span class="icon-subroutine"></span> Rez\\DeRez'):'');
			// Trash Card
			outp += menuButton(src,idx,"actTrash",faction,'<span class="icon-trash"></span> Trash');
			
			// Add\Remove counters
			outp += menuButton(src,idx,"actAddCount",faction,'<span class="icon-click"></span> Add Counter');
			outp += menuButton(src,idx,"actRemAll",faction,'<span class="icon-click"></span> Remove Counters');
			
			outp += '</div>';
			
			$('#popupmenu').html(outp);
			$('#popupmenu').css({"left":ev.pageX,"top":ev.pageY});
			$('#popupmenu').toggle();
		})
		.on('click','.card-counter',function(ev)	{
			var outp = '';
			var src  = $(this).closest('div.region').attr('id');
			var idx = $(this).parent().find('.card-deck').data('idx');
			var faction = $(this).closest('div.region').attr('for');
			outp = '<div class="small"><b>Move To:</b></div>'
				+ '<div class="btn-group-vertical btn-group-sm" style="padding: 5px;">'
				+ menuButton(src,idx,"actAddCount",faction,'<span class="icon-click"></span> Add Counter')
				+ menuButton(src,idx,"actRemCount",faction,'<span class="icon-click"></span> Remove Counter')
				+ menuButton(src,idx,"actRemAll",faction,'<span class="icon-click"></span> Remove All')
				+ '</div>';
			
			$('#popupmenu').html(outp);
			$('#popupmenu').css({"left":ev.pageX,"top":ev.pageY});
			$('#popupmenu').toggle();
		});
	function menuButton(src,idx,tgt,faction,btnTxt)	{
		var btn = '<button type="button" class="btn btn-default" '
				+ 'data-src="' + src + '" '
				+ 'data-idx="' + idx + '" '
				+ 'data-tgt="' + tgt + '" '
				+ 'for="' + faction + '" '
				+ '>' 
				+ btnTxt
				+ '</button>'
		return btn;
	}

// Click Menu
	$('#popupmenu').on('click','.btn',function()	{
		var faction = $(this).attr('for');
		var src = regions[faction][$(this).data('src')];
		var idx = $(this).data('idx');
		var tgt = regions[faction][$(this).data('tgt')];
		
		if ($(this).data('tgt') == 'stolen')	{
			tgt = regions['run']['stolen'];
			if (typeof src == 'undefined')	{
				drawCard(decks['corp'],idx);
				src = regions['corp']['corphand'];
				idx = regions['corp']['corphand'].length-1;
			} 
		}
		
		$('#popupmenu').toggle();
		switch ($(this).data('tgt')) {
		// New region
			case ("new"):
				regCount ++;
				$('#corparea').append('<div class="col-md-12 region remote" for="corp" name="Region ' + regCount + '" id="region' + regCount + '"></div>');
				tgt = regions[faction]["region" + regCount] = [];
				moveCrd(src,idx,tgt);
				break;
			case ("deck"):
				if (decks[faction].returnToDeck(src[idx].code))	{
					src.splice(idx,1);
				}
				break;
			case ("actTrash"):
				if (decks[faction].discardCard(src[idx].code))	{
					src.splice(idx,1);
				}
				break;
			case ("actAddCount"):
				src[idx].counters ++;
				break;
			case ("actRemCount"):
				src[idx].counters --;
				break;
			case ("actRemAll"):
				src[idx].counters = 0;
				break;
			case ("actRez"):
				src[idx].rez = !src[idx].rez;
				break;
			default:	// Region
				if (typeof tgt !== "undefined")	{
					moveCrd(src,idx,tgt);
				}
		}
		$.each(['corp','run'],function(idx,faction) {
			updateRegion(faction);
			updateChooseList(faction);
		});
	});

	function moveCrd(src,idx,tgt)	{
		var res, gain;
		tgt.push(src.splice(idx,1)[0]);
		// Adjust Runner Creds etc.
		var code = tgt.slice(-1)[0].code;
		var card = _cards({"code":code}).first();
		/*var regex = /^Gain\s([0-9]+)\[credit\]/gi;
		
		card.text.match(regex);
		res = RegExp.$1;
		
		gain = (res == '' ? 0 : parseInt(res,10));
		
		if (card.side_code == 'runner')	{
			players['run'].addCreds(card.cost * -1);
			players['run'].addCreds(gain);
		}
		if (card.side_code == 'corp' && card.type_code == 'operation')	{
			players['corp'].addCreds(card.cost * -1);
			players['corp'].addCreds(gain);
		}*/
		$.each(['corp','run'],function(idx,faction) {
			updateInfo(faction);
			updateRegion(faction);
			updateChooseList(faction);
		});
	}
	
// Screen Rendering Functions
	function updateChooseList(faction)	{
		var outp='';
		outp += '<div class="btn-group"><button type="button" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown">Choose <span class="caret"></span></button>';
		outp += '<ul class="dropdown-menu scrollable-menu" role="menu" for="' + faction + '">';
		
		$.each(decks[faction].getDeck(),function (id,code) {
			card = _cards({"code":code}).first();
			outp += '<li style="cursor: pointer;" role="presentation" class="card-picker" data-index="' + id + '"><a role="menuitem" class="card-picker" data-code="' + code + '">' + card.title + '</a></li>';
		});
		
		outp += '</ul>';
		outp += '</div>';
		$('#' + faction + 'cardlist').html (outp);
	}
	
	function updateInfo(faction)	{
		var deck = decks[faction];
		$('#' + faction + 'info').html ('<h3>' + deck.getMeta('title') + '</h3><b>' + deck.getMeta('idname') + '</b>');
		$('#' + faction + 'creds').html(players[faction].getCreds());
		$('#' + faction + 'score').html(players[faction].getScore());
		if (faction == 'run') {
			$('#runmu').html(players['run'].getMU());
		}
	}
	
	function updateRegion(faction)	{
		var outp;
		// Remove empty corp regions
		$('#corparea').find('.remote').each(function(idx,ele)	{
			var rgn = $(ele).attr('id');
			if (regions['corp'][rgn].length == 0)	{
				delete regions['corp'][rgn];
				ele.remove();
			}
		});
		$.each(regions[faction], function(rgn,crds)	{
			outp = '<div class="region-title">' + $('#' + rgn).attr('name') + '</div>';
		// Add Root for HQ, Archives & R&D
			switch (rgn)	{
				case ('archives'):
					outp += '<div class="region-root">';
					outp += '<img src="img\\corp_back.png" class="card card-root" draggable="false"'
						+ (decks[faction].cardsInDiscard() == 0 ? ' style="opacity: 0.5;"' : '')
						+ '></img>';
					outp += '<span class="card-count">' + decks[faction].cardsInDiscard() + '</span>';
					break;
				case ('randd'):
					outp += '<div class="region-root">';
					outp += '<img src="img\\corp_back.png" class="card card-root" draggable="false"'
						+ (decks[faction].cardsInDeck() == 0 ? ' style="opacity: 0.5;"' : '')
						+ '></img>';
					outp += '<span class="card-count">' + decks[faction].cardsInDeck() + '</span>';
					break;
				case ('hq'):
					outp+='<div class="region-root">';
					outp+=getCardImgEle(faction,rgn,{code: decks[faction].meta.idcode,rez:true},0)
					outp+='</div>';
			}
			$.each(crds, function(idx,regCrd)	{
				outp += getCardImgEle(faction,rgn,regCrd,idx);
			});
			$('#' + rgn).html(outp);
			//console.log (regions[faction]);
		});
	}
	function getCardImgEle(faction,rgn,regCrd,idx)	{
		var crd = _cards({"code":regCrd.code}).first();
		var outp = '<div class="region-installed region-installed-' + faction + '">';
		outp += '<img '
			+ 'src="' + imgurl.replace('{code}',crd.code) + '"'
			+ 'class="card card-deck'
			+ (!regCrd.rez && $.inArray(rgn,['corphand'])==-1 ? ' card-unrezzed' : '')
			+ '" draggable="true" '
			+ 'alt="' + crd.title + '" '
			+ 'data-code="'+ crd.code + '" '
			+ 'data-idx="'+ idx + '">'
			+ '</img>';
		if (regCrd.counters > 0)	{
			outp += '<span class="card-counter"><span class="counter-value">' + regCrd.counters + '</span></span>';
		}
		outp += '<span class="card-info" data-code="' + crd.code + '"><i class="fa fa-info-circle fa-lg" aria-hidden="true"></i></span>';
		outp += '</div>';
		return outp;
	}

// Load Deck Text
	function parseDeck(data)	{
	// Create decklist from cards
	
		var crd;
		var deck = {};
		deck.title = "";
		deck.idname = "";
		deck.data = [];
	// Find Identity
		var idregex = /(.+)/g
		var idres = data.match(idregex);
	// Simple
		deck.title = idres[0];
		deck.idname = idres[1];
		deck.idcode = _cards({"stripped_title":deck.idname}).first().code; //Special Characters - CT fixed by not using \\uuml; on textarea
			
	// 
		var regex = /([0-9])x\s((.+)\s\s\W+|(.+))/g;				// Look out for STAR special character
		var res = data.match(regex);
				
		$.each(res, function (id, item) {
			item.match(regex);
			var qty = parseInt(RegExp.$1, 10);
			// STRIP DOTS ●
			// 3x Predictive Planogram ●●●
			var cname = (RegExp.$4 != "" ? RegExp.$2 : RegExp.$3).replace(/\s[\u25cf]{1,}/g,'');
			crd = _cards({"stripped_title":cname}).last();
			//console.log (qty + 'x ' + crd.title);
			for (var i=0; i < qty; i++)	{deck.data.push(crd.code);}
		});
		//console.log('Deck Loaded:');
		//console.log(deck);
		
		return deck;		
	}
	
// Drag and Drop
	$(document)
		.on('dragstart','.card-deck', function(ev)	{
			var jsonData = {};
			jsonData["faction"] = $(this).closest('div.region').attr('for');
			jsonData["src"] = $(this).closest('div.region').attr('id');
			jsonData["idx"] = $(this).data('idx');
			console.log(jsonData);
			ev.originalEvent.dataTransfer.setData('text/plain',JSON.stringify(jsonData));
		});
	$(document)
		.on('dragover','.region',function(ev)	{
			ev.preventDefault();
			ev.originalEvent.dataTransfer.dropEffect = 'move'; 
			$(this).addClass('region-drop');
		})
		.on('dragleave','.region',function(ev)	{
			$(this).removeClass('region-drop');
		})
		.on('drop','.region',function(ev)	{
			ev.preventDefault();
			$(this).removeClass('region-drop');
			
			var jsonData = JSON.parse(ev.originalEvent.dataTransfer.getData('text'));
			var tgt = $(this).attr('id');
			var tgtFaction = $(this).attr('for');
			
			moveCrd(regions[jsonData.faction][jsonData.src],jsonData.idx,regions[tgtFaction][tgt]);
		});
	
	
	
	
});