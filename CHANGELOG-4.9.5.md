# Changelog v4.9.5 — Peningkatan Koleksi Melolo (130+ Drama per Halaman)

Pembaruan pada sistem pemuatan beranda Melolo (`loadMeloloHomeBundle`) agar menampilkan ratusan judul drama sekaligus sejak halaman pertama, menjawab pertanyaan mengapa sebelumnya hanya sedikit judul yang tampil.

---

## 1. Mengapa Sebelumnya Hanya Sedikit Drama Melolo yang Tampil?
- Secara bawaan, panggilan tunggal ke endpoint `/bookmall?lang=id` atau `/bookmall/tabs?gender=0&lang=id` hanya mengembalikan **18 judul buku/drama** per satu kali permintaan (*page size* 18).
- Hal ini membuat tampilan beranda Melolo terasa pendek apabila pengguna hanya melihat 1 tab saja.

---

## 2. Pembaruan di v4.9.5 (Multi-Source Aggregation)
- **130+ Judul Unik di Halaman Pertama:** Pada `loadMeloloHomeBundle(page)`, sistem kini memanggil 5 endpoint secara paralel menggunakan *Kotlin Coroutines Async*:
  1. `/bookmall?lang=id` (18 judul)
  2. `/bookmall/tabs?gender=0&lang=id` (18 judul)
  3. `/bookmall/tabs?gender=2&lang=id` (18 judul)
  4. `/search?q=cinta&lang=id&limit=50&offset=0` (63 judul populer ber-tag cinta)
  5. `/search?q=boss&lang=id&limit=50&offset=0` (50 judul populer ber-tag boss)
- Seluruh data digabungkan dan disaring duplikatnya (`dedupe`), menghasilkan **lebih dari 130 judul drama unik bahasa Indonesia (`lang=id`)** langsung pada saat halaman pertama dibuka.
- **Scroll Tanpa Batas (Pagination):** Pada halaman 2, 3, 4, dan 5, aplikasi memuat kombinasi pencarian terpopuler lainnya (`nikah`, `istri`, `suami`, `ceo`), memberikan koleksi lebih dari 400+ judul Melolo secara keseluruhan.
