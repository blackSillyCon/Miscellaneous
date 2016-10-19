 ##########################################################################
# Author: Coulibaly Falla Zoumana                                           #
# Date: 03/04/2016                                                          #
# Description: Custom export script that retrieves and writes to a txt      #
#              file frame by frame the location and rotation of the object. #
#              How does it work?                                            #
#              The AABB of an object refers to the child of that object     #
#              So the script will export only exportobjects that have a     #
#              child or AABB linked to it and is not a camera or lamp       #
 ##########################################################################

import bpy

sce = bpy.context.scene
ob_list = sce.objects

try:
    outfile = open('export.dat', 'w')
    #Iterate through every object in the scene
    for ob in ob_list:
        outfile.write( "Object name %s\n " % (ob.name))
        for f in range(sce.frame_start, sce.frame_end+1):
            # write object name and coords and rotation followed by a newline
            x, y, z = ob.location # unpack the ob.loc tuple for printing
            x2, y2, z2 = ob.rotation_euler # unpack the ob.rot tuple for printing
            
            outfile.write( "Frame %i\n" %f)
            outfile.write( "Location : x = %f y = %f\n" % (x, y) )
            outfile.write( "Rotation : x = %f y = %f\n" % (x2, y2) )
            
            #Move to the next frame
            sce.frame_set(f)
            
            print("Frame %i" % f)
    
    outfile.close()
    
except Exception as e:
    print ("Oh no! something went wrong:", e)
    
else:
    if outfile: outfile.close()
    print("done writing")
