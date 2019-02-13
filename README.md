# parking_application

### Projekt aplikacji webowej do zarządzania parkomatem.

----------

### Aplikacja pozwala na podstawowe funkcjonalności:
  - Dla kierowcy:
      - Start parkometru (naliczania opłaty)  [ POST ] [/driver/start/{driverType}/{currencyType}/]
      - Sprawdzenie kwoty do zapłacenia  [ GET ] [/driver/cost/ID/{id}] lub [/driver/cost/{licensePlate}]
      - Stop parkometru [ PUT ] [/driver/stop/ID/{id}] lub [/driver/stop/{licensePlate}]
      
  - Dla obsługi parkomatu
      - Sprawdzenie czy kierowca ma aktywny biler [ GET ] [/driver/check/ID/{id}] lub [/driver/check/{licensePlate}]
      
  - Dla właściciela parkomatu
      - Sprawdzenie zysku za dany dzień [ GET ] [/day/{year}/{month}/{day}/profit/{currencyType}/ ]

  - Ponadto:
      - Sprawdzenie informacji o kierowcy [ GET ] [/driver/show/ID/{id}] lub [/driver/show/{licensePlate}]
      - Sprawdzenie informacji o danym dniu [ GET ] [/day/{year}/{month}/{day}/show/{currencyType}/]
      
      
----------      
### URUCHOMIENIE APLIKACJI: 
  - Uruchomic skrypt start.sh

----------      
### URUCHOMIENIE TESTÓW: 
  - Uruchomic skrypt run_tests.sh

----------
### Użyte technologie:

  - Java,
  - Spring MVC,
  - Hibernate,
  - H2 Database,
  - Thye,
  - Project Lombok,
  - jUnit,
  - Mockito,
  - Maven,
  - REST.
