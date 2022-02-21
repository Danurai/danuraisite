from dxfwrite import DXFEngine as dxf
import math

def hex_corner (center, size, i):
     ar = 60 * i * math.pi / 180
     return (center[0] + size * math.cos(ar), center[1] + size * math.sin(ar))

dxfdoc = dxf.drawing('hexy.dxf')
size = 5

for q in range(10):
     for r in range(10):
          x = size * (           3/2 * q)
          y = size * (math.sqrt(3)/2 * q + math.sqrt(3) * r)
          hexpoints = [hex_corner( (x, y), size - 1, a) for a in range(6)]
          hex = dxf.polyline( hexpoints )
          hex.close()
          dxfdoc.add( hex )

dxfdoc.save()
