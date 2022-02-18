import re
import json

rangename = ''
dx = []
nos = ['%03d' % i for i in range(1, 254)]

m22 = open('resources/private/molotow2022.txt','r')
for line in m22.readlines():
     res = re.match('\#\s(\d{3})\s(.+)\s327\.[0-9,a-z]{3}\s.+([a-f,0-9]{6}|\-\-\-)', line)
     if res == None:
          rangename = re.match('PREMIUM\s(\w+)',line)[1].capitalize()
     else:
          clr = {}
          clr['code'] = res[1]
          clr['name'] = res[2]
          clr['hex']  = '#' + res[3]
          clr['brand'] = 'Molotow 22'
          clr['range'] = rangename
          print(clr)
          nos.remove(res[1])
          dx.append(clr)
print( len(dx) )
print( nos )
with open('resources/private/colour_molotow22.json', 'w+') as f:
     json.dump(dx, f, indent = 2)