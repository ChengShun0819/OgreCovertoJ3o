/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Ref Code: https://github.com/mifth/JME-Simple-Examples/tree/master/Examples/src/SimpleChaseCamera
 * Ref Youtube: https://www.youtube.com/watch?v=GGZa8JIcc48
 */
public class ModifiedChaseCamera extends AbstractAppState implements ActionListener, AnalogListener {

    private Node chaseGeneralNode, chaseCamNode, chaseRotateHelper;
    private Application app;
    private InputManager inputManager;
    private String ChaseCamDown = "ChaseCamDown";
    private String ChaseCamUp = "ChaseCamUp";
    private String ChaseCamZoomIn = "ChaseCamZoomIn";
    private String ChaseCamZoomOut = "ChaseCamZoomOut";
    private String ChaseCamMoveLeft = "ChaseCamMoveLeft";
    private String ChaseCamMoveRight = "ChaseCamMoveRight";
    private String ChaseCamToggleRotate = "ChaseCamToggleRotate";
    private boolean doRotate,doZoom, zoomIn;
    private float horizontRotate, verticalRotate, verticalUpLimit, verticalDownLimit;
    private float rotateSpeed, zoomStep, zoomMax, zoomMin;
    private String[] inputs;
    private Spatial spatialToFollow;

    public ModifiedChaseCamera(Application app, InputManager inputManager) {
        this.app = app;
        this.inputManager = inputManager;

        doRotate = false;
        horizontRotate = 0.0f;
        verticalRotate = 0.0f;
        verticalUpLimit = FastMath.DEG_TO_RAD * 30;
        verticalDownLimit = FastMath.DEG_TO_RAD * 70;
        rotateSpeed = 5.0f;

        doZoom = false;
        zoomIn = false;
        zoomStep = 1.0f;
        zoomMin = 2f;
        zoomMax = 100f;
        
        
        chaseGeneralNode = new Node("chaseNode");
        //跟隨攝影機的Node
        chaseCamNode = new Node("chaseCamNode");
        chaseGeneralNode.attachChild(chaseCamNode);
        
        chaseRotateHelper = new Node("chaaseRotateHelper");
        chaseGeneralNode.attachChild(chaseRotateHelper);

        chaseCamNode.setLocalTranslation(0, 0, 30f);
        chaseCamNode.setLocalRotation(new Quaternion(0.0f, 1.0f, 0.0f, 0.0f));

        registerWithInput(inputManager);
        constraintCamera();
    }

