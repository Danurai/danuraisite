// Advanced Styling Scripts

warhammerCardTooltip.init({
  findCardLinks: () => document.querySelectorAll("#find-card-links span")
})

warhammerCardTooltip.init({
  findCardLinks: () => document.querySelectorAll("#get-card-name span"),
  getCardName: (el) => el.innerHTML.match(/badger/i) ? "Aetherwing Scout" : "Abjuration"
})

warhammerCardTooltip.init({
  findCardLinks: () => document.querySelectorAll("#activate-card-link span"),
  activateCardLink: ({element, name}) => {
    console.log("I activated a link for", name)
    element.style.backgroundColor = "pink"
  }
})

warhammerCardTooltip.init({
  findCardLinks: () => document.querySelectorAll("#create-tooltip span"),
  cardFields: ["alliance"],
  createTooltip: (cardLink, cardData, cardImagesBaseUrl, options) => {
    // Show all images instead of just the default one
    const availableSkus = cardData.skus.filter(s => s.lang === options.language && s.finish === "matte")
    const div = $("<div/>").append(
      $("<div/>").text(cardData.alliance),
      $("<h1/>").text(cardData.name),
      $("<div/>").append(
        availableSkus.map(sku => $("<img/>", {src: `${cardImagesBaseUrl}/${sku.id}.jpg`}).css({height: "200px"}))
      ).css({
        display: "flex",
        justifyContent: "space-evenly"
      })
    ).css({
      display: "none",
      background: "red",
      padding: "1em",
      borderRadius: "100%"
    })
    return div.get(0)
  },
  onMouseOver: (ev, tooltip) => tooltip.style.display = "block",
  onMouseOut: (ev, tooltip) => tooltip.style.display = "none"
})

warhammerCardTooltip.init({
  findCardLinks: () => document.querySelectorAll("#no-tooltip span"),
  createTooltip: () => null,
  cardFields: ["category"],
  activateCardLink: ({element, name}, cardData) => {
    $(element).text((i, t) => t + ` (${cardData.category.en})`)
  }
})

warhammerCardTooltip.init({
  findCardLinks: () => document.querySelectorAll("#link-to-database span"),
  linkToCardDatabase: true
})

warhammerCardTooltip.init({
  findCardLinks: () => document.querySelectorAll("#popper-options span"),
  popperOptions: {
    flip: {
      enabled: false
    },
    placement: "top"
  }
})
