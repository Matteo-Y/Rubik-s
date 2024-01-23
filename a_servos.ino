
#include <Servo.h>

//                 Ur Ub Ul Uf  Dr Db Dl  Df 
int SERVO_PINS[] = {4, 0, 2, 14, 5, 1, 3, 16};

#define SERVOMIN  500//475 // This is the 'minimum' pulse length count (out of 4096)
#define SERVOMAX  3550//3550 // This is the 'maximum' pulse length count (out of 4096)

#define DELAY_BETWEEN_STEPS (200)
#define DELAY_BETWEEN_STEPS2 (300)

//relativo ao phy_med
int place[] = {48, 52, 150};

//Base class
class RServo {
  protected:

    Servo servo;
    int channel;
    int phy_min;
    int phy_med;
    int phy_max;

  public:
    RServo(int channel) {
      this->channel = channel;
    }
    
    void setAngle(int angle) {
      servo.write(angle);
    }
};

class Rotater : public RServo {
  
  public:
    Rotater(int channel) : RServo(channel) {
      this->channel = channel;
      this->phy_min = 0;
      this->phy_med = 90;
      this->phy_max = 180;
      //this->servo.attach(channel, SERVOMIN, SERVOMAX);
    }

    void attach() {
      servo.attach(channel, SERVOMIN, SERVOMAX);
    }
    
    void rotate(int angle) {
      setAngle(angle);
    }

};

class Pusher : public RServo {

  protected:
    int offset = 0;
  
  public:
    Pusher(int channel, int offset) : RServo(channel) {
      this->channel = channel;
      this->phy_min = 60;
      this->phy_med = 90;
      this->phy_max = 120;
      this->offset = offset;
      //this->servo.attach(channel, SERVOMIN, SERVOMAX);
    }

    void attach() {
      servo.attach(channel, SERVOMIN, SERVOMAX);
    }
    
    void rmove(int phy) {
      setAngle(phy + offset);
    }
};


Rotater Up_Right(SERVO_PINS[0]);  
Rotater Up_Back(SERVO_PINS[1]);   
Rotater Up_Left(SERVO_PINS[2]);              //motor para rodar a face da esquerda no pino 2
Rotater Up_Front(SERVO_PINS[3]);             //motor para rodar a face da frente no pino 3

Pusher Down_Right(SERVO_PINS[4], 0);            //motor para andar a face da direita no pino 4
Pusher Down_Back(SERVO_PINS[5], 0);             //motor para andar a face de trás no pino 5
Pusher Down_Left(SERVO_PINS[6], 0);             //motor para andar a face da esquerda no pino 6
Pusher Down_Front(SERVO_PINS[7], 0);          //motor para andar a face da frente no pino 7


void init_uppers() {
  Up_Right.attach();
  Up_Back.attach();
  Up_Left.attach();
  Up_Front.attach();

  Up_Right.rotate(90);
  Up_Back.rotate(90);
  Up_Left.rotate(90);
  Up_Front.rotate(90);
}
  
void servos_init() {
  Up_Right.attach();
  Up_Back.attach();
  Up_Left.attach();
  Up_Front.attach();

  Down_Right.attach();
  Down_Back.attach();
  Down_Left.attach();
  Down_Front.attach();  

  Up_Right.rotate(90);
  Up_Back.rotate(90);
  Up_Left.rotate(90);
  Up_Front.rotate(90);

  Down_Right.rmove(place[1]);
  Down_Back.rmove(place[1]);
  Down_Left.rmove(place[1]);
  Down_Front.rmove(place[1]);
}

void individual_test() {
  while(true) {
    ServosFace_RightCW();
    delay(500);
    ServosFace_BackCW();
    delay(500);
    ServosFace_LeftCW();
    delay(500);
    ServosFace_FrontCW();
    delay(500);
  }
}

void servos_load_cube() {
    Down_Right.rmove(place[2]);
    Down_Back.rmove(place[2]);
    Down_Left.rmove(place[2]);
    Down_Front.rmove(place[2]); 
    
    delay(3000);
    
    Down_Right.rmove(place[1]);
    Down_Back.rmove(place[1]);
    Down_Left.rmove(place[1]);
    Down_Front.rmove(place[1]);
}

void servos_unload_cube() {
    Down_Right.rmove(place[2]);
    Down_Back.rmove(place[2]);
    Down_Left.rmove(place[2]);
    Down_Front.rmove(place[2]); 
    
    delay(3000);
}

void servos_test() {
  int wait = 500;
  ServosFace_RightCW();
  delay(wait);
  ServosFace_BackCW();
  delay(wait);
  ServosFace_LeftCW();
  delay(wait);
  ServosFace_FrontCW();
  delay(wait);
  ServosCube_MoveX();
  delay(wait);
  ServosCube_Movex();
  delay(wait);
  ServosCube_MoveZ();
  delay(wait);
  ServosCube_Movez();
  delay(wait);
  ServosFace_RightCCW();
  delay(wait);
  ServosFace_BackCCW();
  delay(wait);
  ServosFace_LeftCCW();
  delay(wait);
  ServosFace_FrontCCW();
  delay(wait);
}

