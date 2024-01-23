#define DELAY_BETWEEN_STEPS1 (500) 


static void clearVision() {
  
  Down_Right.rmove(place[2]);
  Down_Left.rmove(place[2]);
  delay(DELAY_BETWEEN_STEPS1);
  Down_Front.rmove(place[0]);
  Down_Back.rmove(place[0]);
  delay(DELAY_BETWEEN_STEPS1);
  Up_Back.rotate(180);
  Up_Front.rotate(0);
  
}

void sequencia_de_armazenamento() {

   //face 0 = right                                 GREEN
   clearVision();
   getBit(RF_RIGHT);
   Up_Back.rotate(0);
   Up_Front.rotate(180);
   //face 1 = left                                  BLUE
   getBit(RF_LEFT);
   Up_Front.rotate(90);
   Up_Back.rotate(90);
   Down_Front.rmove(place[1]);
   Down_Back.rmove(place[1]);
   delay(DELAY_BETWEEN_STEPS1);
   Down_Right.rmove(place[0]);
   Down_Left.rmove(place[0]);
   delay(DELAY_BETWEEN_STEPS1);
   Down_Front.rmove(place[2]);
   Down_Back.rmove(place[2]);
   delay(DELAY_BETWEEN_STEPS1);
   Up_Right.rotate(0);
   Up_Left.rotate(180);
   //face 2 = Up                                    YELLOW
   getBit(RF_UP);
   Up_Right.rotate(180);
   Up_Left.rotate(0);
   //face 3 = Down                                  WHITE
   getBit(RF_DOWN);
   Up_Right.rotate(90);
   Up_Left.rotate(90);
   Down_Right.rmove(place[1]);
   Down_Left.rmove(place[1]);
   delay(DELAY_BETWEEN_STEPS1);
   Down_Back.rmove(place[1]);
   Down_Front.rmove(place[1]);
   delay(DELAY_BETWEEN_STEPS1);
   ServosCube_MoveZ();
   //face 4 = front                                 RED
   clearVision();
   getBit(RF_FRONT);
   Up_Back.rotate(0);
   Up_Front.rotate(180);
   //face 5 = back                                  ORANGE
   getBit(RF_BACK);
   Up_Back.rotate(90);
   Up_Front.rotate(90);
   Down_Front.rmove(place[1]);
   Down_Back.rmove(place[1]);
   delay(DELAY_BETWEEN_STEPS1);
   Down_Left.rmove(place[1]);
   Down_Right.rmove(place[1]);
   delay(DELAY_BETWEEN_STEPS1);
   ServosCube_Movez();
   delay(DELAY_BETWEEN_STEPS1);
}
