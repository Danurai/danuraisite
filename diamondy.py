from PIL import Image, ImageDraw
from dxfwrite import DXFEngine as dxf
import math

size = 100
space = 5
width = 10
height = 10

w = size * math.cos( math.pi / 3 )
h = size * math.sin( math.pi / 3 )

img = Image.new('RGBA', ( math.ceil( (w + space) * 2 * width ), math.ceil( (h + space) * 2 * height ) ), (0, 0, 0, 0) )
draw = ImageDraw.Draw(img)

dxfdoc = dxf.drawing('diamo.dxf')


for q in range( width ):
     for r in range( height ):
          x = q * 2 * (w + space) 
          y = r * 2 * (h + space)
          diapoints = [
               (x + w, y ),
               (x + w * 2, y + h ),
               (x + w, y + h * 2 ),
               (x, y + h ),
               (x + w, y )
          ]
          draw.line(diapoints, fill = (0,0,0), width = 1 )
          dia = dxf.polyline( diapoints )
          dxfdoc.add( dia )

img.save('diamo.png','PNG')
dxfdoc.save()
