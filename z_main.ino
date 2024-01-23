
// Test sequence:
// -WBORGORBWYGROBGYBYRWGWYYGOOBYGOWRBYRWRGRRWBGYWGOYOWBBO

// -GYWOGGWORRROBBRWWGWORBYGOWBYGBGWRBBYYYOWRBRYBOYGWORYOG
     
void setup() {
  Serial.begin(9600);
  delay(2000);
  pinMode(15, OUTPUT);
  servos_init();
  delay(2000);
//  servos_load_cube();
//  delay(2000);
//  for(int i = 0; i < 5; i++) {
//    servos_test();
//  }
//  delay(2000);
//  servos_unload_cube();
}

void loop() {
  servos_load_cube();
  delay(1000);
  rubik.Init();
  sequencia_de_armazenamento();
  if(!phoneMode) rubik.Print();

  if(!phoneMode) {
    Serial.print(F("Solving the cube.")); delay(600);
    Serial.print(F(".")); delay(600);
    Serial.print(F(".")); delay(800); Serial.print(F("\n"));
    Serial.print(F("\n")); Serial.print(F("Moves to solve the cube: ")); Serial.print(F("\n"));
  }
  rubik.SolveSequence();
  blink(10);
  servos_unload_cube();
  rubik.Print();
  delay(30000);
}
