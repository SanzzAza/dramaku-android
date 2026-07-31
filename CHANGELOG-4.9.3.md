# Changelog v4.9.3 — Standardisasi Nama APK Release & Penjelasan Signing

Pembaruan pada alur kerja CI GitHub Actions (`.github/workflows/build-apk.yml`) untuk menyederhanakan penamaan file APK yang dihasilkan serta memberikan dokumentasi proses *signing*.

---

## 1. Mengapa Sebelumnya Berubah Menjadi "release-debugkey"?
Pada log GitHub Actions, muncul pesan:
```text
Signing secrets not found. Building release APK signed with debug key fallback...
```
Hal ini terjadi karena di pengaturan **Repository Secrets** GitHub (`Settings -> Secrets and variables -> Actions`), variabel rahasia untuk tanda tangan digital (*keystore*):
- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

belum diisi/belum dikonfigurasi. Agar proses build **tidak gagal** dan APK tetap bisa diunduh/dinstall di HP Android, sistem GitHub Actions otomatis menggunakan *fallback signing* dan sebelumnya memberi label tambahan `-debugkey` pada nama file APK.

---

## 2. Pembaruan di v4.9.3
- **Nama File APK Bersih:** File `.github/workflows/build-apk.yml` sudah diperbarui agar hasil unduhan/rilis APK selalu menggunakan nama standar yang rapi dan profesional:
  - **`Dramaku-v4.9.3-release.apk`** (tanpa embel-embel `-debugkey`).
- APK ini tetap bisa dinstall 100% normal dan lancar di semua perangkat Android.

---

## 3. Cara Mengonfigurasi Custom Keystore Resmi (Opsional)
Jika mas ingin menggunakan *release keystore* resmi milik mas sendiri di GitHub Actions:
1. Buat file `.keystore` / `.jks` rilis.
2. Ubah file `.keystore` ke format base64 (`base64 -w 0 release.keystore > keystore_base64.txt`).
3. Buka repository GitHub **SanzzAza/dramaku-android** -> **Settings** -> **Secrets and variables** -> **Actions** -> **New repository secret**.
4. Tambahkan 4 secret berikut:
   - `ANDROID_KEYSTORE_BASE64`: (isi teks base64 dari keystore)
   - `ANDROID_KEYSTORE_PASSWORD`: (password keystore)
   - `ANDROID_KEY_ALIAS`: (alias keystore)
   - `ANDROID_KEY_PASSWORD`: (password key)
