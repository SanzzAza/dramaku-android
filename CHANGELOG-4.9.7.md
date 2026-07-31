# Changelog v4.9.7 — Penyederhanaan Nama File APK (Tanpa Label "-release")

Pembaruan pada konfigurasi CI GitHub Actions (`.github/workflows/build-apk.yml`) agar nama file aplikasi yang dihasilkan dan diunduh tidak mengandung kata "release" atau "debugkey".

---

## 1. Perubahan Nama File APK
- **Sebelumnya:** File unduhan APK di halaman rilis bernama `Dramaku-v4.9.6-release.apk` (atau `Dramaku-v4.9.6-release-debugkey.apk`).
- **Sekarang (v4.9.7):** Nama file APK kini disederhanakan menjadi format standar:
  - **`Dramaku-v4.9.7.apk`**
- Demikian pula, nama artifact pada GitHub Actions log kini bernama **`Dramaku-v4.9.7`**.

---

## 2. Kemudahan Instalasi
- Pengguna dapat langsung mengunduh file **`Dramaku-v4.9.7.apk`** melalui menu **Releases -> Assets** di GitHub dan memasangnya di seluruh perangkat Android tanpa kebingungan label versi pada nama file.
