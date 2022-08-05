const forward_nodes = [
	{"id": 1, "targets": [2]},
	{"id": 2, "targets": [3, 10]},
	{"id": 3, "targets": [4,16]},
	{"id": 16, "targets": [4], "gain": ["k4"]},
	{"id": 4, "targets": [17, 5, 11]},
	{"id": 17, "targets": [12,5], "keys": ["k4"]},
	{"id": 5, "targets": [6]},
	{"id": 6, "targets": [7, 13], "keys": ["k1"]},
	{"id": 7, "targets": [8, 14]},
	{"id": 8, "targets": [9]},
	{"id": 9, "targets": [100, 15], "keys": ["k3"]},
	{"id": 100},
	{"id": 10, "targets": [3], "gain": ["k1"]},
	{"id": 11, "targets": [5], "gain": ["k2"]},
	{"id": 12, "targets": [5], "gain": ["k3"]},
	{"id": 13},
	{"id": 14, "targets": [8], "keys": ["k2"]},
	{"id": 15},
]
	
const svg = d3.select('svg');
const radius = 8;
const width = window.innerWidth;
const height = window.innerHeight - 25;
const N = forward_nodes.map(d=>d.id)
let links = []
forward_nodes.forEach(n=>{
	if ('targets' in n) {
		n.targets.forEach(t => {
			let link={};
			link.source = N.indexOf(n.id);
			link.target = N.indexOf(t);
			links.push(link);
		})
	}
})


// PATHFINDING

// REVERSE PATH
let nodes = [];
forward_nodes.forEach( node => {
	let newnode = JSON.parse(JSON.stringify(node));
	let targets = forward_nodes.filter(n => 'targets' in n ? n.targets.indexOf(node.id) > -1 : false)
	if (targets.length>0) {
		newnode.targets = targets.map(t=>t.id)
	} else {
		delete newnode.targets
	}
	nodes.push(newnode);
});




let start = 100 // nodes[0].id
let goal = 1 //nodes.slice(0,-1).id

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
			let new_cost = cost_so_far[current] + 10;
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

let path = [];
let current_node = goal;
do {
	path.push(current_node);
	current_node = came_from[current_node]
} while (current_node != null)
let keys = [];
path.forEach(p => {
	let node = nodes.filter(n => n.id == p)[0]
	if ('keys' in node) node.keys.forEach(k=>keys.push(k))
})

d3.select('#info').append('div').text(JSON.stringify(path.reverse()))
d3.select('#info').append('div').text(JSON.stringify(keys))



svg
	.attr('width', width)
	.attr('height',height)
	.select('marker').attr('refX',radius + 10)

let max_cost = Math.max.apply(Math,Object.values(cost_so_far));
let force_step = width / max_cost;

const simulation = d3.forceSimulation(nodes)
	.force('charge', d3.forceManyBody().strength(-100))
	//.force('center', d3.forceCenter( width / 2, height / 2 ))
	.force('y', d3.forceY(height / 2))
	.force('x', d3.forceX(d => d.id < 10 ? d.id * width / 11 : d.id == 100 ? width * 10/11 : width / 2)) //.strength(0.05))
	//.force('x', d3.forceX(d => (cost_so_far[d.id] * force_step))) //.strength(0.05))
	.force('link', d3.forceLink(links))
	.force("collide",d3.forceCollide().radius(radius * 5))
	.on('tick',ticker);

function ticker() {

	svg.selectAll('line')
		.data(links)
		.join('line')
			.attr('x1', d => getHPosition(d.source.x))
			.attr('y1', d => getVPosition(d.source.y))
			.attr('x2', d => getHPosition(d.target.x))
			.attr('y2', d => getVPosition(d.target.y))
			.attr('stroke-width', 1)
			.attr('stroke', '#aaa')
			.attr('marker-end','url(#arrowhead')

	svg.selectAll('circle')
		.data(nodes)
		.join('circle')
			.attr('r', radius)
			.attr('fill', d => ('gain' in d) ? '#3a3' : ('keys' in d) ? '#a00' : ('targets' in d) ? '#777' :  '#222')
			.attr('cx', d => getHPosition(d.x))
			.attr('cy', d => getVPosition(d.y))
			.attr('title',d => d.id)
			.call( drag(simulation) );

	svg.selectAll('text')
		.data(nodes)
		.join('text')
			.text(d => d.id)
			.attr('dx', radius+2)
			.attr('dy', 4)
			.attr('x', d => getHPosition(d.x))
			.attr('y', d => getVPosition(d.y))
}

function getHPosition(dx) {return Math.max(radius, Math.min(width - radius, dx));}
function getVPosition(dy) {return Math.max(radius, Math.min(height - radius, dy));}

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