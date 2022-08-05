const data = `/js/nodes/creature.json`;
fetch(data).then((r) => r.json()).then((data) => {
	const nodes = data;
	const groups = [...new Set(nodes.map(x=>x.group))];
	let links = [];
	let N = nodes.map(node => node.id);
	const start = 1
	const goal = nodes.slice(-1)[0].id;

	d3.select('svg').append('g').attr('id','svglink')
	d3.select('svg').append('g').attr('id','svgpath')
	d3.select('svg').append('g').attr('id','svgnodes')

	
	// create links, including nodes for missing targets.
	nodes.forEach(n => {
		if ('targets' in n) n.targets.forEach(t => {
				let tgtidx = N.indexOf(t);
				if (tgtidx > -1) {
					links.push({source: N.indexOf(n.id), target: N.indexOf(t)})
				} else {
					links.push({source: N.indexOf(n.id), target: nodes.length})
					nodes.push({id: t})
				}
		});
		if ('keytargets' in n) n.keytargets.forEach(t => {
			let tgtidx = N.indexOf(t);
			if (tgtidx > -1) {
				links.push({source: N.indexOf(n.id), target: N.indexOf(t)});
			} else {
				links.push({source: N.indexOf(n.id), target: nodes.length})
				nodes.push({id: t});
			}
		});
	});

	N = nodes.map(node => node.id);


	// Breadth-first search path

	let frontier = []
	let came_from = {}
	let cost_so_far = {}
	frontier.push(start);
	came_from[start] = null;
	cost_so_far[start] = 0;

	while (frontier.length > 0) {
		let current = frontier.pop();
		let current_node = nodes.filter(n => n.id == current)[0];
		if (current_node == goal) break;
		if ('targets' in current_node) {
			current_node.targets.forEach( t => {
				let new_cost = cost_so_far[current] + ('gain' in (nodes.filter(n=>n.id==t))[0] ? -5 : 10);	// favour picking up items
				let visited = (t in came_from);
				let shorter = visited ? new_cost < cost_so_far[t] ? true : false : true 
				if (!visited || shorter) {
					frontier.push(t);
					came_from[t] = current;
					cost_so_far[t] = new_cost;
				}
			})
		}
	}

	// Create Path and links for force directed simulation
	let path = []
	let pathLinks = []
	let cnode = goal;
		
	if (cnode in came_from) {
		path.push(cnode);
		while (came_from[cnode] != null) {
			pathLinks.push({source: N.indexOf(came_from[cnode]), target: N.indexOf(cnode)});
			cnode = came_from[cnode];
			path.push(cnode);
		}
	}

	//d3.select('#info').append('div').attr('style','word_break: break_all').text(JSON.stringify(pathLinks))
	d3.select('#info').append('div').attr('style','word_break: break_all').text(JSON.stringify(path.reverse()))
	//path.reverse().forEach(n => result.append('div').text(n).attr('style', 'margin-right: .2em'))

	function getNeighbours(current) {
		let node = nodes.filter(n => n.id == current)[0];
		let targets = ('targets' in node ? node.targets : [])
		let keys = getKeys(current);
		if ('keytargets' in node) {
			node.keytargets.forEach((kt,idx) => {
				if (keys.indexOf(node.keys[idx]) > -1) targets.push(kt);
			});
		}
		return targets;
	}

	function getKeys(current) {
		let keys = [];
		let cnode = current;
		while (came_from[cnode] != null) {
			let node = nodes.filter(n=>n.id == cnode)[0];
			if (typeof(node) == 'undefined') console.log(cnode)
			if ('gain' in node) node.gain.forEach(g => keys.push(g));
			cnode = came_from[cnode];
		}
		return keys;
	} 

	function log(it1,it2,next,fr,cur,neigh,from,keys) {
		d3.select('tbody').append('tr')
		let row = d3.select('tbody:last-child');
		row.append('td').text(it1 + (it2 > -1 ? "." + (it2+1) : ""));
		row.append('td').text(next);
		row.append('td').text(JSON.stringify(fr));
		row.append('td').text(cur);
		row.append('td').text(JSON.stringify(neigh));
		row.append('td').text(''); //JSON.stringify(from));
		row.append('td').text(JSON.stringify(keys));
	}

	// FORCE DIRECTED SIMULATION

	const height = window.innerHeight;
	const width = window.innerWidth; //900; //d3.select('svg').attr('width');
	const radius = 6;

	d3.select('svg')
		.attr('height', height)
		.attr('width', width)
		//.call(d3.zoom()
		//	.extent([[0, 0], [width, height]])
		//	.scaleExtent([1, 8])
		//	.on("zoom", zoomed));
			
	function zoomed({transform}) {
		d3.select('svg').attr("transform", transform);
	}

	const zonewidth = width / (groups.length + 1)
	const zoneheight = height / 3
	const simulation = d3.forceSimulation(nodes)
		.force('charge',d3.forceManyBody().strength(-5))
		.force('center', d3.forceCenter(width / 2, height / 2))
		//.force('y', d3.forceY( d => zoneheight * (d.group % 2 == 1 ? 2 : 1) ))
		//.force('x', d3.forceX( d => zonewidth * d.group))
		.force('link', d3.forceLink().links(links))
		.force("collide",d3.forceCollide().radius(radius * 5))
		.on('tick', ticked);


	const nodeElements = d3.select('#svgnodes')
		.selectAll('circle')
		.data(nodes)
		.enter().append('circle')
			.attr('r', radius)
			.attr('fill',getNodeColour)
			.call( drag(simulation) );
		
	const textElements = d3.select('#svgnodes')
		.selectAll('text')
		.data(nodes)
		.enter().append('text')
			.text(d => d.id)
			.attr('dx',5)
			.attr('dy',4)

	const linkElements = d3.select('#svglink')
		.selectAll('line')
		.data(links)
		.enter().append('line')
		.attr('stroke-width',1)
		.attr('stroke','#aaa')
		.attr('marker-end','url(#arrowhead')

	function ticked() {
		nodeElements
			.attr('cx', d => Math.max(radius, Math.min(width - radius, d.x)))
			.attr('cy', d => Math.max(radius, Math.min(height- radius, d.y)))
		textElements
			.attr('x', d => Math.max(radius, Math.min(width - radius, d.x)))
			.attr('y', d => Math.max(radius, Math.min(height- radius, d.y)))
		linkElements
			.attr('x1', d => Math.max(radius, Math.min(width - radius, d.source.x)))
			.attr('y1', d => Math.max(radius, Math.min(height - radius, d.source.y)))
			.attr('x2', d => Math.max(radius, Math.min(width - radius, d.target.x)))
			.attr('y2', d => Math.max(radius, Math.min(height - radius, d.target.y)))
			.attr('stroke', d => isOnPath(d) ? '#a00' : '#aaa')
			.attr('stroke-width', d => isOnPath(d) ? 2 : 1)
	}

	function getNodeColour(node) {
		if ('gain' in node) {
			return 'green'
		} else if ('keys' in node) {
			return '#a00'
		} else if (!('targets' in node)) {
			return 'black'
		} else {
			return 'steelblue'
		}
	}

	function isOnPath(d) {
		let sourceindex = path.indexOf(d.source.id);
		let pathline = false
		if (sourceindex > -1) {
			if (path[sourceindex + 1] == d.target.id) pathline = true
		} 
		return pathline
	}

	function drag(simulation) {    
		function dragstarted(event) {
			if (!event.active) simulation.alphaTarget(0.3).restart();
			event.subject.fx = event.subject.x;
			event.subject.fy = event.subject.y;
		}
		
		function dragged(event) {
			event.subject.fx = event.x;
			event.subject.fy = event.y;
		}
		
		function dragended(event) {
			if (!event.active) simulation.alphaTarget(0);
			event.subject.fx = null;
			event.subject.fy = null;
		}
		
		return d3.drag()
			.on("start", dragstarted)
			.on("drag", dragged)
			.on("end", dragended);
	}
});