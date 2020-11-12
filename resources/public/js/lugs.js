var _keywords = [];
var _icons = [];
var _cards = [];
//var _char = {
//  occupation: "",
//  weapons: [],
//  talents: [],
//  kit: []
//};
var _char={"weapons":["GS-WE00","GS-WE01"],"talents":[],"kit":[],"occupation":"GS-OC01"}


/*

Max 2 W, 2 Items, 1 Outfit

L1 Base Weapon + 3 Talents
Lone Wolf +1 Skill Talent, +1 Weapon Talent, +1 Kit

L Benefit #upgrades/talents
2 Upgrade  1/3
3 Talent   1/4
4 Upgrade  2/4
5 Upgrade  3/4
6 Talent   3/5
7 Upgrade  4/5
8 Upgrade  5/5
9 Apprentice
*/

function write_char() {
  var outp  = '<div class="card w-100"><div class="card-body">';
  var occ = _cards({"id": _char.occupation}).first();
  if (occ) {
    var occ = _cards({"id": _char.occupation}).first();
    outp += '<h3 class="text-secondary">' + occ.name 
      + '<span class="float-right">' + get_icon(occ.specialty) + '</span>'
      + '</h3>';
  }
  outp += '<div class="row"><div class="col-sm-6">';
  if (occ) {
    outp += data_table(occ.attributes);
  }
  outp += '</div><div class="col-sm-6">';
  $.each(_char.weapons,function (id, w) {
    var wep = _cards({"id":w}).first();
    outp+= '<div>' + wep.basic.name + '</div>';
  });
  outp += '</div></div>';
  
  outp += '</div></div>';
  $('#hdata').html(outp);
}

$.getJSON("/api/data/lu",function(data) {
  _keywords = data.keywords;
  _icons = TAFFY(data.icons);
  _cards = TAFFY(data.cards);
  
  write_char();
  $('#optionbuttons').trigger('change');
});

function get_icon(ico) {
  var icon = _icons({"name":ico}).first();
  
  if (typeof ico == 'undefined') {
    return '';
  } else if (typeof icon.fa !== 'undefined') {
    return '<i class="' + icon.fa + '" title="' + icon.name + '"/>';
  } else {
    return '<i>' + ico + '</i>';
  }
}

function markdown(t) {
  var rtn = t;
  rtn = rtn.replace(/(\+[0-9]+)/g,'<b>$1</b>');
  $.each(rtn.match(/\[\w+\]/g),function (k,v) {
    rtn = rtn.replace(v,get_icon(v.replace(/\[|\]/g,'')));
  });
  $.each(_keywords,((id,kw) => rtn = rtn.replace(new RegExp('(' + kw + ')','g'),'<b>$1</b>')));
  return rtn;
}

function data_table(data) {
  var rtn = '<table class="table table-sm table-striped">';
  $.each(data, function (k,v) {
    rtn += '<tr><th>' + k + '</th><td>' + v + '</td></tr>';
  });
  rtn +='</table>';
  return rtn
}
function b_u_toggle(id,basic) {
  return '<button type="button" class="btn btn-outline-warning btn-sm float-right upgraded-btn' + (basic ? '' : ' active') + '" data-id="' + id + '">'
    + (basic ? "Basic" : "Upgraded") 
    + '</button>';
}
function card_footer(c,inChar) {
  return '<div class="card-footer py-1">'
    + '<small class="text-muted">' + c.id + '</small>'
    + (!inChar
      ? '<button class="float-right btn btn-sm btn-outline-success addrmvcard" data-id="' + c.id + '" data-add=true><i class="fas fa-plus" /></button>'
      : '<button class="float-right btn btn-sm btn-outline-danger addrmvcard" data-id="' + c.id + '" data-add=false><i class="fas fa-minus" /></button>')
    + '</div>';
}

