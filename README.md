🏙️ Mini City Builder – Java Edition

Mini City Builder este un joc modern de tip city-building, dezvoltat în Java 21 LTS, cu o interfață grafică dark-themed 🌙 și un sistem de simulare în timp real. Jocul permite gestionarea completă a unui oraș: construcții, buget 💰, populație 👥, fericire 😊 și evenimente dinamice.

🔍 Prezentare generală

Construiește și administrează propriul oraș pe o hartă de 22×22 🗺️. Poți plasa case, blocuri, fabrici, magazine, parcuri și drumuri. Fiecare acțiune influențează economia orașului, gradul de ocupare și nivelul de satisfacție al locuitorilor.

Istoricul de evenimente afișează în timp real:

- trecerea la ziua următoare ⏭️
- construcții și demolări 🏗️ 
- venituri și cheltuieli 💸
- modificări de fericire și ocupare
- evenimente aleatorii 🎲

⚡ Pornire rapidă

🪟 Windows (recomandat)
```
cd Java-City-Builder
.\run.bat
```

Compilează proiectul, copiază resursele și pornește jocul automat.

🧪 Mod demo (fără interfață grafică)
```
java -cp target/classes main.Runner --demo-only
```

🐧 macOS / Linux
```
javac -d target/classes src/main/java/main/*.java src/main/java/boardPieces/*.java
cp -R src/main/resources/* target/classes/
java -cp target/classes main.Runner
```

🎮 Controale joc

- Zi următoare (N) – avansează simularea cu o zi ⏭️
- Redare / Pauză (P) – pornește sau oprește rularea automată ▶️⏸️
- Undo (U) – revine la ultima acțiune 🔙 max. 10 pași
- Setări (S) – configurarea jocului ⚙️
- Vezi prețuri (V) – afișează costurile clădirilor 💲
- Salvare joc – salvează progresul 💾
- Încărcare joc – restaurează o salvare 📂
- Istoric evenimente – listă completă cu toate acțiunile 📜

🖥️ Cerințe de sistem

☕ Java 21 LTS sau mai nou

❌ Nu necesită Maven

🪟 Windows / 🍎 macOS / 🐧 Linux

📐 Rezoluție minimă: 1200×780 (recomandat 1400×900+)


🏗️ Tipuri de clădiri
```
Clădire	    Cost	     Locuitori	   Locuri de muncă	Condiții
🏠 Casă	    $800	     4–6	         0	              Lângă drum sau iarbă adiacentă
🏢 Bloc	    $3,000	   20–30	       0	              Lângă drum
🏭 Fabrică	$10,000	   0	           50–100	          Lângă drum și apă
🛒 Magazin	$6,000	   0	           30–50	          Lângă drum
🌳 Parc	    $2,500	   0	           0	              Lângă apă
🛣️ Drum	   $200	      0	            0	               Trebuie să atingă un alt drum
```

📈 Costurile cresc progresiv cu aproximativ 10% pentru fiecare clădire nouă de același tip.
🧨 Demolarea are un cost inițial de $1,250, care se dublează la fiecare utilizare.

💰 Sistem economic

💵 Buget inițial: $2,000
📅 Venit zilnic calculat automat
😊 Bonus de fericire
👷 Bonus de ocupare a forței de muncă
🧱 Bonus de diversitate a clădirilor

👥 Populație și angajare

Populația este determinată de clădirile rezidențiale
Locurile de muncă provin din fabrici și magazine
Rata șomajului influențează veniturile
Multiplicatorul economic variază între 0 și 1

😄 Sistem de fericire

+0.01 per locuitor (bază)
+0.05 per locuitor lângă parcuri 🌳 sau apă 💧
–10 fericire lângă fabrici 🏭
Influențează direct economia orașului

🎲 Evenimente aleatorii

apar cu o probabilitate de aproximativ 8% pe zi
pot genera bonusuri sau penalizări
influențează bugetul și fericirea

💾 Salvare și încărcare

salvarea stării complete a jocului
restaurare exactă a progresului
compatibil cu sistemul undo / redo

🧠 Arhitectură și design

Model–View–Controller (MVC)
Observer pentru actualizări UI
Strategy pentru comportamentul clădirilor
Singleton pentru jurnalul de evenimente
Memento pentru undo și salvare


🚀 Dezvoltări viitoare

🎵 sunete și muzică
📊 grafice economice
🌍 hărți multiple
🤖 asistent AI

📦 Versiune și autori
🏷️ Versiune: 2.1.0
☕ Limbaj: Java 21 LTS
👨‍💻 Autori: Popa Andrei și Varvara Matei
🌙 ciclu zi/noapte
🏆 clasamente
