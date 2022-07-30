const nodes = [
	{"id": 1, "targets": [270, 66], "check": "start", "name": "Entrance"},
	{"id": 270, "targets": [66], "gain": "gold 2", "name": "Box"},
	{"id": 66, "targets": [293, 119], "name": "T-Junction"},
	{"id": 119, "targets": [56, 293], "name": "East Passage"},
	{"id": 56, "targets": [373, 215], "name": "Spore Ball"},
	{"id": 373, "targets": [13]},
	{"id": 215, "targets": [13], "cost": 2},
	{"id": 13, "targets": [147, 182]},
	{"id": 147, "targets": [182], "gain": "bamboo_pipe_drink"},
	{"id": 182, "targets": [25, 242], "check": "bamboo_pipe_drink"},
	{"id": 25, "targets": [197]},
	{"id": 242, "targets": [48, 366], "check": "skill"},
	{"id": 48, "targets": [197]},
	{"id": 366},
	{"id": 197,  "name": "Exit"},
	{"id": 293, "name": "West Passage"}
]
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

const width = window.innerWidth;
const height = window.innerHeight;


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
		.attr('r', 4)
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
	.force('charge', d3.forceManyBody().strength(-80))
	.force('center', d3.forceCenter(width / 2, height / 2))
	//.force('link', d3.forceLink().id(link => link.id))
	.force('link', d3.forceLink().links(links))
	.on('tick', ticked);


function ticked() {
	nodeElements
		.attr('cx', d => d.x)
		.attr('cy', d => d.y);
	textElements
		.attr('x', d => d.x)
		.attr('y', d => d.y);
	linkElements
		.attr('x1', link => link.source.x)
		.attr('y1', link => link.source.y)
		.attr('x2', link => link.target.x)
		.attr('y2', link => link.target.y)
}
/*function getNodeColour(node) {
	if (typeof(node.targets) == 'undefined') {
		return 'black'
	} else {
		return 'blue'
	}
}
const textElements = svg.append('g')
	.selectAll('text')
	.data(nodes)
	.enter().append('text')
		.attr('font-size', 15)
		.attr('dx', 15)
		.attr('dy', 4)*/

