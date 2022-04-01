#include <SPI.h>
#include <MFRC522.h>
 
#define SS_PIN 10
#define RST_PIN 4
MFRC522 mfrc522(SS_PIN, RST_PIN);   
 
void setup() 
{
  Serial.begin(9600);  
  SPI.begin();      
  mfrc522.PCD_Init();   
  Serial.println("Approximate your card to the reader...");
  Serial.println();

}
void loop() 
{
  
  if ( ! mfrc522.PICC_IsNewCardPresent()) 
  {
    //Serial.print("No card present.");
    //Serial.println();
    return;
  } else {
    Serial.print("Card found.");
    Serial.println();
  }
  
  if ( ! mfrc522.PICC_ReadCardSerial()) 
  {
    return;
  }

  mfrc522.PICC_DumpDetailsToSerial(&(mfrc522.uid));
 
  Serial.print("UID tag :");
  String content= "";
  byte letter;
  for (byte i = 0; i < mfrc522.uid.size; i++) 
  {
     Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
     Serial.print(mfrc522.uid.uidByte[i], HEX);
     content.concat(String(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " "));
     content.concat(String(mfrc522.uid.uidByte[i], HEX));
  }
  Serial.println();
//  Serial.print("Message : ");
//  Serial.println();
  delay(3000);
}
//  content.toUpperCase();
//  if (content.substring(1) == "29 C2 07 5E") // Make sure you change this with your own UID number
//  {
//    Serial.println("Authorised access");
//    Serial.println();
//    delay(3000);
//  }
// 
// else   {
//    Serial.println(" Access denied");
//    delay(3000);
//  }