function occupation_card (c) {
  return '<div class="card-body">'
    + '<h5 class="card-title">' + c.name 
    + '<span class="float-right">' + get_icon(c.specialty) + '</span>'
    + '</h5>'
    + data_table(c.attributes)
    + '</div>'
    + card_footer(c,c.id == _char.occupation);
}
function weapon_card(c, basic) {
  var we = (basic == true ? c.basic : c.upgraded);
  return '<div class="card-body">'
    + '<h5 class="card-title">' + we.name + b_u_toggle(c.id,basic) + '</h5>'    
    + '<div class="muted">' + c.type + '</div>'
    + data_table(we.attack)
    + '<div class="mb-2"><i>' + we.text + '</i></div>'
    + '<div><b>' + c.broken.title + '</b>&nbsp;' + markdown(c.broken.text) + '</div>'
    + '</div>'
    + card_footer(c,$.inArray(c.id,_char.weapons)>-1);
}
function weapontalent_card(c) {
  var rtn = '<div class="card-body">'
    + '<h5 class="card-title">' + c.name
    + '<span class="text-muted float-right">' + c.weapon + '</span>'
    + '</h5>';
  $.each(c.abilities, function (id, a) {
    rtn += '<div class="border-top border-secondary pt-1 mb-2">';
    $.each(a.icons,((k,ico) => rtn+='<span class="float-right ml-1">'+get_icon(ico)+'</span>'));
    rtn += '<div><b>' + a.name + '</b></div>'
      + '<div>'
      + '<span class="h4 mr-2">' + get_icon(a.cost) + '</span>'
      + markdown(a.text) + '</div></div>';
  });
  rtn += '</div>'
    + card_footer(c,$.inArray(c.id,_char.talents)>-1);
  return rtn;
}
function skilltalent_card(c) {
  var rtn = '<div class="card-body">'
    + '<h5 class="card-title">' + c.name 
    + '<span class="float-right">' + get_icon(c.specialty) + '</span>'
    + '</h5>'
    + '<div class="text-muted">' + c.background + '</div>';
  $.each(c.abilities, function (id, a) {
    rtn += '<div class="border-top border-secondary pt-1 mb-2">';
    $.each(a.icons,((k,ico) => rtn+='<span class="float-right ml-1">'+get_icon(ico)+'</span>'));
    rtn += '<div><b>' + a.name + '</b></div>'
      + '<div><i>' + a.cost + '</i></div>'
      + '<div>' + markdown(a.text) + '</div></div>';
  });
  rtn += '</div>'
    + card_footer(c,$.inArray(c.id,_char.talents)>-1);
  return rtn;
}
function kit_card(c,basic) {
  var ki = (basic == true ? c.basic : c.upgraded);
  return '<div class="card-body">'
    + '<h5 class="card-title">' + ki.name + b_u_toggle(c.id,basic) + '</h5>'    
    + '<div class="text-muted">' + c.type + '</div>'
    + '<div style="white-space: pre-wrap;">' + ki.text + '</div>'
    + (typeof c.broken != 'undefined' ? '<div><b>' + c.broken.title + '</b>&nbsp;' + c.broken.text + '</div>' : '')
    + '</div>'
    + card_footer(c,$.inArray(c.id,_char.talents)>-1);
}

function card_html(c,basic) {
  var re = /([A-Z]{2})\-([A-Z]{2})([0-9]{2})/
  re.exec(c.id);
  switch (RegExp.$2) {
    case "OC": 
      return occupation_card(c);
      break;
    case "WE":
      return weapon_card(c,basic);
      break;
    case "ST":
      return skilltalent_card(c);
      break;
    case "WT":
      return weapontalent_card(c);
      break;
    case "OU":
    case "KI":
      return kit_card(c,basic);
      break;
  }
}
  

function buildlist(obj) {
  var outp = '';
  obj().each(function(c,id) {
    if ((id % 2) == 0) {outp += '<div class="card-deck mb-2  mx-0 w-100">'}
    outp += '<div class="card" data-id="' + c.id + '" data-type="' + c.type + '">';
    outp += card_html(c,true);  
    outp += '</div>';
    if ((id % 2) == 1) {outp += '</div>'};
  });
  $('#cardlist').html(outp);
}

$('#cardlist').on('click','.upgraded-btn',function() {
  var basic = $(this).hasClass('active');
  var c = _cards({"id":$(this).data('id')}).first();
  
  $(this).closest('.card').html(card_html(c,basic));
});

$('#optionbuttons').on('change',function () {
  var cardtype = $(this).find('input:checked')[0].id;
  switch (cardtype) {
    case "weaponupgrade":
      buildlist(TAFFY(_cards({"type":["Weapon"],"id":{"!=":"GS-WE00"}}).get()));
      break;
    case "skilltalent":
      buildlist(TAFFY(_cards({"type": "Skill Talent"}).get()));
      break;
    case "weapontalent":
      buildlist(TAFFY(_cards({"type": "Weapon Talent"}).get()));
      break;
    case "outfitupgrade":
      buildlist(TAFFY(_cards({"type": "Outfit"}).get()));
      break;
    case "kitupgrade":
      buildlist(TAFFY(_cards({"type": "Kit"}).get()));
      break;
    default: 
      buildlist(TAFFY(_cards({"id":{"left":"GS-OC"}}).get()));
  }
});

$('#cardlist').on('click','.addrmvcard',function () {
  var id = $(this).data('id');
  var addcard = $(this).data('add');
  var c;
  /([A-Z]{2})\-([A-Z]{2})([0-9]{2})/.exec(id)
  switch (RegExp.$2) {
    case "OC":
      delete _char.occupation;
      if (addcard) {_char.occupation = id};
      break;
    case "WE":
      _char.weapons = _char.weapons.filter(w => w != id);
      if (addcard) {_char.weapons.push(id)};
      break;
    case "ST":
    case "WT":
      _char.talents = _char.talents.filter(w => w != id);
      if (addcard) {_char.talents.push(id)};
      break;
    case "OU":
    case "KI":
      _char.kit = _char.talents.filter(w => w != id);
      if (addcard) {_char.kit.push(id);}
      break;
  }
  
  $('#optionbuttons').trigger('change');
  write_char();
  
});