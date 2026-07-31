# Changelog v4.9.8 — Perbaikan Bug ID Mismatch pada "Lanjutkan Menonton"

Pembaruan kritis pada seluruh resolver detail drama (`loadDetail`) di `MainActivity.kt` untuk mengatasi masalah riwayat "Lanjutkan menonton" yang tidak terhubung dengan kartu di beranda.

---

## 1. Analisis Akar Masalah (Mengapa Riwayat "Lanjutkan Menonton" Gagal Terhubung?)
- **Akar Masalah (ID Mismatch):** Pada beberapa API (khususnya Melolo `/multi-video` dan MovieBox), objek detail di dalam respons menggunakan ID internal yang berbeda dengan ID pada daftar beranda/pencarian:
  - ID di beranda (`input.id`): misal `7602461581250661429` (`book_id`)
  - ID di dalam respons detail: misal `7602461581250661000` (`series_id`)
- Sebelumnya, fungsi `loadDetail(input)` menimpa `id` menggunakan ID dari dalam respons detail (`series_id`).
- Akibatnya:
  1. Saat video diputar, fungsi `saveHistory()` dan `updateProgress()` menyimpan progress pemutaran di bawah ID baru (`series_id`).
  2. Ketika pengguna kembali ke Beranda, kartu beranda yang memiliki ID `book_id` **tidak menemukan riwayat yang sesuai** di SharedPreferences.
  3. Kartu di baris "Lanjutkan menonton" pun gagal terhubung ke pemutaran terakhir atau tidak muncul sebagaimana mestinya.

---

## 2. Perbaikan di v4.9.8
- **Konsistensi `id = input.id` di Seluruh Resolver Detail:**
  - Seluruh resolver (`melolo`, `moviebox`, `drakor`, `goodshort`, `reelshort`, `flickreels`, `dramanova`, `dramabox`) kini dijamin 100% mempertahankan dan menggunakan `input.id` asli sebagai pengenal utama.
  - Kemajuan pemutaran, persentase tontonan (`pct`), serta episode terakhir kini selalu tepat terhubung antara Beranda, Halaman Detail, dan Pemutar Video.
