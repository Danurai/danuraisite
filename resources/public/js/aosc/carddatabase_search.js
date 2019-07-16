$(document).ready(function () {
  var size = 1000;
  $.post("https://carddatabase.warhammerchampions.com/warhammer-cards/_search").done(
    function (data) {
      const query = {size: data.hits.total, from: 1, sort: [{"id": "asc"}]};
      const req =  fetch("https://carddatabase.warhammerchampions.com/warhammer-cards/_search", {
          method: "post",
          headers: {"Content-Type": "application/json"},
          body: JSON.stringify(query)
      })
      req
        .then((resp) => resp.json())
        .then((data) => $("body").html(JSON.stringify(data.hits.hits.map(value => value._source))))
    });
});