void grip_cube() {
//  Down_Front.rmove(place[0]);
//  Down_Back.rmove(place[0]);
//  Down_Right.rmove(place[0]);
//  Down_Left.rmove(place[0]);
//  delay(100);
}

void ungrip_cube() {
//  Down_Front.rmove(place[1]);
//  Down_Back.rmove(place[1]);
//  Down_Right.rmove(place[1]);
//  Down_Left.rmove(place[1]);
//  delay(100);
}

//Rotações dos servos da direita:
//______________________________________________________________________

void ServosFace_RightCW() {
  grip_cube();
  
  Up_Right.rotate(0);            delay(DELAY_BETWEEN_STEPS2);
  Down_Right.rmove(place[2]);     delay(DELAY_BETWEEN_STEPS);
  Up_Right.rotate(90);             delay(DELAY_BETWEEN_STEPS);
  Down_Right.rmove(place[1]);     delay(DELAY_BETWEEN_STEPS);

  ungrip_cube();
}

void ServosFace_RightCCW() {
  grip_cube();

  Up_Right.rotate(180);             delay(DELAY_BETWEEN_STEPS2);
  Down_Right.rmove(place[2]);     delay(DELAY_BETWEEN_STEPS);
  Up_Right.rotate(90);             delay(DELAY_BETWEEN_STEPS);
  Down_Right.rmove(place[1]);     delay(DELAY_BETWEEN_STEPS);

  ungrip_cube();
}


//RotaÃ§Ãµes dos servos da esquerda:
//___________________________________________________________________

void ServosFace_LeftCW() {
  grip_cube();

  Up_Left.rotate(0);              delay(DELAY_BETWEEN_STEPS2);
  Down_Left.rmove(place[2]);     delay(DELAY_BETWEEN_STEPS);
  Up_Left.rotate(90);             delay(DELAY_BETWEEN_STEPS);
  Down_Left.rmove(place[1]);     delay(DELAY_BETWEEN_STEPS);

  ungrip_cube();
}

void ServosFace_LeftCCW() {
  grip_cube();

  Up_Left.rotate(180);            delay(DELAY_BETWEEN_STEPS2);
  Down_Left.rmove(place[2]);     delay(DELAY_BETWEEN_STEPS);
  Up_Left.rotate(90);             delay(DELAY_BETWEEN_STEPS);
  Down_Left.rmove(place[1]);     delay(DELAY_BETWEEN_STEPS);

  ungrip_cube();
}


//RotaÃ§Ãµes dos servos da frente:
//___________________________________________________________________________

void ServosFace_FrontCW() {
  grip_cube();
 
  Up_Front.rotate(180);            delay(DELAY_BETWEEN_STEPS2);
  Down_Front.rmove(place[2]);     delay(DELAY_BETWEEN_STEPS);
  Up_Front.rotate(90);             delay(DELAY_BETWEEN_STEPS);
  Down_Front.rmove(place[1]);     delay(DELAY_BETWEEN_STEPS);

  ungrip_cube();
}
//
void ServosFace_FrontCCW() {
  grip_cube();
  
  Up_Front.rotate(0);            delay(DELAY_BETWEEN_STEPS2);
  Down_Front.rmove(place[2]);     delay(DELAY_BETWEEN_STEPS);
  Up_Front.rotate(90);             delay(DELAY_BETWEEN_STEPS);
  Down_Front.rmove(place[1]);     delay(DELAY_BETWEEN_STEPS);

  ungrip_cube();
}


//RotaÃ§Ãµes dos servos de trÃ¡s:
//______________________________________________________________________________

void ServosFace_BackCW() {
  grip_cube();
  
  Up_Back.rotate(0);            delay(DELAY_BETWEEN_STEPS2);
  Down_Back.rmove(place[2]);     delay(DELAY_BETWEEN_STEPS);
  Up_Back.rotate(90);             delay(DELAY_BETWEEN_STEPS);
  Down_Back.rmove(place[1]);     delay(DELAY_BETWEEN_STEPS);

  Down_Right.rmove(place[1]);
  Down_Left.rmove(place[1]);
}

void ServosFace_BackCCW() {
  grip_cube();

  Up_Back.rotate(180);            delay(DELAY_BETWEEN_STEPS2);
  Down_Back.rmove(place[2]);     delay(DELAY_BETWEEN_STEPS);
  Up_Back.rotate(90);             delay(DELAY_BETWEEN_STEPS);
  Down_Back.rmove(place[1]);     delay(DELAY_BETWEEN_STEPS);

  ungrip_cube();
}


