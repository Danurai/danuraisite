from urllib.request import Request, urlopen
import json 
import xml.etree.ElementTree as ET
import re

tree = ET.parse('resources/private/citadelcolourrange.xml')
root = tree.getroot()

dx = []

for n in root.iter('li'):
     clr = {}
     sp = n.find('span')
     img = sp.find('img')
     gtm = json.loads(sp.attrib.get('data-gtm-productfieldobject'))
     yearres = re.search('\((\d{,4})\)', gtm['name'])
     src = img.attrib.get('src')
     req = Request(src,headers={'User-Agent': 'Mozilla/5.0'})
     svg = urlopen(req).read().decode('utf-8')

     #add to json
     clr['brand'] = "Citadel"
     clr['range'] = re.search('(\w+)', gtm['name'])[0]
     clr['name'] = img.attrib.get('alt')
     clr['year'] = yearres[1] if yearres != None else None 
     clr['code'] = gtm['id'][6:]
     clr['url'] = src
     
     gradient = re.search('gradient_(\#[0-9 A-F]{6})_(\#[0-9 A-F]{6})',svg)
     print (gradient)
     if re.search('linearGradient', svg) != None and gradient != None:
          clr['transparent'] = True
          clr['hex'] = gradient[1]
          clr['hex2'] = gradient[2]
     elif re.search('radialGradient',svg) != None  and gradient != None:
          clr['metallic'] = True
          stops = re.search('\<stop offset=\"50%\" style\=\"stop-color:\srgb\((\d+),\s(\d+),\s(\d+)',svg)
          clr['hex'] = gradient[1]
          clr['hex1'] = '#%02x%02x%02x' %  tuple([int(i) for i in stops.groups()])
          clr['hex2'] = gradient[2]
     else:
          clr['hex'] = re.search('fill\=\"(\#.+?)\"',svg)[1]
     
     print (clr)
     dx.append(clr)

with open('resources/private/colour_citadel.json', 'w+') as f:
     json.dump(dx, f, indent = 2)