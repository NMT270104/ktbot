'use strict';

var Turtle = {};

Turtle.sensor = 0;

Turtle.touchPort = "00";


Turtle.temperature = 0;

Turtle.potenl = 0;

Turtle.execute = function(code) {
try {
    eval(code);

  } catch (e) {
    if (e !== Infinity) {
      alert(e);
    }
  }


};
function sleep(milliseconds) {
  const date = Date.now();
  let currentDate = null;
  do {
    currentDate = Date.now();
  } while (currentDate - date < milliseconds);
}
// Move M1, M2 with forward_by method or backward by method.
Turtle.WMF = function(value){
    Android.mForward(value);
}
Turtle.WMB = function(value){
    Android.mBackward(value);
}


// Servo
Turtle.servo_no = function(value_port, speed_servo){
       Android.mservo_NO(value_port, speed_servo);
};


//ultrasensor
 Turtle.ultraSensor = function(port){
 Turtle.sensor = Android.requestSensor(port);
 return Turtle.sensor;
 }

//servo
Turtle.servo = function(port, slot, angle){
    Android.servo(port, slot, angle);
}

Turtle.mAFServo = function(port, angle, duration){
    Android.mAServo(port, angle, duration);
}

//temperature
Turtle.temperature = function(port){
     return Android.getTemVal(port);
}

//potenl
Turtle.potenl = function(port){
     return Android.getPotenlVal(port);
}
//follow2
Turtle.lineFollow2 = function(port, side, color){
    return Android.lineFollow2(port, side, color);
}

//touch
Turtle.touch = function(port){
    Turtle.touchPort = port;
    return Android.getTouchVal(port);
}

//avoid
Turtle.avoid = function(port){
    Turtle.touchPort = port;
    return Android.getAvoidVal(port);
}

Turtle.requestTouch = function(){
    return Android.requestTouch(Turtle.touchPort);
}

//light
Turtle.lightSensor = function(port){
    Android.lightSensor(port);
}

//sound
Turtle.sound = function(port){
   return Android.getSoundVal(port);
}

//stop
Turtle.isStop = function(){
if(Android.isStop() == 1){
    return true;
}
return false;
}
