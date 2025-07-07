PDF-Geek merupakan fungsionalitas untuk file pdf yang dibangun dengan
javafx. 

Fitur : 
- Hapus halaman pdf
- Sisipkan halaman
- Gabung pdf

Dibangun dengan :
- java 21 graalvm
- maven
- scene builder

Cara menjalankan :
- install maven dan java 21
- `mvn install` 
- lalu jalankan main class

NOTE :
Sampai saat ini saya sudah berusaha untuk membuat native app
tetapi belum bisa, dikarenakan SDK Javafx sudah tidak dibundle
oleh JDK java. Saya sudah mencoba dengan jpackage, jlink dan
menggunakan jar lalu ke graalvm untuk native. Tetapi belum berhasil.

![image](img/ss1.png)
![image](img/ss2.png)
