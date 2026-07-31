# Changelog v4.9.1 — Perbaikan Pemutaran Video (GoodShort, ReelShort & Melolo)

Pembaruan sistem resolusi stream untuk beradaptasi dengan perubahan struktur JSON pada endpoint Worker (`new-api.sonzaix.workers.dev`) v2.

---

## 1. GoodShort (Perbaikan: Hanya Episode 1 yang Bisa Diputar)
- **Masalah:** Sebelumnya resolver GoodShort hanya mengambil daftar episode dari `/detail?bookId=...` yang terbatas pada episode awal (episode 1).
- **Perbaikan:** Kini sistem memprioritaskan panggilan ke `/streamv2?id={id}&ep={ep}` dan `/stream?id={id}&ep={ep}` menggunakan ekstraktor V2 (`tryUnifiedStream()`), sehingga seluruh episode (1, 2, 3, dst.) berhasil memuat link m3u8/mp4 dengan lancar.

---

## 2. ReelShort (Perbaikan: "Video Tidak Tersedia")
- **Masalah:** Worker ReelShort mengubah struktur respons JSON dari format lawas bertingkat `{ "data": { "videoList": [...] } }` menjadi struktur V2 langsung `{ "episodes": [ { "cdnList": [...] } ] }`. Hal ini menyebabkan pengecekan `.optJSONObject("data")` gagal dan memicu error "Video tidak tersedia".
- **Perbaikan:** Kini resolver ReelShort menggunakan ekstraktor V2 terlebih dahulu sebelum fallback ke skema lama. Seluruh episode ReelShort kini berhasil dimuat.

---

## 3. Melolo (Perbaikan: "Link Expired / Berubah")
- **Masalah:** Resolver Melolo sebelumnya mencoba memanggil `/stream?id=...` versi lama yang mengembalikan parameter DRM terenkripsi lama dan membangun URL proxy `/melolo?url=...&kid=...` yang sudah kedaluwarsa.
- **Perbaikan:** Resolver kini memprioritaskan endpoint `/streamv2?id={id}&ep={ep}` yang mengembalikan link `videoPath` baru yang tidak kedaluwarsa dari Worker.

---

## 4. Ekstraktor Universal V2 (`extractStreamV2Url`)
- Ditambahkan dukungan pencarian otomatis pada key `episodes`, `list`, dan `videoList`.
- Pencarian hierarki CDN (`cdnList -> videoPathList -> videoPath`) dengan prioritas resolusi HD/720p.
