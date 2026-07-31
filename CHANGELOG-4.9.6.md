# Changelog v4.9.6 — Perbaikan Fitur "Lanjutkan Menonton" (Continue Watching)

Pembaruan penuh pada sistem pencatatan riwayat (`LocalStore.updateProgress`) dan interaksi klik pada kartu **Lanjutkan menonton** agar pemutaran video langsung berlanjut di tempat terakhir pengguna menonton.

---

## 1. Mengapa Sebelumnya "Lanjutkan Menonton" Terasa Belum Bekerja?
- **Pencatatan Riwayat Gagal saat Tidak Ada di Cache:** Sebelumnya, fungsi `updateProgress()` hanya memperbarui item yang *sudah ada* di daftar `history()`. Jika karena suatu hal item belum tersimpan, `updateProgress()` tidak menambahkan item baru ke riwayat.
- **Persentase `0%` Karena Durasi HLS Tidak Terbaca:** Pada pemutaran streaming HLS/m3u8 tertentu, ExoPlayer belum mengetahui durasi total di detik awal (`dur = 0`). Akibatnya, persentase tontonan (`pct = pos * 100 / dur`) bernilai 0% dan batang progress merah tidak muncul.
- **Klik Kartu Malah Membuka Sinopsis:** Saat pengguna mengklik kartu di baris "Lanjutkan menonton" (di Beranda maupun di Koleksi), aplikasi sebelumnya malah membuka halaman detail sinopsis terlebih dahulu.

---

## 2. Pembaruan di v4.9.6
1. **Penyisipan Riwayat Otomatis (`updateProgress`):**
   - Kini, apakah item sudah ada atau belum (`idx == -1`), fungsi `updateProgress(id, platform, ep, pos, dur)` **selalu menyimpan atau menambahkan item ke daftar riwayat** (`HistoryItem`).
2. **Jaminan Durasi Non-Nol untuk Persentase Akurat:**
   - Ketika durasi dari player bernilai `0`, sistem otomatis mempertahankan durasi sebelumnya atau memakai nilai default `120_000L` (2 menit). Persentase progress tontonan (misal `45% ditonton`) kini selalu akurat dan batang merah menyala jelas pada kartu.
3. **Langsung Putar Video (Instant Resume Playback):**
   - Klik pada kartu di baris **Lanjutkan menonton** (Beranda) maupun di tab **Lanjutkan** (Koleksi) kini memunculkan notifikasi `"Lanjut memutar Ep X..."` dan **langsung membuka pemutar video di episode dan detik terakhir tontonan**, tanpa perlu melewati halaman sinopsis lagi.
