#include <Adafruit_CircuitPlayground.h>
#include <Adafruit_Circuit_Playground.h>

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


unsigned char Rubik[RF_TOTAL_FACES][9];
