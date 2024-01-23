#include <Adafruit_CircuitPlayground.h>
#include <Adafruit_Circuit_Playground.h>

bool phoneMode = false;

enum Rubik_Faces {
    RF_FRONT,   //0
    RF_UP,      //1
    RF_LEFT,    //2
    RF_RIGHT,   //3
    RF_DOWN,    //4
    RF_BACK,    //5
    RF_TOTAL_FACES, //6 (número total de faces)
};

enum Rubik_Colors {
    RC_RED,
    RC_YELLOW,
    RC_BLUE,
    RC_GREEN,
    RC_WHITE,
    RC_ORANGE,
    RC_TOTAL_COLORS,
};

int Rubik_FaceToColorLUT[] = {
    RC_RED,
    RC_YELLOW,
    RC_BLUE,
    RC_GREEN,
    RC_WHITE,
    RC_ORANGE,
};

void blink(int val) {
  for(int i = 0; i < val; i++) {
    digitalWrite(15, HIGH);
    delay(100);
    digitalWrite(15, LOW);
    delay(100);
  }
}

void await(char cont_char) {
  while(true) {
    if(Serial.available() > 0) {
      int data = Serial.read();
      if(data == cont_char) {
       return;
      }
    }
  }
}

String insert_move(char c, int i, String s) {
  String result = "";
  if(i == 0) return result + c + s;
  result = s.substring(0, i - 1) + c + s.substring(i);
  return result;
}

bool char_in_array(char c, char arr[]) {
  for(int i = 0; i < sizeof(arr); i++) {
    if(c == arr[i]) return true;
  }
  return false;
}

unsigned char Rubik[RF_TOTAL_FACES][9];
