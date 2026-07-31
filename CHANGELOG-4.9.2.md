# Changelog v4.9.2 — Migrasi Endpoint Melolo ke captain.sapimu.au (Lang ID)

Migrasi penuh layanan **Melolo** (`"melolo"`) dari Worker lawas ke endpoint baru `https://captain.sapimu.au/melolo/api/v1` dengan bahasa Indonesia (`lang=id`) serta header autentikasi resmi.

---

## 1. Integrasi 8 Endpoint Melolo Baru (`lang=id`)

Semua request ke API Melolo kini dikirim dengan header wajib:
- `Authorization: Bearer 15693e658f723c5b4c45900a5d045ef0ab6a053ecda4dadb831c68fef773ba5e`
- `User-Agent: Mozilla/5.0`
- Parameter `lang=id` untuk semua endpoint yang mendukung pemilihan bahasa.

### Pemetaan Endpoint di Codebase (`MainActivity.kt`):

1. **Languages (`GET /languages`)**
   - Ditambahkan melalui fungsi `DramakuRepository.meloloLanguages()`.
2. **Search (`GET /search?q=...&lang=id&limit=50&offset=0`)**
   - Dipakai pada `searchPlatform("melolo")` dengan limit 50 item dan `lang=id`.
3. **Search Suggest (`GET /search/suggest?q=...&lang=id`)**
   - Ditambahkan melalui fungsi `DramakuRepository.meloloSearchSuggest()`.
4. **Bookmall Home (`GET /bookmall?lang=id`)**
   - Dipakai pada `homeUrls("melolo")` untuk tab **Recommended** (`h`).
5. **Bookmall Tabs (`GET /bookmall/tabs?gender=0&lang=id`)**
   - Dipakai pada `homeUrls("melolo")` untuk tab **Popular** (`gender=0`) dan **Newest** (`gender=1`). Mendukung pagination (`page=$sp`).
6. **Book Detail (`GET /book?id=...&lang=id`)**
   - Digunakan sebagai fallback pemuatan detail pada `loadDetail()` bila judul bertipe *book*.
7. **Series Detail (`GET /series?id=...&lang=id`)**
   - Digunakan sebagai fallback pemuatan detail pada `loadDetail()` bila judul bertipe *series*.
8. **Multi-Video Stream & Detail (`GET /multi-video?id=...&lang=id`)**
   - Digunakan sebagai endpoint utama pada `loadDetail()` dan `resolveStream()` untuk memuat seluruh episode (hingga 50+ episode) lengkap dengan parameter `stream_url` aktif untuk setiap episode.

---

## 2. Peningkatan Parser & Flattening
- **Pembaruan `flat()`:** Ditambahkan penanganan hierarki penampung `cell -> cell_data -> books`, `book_tab_infos -> cells -> cell_data`, serta `cells -> books` agar seluruh buku pada endpoint baru berhasil diekstrak tanpa terlewat.
- **Pembaruan `normalize()`:** Ditambahkan pembacaan field `book_name`, `book_id`, `abstract`, dan `first_chapter_cover` agar metadata buku dari Melolo dipetakan dengan sempurna.
