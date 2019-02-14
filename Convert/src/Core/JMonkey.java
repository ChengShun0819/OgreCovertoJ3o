package Core;

import Utility.ConvertUtility;
import Utility.FileUtility;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetKey;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import java.awt.Canvas;
import java.awt.Dimension;
import java.io.File;

public class JMonkey extends SimpleApplication {

    public boolean IsFinsh = false;
    private SimpleApplication app = null;
    public boolean IsSelectedFinish = false;

    public Node mainNode = null;
    public Canvas coreCanvas = null;
    private ModifiedChaseCamera chaseCam = null;

    public JMonkey(int canvasWidth, int canvasHeight) {
        app = this;
        IsFinsh = false;
        AppSettings appSettings = new AppSettings(true);
        appSettings.setFullscreen(false);
        appSettings.setRenderer("LWJGL-OpenGL2");
        this.settings = appSettings;
        this.setDisplayFps(false);
        this.setDisplayStatView(false);
        this.setPauseOnLostFocus(false);
        this.coreCanvas = this.getCanvas(canvasWidth, canvasHeight);
    }

    public Canvas getCanvas(int width, int height) {
        JmeCanvasContext jmeCanvasContext = null;
        if (this.getContext() == null) {
            this.createCanvas();
            jmeCanvasContext = (JmeCanvasContext) this.getContext();
            jmeCanvasContext.setSystemListener(this);
        }
        Dimension dim = new Dimension(width, height);
        jmeCanvasContext.getCanvas().setPreferredSize(dim);
        this.startCanvas();
        return jmeCanvasContext.getCanvas();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        flyCam.setEnabled(false);
        flyCam.setDragToRotate(true);
        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);
        createSunLight(ColorRGBA.White, new Vector3f(10, 10, 10), new Vector3f(0, 0, 0), rootNode);
        createAmbientLight(ColorRGBA.White, rootNode);
        chaseCam = new ModifiedChaseCamera(app, app.getInputManager());
        stateManager.attach(chaseCam);
        IsFinsh = true;
    }

    public void selectedModel(final String modelFilePath) {
        this.enqueue(new Runnable() {
            @Override
            public void run() {
                if (!modelFilePath.isEmpty()) {
                    createModel(modelFilePath);
                }

            }
        });
    }

    public void createModel(final String parentPath) {
        rootNode.detachAllChildren();
        rootNode.updateGeometricState();
        mainNode = null;
        File parentFile = new File(parentPath);
        Node modelNode = null;
        assetManager.registerLocator(parentFile.getPath(), FileLocator.class);

        for (File file : parentFile.listFiles()) {
            if (FileUtility.getExtensionName(file.getName()).toLowerCase().equals("scene")) {
                Spatial spatial = assetManager.loadModel(file.getName());

                //clear cache
//                assetManager.clearCache();
                
                deleteTextureCacheOfSpatial(spatial);
                deleteModelCache((ModelKey) spatial.getKey());
                
                ConvertUtility.convertToJ3O(spatial, file.getPath());

                break;
            }
        }

        Spatial model = null;
        for (File file : parentFile.listFiles()) {
            if (FileUtility.getExtensionName(file.getName()).equals("j3o")) {
                modelNode = new Node();
                model = assetManager.loadModel(file.getName());
                model.center();
                modelNode.attachChild(model);
                rootNode.attachChild(modelNode);
                chaseCam.setSpatialToFollow(modelNode);
                break;
            }
        }
        assetManager.unregisterLocator(parentFile.getPath(), FileLocator.class);
        rootNode.updateGeometricState();
    }

    public static DirectionalLight createSunLight(ColorRGBA lightColor, Vector3f from, Vector3f to, Node targetNode) {
        DirectionalLight sunLight = new DirectionalLight();
        sunLight.setColor(lightColor);
        sunLight.setDirection(new Vector3f(to.x - from.x, to.y - from.y, to.z - from.z));
        targetNode.addLight(sunLight);
        return sunLight;
    }

    public static void createAmbientLight(ColorRGBA lightColor, Node targetNode) {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(lightColor.mult(1.0f));
        targetNode.addLight(ambientLight);
    }

    private void deleteModelCache(ModelKey modelKey) {
        assetManager.deleteFromCache(modelKey);
    }

    private void deleteTextureCacheOfSpatial(Spatial spatial) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (Spatial child : node.getChildren()) {
                deleteTextureCacheOfSpatial(child);
            }
        } else if (spatial instanceof Geometry) {
            Geometry geometry = (Geometry) spatial;
            AssetKey key = geometry.getMaterial().getTextureParam("DiffuseMap").getTextureValue().getKey();
            if (key != null && assetManager.getFromCache(key) != null) {
                assetManager.deleteFromCache(key);
            }
        }
    }

}
