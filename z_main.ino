     
void setup() {
  Serial.begin(9600);
  delay(2000);
  pinMode(15, OUTPUT);
  //init_uppers();
  servos_init();
  delay(2000);
  servos_load_cube();
  delay(2000);
  //individual_test();
  for(int i = 0; i < 5; i++) {
    servos_test();
  }
  delay(2000);
  servos_unload_cube();
}

void loop() {
  delay(2000);
  //servos_test();
//  servos_load_cube();
//  rubik.Init();
//  sequencia_de_armazenamento();
//  blink(5);
//  rubik.Print();
//  Menu_Print();
//
//  
//  Serial.print(F("Solving the cube.")); delay(600);
//  Serial.print(F(".")); delay(600);
//  Serial.print(F(".")); delay(800); Serial.print(F("\n"));
//  Serial.print(F("\n")); Serial.print(F("Moves to solve the cube: ")); Serial.print(F("\n"));
//  rubik.SolveSequence();
//  blink(10);
//  servos_load_cube();
//  rubik.Print();
//  delay(10000);
}

void blink(int val) {
  for(int i = 0; i < val; i++) {
    digitalWrite(15, HIGH);
    delay(100);
    digitalWrite(15, LOW);
    delay(100);
  }
}
