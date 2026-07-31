# Changelog v4.9.4 — Perbaikan Bug "Belum bisa dimuat / Data kosong" Melolo

Pembaruan kritis pada logika pengurai (*parser*) JSON `flat()` di `MainActivity.kt` untuk mengatasi kendala daftar drama Melolo kosong ("Data kosong") pada Beranda.

---

## 1. Analisis Akar Masalah (Mengapa Muncul "Data kosong" pada Melolo?)
Pada screenshot dilaporkan muncul error:
```text
Belum bisa dimuat: Data kosong
```
- **Akar Masalah:** Saat API Melolo (`/bookmall/tabs?gender=0&lang=id`) mengembalikan data tab beranda, struktur JSON di level sel pertama memiliki properti ganda:
  ```json
  {
    "books": [],
    "cell_data": [ ... 18 item buku ... ]
  }
  ```
- Sebelumnya, pada fungsi `flat()`, pengecekan ditulis:
  ```kotlin
  val b = o.optJSONArray("books")
  if (b != null) out += flat(b, fp) else out += flat(o, fp)
  ```
- Karena properti `"books"` **tidak null** (merupakan array kosong `[]`), kondisi `if (b != null)` bernilai benar dan memanggil `flat([], fp)` yang menghasilkan daftar kosong. Akibatnya, pemanggilan `else out += flat(o, fp)` yang bertugas memeriksa isi `"cell_data"` (tempat 18 buku disimpan) **dilewatkan sepenuhnya**.

---

## 2. Perbaikan di v4.9.4
- **Validasi `length() > 0` pada Seluruh Penampung Array:** Fungsi `flat()` telah diperbarui agar selalu memeriksa apakah array tidak kosong (`length() > 0`) sebelum melakukan penelusuran:
  ```kotlin
  val b = o.optJSONArray("books")
  if (b != null && b.length() > 0) out += flat(b, fp) else out += flat(o, fp)
  ```
- Selain itu, seluruh pengecekan key kontainer (`cell`, `book_tab_infos`, `cells`, `cell_data`, `books`, `items`, `subjects`, `results`) kini dilengkapi validasi `length() > 0`, sehingga array kosong tidak akan menghalangi penelusuran ke key lain dalam objek yang sama.
- Seluruh 18+ judul buku/drama pada tab *Recommended*, *Popular*, dan *Newest* Melolo kini sukses ditampilkan tanpa error "Data kosong".
