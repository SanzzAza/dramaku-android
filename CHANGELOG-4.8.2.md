# Changelog v4.8.2 — Migrasi Endpoint MovieBox

Migrasi penuh layanan **MovieBox** ke endpoint baru `https://captain.sapimu.au/moviebox/api` dengan optimasi autentikasi, dukungan *Short Drama* (Reels), dan penanganan error yang lebih tangguh.

---

## 1. Integrasi 11 Endpoint MovieBox Baru

Semua panggilan ke domain `captain.sapimu.au` sekarang diatur dengan header wajib:
- `Authorization: Bearer 15693e658f723c5b4c45900a5d045ef0ab6a053ecda4dadb831c68fef773ba5e`
- `User-Agent: Mozilla/5.0`
- `Content-Type: application/json` (untuk endpoint POST)

### Pemetaan Endpoint di Codebase (`MainActivity.kt`):

1. **Home Content (`GET /tabs/home-content?lang=id`)**
   - Dipakai di `homeUrls("moviebox")` untuk tab **Recommended**.
2. **Categories (`GET /tabs/categories?lang=id`)**
   - Ditambahkan via fungsi `DramakuRepository.movieboxCategories()`.
3. **Category Content (`GET /tabs/category-content?type=1&lang=id`)**
   - Dipakai di `homeUrls("moviebox")` untuk tab **Popular** dan kategori spesifik.
4. **Search (`POST /subject/search?keyword=...&page=1&perPage=10`)**
   - Dipakai di `searchPlatform("moviebox")`. Panggilan otomatis dikirim dengan metode POST dan body JSON kosong (`""`) agar tidak ditolak oleh proteksi origin/WAF Cloudflare.
5. **Subject Detail (`GET /subject/get?subjectId=...&lang=id`)**
   - Dipakai sebagai endpoint utama di `loadDetail()`.
6. **Stream Movie / Series (`GET /stream/{id}?ep={ep}&se=1&subjectId={id}&lang=id`)**
   - Dipakai di `resolveMovieboxFrom()` untuk resolusi link video & subtitle utama.
7. **Shorts Most Trending (`POST /shorts/most-trending?page=1&perPage=10`)**
   - Dipakai di `homeUrls("moviebox")` untuk tab **Newest / Trending Shorts**.
8. **Shorts Reel (`POST /shorts/reel?page=1&perPage=10`)**
   - Ditambahkan via fungsi `DramakuRepository.movieboxShortsReel()`.
9. **Shorts Info (`GET /shorts/info?subjectId=...&lang=id`)**
   - Dipakai sebagai *fallback detail* di `loadDetail()` ketika ID subjek bertipe Short Drama/Reels.
10. **Shorts Mini-List / Stream (`GET /shorts/mini-list?subjectId=...&ep={ep}&lang=id`)**
    - Dipakai sebagai *fallback streaming* di `resolveMovieboxFrom()` untuk memutar Short Drama MovieBox.
11. **Languages (`GET /tabs/languages?lang=id`)**
    - Ditambahkan via fungsi `DramakuRepository.movieboxLanguages()`.

---

## 2. Perbaikan Fitur & Proteksi Bug

- **Resiliensi Detail (Movie/Series vs Short Drama):**
  `loadDetail()` kini mencoba `/subject/get` terlebih dahulu. Jika respons kosong atau subjek adalah Short Drama, sistem secara halus beralih ke `/shorts/info`.
- **Dukungan Streaming Universal:**
  `resolveMovieboxFrom()` kini mendukung pemutaran dari `/stream/{id}` (`resourceLink`) sekaligus `/shorts/mini-list` (`url`).
- **Penanganan Subtitle:**
  Mendukung pengambilan subtitle secara otomatis dari objek `subtitle`, `extCaptions`, maupun field `caption`.
- **Header Player Media:**
  `streamHeaders("moviebox")` kini menyertakan `User-Agent: Mozilla/5.0` dan `Authorization: Bearer ...` untuk kestabilan pemutaran dari CDN.
