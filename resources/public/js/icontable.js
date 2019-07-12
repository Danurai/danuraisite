$.getJSON("/api/data/lugs",function(data) {
  var outp = '';
  $.each(data.icons, function (id, i) {
    outp = '<tr>'
      + '<td>' + i.name + '</td>'
      + '<td>' + i.type + '</td>'
      + '<td><i class="' + i.fa + '" /></td>'
      + '<td>' + i.fa + '</td>';
    $('#icontable').append (outp);
  });

});


//          [:tr [:th "Name"][:th "Set"][:th "Icon"][:th "Icon name"]]
//          [:tbody
//            (for [icon model/lugsicons]
//              [:tr
//                [:td (:name icon)]
//                [:td (:type icon)]
//                [:td [:i {:class (:fa icon)}]]
//                [:td (:fa icon)]
//                [:td (:fa icon)]
//                ])]]]