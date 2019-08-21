
var compare = []
var outp = '';
var _filter = {};
var _sort = '';
var data = [{"name": "'Ardcoat","range": "Technical","hex": "#E2DEDF"}, {"name": "Abaddon Black","oldgw": "Chaos Black","range": "Base","hex": "#231F20"}, {"name": "Administratum Grey","oldgw": "Fortress Grey","range": "Layer","hex": "#949B95"}, {"name": "Agrax Earthshade (Gloss)","oldgw": "Devlan Mud","range": "Shade","hex": "#5A573F"}, {"name": "Agrax Earthshade","oldgw": "Devlan Mud","range": "Shade","hex": "#5A573F"}, {"name": "Agrellan Badland","range": "Texture","hex": ""}, {"name": "Agrellan Earth","range": "Technical","hex": "#9A816B"}, {"name": "Agrellan Earth","range": "Texture","hex": ""}, {"name": "Ahriman Blue","range": "Layer","hex": "#1F8C9C"}, {"name": "Alaitoc Blue","range": "Layer","hex": "#295788"}, {"name": "Altdorf Guard Blue","oldgw": "Ultramarines Blue","range": "Layer","hex": "#1F56A7"}, {"name": "Armageddon Dunes","range": "Texture","hex": ""}, {"name": "Armageddon Dust","range": "Texture","hex": "#D3A907"}, {"name": "Astorath Red","range": "Dry","hex": "#DD482B"}, {"name": "Astrogranite Debris","range": "Texture","hex": ""}, {"name": "Astrogranite","range": "Texture","hex": "#757679"}, {"name": "Athonian Camoshade","range": "Shade","hex": "#6D8E44"}, {"name": "Auric Armour Gold (Metal)","oldgw": "Burnished Gold (Metal)","range": "Layer","hex": "#E8BC6D"}, {"name": "Averland Sunset","oldgw": "Iyanden Darksun","range": "Base","hex": "#FDB825"}, {"name": "Baharroth Blue","range": "Edge","hex": "#58C1CD"}, {"name": "Balor Brown","oldgw": "Snakebite Leather, Tausept Ochre","range": "Layer","hex": "#8B5910"}, {"name": "Balthasar Gold (Metal)","range": "Base","hex": "#A47552"}, {"name": "Baneblade Brown","oldgw": "Kemri Brown","range": "Layer","hex": "#937F6D"}, {"name": "Bestigor Flesh","oldgw": "Hobgolblin Orange*","range": "Layer","hex": "#D38A57"}, {"name": "Biel-Tan Green","oldgw": "Thraka Green","range": "Shade","hex": "#1BA169"}, {"name": "Blackfire Earth","range": "Texture","hex": "#A75820"}, {"name": "Blood for the Blood God","range": "Technical","hex": "#67080B"}, {"name": "Bloodletter","range": "Glaze","hex": "#F37355"}, {"name": "Blue Horror","range": "Edge","hex": "#A2BAD2"}, {"name": "Brass Scorpion (Metal)","range": "Layer","hex": "#B7885F"}, {"name": "Bugmans Glow","range": "Base","hex": "#834F44"}, {"name": "Cadian Fleshtone","oldgw": "Tallarn Flesh","range": "Layer","hex": "#C77958"}, {"name": "Caledor Sky","oldgw": "Enchanted Blue","range": "Base","hex": "#396E9E"}, {"name": "Calgar Blue","range": "Layer","hex": "#4272B8"}, {"name": "Caliban Green","oldgw": "Dark Angels Green, Orkhide Green, Woodland Green*","range": "Base","hex": "#00401F"}, {"name": "Carroburg Crimson","oldgw": "Baal Red","range": "Shade","hex": "#A82A70"}, {"name": "Casandora Yellow","range": "Shade","hex": "#FECE5A"}, {"name": "Castellan Green","oldgw": "Catachan Green","range": "Base","hex": "#314821"}, {"name": "Celestra Grey","oldgw": "Astronomican Grey","range": "Base","hex": "#90A8A8"}, {"name": "Ceramite White","range": "Base","hex": "#FFFFFF"}, {"name": "Changeling Pink","oldgw": "Tentacle Pink*, Titillating Pink*","range": "Dry","hex": "#F4AFCD"}, {"name": "Chronus Blue","range": "Dry","hex": "#72A8D1"}, {"name": "Coelia Greenshade","range": "Shade","hex": "#0E7F78"}, {"name": "Daemonette Hide","oldgw": "Hormagaunt Purple, Worm Purple*","range": "Base","hex": "#696684"}, {"name": "Dark Reaper","range": "Layer","hex": "#3B5150"}, {"name": "Dawnstone","oldgw": "Codex Grey, Elf Grey*","range": "Dry","hex": "#919C9F"}, {"name": "Dawnstone","range": "Layer","hex": "#70756E"}, {"name": "Death Guard Green","oldgw": "Ghould Grey*","range": "Base","hex": "#848A66"}, {"name": "Deathclaw Brown","range": "Layer","hex": "#B36853"}, {"name": "Deathworld Forest","oldgw": "Gretchin Green","range": "Base","hex": "#5C6730"}, {"name": "Dechala Lilac","oldgw": "Tentacle Pink*","range": "Edge","hex": "#B69FCC"}, {"name": "Doombull Brown","oldgw": "Dark Flesh","range": "Layer","hex": "#5D0009"}, {"name": "Dorn Yellow","range": "Edge","hex": "#FFF200"}, {"name": "Drakenhof Nightshade","oldgw": "Asurman Blue","range": "Shade","hex": "#125899"}, {"name": "Druchii Violet","oldgw": "Leviathan Purple","range": "Shade","hex": "#7A468C"}, {"name": "Dryad Bark","range": "Base","hex": "#33312D"}, {"name": "Eldar Flesh","oldgw": "Bronzed Flesh* #F7944A","range": "Dry","hex": "#ECC083"}, {"name": "Elysian Green","oldgw": "Camo Green","range": "Layer","hex": "#748F39"}, {"name": "Emperors Children","oldgw": "Imperial Purple*","range": "Layer","hex": "#B94278"}, {"name": "Eshin Grey","range": "Layer","hex": "#4A4F52"}, {"name": "Etherium Blue","range": "Dry","hex": "#A2BAD2"}, {"name": "Evil Sunz Scarlet","oldgw": "Blood Red","range": "Layer","hex": "#C2191F"}, {"name": "Fenrisian Grey","oldgw": "Space Wolves Grey","range": "Layer","hex": "#719BB7"}, {"name": "Fire Dragon Bright","oldgw": "Fiery Orange","range": "Layer","hex": "#F58652"}, {"name": "Flash Gitz Yellow","oldgw": "Sunburst Yellow","range": "Layer","hex": "#FFF200"}, {"name": "Flayed One Flesh","range": "Layer","hex": "#F0D9B8"}, {"name": "Fuegan Orange","range": "Shade","hex": "#C77E4D"}, {"name": "Fulgrim Pink","oldgw": "Titillating Pink*","range": "Edge","hex": "#F4AFCD"}, {"name": "Fulgurite Copper (Metal)","range": "Layer","hex": "#FCFCDE"}, {"name": "Gauss Blaster Green","range": "Edge","hex": "#84C3AA"}, {"name": "Gehenna's Gold (Metal)","oldgw": "Shining Gold (Metal)","range": "Layer","hex": "#DBA674"}, {"name": "Genestealer Purple","range": "Layer","hex": "#7761AB"}, {"name": "Golden Griffon (Metal)","range": "Dry","hex": "#A99058"}, {"name": "Golgfag Brown","range": "Dry","hex": "#C2804F"}, {"name": "Gorthor Brown","range": "Layer","hex": "#654741"}, {"name": "Guilliman Blue","range": "Glaze","hex": "#2F9AD6"}, {"name": "Hashut Copper (Metal)","oldgw": "Dwarf Bronze (Metal)","range": "Layer","hex": "#B77647"}, {"name": "Hellion Green","range": "Dry","hex": "#84C3AA"}, {"name": "Hexos Palesun","range": "Dry","hex": "#FFF200"}, {"name": "Hoeth Blue","range": "Dry","hex": "#57A9D4"}, {"name": "Hoeth Blue","range": "Layer","hex": "#4C7FB4"}, {"name": "Imperial Primer","range": "Technical","hex": "#231F20"}, {"name": "Imrik Blue","range": "Dry","hex": "#67AED0"}, {"name": "Incubi Darkness","range": "Base","hex": "#0B474A"}, {"name": "Ironbreaker (Metal)","oldgw": "Chainmail (Metal)","range": "Layer","hex": "#A1A6A9"}, {"name": "Jokaero Orange","oldgw": "Macharius Solar Orange","range": "Base","hex": "#EE3823"}, {"name": "Kabalite Green","range": "Layer","hex": "#038C67"}, {"name": "Kantor Blue","oldgw": "Regal Blue, Necron Abyss, Moody Blue*","range": "Base","hex": "#002151"}, {"name": "Karak Stone","oldgw": "Kommando Khaki","range": "Layer","hex": "#BB9662"}, {"name": "Khorne Red","oldgw": "Scab Red","range": "Base","hex": "#6A0001"}, {"name": "Kindleflame","range": "Dry","hex": "#F79E86"}, {"name": "Kislev Flesh","oldgw": "Elf Flesh","range": "Layer","hex": "#D6A875"}, {"name": "Krieg Khaki","range": "Edge","hex": "#C0BD81"}, {"name": "Lahmian Medium","range": "Technical","hex": "#F5EDE2"}, {"name": "Lamenters Yellow","range": "Glaze","hex": "#FFF56B"}, {"name": "Leadbelcher (Metal)","oldgw": "Boltgun Metal (Metal)","range": "Base","hex": "#888D8F"}, {"name": "Liberator Gold (Metal)","range": "Layer","hex": "#D3B587"}, {"name": "Liquid Green Stuff","range": "Technical","hex": "#3B7A5F"}, {"name": "Longbeard Grey","range": "Dry","hex": "#CECEAF"}, {"name": "Loren Forest","oldgw": "Knarloc Green","range": "Layer","hex": "#50702D"}, {"name": "Lothern Blue","oldgw": "Ice Blue","range": "Layer","hex": "#34A2CF"}, {"name": "Lucius Lilac","oldgw": "Tentacle Pink*","range": "Dry","hex": "#B69FCC"}, {"name": "Lugganath Orange","range": "Edge","hex": "#F79E86"}, {"name": "Lustrian Undergrowth","range": "Texture","hex": "#415A09"}, {"name": "Macragge Blue","oldgw": "Mordian Blue","range": "Base","hex": "#0D407F"}, {"name": "Martian Ironcrust","range": "Texture","hex": ""}, {"name": "Martian Ironearth","range": "Technical","hex": "#C15A4B"}, {"name": "Martian Ironearth","range": "Texture","hex": ""}, {"name": "Mechanicus Standard Grey","oldgw": "Adeptus Battlegrey","range": "Base","hex": "#3D4B4D"}, {"name": "Mephiston Red","oldgw": "Mechrite Red","range": "Base","hex": "#9A1115"}, {"name": "Moot Green","oldgw": "Scorpion Green","range": "Layer","hex": "#52B244"}, {"name": "Mourn Mountain Snow","range": "Texture","hex": "#E9EAEB"}, {"name": "Mournfang Brown","oldgw": "Bestial Brown, Calthan Brown","range": "Base","hex": "#640909"}, {"name": "Naggaroth Night","oldgw": "Liche Purple*, Imperial Purple xx","range": "Base","hex": "#3D3354"}, {"name": "Necron Compound (Metal)","range": "Dry","hex": "#828B8E"}, {"name": "Niblet Green","oldgw": "Bilious Green* #A9D171","range": "Dry","hex": "#7DC734"}, {"name": "Nihilakh Oxide","range": "Technical","hex": "#6CB79E"}, {"name": "Nuln Oil (Gloss)","oldgw": "Badab Black","range": "Shade","hex": "#14100E"}, {"name": "Nuln Oil","oldgw": "Badab Black","range": "Shade","hex": "#14100E"}, {"name": "Nurgle's Rot","range": "Technical","hex": "#9B8F22"}, {"name": "Nurgling Green","oldgw": "Rotting Flesh","range": "Dry","hex": "#B8CC82"}, {"name": "Nurgling Green","range": "Layer","hex": "#849C63"}, {"name": "Ogryn Camo","range": "Layer","hex": "#9DA94B"}, {"name": "Pallid Wych Flesh","range": "Layer","hex": "#CDCEBE"}, {"name": "Pink Horror","range": "Layer","hex": "#90305D"}, {"name": "Praxeti White","range": "Dry","hex": "#FFFFFF"}, {"name": "Rakarth Flesh","oldgw": "Dheneb Stone","range": "Base","hex": "#A29E91"}, {"name": "Ratskin Flesh","oldgw": "Dwarf Flesh","range": "Base","hex": "#AD6B4C"}, {"name": "Reikland Fleshshade (Gloss)","oldgw": "Ogryn Flesh, Flesh Wash* #CE8C42","range": "Shade","hex": "#CA6C4D"}, {"name": "Reikland Fleshshade","oldgw": "Ogryn Flesh, Flesh Wash* #CE8C42","range": "Shade","hex": "#CA6C4D"}, {"name": "Retributor Armour (Metal)","range": "Base","hex": "#C39E81"}, {"name": "Rhinox Hide","oldgw": "Scorched Brown","range": "Base","hex": "#493435"}, {"name": "Runefang Steel (Metal)","oldgw": "Mithril Silver (Metal)","range": "Layer","hex": "#C3CACE"}, {"name": "Runelord Brass (Metal)","range": "Layer","hex": "#B6A89A"}, {"name": "Russ Grey","range": "Layer","hex": "#547588"}, {"name": "Ryza Rust","range": "Dry","hex": "#EC631A"}, {"name": "Screamer Pink","oldgw": "Warlock Purple","range": "Base","hex": "#7C1645"}, {"name": "Screaming Bell (Metal)","range": "Base","hex": "#C16F45"}, {"name": "Screaming Skull","range": "Layer","hex": "#D2D4A2"}, {"name": "Seraphim Sepia","oldgw": "Gryphonne Speia","range": "Shade","hex": "#D7824B"}, {"name": "Sigmarite","range": "Dry","hex": "#CAAD76"}, {"name": "Skarsnik Green","range": "Layer","hex": "#5F9370"}, {"name": "Skavenblight Dinge","range": "Layer","hex": "#47413B"}, {"name": "Skink Blue","range": "Dry","hex": "#58C1CD"}, {"name": "Skrag Brown","oldgw": "Vermin Brown","range": "Layer","hex": "#90490F"}, {"name": "Skullcrusher Brass (Metal)","range": "Layer","hex": "#F1C78E"}, {"name": "Slaanesh Grey","oldgw": "Tentacle Pink*","range": "Dry","hex": "#DBD5E6"}, {"name": "Slaanesh Grey","range": "Layer","hex": "#8E8C97"}, {"name": "Sotek Green","oldgw": "Hawk Turquoise","range": "Layer","hex": "#0B6974"}, {"name": "Soulstone Blue","range": "Technical","hex": "#004EFA"}, {"name": "Spiritstone Red","range": "Technical","hex": "#FF4B24"}, {"name": "Squig Orange","range": "Layer","hex": "#AA4F44"}, {"name": "Steel Legion Drab","oldgw": "Graveyard Earth","range": "Base","hex": "#5E5134"}, {"name": "Stegadon Scale Green","range": "Base","hex": "#074863"}, {"name": "Stirland Battlemire","range": "Texture","hex": ""}, {"name": "Stirland Mud","range": "Texture","hex": "#492B00"}, {"name": "Stormfang","range": "Dry","hex": "#80A7C1"}, {"name": "Stormhost Silver (Metal)","range": "Layer","hex": "#BBC6C9"}, {"name": "Stormvermin Fur","oldgw": "Charadon Granite","range": "Layer","hex": "#736B65"}, {"name": "Straken Green","range": "Layer","hex": "#628126"}, {"name": "Sybarite Green","range": "Layer","hex": "#30A56C"}, {"name": "Sycorax Bronze (Metal)","range": "Layer","hex": "#CBB394"}, {"name": "Sylvaneth Bark","range": "Dry","hex": "#AC8262"}, {"name": "Tallarn Sand","oldgw": "Desert Sand","range": "Layer","hex": "#A67610"}, {"name": "Tau Light Ochre","oldgw": "Vomit Brown, Bronzed Flesh* #F7944A","range": "Layer","hex": "#BF6E1D"}, {"name": "Teclis Blue","oldgw": "Electric Blue*","range": "Layer","hex": "#317EC1"}, {"name": "Temple Guard Blue","range": "Layer","hex": "#339A8D"}, {"name": "Terminatus Stone","range": "Dry","hex": "#BDB192"}, {"name": "The Fang Grey","oldgw": "Shadow Grey, Fenris Grey","range": "Base","hex": "#436174"}, {"name": "Thousand Sons Blue","range": "Base","hex": "#18ABCC"}, {"name": "Thunderhawk Blue","range": "Dry","hex": "#509BA9"}, {"name": "Thunderhawk Blue","range": "Layer","hex": "#417074"}, {"name": "Troll Slayer Orange","oldgw": "Blazing Orange, Hobgblin Orange*","range": "Layer","hex": "#F36D2D"}, {"name": "Tuskgor Fur","oldgw": "Swamp Brown*","range": "Layer","hex": "#883636"}, {"name": "Typhus Corrosion","range": "Technical","hex": "#463D2B"}, {"name": "Tyrant Skull","range": "Dry","hex": "#CDC586"}, {"name": "Ulthuan Grey","range": "Layer","hex": "#C7E0D9"}, {"name": "Underhive Ash","range": "Dry","hex": "#C0BD81"}, {"name": "Ungor Flesh","oldgw": "Bronzed Flesh* #F7944A","range": "Layer","hex": "#D6A766"}, {"name": "Ushabti Bone","oldgw": "Bleached Bone","range": "Layer","hex": "#BBBB7F"}, {"name": "Valhallan Blizzard","range": "Texture","hex": ""}, {"name": "Verminlord Hide","range": "Dry","hex": "#A16954"}, {"name": "Waaagh! Flesh","range": "Base","hex": "#1F5429"}, {"name": "Warboss Green","oldgw": "Goblin Green","range": "Layer","hex": "#3E805D"}, {"name": "Warpfiend Grey","range": "Layer","hex": "#6B6A74"}, {"name": "Warplock Bronze (Metal)","oldgw": "Tin Bitz (Metal)","range": "Base","hex": "#927D7B"}, {"name": "Warpstone Glow","oldgw": "Snot Green, Woodland Green*","range": "Layer","hex": "#1E7331"}, {"name": "Waystone Green","range": "Technical","hex": "#00C000"}, {"name": "Waywatcher Green","range": "Glaze","hex": "#6DC066"}, {"name": "Wazdakka Red","oldgw": "Red Gore","range": "Layer","hex": "#8C0A0C"}, {"name": "White Scar","oldgw": "Skull White","range": "Layer","hex": "#FFFFFF"}, {"name": "Wild Rider Red","range": "Layer","hex": "#EA2F28"}, {"name": "Wrack White","range": "Dry","hex": "#FCFBFA"}, {"name": "XV-88","range": "Base","hex": "#72491E"}, {"name": "Xereus Purple","oldgw": "Liche Purple","range": "Layer","hex": "#471F5F"}, {"name": "Yriel Yellow","oldgw": "Golden Yellow","range": "Layer","hex": "#FFDA00"}, {"name": "Zamesi Desert","oldgw": "Bubonic Brown, Spearstaff Brown*, Orc Brown*","range": "Layer","hex": "#DDA026"}, {"name": "Zandri Dust","range": "Base","hex": "#9E915C"}]

