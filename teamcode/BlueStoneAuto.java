package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@Autonomous(name = "BlueStoneAuto", group = "Auto")
public class BlueStoneAuto extends LinearOpMode {
    private DcMotor fl;
    private DcMotor fr;
    private DcMotor bl;
    private DcMotor br, vLift, hLift;

    private Servo foun1, claw;


    //holonomic encoder counts are slightly innacurate and need to be tested due to different amounts of force and friction on the wheels depending on what you get
//please adjust personally to each program, we have accounted for slight slippage but just please make sure
    private double error = 0;
    BNO055IMU imu;
    @Override
    public void runOpMode() throws InterruptedException {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
        // make sure the imu gyro is calibrated before continuing.
        while (!isStopRequested() && !imu.isGyroCalibrated())
        {

            sleep(50);
            idle();
        }

        fr = hardwareMap.get(DcMotor.class, "frontRight");
        fl = hardwareMap.get(DcMotor.class, "frontLeft");
        br = hardwareMap.get(DcMotor.class, "backRight");
        bl = hardwareMap.get(DcMotor.class, "backLeft");
        hLift = hardwareMap.get(DcMotor.class, "hLift");
        vLift = hardwareMap.get(DcMotor.class, "vLift");

        foun1 = hardwareMap.get(Servo.class, "foun1");
        claw = hardwareMap.get(Servo.class, "claw");

        foun1.setDirection(Servo.Direction.FORWARD);
        claw.setDirection(Servo.Direction.FORWARD);

        fl.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.FORWARD);
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        hLift.setDirection(DcMotor.Direction.REVERSE);
        vLift.setDirection(DcMotor.Direction.REVERSE);

        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        waitForStart();
        if(opModeIsActive()){

            strafe(190);
            hLift.setTargetPosition(2000);
            hLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            hLift.setPower(.7);
            while(hLift.isBusy()){}
            hLift.setPower(0);
            claw.setPosition(.5);
            goForward(1000);
            rotate(90);
            strafe(1130);
            claw.setPosition(0);
            sleep(500);
            goForward(100);
            strafe(-1100);
            rotate(90);
            goForward(2300);
            claw.setPosition(.5);
            sleep(500);
            goForward(-2600);
            rotate(270);
            strafe(-1400);
            goForward(350);
            strafe(1250);
            claw.setPosition(0);
            sleep(500);
            rotate(90);
            goForward(2700);
            claw.setPosition(.5);
            sleep(200);
            goForward(-450);





            telemetry.update();



        }
    }

    private int runDetect( Camera sky){
        int result = (int) sky.getPos();
        sky.stop();
        return result;
    }


    private void setMode(DcMotor.RunMode mode) {
        fl.setMode(mode);
        fr.setMode(mode);
        bl.setMode(mode);
        br.setMode(mode);
    }
    private void setPowers(double flp, double frp, double blp, double brp) {
        fl.setPower(flp);
        fr.setPower(frp);
        bl.setPower(blp);
        br.setPower(brp);
    }
    private void powerBusy() {
        setPowers(0.78, 0.7, 0.7, 0.7);
        while (fl.isBusy() && fr.isBusy()&&bl.isBusy() && br.isBusy()){}
        setPowers(0, 0, 0, 0);
    }
    private void goForward(int gofront){
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setPowers(0, 0, 0, 0);
        fl.setTargetPosition((int)Math.round(1.0*gofront));
        fr.setTargetPosition((int)Math.round(1.0*gofront));
        bl.setTargetPosition((int)Math.round(1.0*gofront));
        br.setTargetPosition((int)Math.round(1.0*gofront));
        setMode(DcMotor.RunMode.RUN_TO_POSITION);
        powerBusy();
    }

    private void strafe(int ticks) {
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setPowers(0, 0, 0, 0);
        fl.setTargetPosition(ticks);
        fr.setTargetPosition(-ticks);
        bl.setTargetPosition(-ticks);
        br.setTargetPosition(ticks);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);
        powerBusy();    }

    private double getAngle() {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return (angles.firstAngle+360) % 360;
    }
    private void rotate(int degrees) {
        double startTime = getRuntime();
        double finalangle = (degrees) % 360;
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setPowers(0, 0, 0, 0);
        setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        do {
            error = (finalangle - getAngle()) % 360;
            if (error < -180.0)
                error += 360.0;
            if (error >= 180.0)
                error -= 360.0;
            telemetry.addData("error", error);
            double correction = Range.clip((Math.sqrt(Math.abs(error / 60))+.02) * Math.signum(error), -.5, .5) * 0.7;

            telemetry.addData("correction", correction);
            telemetry.update();
            setPowers(-correction, correction, -correction, correction);
        } while (opModeIsActive() && Math.abs(error) > .2);
        setPowers(0, 0, 0, 0);
    }
}



