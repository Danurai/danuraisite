
// PriorityQueue
const pqtop = 0;
const parent = i => ((i + 1) >>> 1) - 1;
const left = i => (i << 1) + 1;
const right = i => (i + 1) << 1;

class PriorityQueue {
  constructor(comparator = (a, b) => a > b) {
    this._heap = [];
    this._comparator = comparator;
  }
  size() {
    return this._heap.length;
  }
  isEmpty() {
    return this.size() == 0;
  }
  peek() {
    return this._heap[pqtop];
  }
  push(...values) {
    values.forEach(value => {
      this._heap.push(value);
      this._siftUp();
    });
    return this.size();
  }
  pop() {
    const poppedValue = this.peek();
    const bottom = this.size() - 1;
    if (bottom > pqtop) {
      this._swap(pqtop, bottom);
    }
    this._heap.pop();
    this._siftDown();
    return poppedValue;
  }
  replace(value) {
    const replacedValue = this.peek();
    this._heap[pqtop] = value;
    this._siftDown();
    return replacedValue;
  }
  _greater(i, j) {
    return this._comparator(this._heap[i], this._heap[j]);
  }
  _swap(i, j) {
    [this._heap[i], this._heap[j]] = [this._heap[j], this._heap[i]];
  }
  _siftUp() {
    let node = this.size() - 1;
    while (node > pqtop && this._greater(node, parent(node))) {
      this._swap(node, parent(node));
      node = parent(node);
    }
  }
  _siftDown() {
    let node = pqtop;
    while (
      (left(node) < this.size() && this._greater(left(node), node)) ||
      (right(node) < this.size() && this._greater(right(node), node))
    ) {
      let maxChild = (right(node) < this.size() && this._greater(right(node), left(node))) ? right(node) : left(node);
      this._swap(node, maxChild);
      node = maxChild;
    }
  }
}

const nodes = [
	{id: 1, targets: [2, 5]},
	{id: 2, targets: [3]},
	{id: 3, targets: [4], gain: "k1"},
	{id: 4, targets: [5]},
	{id: 5, targets: [6], key: "k1"},
	{id: 6}
] 
/*[
	{"id": 1, "targets": [270, 66], "name": "Entrance"},
	{"id": 270, "targets": [66], "gain": "gold 2", "name": "Box"},
	{"id": 66, "targets": [293, 119], "name": "T-Junction"},
	{"id": 119, "targets": [56, 293], "name": "East Passage"},
	{"id": 56, "targets": [373, 215], "name": "Spore Ball"},
	{"id": 373, "targets": [13]},
	{"id": 215, "targets": [13], "cost": 2},
	{"id": 13, "targets": [147, 182]},
	{"id": 147, "targets": [182], "gain": "bamboo_pipe_drink"},
	{"id": 182, "targets": [25, 242], "key": "bamboo_pipe_drink"},
	{"id": 25}, //, "targets": [197]},
	{"id": 242, "targets": [48, 366], "check": "skill"},
	{"id": 48, "targets": [197]},
	{"id": 366},
	{"id": 197,  "name": "Exit"},
	{"id": 293, "targets": [137,387], "name": "West Passage"},
	{"id": 137},
	{"id": 387}
]*/
const N = nodes.map(d => d.id);
const links = [];
nodes.forEach(node => {
	if (typeof(node.targets) !== 'undefined') {
		node.targets.forEach(function (tgt) {
			let link = {}; 
			link.source = N.indexOf(node.id); 
			link.target = N.indexOf(tgt); 
			links.push(link); 
		})
	}
});

const width = window.innerWidth * 0.8;
const height = window.innerHeight - 10;
const radius = 5;

const svg = d3.select('svg')
	.attr('width', width)
	.attr('height', height);

const textElements = svg.append('g')
	.selectAll('text')
	.data(nodes)
	.enter().append('text')
		.text(d => d.id)
		.attr('font-size', 10)
		.attr('dx', 10)
		.attr('dy', 4);

const linkElements = svg.append('g')
	.selectAll('line')
	.data(links)
	.enter().append('line')
		.attr('stroke-width', 1)
		.attr('stroke', '#ccc')

const nodeElements = svg.append('g')
	.selectAll('circle')
	.data(nodes)
	.enter().append('circle')
		.attr('r', radius)
		.attr('fill', d => {
			if (typeof(d.name) != 'undefined') {
				switch (d.name) {
					case 'Exit':
						return 'red';
						break;
					case 'Entrance':
						return 'green';
						break;
					defel:
						return 'grey';
				} 
			} else {'grey'};
		});

const simulation = d3.forceSimulation(nodes)
	.force('charge', d3.forceManyBody().strength(-25))
	.force('center', d3.forceCenter(width / 2, height / 2))
	.force('link', d3.forceLink().links(links))
	.on('tick', ticked);