// add "" for undefined oldgw
add_hsl();
var _data = TAFFY(data.map(c => (typeof c.oldgw == 'undefined' ? $.extend({oldgw: ""},c) : c)));

var ranges = data.map(c => c.range).filter(function (value, index, self) {return self.indexOf(value) === index});
$('#selectrange').html(ranges.sort().map(c => '<option>' + c + '</option>'));
$('#selectrange').selectpicker('refesh');


write_table();

function write_table () {
  $('#colours').empty();
  _data(_filter).order(_sort).each(function(v) {
    outp = '<tr>'
      + '<td>' + v.name + '</td>'
      + '<td>' + v.range + '</td>'
      + '<td>' + v.oldgw.replace(/\#([a-f\d]{6})/i,'<a href="hsl?q=$1" target="_blank">$&</a>') + '</td>'
      + '<td><a href="hsl?q=' + v.hex.slice(1) + '">' + v.hex + '</a></td>'
      + '<td>' + v.h + '</td>'
      + '<td>' + v.s + '</td>'
      + '<td>' + v.l + '</td>'
      + '<td class="sample" data-name="' + v.name + '" data-range="' + v.range + '" style = "background-color: ' + v.hex + '; width: 20%;">&nbsp;</td>'
      + '</tr>';
    $('#colours').append(outp);
  });
}

function swatch (c) {
  return '<div class="col-sm-2 px-0">'
    + '<div class="text-center">' + c.name + '<br>' + c.range + '</div>'
    + '<div class="text-center sample"  data-name="' + c.name + '" data-range="' + c.range + '" style="background-color: ' + c.hex + '; color: ' + fontcolour(c.hex) + ';">' 
    + '<div>' + c.hex + '</div>'
    + '<div>h: ' + c.h + ' s: ' + Math.round(c.s) + '% l: ' + Math.round(c.l) + '%</div>'
    + '<div>' + (c.oldgw == "" ? '&nbsp;' : c.oldgw) + '</div>'
    + '</div>' 
    + '</div>';
}

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
              
function write_comparison () {
  $('#comparison').empty();
  $.each(compare, function (k, v) {
    $('#comparison').append( swatch (_data({"name": v.name, "range": v.range}).first() ) );
  });
}

$('#colours').on('click','.sample', function () {
  var c = _data({name: $(this).data('name'), range: $(this).data('range')}).first();
  if (c.hex != "") { 
    compare.push();
    write_comparison();
  }
});
$('#comparison').on('click','.sample', function () {
  var q = {name: $(this).data('name'), range: $(this).data('range')};
  compare.splice(compare.findIndex(x => x.name==q.name && x.range == q.range),1);
  write_comparison();
});

// Sort and Filter

$('#selectrange').on('changed.bs.select', function (e) {
  var rngflt = $('#selectrange').selectpicker('val');
  if (rngflt.length == 0) {
    delete _filter.range;
  } else {
    _filter.range = rngflt;
  }
  write_table();
});

$('#filter').on('input', function () {
  if ($(this).val() == "") {
    delete _filter.name;
  } else {
    _filter.name = {"likenocase" : $(this).val()};
  }
  write_table();
}); 

$('.sortable').on('click', function () {
  var fld = ["name","range","oldgw","hex","h","s","l"][this.cellIndex];
  var dir = "asec";
  if (_sort.slice(0,fld.length) == fld ) {
    dir = (_sort.slice(-4) == "asec" ? "desc" : "asec")
  }
  _sort = fld + ' ' + dir;
  write_table();
  
  // visual clues
  $(this).closest('tr').remove('.caret');
  
  
})



// HSL CODE //

function add_hsl() {
// update data to include HSL values based on hex
  data = data.map(c => $.extend(hsl_codes(c.hex),c));
}

function hexToRgb (hex) {
  var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  return [parseInt(result[1], 16), parseInt(result[2], 16), parseInt(result[3], 16)];
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
