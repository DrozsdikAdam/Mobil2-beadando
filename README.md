# Realtime Chat Alkalmazás

Valós idejű kommunikációs platform, amely modern Java (Spring Boot) backend technológiára és natív Android kliensre épül. Az alkalmazás célja, hogy gördülékeny, azonnali üzenetváltást tegyen lehetővé privát és csoportos formában egyaránt, miközben kiemelt figyelmet fordít a biztonságra és az adatok perzisztenciájára.

---

## Áttekintés

A projekt egy teljes körű (full-stack) chat megoldás, amely a következő alapelvekre épül:
- **Azonnaliság**: A WebSocket technológia révén az üzenetek késleltetés nélkül érkeznek meg a címzetthez.
- **Biztonság**: Minden kommunikáció titkosított csatornán zajlik, a hozzáférés-kezelés pedig iparági sztenderd JWT tokenekkel történik.
- **Megbízhatóság**: Az Android kliens helyi adatbázist (Room) használ a gyors válaszidő és az offline elérés érdekében.

---

## Funkcionalitás és Modulok

### 1. Felhasználói Rendszer
A rendszer alapja a biztonságos és rugalmas felhasználókezelés:
- **Regisztráció és Beléptetés**: Validált regisztráció után a felhasználók JWT alapú munkamenetet kapnak, amely biztosítja a későbbi kérések hitelességét.
- **Profilmenedzsment**: Lehetőség van egyedi felhasználónevek beállítására, amelyek azonnal megjelennek a beszélgetőpartnerek számára.
- **Vizuális Jelenlét**: A rendszer valós időben jelzi, hogy ki érhető el (Online), ki nincs bejelentkezve (Offline).

### 2. Chat és Üzenetküldés
A kommunikáció központja a dinamikus szobakezelés:
- **Beszélgetési formák**: Egyéni (1:1) privát chat mellett tetszőleges számú résztvevőt befogadó csoportos szobák is létrehozhatóak.
- **Üzenetkezelés**: A rendszer csak szöveges üzeneteket támogat.
- **Szobánkénti témák**: Minden chat szoba egyedi vizuális megjelenéssel (színvilág, háttér) látható el, amely segít megkülönböztetni a különböző beszélgetéseket.
- **Előzmények**: A korábbi beszélgetések intelligens módon, lapozható (pagination) formában töltődnek be az adatbázisból, minimalizálva az adatforgalmat.

---

## Technológiai Specifikáció

### Backend Stack
A backend szolgáltatás Spring Boot alapú mikroszolgáltatásként működik:
| Technológia | Szerep |
|---|---|
| **Java 17+** | Stabil, típusbiztos futtatókörnyezet |
| **Spring Boot 3** | Központi keretrendszer (REST, DI, Auth) |
| **WebSocket + STOMP** | A valós idejű üzenetközvetítés protokollja |
| **PostgreSQL** | Strukturált, relációs adattárolás (JPA/Hibernate) |
| **Spring Security** | JWT alapú stateless védelem |

### Android Kliens Stack
A mobil alkalmazás a modern Android fejlesztés legjobb gyakorlatait követi:
| Technológia | Szerep |
|---|---|
| **Kotlin / Java** | Natív teljesítmény és modern nyelvi elemek |
| **MVVM** | Tiszta architektúra (ViewModel + LiveData) |
| **Retrofit / OkHttp** | Hálózati kommunikáció és WebSocket kezelés |
| **Room Database** | Helyi SQLite absztrakció az offline működéshez |
| **Material 3** | Google legfrissebb design irányelvei szerint |

---

## Rendszerarchitektúra és Működés

Az alkalmazás rétegelt architektúrát használ. A kliens és a szerver közötti kapcsolat két fő csatornán zajlik:
1.  **REST API**: A kevésbé időérzékeny műveletekhez (regisztráció, profilmódosítás, szobák listázása, téma beállítása).
2.  **WebSocket (STOMP)**: A valós idejű eseményekhez (üzenetküldés, státuszfrissítés, gépelés jelzése).

Az adatok mentése minden esetben duplikált: a szerveroldali központi PostgreSQL mellett az Android kliensen is tárolódnak (Room), így a felhasználói élmény folyamatos marad gyenge hálózati kapcsolat esetén is.

---

## Fejlesztési Ütemterv

A projekt fejlesztése több fázisban valósul meg:

1.  **Fázis (Alapok)**: Projekt inicializálása, adatbázis séma felépítése.
2.  **Fázis (Auth)**: JWT alapú regisztráció és beléptetés implementálása mindkét oldalon.
3.  **Fázis (Rooms)**: Csoportos és privát szobák kezelése, REST végpontok kialakítása.
4.  **Fázis (Real-time)**: WebSocket integráció, üzenettovábbítás STOMP protokollal.
5.  **Fázis (UX & Vizualitás)**: Design polírozás és szobánkénti témák (színek, hátterek) implementálása.
6.  **Fázis (QA)**: Tesztelés, bugfix és dokumentáció véglegesítése.

---