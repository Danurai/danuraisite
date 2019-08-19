
var compare = []
var outp = '';
var _filter = '';
var data = [{"name":"Abaddon Black","range":"Base","hex":"#231F20"},{"name":"Averland Sunset","range":"Base","hex":"#FDB825"},{"name":"Balthasar Gold (Metal)","range":"Base","hex":"#A47552"},{"name":"Bugmans Glow","range":"Base","hex":"#834F44"},{"name":"Caledor Sky","range":"Base","hex":"#396E9E"},{"name":"Caliban Green","range":"Base","hex":"#00401F"},{"name":"Castellan Green","range":"Base","hex":"#314821"},{"name":"Celestra Grey","range":"Base","hex":"#90A8A8"},{"name":"Ceramite White","range":"Base","hex":"#FFFFFF"},{"name":"Daemonette Hide","range":"Base","hex":"#696684"},{"name":"Death Guard Green","range":"Base","hex":"#848A66"},{"name":"Deathworld Forest","range":"Base","hex":"#5C6730"},{"name":"Dryad Bark","range":"Base","hex":"#33312D"},{"name":"Incubi Darkness","range":"Base","hex":"#0B474A"},{"name":"Jokaero Orange","range":"Base","hex":"#EE3823"},{"name":"Kantor Blue","range":"Base","hex":"#002151"},{"name":"Khorne Red","range":"Base","hex":"#6A0001"},{"name":"Leadbelcher (Metal)","range":"Base","hex":"#888D8F"},{"name":"Macragge Blue","range":"Base","hex":"#0D407F"},{"name":"Mechanicus Standard Grey","range":"Base","hex":"#3D4B4D"},{"name":"Mephiston Red","range":"Base","hex":"#9A1115"},{"name":"Mournfang Brown","range":"Base","hex":"#640909"},{"name":"Naggaroth Night","range":"Base","hex":"#3D3354"},{"name":"Rakarth Flesh","range":"Base","hex":"#A29E91"},{"name":"Ratskin Flesh","range":"Base","hex":"#AD6B4C"},{"name":"Retributor Armour (Metal)","range":"Base","hex":"#C39E81"},{"name":"Rhinox Hide","range":"Base","hex":"#493435"},{"name":"Screamer Pink","range":"Base","hex":"#7C1645"},{"name":"Screaming Bell (Metal)","range":"Base","hex":"#C16F45"},{"name":"Steel Legion Drab","range":"Base","hex":"#5E5134"},{"name":"Stegadon Scale Green","range":"Base","hex":"#074863"},{"name":"The Fang Grey","range":"Base","hex":"#436174"},{"name":"Thousand Sons Blue","range":"Base","hex":"#18ABCC"},{"name":"Waaagh! Flesh","range":"Base","hex":"#1F5429"},{"name":"Warplock Bronze (Metal)","range":"Base","hex":"#927D7B"},{"name":"XV-88","range":"Base","hex":"#72491E"},{"name":"Zandri Dust","range":"Base","hex":"#9E915C"},{"name":"Administratum Grey","range":"Layer","hex":"#949B95"},{"name":"Ahriman Blue","range":"Layer","hex":"#1F8C9C"},{"name":"Alaitoc Blue","range":"Layer","hex":"#295788"},{"name":"Altdorf Guard Blue","range":"Layer","hex":"#1F56A7"},{"name":"Auric Armour Gold (Metal)","range":"Layer","hex":"#E8BC6D"},{"name":"Balor Brown","range":"Layer","hex":"#8B5910"},{"name":"Baneblade Brown","range":"Layer","hex":"#937F6D"},{"name":"Bestigor Flesh","range":"Layer","hex":"#D38A57"},{"name":"Brass Scorpion (Metal)","range":"Layer","hex":"#B7885F"},{"name":"Cadian Fleshtone","range":"Layer","hex":"#C77958"},{"name":"Calgar Blue","range":"Layer","hex":"#4272B8"},{"name":"Dark Reaper","range":"Layer","hex":"#3B5150"},{"name":"Dawnstone","range":"Layer","hex":"#70756E"},{"name":"Deathclaw Brown","range":"Layer","hex":"#B36853"},{"name":"Doombull Brown","range":"Layer","hex":"#5D0009"},{"name":"Elysian Green","range":"Layer","hex":"#748F39"},{"name":"Emperors Children","range":"Layer","hex":"#B94278"},{"name":"Eshin Grey","range":"Layer","hex":"#4A4F52"},{"name":"Evil Sunz Scarlet","range":"Layer","hex":"#C2191F"},{"name":"Fenrisian Grey","range":"Layer","hex":"#719BB7"},{"name":"Fire Dragon Bright","range":"Layer","hex":"#F58652"},{"name":"Flash Gitz Yellow","range":"Layer","hex":"#FFF200"},{"name":"Flayed One Flesh","range":"Layer","hex":"#F0D9B8"},{"name":"Fulgurite Copper (Metal)","range":"Layer","hex":"#FCFCDE"},{"name":"Gehenna's Gold (Metal)","range":"Layer","hex":"#DBA674"},{"name":"Genestealer Purple","range":"Layer","hex":"#7761AB"},{"name":"Gorthor Brown","range":"Layer","hex":"#654741"},{"name":"Hashut Copper (Metal)","range":"Layer","hex":"#B77647"},{"name":"Hoeth Blue","range":"Layer","hex":"#4C7FB4"},{"name":"Ironbreaker (Metal)","range":"Layer","hex":"#A1A6A9"},{"name":"Kabalite Green","range":"Layer","hex":"#038C67"},{"name":"Karak Stone","range":"Layer","hex":"#BB9662"},{"name":"Kislev Flesh","range":"Layer","hex":"#D6A875"},{"name":"Liberator Gold (Metal)","range":"Layer","hex":"#D3B587"},{"name":"Loren Forest","range":"Layer","hex":"#50702D"},{"name":"Lothern Blue","range":"Layer","hex":"#34A2CF"},{"name":"Moot Green","range":"Layer","hex":"#52B244"},{"name":"Nurgling Green","range":"Layer","hex":"#849C63"},{"name":"Ogryn Camo","range":"Layer","hex":"#9DA94B"},{"name":"Pallid Wych Flesh","range":"Layer","hex":"#CDCEBE"},{"name":"Pink Horror","range":"Layer","hex":"#90305D"},{"name":"Runefang Steel (Metal)","range":"Layer","hex":"#C3CACE"},{"name":"Runelord Brass (Metal)","range":"Layer","hex":"#B6A89A"},{"name":"Russ Grey","range":"Layer","hex":"#547588"},{"name":"Screaming Skull","range":"Layer","hex":"#D2D4A2"},{"name":"Skarsnik Green","range":"Layer","hex":"#5F9370"},{"name":"Skavenblight Dinge","range":"Layer","hex":"#47413B"},{"name":"Skrag Brown","range":"Layer","hex":"#90490F"},{"name":"Skullcrusher Brass (Metal)","range":"Layer","hex":"#F1C78E"},{"name":"Slaanesh Grey","range":"Layer","hex":"#8E8C97"},{"name":"Sotek Green","range":"Layer","hex":"#0B6974"},{"name":"Squig Orange","range":"Layer","hex":"#AA4F44"},{"name":"Stormhost Silver (Metal)","range":"Layer","hex":"#BBC6C9"},{"name":"Stormvermin Fur","range":"Layer","hex":"#736B65"},{"name":"Straken Green","range":"Layer","hex":"#628126"},{"name":"Sybarite Green","range":"Layer","hex":"#30A56C"},{"name":"Sycorax Bronze (Metal)","range":"Layer","hex":"#CBB394"},{"name":"Tallarn Sand","range":"Layer","hex":"#A67610"},{"name":"Tau Light Ochre","range":"Layer","hex":"#BF6E1D"},{"name":"Teclis Blue","range":"Layer","hex":"#317EC1"},{"name":"Temple Guard Blue","range":"Layer","hex":"#339A8D"},{"name":"Thunderhawk Blue","range":"Layer","hex":"#417074"},{"name":"Troll Slayer Orange","range":"Layer","hex":"#F36D2D"},{"name":"Tuskgor Fur","range":"Layer","hex":"#883636"},{"name":"Ulthuan Grey","range":"Layer","hex":"#C7E0D9"},{"name":"Ungor Flesh","range":"Layer","hex":"#D6A766"},{"name":"Ushabti Bone","range":"Layer","hex":"#BBBB7F"},{"name":"Warboss Green","range":"Layer","hex":"#3E805D"},{"name":"Warpfiend Grey","range":"Layer","hex":"#6B6A74"},{"name":"Warpstone Glow","range":"Layer","hex":"#1E7331"},{"name":"Wazdakka Red","range":"Layer","hex":"#8C0A0C"},{"name":"White Scar","range":"Layer","hex":"#FFFFFF"},{"name":"Wild Rider Red","range":"Layer","hex":"#EA2F28"},{"name":"Xereus Purple","range":"Layer","hex":"#471F5F"},{"name":"Yriel Yellow","range":"Layer","hex":"#FFDA00"},{"name":"Zamesi Desert","range":"Layer","hex":"#DDA026"},{"name":"Agrax Earthshade","range":"Shade","hex":"#5A573F"},{"name":"Agrax Earthshade (Gloss)","range":"Shade","hex":"#5A573F"},{"name":"Athonian Camoshade","range":"Shade","hex":"#6D8E44"},{"name":"Biel-Tan Green","range":"Shade","hex":"#1BA169"},{"name":"Carroburg Crimson","range":"Shade","hex":"#A82A70"},{"name":"Casandora Yellow","range":"Shade","hex":"#FECE5A"},{"name":"Coelia Greenshade","range":"Shade","hex":"#0E7F78"},{"name":"Drakenhof Nightshade","range":"Shade","hex":"#125899"},{"name":"Druchii Violet","range":"Shade","hex":"#7A468C"},{"name":"Fuegan Orange","range":"Shade","hex":"#C77E4D"},{"name":"Nuln Oil","range":"Shade","hex":"#14100E"},{"name":"Nuln Oil (Gloss)","range":"Shade","hex":"#14100E"},{"name":"Reikland Fleshshade","range":"Shade","hex":"#CA6C4D"},{"name":"Reikland Fleshshade (Gloss)","range":"Shade","hex":"#CA6C4D"},{"name":"Seraphim Sepia","range":"Shade","hex":"#D7824B"},{"name":"Astorath Red","range":"Dry","hex":"#DD482B"},{"name":"Changeling Pink","range":"Dry","hex":"#F4AFCD"},{"name":"Chronus Blue","range":"Dry","hex":"#72A8D1"},{"name":"Dawnstone","range":"Dry","hex":"#919C9F"},{"name":"Eldar Flesh","range":"Dry","hex":"#ECC083"},{"name":"Etherium Blue","range":"Dry","hex":"#A2BAD2"},{"name":"Golden Griffon (Metal)","range":"Dry","hex":"#A99058"},{"name":"Golgfag Brown","range":"Dry","hex":"#C2804F"},{"name":"Hellion Green","range":"Dry","hex":"#84C3AA"},{"name":"Hexos Palesun","range":"Dry","hex":"#FFF200"},{"name":"Hoeth Blue","range":"Dry","hex":"#57A9D4"},{"name":"Imrik Blue","range":"Dry","hex":"#67AED0"},{"name":"Kindleflame","range":"Dry","hex":"#F79E86"},{"name":"Longbeard Grey","range":"Dry","hex":"#CECEAF"},{"name":"Lucius Lilac","range":"Dry","hex":"#B69FCC"},{"name":"Necron Compound (Metal)","range":"Dry","hex":"#828B8E"},{"name":"Niblet Green","range":"Dry","hex":"#7DC734"},{"name":"Nurgling Green","range":"Dry","hex":"#B8CC82"},{"name":"Praxeti White","range":"Dry","hex":"#FFFFFF"},{"name":"Ryza Rust","range":"Dry","hex":"#EC631A"},{"name":"Sigmarite","range":"Dry","hex":"#CAAD76"},{"name":"Skink Blue","range":"Dry","hex":"#58C1CD"},{"name":"Slaanesh Grey","range":"Dry","hex":"#DBD5E6"},{"name":"Stormfang","range":"Dry","hex":"#80A7C1"},{"name":"Sylvaneth Bark","range":"Dry","hex":"#AC8262"},{"name":"Terminatus Stone","range":"Dry","hex":"#BDB192"},{"name":"Thunderhawk Blue","range":"Dry","hex":"#509BA9"},{"name":"Tyrant Skull","range":"Dry","hex":"#CDC586"},{"name":"Underhive Ash","range":"Dry","hex":"#C0BD81"},{"name":"Verminlord Hide","range":"Dry","hex":"#A16954"},{"name":"Wrack White","range":"Dry","hex":"#FCFBFA"},{"name":"Baharroth Blue","range":"Edge","hex":"#58C1CD"},{"name":"Blue Horror","range":"Edge","hex":"#A2BAD2"},{"name":"Dechala Lilac","range":"Edge","hex":"#B69FCC"},{"name":"Dorn Yellow","range":"Edge","hex":"#FFF200"},{"name":"Fulgrim Pink","range":"Edge","hex":"#F4AFCD"},{"name":"Gauss Blaster Green","range":"Edge","hex":"#84C3AA"},{"name":"Krieg Khaki","range":"Edge","hex":"#C0BD81"},{"name":"Lugganath Orange","range":"Edge","hex":"#F79E86"},{"name":"Bloodletter","range":"Glaze","hex":"#F37355"},{"name":"Guilliman Blue","range":"Glaze","hex":"#2F9AD6"},{"name":"Lamenters Yellow","range":"Glaze","hex":"#FFF56B"},{"name":"Waywatcher Green","range":"Glaze","hex":"#6DC066"},{"name":"Agrellan Badland","range":"Texture","hex":""},{"name":"Agrellan Earth","range":"Texture","hex":""},{"name":"Armageddon Dust","range":"Texture","hex":"#D3A907"},{"name":"Armageddon Dunes","range":"Texture","hex":""},{"name":"Astrogranite","range":"Texture","hex":"#757679"},{"name":"Astrogranite Debris","range":"Texture","hex":""},{"name":"Blackfire Earth","range":"Texture","hex":"#A75820"},{"name":"Lustrian Undergrowth","range":"Texture","hex":"#415A09"},{"name":"Martian Ironcrust","range":"Texture","hex":""},{"name":"Martian Ironearth","range":"Texture","hex":""},{"name":"Mourn Mountain Snow","range":"Texture","hex":"#E9EAEB"},{"name":"Stirland Battlemire","range":"Texture","hex":""},{"name":"Stirland Mud","range":"Texture","hex":"#492B00"},{"name":"Valhallan Blizzard","range":"Texture","hex":""},{"name":"'Ardcoat","range":"Technical","hex":"#E2DEDF"},{"name":"Agrellan Earth","range":"Technical","hex":"#9A816B"},{"name":"Blood for the Blood God","range":"Technical","hex":"#67080B"},{"name":"Imperial Primer","range":"Technical","hex":"#231F20"},{"name":"Lahmian Medium","range":"Technical","hex":"#F5EDE2"},{"name":"Liquid Green Stuff","range":"Technical","hex":"#3B7A5F"},{"name":"Martian Ironearth","range":"Technical","hex":"#C15A4B"},{"name":"Nihilakh Oxide","range":"Technical","hex":"#6CB79E"},{"name":"Nurgle's Rot","range":"Technical","hex":"#9B8F22"},{"name":"Soulstone Blue","range":"Technical","hex":"#004EFA"},{"name":"Spiritstone Red","range":"Technical","hex":"#FF4B24"},{"name":"Typhus Corrosion","range":"Technical","hex":"#463D2B"},{"name":"Waystone Green","range":"Technical","hex":"#00C000"}]

