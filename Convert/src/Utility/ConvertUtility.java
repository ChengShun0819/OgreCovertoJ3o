/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Spatial;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shun.fang
 */
public class ConvertUtility {

    public static void convertToJ3O(Spatial reducedModel, String modelPath) {
        try {
            BinaryExporter exporter = new BinaryExporter();
            String spatialName = modelPath.substring(0, modelPath.lastIndexOf("."));
            File j3ofile = new File(spatialName.concat(".j3o"));
            exporter.save(reducedModel, j3ofile);
        } catch (IOException ex) {
            Logger.getLogger(ConvertUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