function ticked() {
	nodeElements
		.attr('cx', d => Math.max(radius, Math.min(width - radius, d.x)))
		.attr('cy', d => Math.max(radius, Math.min(height - radius, d.y)));
	linkElements
		.attr('x1', link => Math.max(radius, Math.min(width - radius, link.source.x)))
		.attr('y1', link => Math.max(radius, Math.min(height - radius, link.source.y)))
		.attr('x2', link => Math.max(radius, Math.min(width - radius, link.target.x)))
		.attr('y2', link => Math.max(radius, Math.min(height - radius,link.target.y)));
	textElements
		.attr('x', d => Math.max(radius, Math.min(width - radius, d.x)))
		.attr('y', d => Math.max(radius, Math.min(height - radius, d.y)));
}


function dragstarted(event) {
	if (!event.active) simulation.alphaTarget(0.1).restart();
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

const dragDrop = d3.drag()
	.on("start", dragstarted)
	.on("drag", dragged)
	.on("end", dragended);

nodeElements.call(dragDrop);

// Breadth First Search, Dijkstra's, A* through a gamebook.


// Breadth first with early exit
let frontier = new PriorityQueue((a,b) => a[1] > b[1]);	// frontier.push(1)
frontier.push([1,0])
let camefrom = {1: null};	// reached.push(1)
let cost_so_far = {1: 0}
let goal = 197
// ff - key items
let keyitems = []

function getTargets(node) {
	if (typeof(node.targets) !== 'undefined') {
		return node.targets;
	} else {
		return [];
	}
}

function haveItem(current, key) {
	let items = [];	
	while(current != 1)	{	//start
		let n = nodes.filter( node => node.id == current)[0]
		if (typeof(n.gain) !== 'undefined') items.push(n.gain)
		current = camefrom[current]
	}
	return items.indexOf(key) > -1;
}

while (frontier.size() > 0) {
	let current = frontier.pop()[0];
	//if (current == goal) break;
	
	let currentnode = nodes.filter(n=>n.id==current)[0]
	let neighbours = getTargets(currentnode)

	neighbours.forEach( (next, idx) => {
		//let newnode = nodes.filter(n=>n.id==next)[0]
		let itemcheck = true;
		if (typeof(currentnode.key) !== 'undefined' && idx == 0) {
			itemcheck = haveItem(current,currentnode.key );
		}

		if (itemcheck) {
			new_cost = 10 //cost_so_far[current] + (typeof(newnode.gain) !== 'undefined' ? -1 : 100 * (typeof(newnode.cost) !== 'undefined' ? newnode.cost : 1))
			//if (typeof(cost_so_far[next]) == 'undefined') {
				cost_so_far[next] = new_cost
				frontier.push([next,new_cost]);
				camefrom[next] = current;
			//} //else if (new_cost < cost_so_far[next]) {
			//	cost_so_far[next] = new_cost;
			//	frontier.push([next,new_cost]);
			//	camefrom[next] = current;
			//}
		}
	});
}
/* set item nodes as -1 cost to encourage interaction
while (frontier.size() > 0) {
	let current = frontier.pop()[0];
	//if (current == goal) break;
	
	let neighbours = getTargets(nodes.filter(n=>n.id==current)[0])

	neighbours.forEach( next => {
		let newnode = nodes.filter(n=>n.id==next)[0]
		new_cost = cost_so_far[current] + (typeof(newnode.gain) !== 'undefined' ? -1 : 100 * (typeof(newnode.cost) !== 'undefined' ? newnode.cost : 1))
		if (typeof(cost_so_far[next]) == 'undefined') {
			cost_so_far[next] = new_cost
			frontier.push([next,new_cost]);
			camefrom[next] = current;
		} else if (new_cost < cost_so_far[next]) {
			cost_so_far[next] = new_cost;
			frontier.push([next,new_cost]);
			camefrom[next] = current;
		}
	});
}*/

let current = nodes.splice(-1)[0].id
let path = [];
while(current != 1)	{	//start
	path.push(current);
	current = camefrom[current]
}
path.push(1);
path.reverse();


d3.select('#results')
	.selectAll('div')
	.data(path) //.slice(0,-1))
	.enter().append('div')
		.text( d => JSON.stringify(d) ) //d + ' > ' + path[path.indexOf(d)+1] )
		.attr('style', 'font-weight: bold;')

/*
// Breadth first with early exit
let frontier = [1];	// frontier.push(1)
let camefrom = {1: null};	// reached.push(1)
let goal = 197

while (frontier.length > 0) {
	let current = frontier.pop();
	if (current == goal) break;
	let currentnode = nodes.filter(n=>n.id==current)[0]
		if (typeof(currentnode.targets) !== 'undefined') {
			currentnode.targets.forEach( next => {
				if (typeof(camefrom[next]) == 'undefined') {
					frontier.push(next);
					camefrom[next] = current;
				}
			});
	};
}

let current = 197;
let path = [];
while(current != 1)	{	//start
	path.push(current);
	current = camefrom[current]
}
path.push(1);
path.reverse();
*/ 