//RotaÃ§Ãµes dos motores para rodar o cubo no sentido de R (movimento x e x`):
//__________________________________________________________________________

int regrip_delay = 300;

void ServosCube_MoveX() {

  Down_Right.rmove(place[0]);      
  Down_Left.rmove(place[0]);
  delay(regrip_delay);
  Down_Front.rmove(place[2]);
  Down_Back.rmove(place[2]);
  delay(DELAY_BETWEEN_STEPS);                
  Up_Right.rotate(180);
  Up_Left.rotate(0);
  delay(regrip_delay);
  Down_Front.rmove(place[0]);
  Down_Back.rmove(place[0]);
  delay(DELAY_BETWEEN_STEPS2);
  Down_Right.rmove(place[2]);
  Down_Left.rmove(place[2]);
  delay(DELAY_BETWEEN_STEPS);
  Up_Right.rotate(90);
  Up_Left.rotate(90);
  delay(DELAY_BETWEEN_STEPS);
  Down_Right.rmove(place[1]);
  Down_Left.rmove(place[1]);
  delay(100);
  Down_Front.rmove(place[1]);
  Down_Back.rmove(place[1]);
  delay(DELAY_BETWEEN_STEPS);
}

void ServosCube_Movex() {

  Down_Right.rmove(place[0]);      
  Down_Left.rmove(place[0]);
  delay(regrip_delay);
  Down_Front.rmove(place[2]);
  Down_Back.rmove(place[2]);
  delay(regrip_delay);
  Up_Right.rotate(0);
  Up_Left.rotate(180);
  delay(regrip_delay);
  Down_Front.rmove(place[0]);
  Down_Back.rmove(place[0]);
  delay(DELAY_BETWEEN_STEPS2);
  Down_Right.rmove(place[2]);
  Down_Left.rmove(place[2]);
  delay(DELAY_BETWEEN_STEPS);
  Up_Right.rotate(90);
  Up_Left.rotate(90);
  delay(DELAY_BETWEEN_STEPS);
  Down_Right.rmove(place[1]);
  Down_Left.rmove(place[1]);
  delay(100);
  Down_Front.rmove(place[1]);
  Down_Back.rmove(place[1]);
  delay(DELAY_BETWEEN_STEPS);
}


//Rotações dos motores para rodar o cubo no sentido de F (movimento z e z`):
//_________________________________________________________________________

void ServosCube_MoveZ() {

  Down_Front.rmove(place[0]);      
  Down_Back.rmove(place[0]);
  delay(regrip_delay);
  Down_Left.rmove(place[2]);
  Down_Right.rmove(place[2]);
  delay(regrip_delay);                
  Up_Front.rotate(180);
  Up_Back.rotate(0);
  delay(regrip_delay);
  Down_Right.rmove(place[0]);
  Down_Left.rmove(place[0]);
  delay(DELAY_BETWEEN_STEPS2);
  Down_Front.rmove(place[2]);
  Down_Back.rmove(place[2]);
  delay(DELAY_BETWEEN_STEPS);
  Up_Front.rotate(90);
  Up_Back.rotate(90);
  delay(DELAY_BETWEEN_STEPS);
  Down_Front.rmove(place[1]);
  Down_Back.rmove(place[1]);
  delay(100);
  Down_Right.rmove(place[1]);
  Down_Left.rmove(place[1]);
  delay(DELAY_BETWEEN_STEPS);
}

void ServosCube_Movez() {

  Down_Front.rmove(place[0]);      
  Down_Back.rmove(place[0]);
  delay(regrip_delay);
  Down_Left.rmove(place[2]);
  Down_Right.rmove(place[2]);      //
  delay(regrip_delay);                //
  Up_Front.rotate(0);
  Up_Back.rotate(180);
  delay(regrip_delay);
  Down_Right.rmove(place[0]);
  Down_Left.rmove(place[0]);
  delay(DELAY_BETWEEN_STEPS2);
  Down_Front.rmove(place[2]);
  Down_Back.rmove(place[2]);
  delay(DELAY_BETWEEN_STEPS);
  Up_Front.rotate(90);
  Up_Back.rotate(90);
  delay(DELAY_BETWEEN_STEPS);
  Down_Front.rmove(place[1]);
  Down_Back.rmove(place[1]);
  delay(100);
  Down_Right.rmove(place[1]);
  Down_Left.rmove(place[1]);
  delay(DELAY_BETWEEN_STEPS);
}

//Rotações dos motores para rodar o cubo no sentido de U (movimento Y e Y ):
//_________________________________________________________________________

void ServosCube_MoveY() {
  ServosCube_MoveX();
  ServosCube_MoveZ();
  ServosCube_Movex();
}

void ServosCube_Movey() {
  ServosCube_MoveX();
  ServosCube_Movez();
  ServosCube_Movex();
}