add_hsl();

write_table();

function write_table () {
  $('#colours').empty();
  $.each(data.filter(c => c.name.toLowerCase().indexOf(_filter) > -1) , function(k, v) {
    outp = '<tr>'
      + '<td>' + v.name + '</td>'
      + '<td>' + v.range + '</td>'
      + '<td><a href = "hsl?q=' + v.hex.slice(1) + '" target="_blank">' + v.hex + '</a></td>'
      + '<td>' + v.h + '</td>'
      + '<td>' + v.s + '</td>'
      + '<td>' + v.l + '</td>'
      + '<td class="sample" data-name="' + v.name + '" style = "background-color: ' + v.hex + '; width: 20%;">&nbsp;</td>'
      + '</tr>'
    $('#colours').append(outp)
  });
}

function swatch (c) {
  if (c.length > 0) {
    c = c[0];
    return '<div class="col-sm-2">'
      + '<div class="row text-center">'
      + c.name
      + '</div>'
      + '<div class="row text-center sample"  data-name="' + c.name + '" style="background-color: ' + c.hex + ';">' + c.hex + '</div>' 
      + '</div>'
}}

function write_comparison () {
  $('#comparison').empty();
  $.each(compare, function (k, v) {
    $('#comparison').append( swatch (data.filter(c => (c.name == v))) );
  });
}

$('#colours').on('click','.sample', function () {
  compare.push($(this).data('name'));
  write_comparison();
});
$('#comparison').on('click','.sample', function () {
  compare.splice($.inArray($(this).data('name'), compare),1);
  write_comparison();
});


$('#filter').on('input', function () {
  _filter = $(this).val().toLowerCase();
  write_table();
}); 


function add_hsl() {
// update data to include HSL values based on hex
  data = data.map(c => $.extend(hsl_codes(c.hex),c));
}

function hsl_codes(hex) {
  if (hex != "") {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return RGBToHSL(parseInt(result[1], 16), parseInt(result[2], 16), parseInt(result[3], 16));
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