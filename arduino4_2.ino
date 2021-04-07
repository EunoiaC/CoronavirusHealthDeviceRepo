char incomingByte;        // incoming data
char questionAnswers [3]; // Array including answers of survey
int  LED = 13;            // LED pin
int counterSurvey=0;      // Counter for survey results
int i=0;                  // Counter
int riskRate;             // Risk rate that will be calculated according to both measurement and survey results.

void setup() {
  Serial.begin(9600); // initialization
  pinMode(LED, OUTPUT);
  i=0;
}

void loop() {
  if (Serial.available() > 0) {  // if data comes
    incomingByte = Serial.read(); // read byte

    if(incomingByte == 'X')
   {   
      counterSurvey++;
      return;
   }    
   else
   {
      //Before resetting counter first check
      if(counterSurvey == 5)    //5 'X' have been sent and answers will be next. 
      {  
        i = 0;      
        questionAnswers[i] = incomingByte; //Save first data
        i++;
        
        while(i < 3)                      //2 more question answers are left. Read them
        {
          if (Serial.available() > 0)     // if data comes
          {  
              questionAnswers[i] = Serial.read(); // read data and store in array.
              i++;                
          }
        }
        riskRate = CalculateRiskRate(questionAnswers[0], questionAnswers[1], questionAnswers[2]);
        Serial.print(riskRate);
        Serial.print('\n');
      }
        counterSurvey=0;    //Reset counter since incomingByte is not 'X' or questionAnswers are already saved and riskRate is generated.      
    }
    
    if(riskRate < 60) 
    {
       digitalWrite(LED, LOW);  // if 1, switch LED Off
       Serial.println("LED OFF. Press 1 to LED ON!");  // print message
    }
    else if(riskRate > 60) 
    {
       digitalWrite(LED, HIGH); // if 0, switch LED on
       Serial.println("LED ON. Press 0 to LED OFF!");
    }  
  }
}

int CalculateRiskRate(char q1, char q2, char q3)
{
  int temp = 0;
  if(q1 == '1')
  temp = temp + 50;
  if(q2 == '1')
  temp = temp + 30;
  if(q3 == '1')
  temp = temp + 20;
  //Here riskRate is calculated depending only survey results. 
  //Later this calculation must be done considering temperature and cough measurements.
  return temp; 
}