    /**
     * Registers inputs with the input manager
     *
     * @param inputManager
     */
    public final void registerWithInput(InputManager inputManager) {

        inputs = new String[]{ChaseCamToggleRotate,
            ChaseCamDown,
            ChaseCamUp,
            ChaseCamZoomIn,
            ChaseCamZoomOut,
            ChaseCamMoveLeft,
            ChaseCamMoveRight};

        inputManager.addMapping(ChaseCamDown, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(ChaseCamUp, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(ChaseCamZoomIn, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping(ChaseCamZoomOut, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping(ChaseCamMoveLeft, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(ChaseCamMoveRight, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(ChaseCamToggleRotate, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, inputs);
    }

    public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(ChaseCamToggleRotate) && isPressed) {
                doRotate = true;
            } else if (name.equals(ChaseCamToggleRotate) && !isPressed) {
                doRotate = false;
                horizontRotate = 0;
                verticalRotate = 0;
            }

            if (name.equals(ChaseCamZoomIn) && isPressed) {
                doZoom = true;
                zoomIn = true;
            } else if (name.equals(ChaseCamZoomOut) && isPressed) {
                doZoom = true;
                zoomIn = false;
            }
    }

    public void onAnalog(String name, float value, float tpf) {

        if (doRotate) {
            if (name.equals(ChaseCamMoveLeft)) {
                horizontRotate = value;
            } else if (name.equals(ChaseCamMoveRight)) {
                horizontRotate = -value;
            } else if (name.equals(ChaseCamUp)) {
                verticalRotate = value;
            } else if (name.equals(ChaseCamDown)) {
                verticalRotate = -value;
            }
        }
    }

    public void constraintCamera() {
        // Over 180
        float angleVerticalNow_y = chaseGeneralNode.getLocalRotation().mult(Vector3f.UNIT_Y).normalize().angleBetween(Vector3f.UNIT_Y);

        if (angleVerticalNow_y > FastMath.HALF_PI) {
            Quaternion xRotAgain = new Quaternion().fromAngleAxis(angleVerticalNow_y - FastMath.HALF_PI, Vector3f.UNIT_X);
            chaseGeneralNode.setLocalRotation(chaseGeneralNode.getLocalRotation().mult(xRotAgain));
        }

        // LIMITS
        float angleVerticalNow_z_inverted = chaseGeneralNode.getLocalRotation().mult(Vector3f.UNIT_Z).normalizeLocal().angleBetween(Vector3f.UNIT_Y);
        if (angleVerticalNow_z_inverted < verticalUpLimit) {
            Quaternion xRotAgain2 = new Quaternion().fromAngleAxis((verticalUpLimit - angleVerticalNow_z_inverted), Vector3f.UNIT_X);
            xRotAgain2.negate();
            chaseGeneralNode.setLocalRotation(chaseGeneralNode.getLocalRotation().mult(xRotAgain2));
        } else if (angleVerticalNow_z_inverted > verticalDownLimit) {
            Quaternion xRotAgain3 = new Quaternion().fromAngleAxis((verticalDownLimit - angleVerticalNow_z_inverted), Vector3f.UNIT_X);
            chaseGeneralNode.setLocalRotation(chaseGeneralNode.getLocalRotation().mult(xRotAgain3));
        }
    }

    public void updatePosition() {
        if (spatialToFollow != null) {
            chaseGeneralNode.setLocalTranslation(spatialToFollow.getLocalTranslation());
        }
    }

    public void update() {

        // MOVE TO SPATIAL
        updatePosition();

        if (doRotate) {

            // HORIZONTAL
            Quaternion chaseRot = chaseGeneralNode.getLocalRotation().clone();
            chaseGeneralNode.setLocalRotation(new Quaternion());
            chaseRotateHelper.setLocalRotation(chaseRot);

            Quaternion yRot = new Quaternion().fromAngleAxis(horizontRotate * rotateSpeed, Vector3f.UNIT_Y);
            chaseGeneralNode.setLocalRotation(chaseGeneralNode.getLocalRotation().mult(yRot));
            chaseGeneralNode.setLocalRotation(chaseRotateHelper.getWorldRotation());

            // VERTICAL
            Quaternion xRot = new Quaternion().fromAngleAxis(verticalRotate * rotateSpeed, Vector3f.UNIT_X);
            chaseGeneralNode.setLocalRotation(chaseGeneralNode.getLocalRotation().mult(xRot));

            horizontRotate = 0f;
            verticalRotate = 0f;
        }

        if (doZoom) {
            Vector3f zoomVec = Vector3f.UNIT_Z.clone().multLocal(zoomStep);

            zoomVec.multLocal(0.5f + (0.05f * chaseCamNode.getLocalTranslation().getZ()));

            if (zoomIn) {
                chaseCamNode.setLocalTranslation(chaseCamNode.getLocalTranslation().add(zoomVec.negateLocal()));
            } else {
                chaseCamNode.setLocalTranslation(chaseCamNode.getLocalTranslation().add(zoomVec));
            }

            if (chaseCamNode.getLocalTranslation().z > zoomMax) {
                chaseCamNode.setLocalTranslation(new Vector3f(0, 0, zoomMax));
            } else if (chaseCamNode.getLocalTranslation().z < zoomMin) {
                chaseCamNode.setLocalTranslation(new Vector3f(0, 0, zoomMin));
            }

            doZoom = false;
        }

        app.getCamera().setLocation(chaseCamNode.getWorldTranslation());
        app.getCamera().setRotation(chaseCamNode.getWorldRotation());

    }


    public void setSpatialToFollow(Spatial spatialToFollow) {
        this.spatialToFollow = spatialToFollow;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        this.update();
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    public ModifiedChaseCamera getChaseCamera() {
        return this;
    }
}

